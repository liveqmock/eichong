package com.wanma.controller.cec;

		import java.math.BigDecimal;
		import java.text.SimpleDateFormat;
		import java.util.ArrayList;
		import java.util.Calendar;
		import java.util.Date;
		import java.util.HashMap;
		import java.util.List;
		import java.util.Map;

		import net.sf.json.JSONArray;

		import org.apache.commons.lang.StringUtils;
		import org.slf4j.Logger;
		import org.slf4j.LoggerFactory;
		import org.springframework.beans.factory.annotation.Autowired;
		import org.springframework.stereotype.Controller;
		import org.springframework.web.bind.annotation.RequestBody;
		import org.springframework.web.bind.annotation.RequestMapping;
		import org.springframework.web.bind.annotation.ResponseBody;

		import com.alibaba.fastjson.JSON;
		import com.alibaba.fastjson.JSONObject;
		import com.sgcc.utils.DateUtil;
		import com.wanma.model.TblChargingOrder;
		import com.wanma.model.TblElectricpile;
		import com.wanma.model.TblElectricpilehead;
		import com.wanma.model.TblPartner;
		import com.wanma.model.TblPowerstation;
		import com.wanma.model.TblRateInformation;
		import com.wanma.model.TcbElectric;
		import com.wanma.service.PileFilterService;
		import com.wanma.service.TblChargingOrderService;
		import com.wanma.service.TblElectricpileHeadService;
		import com.wanma.service.TblElectricpileService;
		import com.wanma.service.TblPowerstationService;
		import com.wanma.service.TblRateInformationService;
		import com.wanma.service.TblReconciliationService;
		import com.wanma.service.TcbPartnerService;
		import com.wanma.support.common.RedisService;
		import com.wanma.support.common.ResultResponse;
		import com.wanma.support.common.WanmaConstants;
		import com.wanma.support.simple.JudgeNullUtils;
		import com.wanma.support.utils.AesCBC;
		import com.wanma.support.utils.JsonResult;
		import com.wanma.support.utils.RandomUtil;
		import com.wanma.support.utils.RateinformationUtil;
		import com.wanma.support.utils.TokenUtil;

@Controller
@RequestMapping("/v1.0.0")
public class CecControllerV1 {
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger LOGGER = LoggerFactory.getLogger(CecControllerV1.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private RedisService redisService;
    @Autowired
    private TblElectricpileHeadService hService;
    @Autowired
    private TblRateInformationService rateService;
    @Autowired
    private TblPowerstationService psService;
    @Autowired
    private TblElectricpileService elcService;
    @Autowired
    private PileFilterService pileFilterService;
    @Autowired
    private TblElectricpileHeadService headService;
    @Autowired
    private TblChargingOrderService ordService;
    @Autowired
    private TblReconciliationService recobcilicationService;
    @Autowired
    private TcbPartnerService partnerService;

    /**
     * 1:获取Token
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/query_token", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String query_token(@RequestBody Map<String, String> params)
            throws Exception {
        String dataStr = params.get("Data");
        String OperatorID = params.get("OperatorID");
        LOGGER.info("~~~~~~~~~~~~~~~~~~~~请求Data：" + dataStr + "~~~~~~~~~~~~~~~~~~~~");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject reqData = JSON.parseObject(AesCBC.getInstance().decrypt(
                dataStr, "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        if (reqData == null) {
            return JsonResult.handleResult(JsonResult.RESULT_Post, JsonResult.MSG_Null, "", "", "").toString();
        }
        String org = reqData.get("OperatorID").toString();
        String secret = reqData.get("OperatorSecret").toString();
        if (StringUtils.isBlank(org) || StringUtils.isBlank(secret)) {
            return JsonResult.handleResult(JsonResult.RESULT_Null, JsonResult.MSG_Null, "", "", "").toString();
        }
        Map<String, Object> data = new HashMap<String, Object>();
        if (!tblPartner.getWmTokenSecret().equals(secret)) {
            data.put("operatorID", org);
            data.put("succStat", 1);
            data.put("accessToken", "");
            data.put("tokenAvailableTime", 0);
            data.put("failReason", 2);
            data.put("accessToken", "");
            String date = AesCBC.getInstance().encrypt(
                    new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
            String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + date;
            return JsonResult.handleResult(JsonResult.RESULT_Key, JsonResult.MSG_Key, date, tblPartner.getSigSecret(), key).toString();
        }
        String tkVal = redisService.strGet(WanmaConstants.PREFIX_TOKWEN + org);
        String t = "";
        if (tkVal != null) {
            long termT = System.currentTimeMillis()
                    - Long.valueOf(tkVal.split(",")[0]);
            if (termT > WanmaConstants.PREFIX_TOKWEN_TERM) {
                t = TokenUtil.makeToken(org, secret);
                redisService.strSet(WanmaConstants.PREFIX_TOKWEN + org,
                        System.currentTimeMillis() + "," + t);
            } else {
                t = tkVal.split(",")[1];
            }
        } else {
            t = TokenUtil.makeToken(org, secret);
            redisService.strSet(WanmaConstants.PREFIX_TOKWEN + org,
                    System.currentTimeMillis() + "," + t);
        }
        data.put("OperatorID", org);
        data.put("SuccStat", 0);
        data.put("AccessToken", t);
        data.put("TokenAvailableTime", WanmaConstants.PREFIX_TOKWEN_TERM);
        data.put("FailReason", 0);
        String date = AesCBC.getInstance().encrypt(
                new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
        String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + date;
        return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, date, key, tblPartner.getSigSecret());

    }

    /**
     * @return
     * @throws Exception
     * @Description: 查询设备状态
     */
    @SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
    @RequestMapping(value = "/query_station_status", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String stationStatus(@RequestBody Map<String, String> params)
            throws Exception {
        LOGGER.info("================查询设备状态begin==================");
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(
                params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        String ids = jsonData.get("StationIDs").toString();
        List<String> stationIDs = JSONArray.toList(JSONArray.fromObject(ids));
        int count = stationIDs.size();
        if (count > 50) {
            ResultResponse resultRespone = new ResultResponse();
            resultRespone.setStatus(500);
            resultRespone.setMsg("数组长度超长！");
            return resultRespone.toString();
        }
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        Map<String, Object> psData = null;
        for (String psId : stationIDs) {
            psData = new HashMap<String, Object>();
            psData.put("StationID", psId);
            TblElectricpilehead hModel = new TblElectricpilehead();
            hModel.setPsId(Integer.parseInt(psId));
            hModel.setCpyId(tblPartner.getCpyId());
            List<Map<String, Object>> mapList = hService.echongGetHeadStsByPsId(hModel);
            for (Map<String, Object> stringObjectMap : mapList) {
                if ("0".equals(stringObjectMap.get("Status").toString())) {
                    stringObjectMap.put("Status", 0);
                } else if ("1".equals(stringObjectMap.get("Status").toString())) {
                    stringObjectMap.put("Status", 1);
                } else if ("3".equals(stringObjectMap.get("Status").toString())) {
                    stringObjectMap.put("Status", 3);
                } else if ("2".equals(stringObjectMap.get("Status").toString())) {
                    stringObjectMap.put("Status", 2);
                } else if ("4".equals(stringObjectMap.get("Status").toString())) {
                    stringObjectMap.put("Status", 4);
                } else {
                    stringObjectMap.put("Status", 255);
                }
            }
            psData.put("ConnectorStatusInfos", mapList);
            data.add(psData);
        }
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("Total", stationIDs.size());
        returnMap.put("StationStatusInfos", data);
        LOGGER.info("================查询设备状态end==================");
        String date = AesCBC.getInstance().encrypt(
                new JSONObject(returnMap).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
        String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + date;
        return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, date, key, tblPartner.getSigSecret()).toString();
    }

    /**
     * @return
     * @throws Exception
     * @Description: 查询站信息
     */
    @RequestMapping(value = "/query_stations_info", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String stationsInfo(@RequestBody Map<String, String> params)
            throws Exception {
        LOGGER.info("================查询电站信息begin==================");
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(
                params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        String lastQueryTime = jsonData.getString("LastQueryTime");
        String pageNo = jsonData.get("PageNo").toString();
        String pageSize = jsonData.get("PageSize").toString();
        if (StringUtils.isBlank(pageNo))
            pageNo = "1";
        if (StringUtils.isBlank(pageSize))
            pageSize = "10";
        List<TblPowerstation> psList = null;
        Map<String, Object> data = new HashMap<String, Object>();
        int count = 0;
        if (lastQueryTime != null && !lastQueryTime.isEmpty()) {
            LOGGER.info("获取最近更新站点信息开始，上次查询时间：" + lastQueryTime + "开始页" + pageNo
                    + "；每页显示数量：" + pageSize);
            Date queryTime = fmt.parse(lastQueryTime);
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("queryTime", queryTime);
            dataMap.put("pageNo", Integer.parseInt(pageNo) - 1);
            dataMap.put("pageSize", Integer.parseInt(pageSize));
            dataMap.put("cpyId", tblPartner.getCpyId());
            count = psService.getUpdatedCount(dataMap);
            psList = psService.getUpdatedList(dataMap);
            LOGGER.info("获取最近更新站点信息结束");
        } else {
            LOGGER.info("获取全部站点信息开始");
            TblPowerstation powerstation = new TblPowerstation();
            powerstation.setCpyId(tblPartner.getCpyId());
            count = psService.getPowerStationCount(powerstation);
            psList = psService.getPowerStationList(powerstation);
            LOGGER.info("获取全部站点信息结束");
        }
        data.put("ItemSize", count);
        data.put("PageCount", count / Integer.parseInt(pageSize) + 1);
        data.put("PageNo", pageNo);
        List<Map<String, Object>> psDataList = new ArrayList<Map<String, Object>>();
        Map<String, Object> psData = null;
        Map<String, Object> elcData = null;
        Map<String, Object> hData = null;
        TblElectricpile pile = new TblElectricpile();
        for (TblPowerstation psModel : psList) {
            psData = new HashMap<String, Object>();
            psData.put("StationID", String.valueOf(psModel.getPkPowerstation()));
            psData.put("OperatorID", "321895837");
            psData.put("EquipmentOwnerID", "321895837");
            psData.put("StationName", psModel.getPostName());
            psData.put("CountryCode", "CN");
            psData.put("AreaCode", psModel.getPostOwnCountyCode());
            psData.put("Address", psModel.getPostAddress());
            psData.put("StationTel", psModel.getPostPhone());
            psData.put("ServiceTel", psModel.getPostPhone());
            psData.put("StationType", 255);
            if (psModel.getPostStatus() == 10)
                psData.put("StationStatus", 5);
            if (psModel.getPostStatus() == 15)
                psData.put("StationStatus", 50);
            else
                psData.put("StationStatus", 0);
            psData.put("ParkNums", 0);
            psData.put("StationLng", psModel.getPostLongitude());
            psData.put("StationLat", psModel.getPostLatitude());
            psData.put("SiteGuide", "");
            psData.put("Construction", 255);
            String postPic = psModel.getPostPic();
            postPic = postPic == null ? "" : postPic;
            psData.put("Pictures", postPic.split(","));
            psData.put("MatchCars", "");
            psData.put("ParkInfo", "");
            psData.put("BusineHours", psModel.getPoStOnlineTime());
            psData.put("Payment", "");
            psData.put("SupportOrder", 0);
            psData.put("Remark", "");
            pile.setElpiRelevancepowerstation(psModel.getPkPowerstation());
            List<TblElectricpile> pList = elcService.getElectricPileByPowerStationId(pile);
            pile = pList.get(0);
            Map<String, Object> rateParm = new HashMap<String, Object>();
            rateParm.put("pkRateinformation", pile.getElpiRateinformationid());
            TblRateInformation rate = rateService.getPriceById(rateParm);
            if (rate.getRaInServiceCharge() != null || !rate.getRaInServiceCharge().equals(0)) {
                String mark = RateinformationUtil.getCurrentPowerRateMark(JudgeNullUtils.nvlStr(rate.getRaInQuantumDate()));
                switch (mark) {
                    case "1":
                        psData.put("ElectricityFee", String.valueOf(rate.getRaInTipTimeTariff()));
                        psData.put("ServiceFee", String.valueOf(rate.getRaInServiceCharge()));
                        break;
                    case "2":
                        psData.put("ElectricityFee", String.valueOf(rate.getRaInPeakElectricityPrice()));
                        psData.put("ServiceFee", String.valueOf(rate.getRaInServiceCharge()));
                        break;
                    case "3":
                        psData.put("ElectricityFee", String.valueOf(rate.getRaInUsualPrice()));
                        psData.put("ServiceFee", String.valueOf(rate.getRaInServiceCharge()));
                    case "4":
                        psData.put("ElectricityFee", String.valueOf(rate.getRaInValleyTimePrice()));
                        psData.put("ServiceFee", String.valueOf(rate.getRaInServiceCharge()));
                        break;
                    default:
                        psData.put("ElectricityFee", "");
                        psData.put("ServiceFee", String.valueOf(rate.getRaInServiceCharge()));
                }
            } else {
                String mark = RateinformationUtil.getCurrentPowerRateMark(JudgeNullUtils.nvlStr(rate.getRaInQuantumDate()));
                switch (mark) {
                    case "1":
                        psData.put("ElectricityFee", String.valueOf(rate.getRaInTipTimeTariff()));
                        psData.put("ServiceFee", String.valueOf(rate.getRaInTipTimeTariffMoney()));
                        break;
                    case "2":
                        psData.put("ElectricityFee", String.valueOf(rate.getRaInPeakElectricityPrice()));
                        psData.put("ServiceFee", String.valueOf(rate.getRaInPeakElectricityMoney()));
                        break;
                    case "3":
                        psData.put("ElectricityFee", String.valueOf(rate.getRaInUsualPrice()));
                        psData.put("ServiceFee", String.valueOf(rate.getRaInUsualMoney()));
                        break;
                    case "4":
                        psData.put("ElectricityFee", String.valueOf(rate.getRaInValleyTimePrice()));
                        psData.put("ServiceFee", String.valueOf(rate.getRaInValleyTimeMoney()));
                        break;
                    default:
                        psData.put("ElectricityFee", "");
                        psData.put("ServiceFee", "");
                }
            }
            psData.put("ParkFee", "");
            Map<String, Object> test = new HashMap<String, Object>();
            test.put("cpyId", tblPartner.getCpyId());
            test.put("stationNo", psModel.getPkPowerstation());
            List<TcbElectric> elcList = elcService.getElectricList(test);
            List<Map<String, Object>> elcDataList = new ArrayList<Map<String, Object>>();
            for (TcbElectric e : elcList) {
                elcData = new HashMap<String, Object>();
                elcData.put("EquipmentID", e.getEquipNo());
                elcData.put("ManufacturerID", "");
                elcData.put("ManufacturerName", "");
                elcData.put("EquipmentModel", e.getEquipmentModel());
                elcData.put("EquipmentName", e.getEquipName());
                elcData.put("ProductionDate", "");
                if ("14".equals(e.getEquipType()))
                    elcData.put("EquipmentType", 2);
                if ("5".equals(e.getEquipType()))
                    elcData.put("EquipmentType", 1);
                else
                    elcData.put("EquipmentType", 3);
                String powerRating = e.getPowerRating();
                String substring = powerRating.substring(0, powerRating.length() - 2);
                BigDecimal power = new BigDecimal(substring);
                elcData.put("Power", power.setScale(1, BigDecimal.ROUND_HALF_UP));
                elcData.put("EquipmentLng", e.getElcLng());
                elcData.put("EquipmentLat", e.getElcLat());
                TblElectricpilehead hModel = new TblElectricpilehead();
                hModel.setPkElectricpile(e.getPkElc());
                List<TblElectricpilehead> headList = hService.getList(hModel);
                List<Map<String, Object>> headDataList = new ArrayList<Map<String, Object>>();
                for (TblElectricpilehead h : headList) {
                    hData = new HashMap<String, Object>();
                    hData.put("ConnectorID", String.format("%s%02d", e.getEquipNo(), h.getEpheElectricpileHeadId()));
                    hData.put("ConnectorName", h.getEpheElectricpileheadname());
                    if ("14".equals(e.getEquipType()))
                        hData.put("ConnectorType", 3);
                    if ("5".equals(e.getEquipType()))
                        hData.put("ConnectorType", 4);
                    hData.put("VoltageUpperLimits", Integer.parseInt(e.getVoltageRated().split("\\.")[0]));
                    hData.put("VoltageLowerLimits", Integer.parseInt(e.getVoltageRated().split("\\.")[0]));
                    hData.put("Current", Integer.parseInt(e.getCurrentRated().split("\\.")[0]));
                    String powers = e.getPowerRating();
                    String substr = powers.substring(0, powers.length() - 2);
                    BigDecimal power1 = new BigDecimal(substr);
                    hData.put("Power", power1.setScale(1, BigDecimal.ROUND_HALF_UP));
                    hData.put("ParkNo", "");
                    hData.put("NationalStandard", 2);
                    headDataList.add(hData);
                }
                elcData.put("ConnectorInfos", headDataList);
                elcDataList.add(elcData);
            }
            psData.put("EquipmentInfos", elcDataList);
            psDataList.add(psData);
        }
        data.put("StationInfos", psDataList);
        LOGGER.info("================查询电站信息end==================");
        String date = AesCBC.getInstance().encrypt(
                new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
        String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + date;
        return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, date, key, tblPartner.getSigSecret()).toString();
    }

    /**
     * 请求开始充电
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping("/query_start_charge")
    @ResponseBody
    public String startCharge(@RequestBody Map<String, String> params)
            throws Exception {
        LOGGER.info("================请求充电begin==================");
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(
                params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        String StartChargeSeq = jsonData.get("StartChargeSeq").toString();
        String ConnectorID = jsonData.get("ConnectorID").toString();
        String QRCode = jsonData.get("QRCode").toString();
        if (StringUtils.isBlank(StartChargeSeq) || StringUtils.isBlank(ConnectorID) || StringUtils.isBlank(QRCode)) {
            return JsonResult.handleResult(JsonResult.RESULT_Null, JsonResult.MSG_Null, "", "", "").toString();
        }
        LOGGER.info("================校验第三方是否有充电业务begin==================");
        if (Integer.parseInt(tblPartner.getValid()) != 1) {
            return JsonResult.handleResult(JsonResult.RESULT_ErrorService, JsonResult.MSG_No_ChargeService, "", "", "").toString();
        }
        LOGGER.info("================校验订单格式begin==================");
        if (StringUtils.length(StartChargeSeq) != 27 || !StringUtils.substring(StartChargeSeq, 0, 9).equals(OperatorID)) {
            return JsonResult.handleResult(JsonResult.RESULT_Data, JsonResult.MSG_ChargeOrder_Error, "", "", "").toString();
        }
        LOGGER.info("================校验订单是否重复begin==================");
        int orderNum = ordService.checkChargeOrderNum(StartChargeSeq);
        if (orderNum >= 1) {
            return JsonResult.handleResult(JsonResult.RESULT_ChargeSeqEcho, JsonResult.MSG_StartChargeSeq_Echo, "", "", "").toString();
        }
        LOGGER.info("================校验能否充电begin==================");
        TblElectricpilehead hModel = new TblElectricpilehead();
        String electricPileCode = StringUtils.substring(ConnectorID, 0, 16);
        String epheElectricpileHeadId = StringUtils.substring(ConnectorID, 16, 18);
        hModel.setElectricPileCode(electricPileCode);
        hModel.setElectricpileHeadId(epheElectricpileHeadId);
        Map<String, Object> electricpilehead = headService.getHeadByPkhead(hModel);
        boolean ok = pileFilterService.checkOk(OperatorID,
                String.valueOf(electricpilehead.get("pkElectricpile")));
        if (ok == false) {
            return JsonResult.handleResult(JsonResult.RESULT_NoStart, JsonResult.MSG_NoStart, "", "", "").toString();
        }
        LOGGER.info("================校验电桩枪口是否空闲状态begin==================");
        if (Integer.valueOf(electricpilehead.get("ElectricpileHeadState").toString()) != 0) {
            return JsonResult.handleResult(JsonResult.RESULT_Employ, JsonResult.MSG_ElectricPile_Employ, "", "", "").toString();
        }
        LOGGER.info("orgNo :" + tblPartner.getOrgNo() + "userIdentity :" + OperatorID + RandomUtil.createData(3)
                + "epCode :" + String.valueOf(electricpilehead.get("electricPileCode"))
                + "epGunNo :" + Integer.valueOf(electricpilehead.get("epheElectricpileHeadId").toString())
                + "extra :" + StartChargeSeq);
        int rtCode = WanmaConstants.cs.startChange(
                Integer.parseInt(tblPartner.getOrgNo()), OperatorID + RandomUtil.createData(3),
                String.valueOf(electricpilehead.get("electricPileCode")),
                Integer.valueOf(electricpilehead.get("epheElectricpileHeadId").toString()), new Short("1"),
                20000, 2, "", "", 0, StartChargeSeq);
        LOGGER.info("下发充电命令结束！");
        Map<String, Object> data = new HashMap<String, Object>();
        if (rtCode > 0) {
            if (rtCode == 6000) {
                data.put("StartChargeSeq", StartChargeSeq);
                data.put("StartChargeSeqStat", 5);
                data.put("ConnectorID", ConnectorID);
                data.put("SuccStat", 1);
                data.put("FailReason", 2);
            } else {
                data.put("StartChargeSeq", StartChargeSeq);
                data.put("StartChargeSeqStat", rtCode);
                data.put("ConnectorID", ConnectorID);
                data.put("SuccStat", 1);
                data.put("FailReason", 3);
            }
        } else {
            data.put("StartChargeSeq", StartChargeSeq);
            data.put("StartChargeSeqStat", 1);
            data.put("ConnectorID", ConnectorID);
            data.put("SuccStat", 0);
            data.put("FailReason", 0);
        }
        String date = AesCBC.getInstance().encrypt(
                new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
        String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + date;
        return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, date, key, tblPartner.getSigSecret()).toString();
    }

    /**
     * 请求结束充电
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping("/query_stop_charge")
    @ResponseBody
    public String query_stop_charge(@RequestBody Map<String, String> params)
            throws Exception {
        LOGGER.info("================请求结束充电begin==================");
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(
                params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        String StartChargeSeq = jsonData.get("StartChargeSeq").toString();
        String ConnectorID = jsonData.get("ConnectorID").toString();
        if (StringUtils.isBlank(StartChargeSeq) || StringUtils.isBlank(ConnectorID)) {
            return JsonResult.handleResult(JsonResult.RESULT_Null, JsonResult.MSG_Null, "", "", "").toString();
        }
        LOGGER.info("================校验第三方是否有充电业务begin==================");
        if (Integer.parseInt(tblPartner.getValid()) != 1) {
            return JsonResult.handleResult(JsonResult.RESULT_ErrorService, JsonResult.MSG_No_ChargeService, "", "", "").toString();
        }
        LOGGER.info("================校验订单格式是否正确==================");
        if (StringUtils.length(StartChargeSeq) != 27 || !StringUtils.substring(StartChargeSeq, 0, 9).equals(OperatorID)) {
            return JsonResult.handleResult(JsonResult.RESULT_Data, JsonResult.MSG_ChargeOrder_Error, "", "", "").toString();
        }
        LOGGER.info("================校验订单是否存在==================");
        TblChargingOrder model = new TblChargingOrder();
        model.setChorParterExtradata(StartChargeSeq);
        model = ordService.selectChargeOrder(model);
        if (model == null) {
            return JsonResult.handleResult(JsonResult.RESULT_Data_Error, JsonResult.MSG_No_ChargeOrder, "", "", "").toString();
        }
        String driverId = model.getChorParterUserLogo();
        LOGGER.info("下发停止充电命令开始，第三方用户编号：" + driverId + ";第三方标识：" + tblPartner.getOrgNo());
        int rtCode = WanmaConstants.cs.stopChange(model.getChorPilenumber(),
                model.getChorMuzzle(), Integer.parseInt(tblPartner.getOrgNo()), driverId, "");
        LOGGER.info("下发停止充电命令结束！");
        Map<String, Object> data = new HashMap<String, Object>();
        if (rtCode > 0) {
            if (rtCode == 6000) {
                data.put("StartChargeSeq", StartChargeSeq);
                data.put("StartChargeSeqStat", 5);
                data.put("ConnectorID", ConnectorID);
                data.put("SuccStat", 1);
                data.put("FailReason", 2);
            } else {
                data.put("StartChargeSeq", StartChargeSeq);
                data.put("StartChargeSeqStat", rtCode);
                data.put("SuccStat", 1);
                data.put("FailReason", 4);
            }
        } else {
            data.put("StartChargeSeq", StartChargeSeq);
            data.put("StartChargeSeqStat", 4);
            data.put("SuccStat", 0);
            data.put("FailReason", 0);
        }
        String date = AesCBC.getInstance().encrypt(
                new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
        String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + date;
        return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, date, key, tblPartner.getSigSecret()).toString();
    }

    /**
     * 查询统计信息
     *
     * @param params
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/query_station_stats")
    public String getStationStats(@RequestBody Map<String, String> params) throws Exception {
        LOGGER.info(".................查询统计信息-begin.......................");
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        String stationId = jsonData.getString("StationID");
        String startTime = jsonData.getString("StartTime");
        String endTime = jsonData.getString("EndTime");
        if (StringUtils.isBlank(stationId) || StringUtils.isBlank(startTime)
                || StringUtils.isBlank(endTime)) {
            return JsonResult.handleResult(JsonResult.RESULT_Null, JsonResult.MSG_Null, "", "", "").toString();
        }
        LOGGER.info(".................检验是否是白名单-begin.......................");
        Map<String, Object> mapl = new HashMap<String, Object>();
        mapl.put("OperatorID", OperatorID);
        mapl.put("stationId", stationId);
        int count = elcService.checkPowerStation(mapl);
        if (count == 0) {
            return JsonResult.handleResult(JsonResult.RESULT_Data_Error, JsonResult.MSG_No_StationID, "", "", "").toString();
        }
        LOGGER.info(".................检验是否是白名单-end.......................");
        //把endTime增加1天
        Date date = sdf.parse(endTime);
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        cld.add(Calendar.DATE, 1);
        String endTimes = sdf.format(cld.getTime());
        //获取充电站统计信息
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("stationId", stationId);
        model.put("startTime", startTime);
        model.put("endTime", endTimes);
        //电站累计电量
        String stationEle;
        List<Map<String, Object>> eleList = new ArrayList<Map<String, Object>>();
        try {
            stationEle = elcService.getStationMeterNum(model);
            eleList = elcService.getEleMeterNum(model);
        } catch (Exception e) {
            return JsonResult.handleResult(JsonResult.RESULT_Data_Error, JsonResult.MSG_No_StationID, "", "", "").toString();
        }
        //查询结果为空
        if (StringUtils.isBlank(stationEle) || eleList.size() == 0) {
            LOGGER.info("...........该电站在该时期内没有相应数据...................");
            Map<String, Object> stationInfo = new HashMap<String, Object>();
            stationInfo.put("StationID", stationId);
            stationInfo.put("StartTime", startTime);
            stationInfo.put("EndTime", endTime);
            stationInfo.put("StationElectricity", 0);
            stationInfo.put("EquipmentStatsInfos", null);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("StationStats", stationInfo);
            LOGGER.info("..................查询统计信息-end...................");
            //数据加密
            String datas = AesCBC.getInstance().encrypt(new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
            String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + datas;
            return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, datas, key, tblPartner.getSigSecret()).toString();
        }
        //查询结果不为空
        BigDecimal stationElectricity = new BigDecimal(stationEle);
        //充电站统计信息
        Map<String, Object> stationInfo = new HashMap<String, Object>();
        stationInfo.put("StationID", stationId);
        stationInfo.put("StartTime", startTime);
        stationInfo.put("EndTime", endTime);
        stationInfo.put("StationElectricity", stationElectricity.setScale(1, BigDecimal.ROUND_HALF_UP));
        //充电设备统计信息
        List<Map<String, Object>> eleInfo = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < eleList.size(); i++) {
            Map<String, Object> eleMap = eleList.get(i);
            String epCode = eleMap.get("epCode").toString();
            String eleMeter = eleMap.get("eleMeter").toString();
            BigDecimal eqElectricity = new BigDecimal(eleMeter);
            Map<String, Object> eleData = new HashMap<String, Object>();
            eleData.put("EquipmentID", epCode);
            eleData.put("EquipmentElectricity", eqElectricity.setScale(1, BigDecimal.ROUND_HALF_UP));
            List<Map<String, Object>> headList = new ArrayList<Map<String, Object>>();
            Map<String, Object> hmap = new HashMap<String, Object>();
            hmap.put("epCode", epCode);
            hmap.put("startTime", startTime);
            hmap.put("endTime", endTimes);
            headList = elcService.getHeadMeterNum(hmap);
            //充电设备接口统计信息
            Map<String, Object> ehData = new HashMap<String, Object>();
            List<Map<String, Object>> ehInfo = new ArrayList<Map<String, Object>>();
            for (int j = 0; j < headList.size(); j++) {
                Map<String, Object> headMap = headList.get(j);
                int headId = Integer.parseInt(headMap.get("headId").toString());
                String headMeter = headMap.get("headMeter").toString();
                String ConnectorID = null;
                if (headId < 10) {
                    ConnectorID = epCode + "0" + headId;
                } else {
                    ConnectorID = epCode + headId;
                }
                ehData.put("ConnectorID", ConnectorID);
                BigDecimal gm = new BigDecimal(headMeter);
                ehData.put("ConnectorElectricity", gm.setScale(1, BigDecimal.ROUND_HALF_UP));
                ehInfo.add(ehData);
            }
            eleData.put("ConnectorStatsInfos", ehInfo);
            eleInfo.add(eleData);
        }
        stationInfo.put("EquipmentStatsInfos", eleInfo);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("StationStats", stationInfo);
        LOGGER.info("..................查询统计信息-end...................");
        //数据加密
        String datas = AesCBC.getInstance().encrypt(new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
        String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + datas;
        return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, datas, key, tblPartner.getSigSecret()).toString();
    }

    /**
     * 查询充电状态
     *
     * @param params
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/query_equip_charge_status")
    public String getChargeStats(@RequestBody Map<String, String> params) throws Exception {
        LOGGER.info("....................查询充电状态-begin.......................");
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        String StartChargeSeq = jsonData.getString("StartChargeSeq");
        if (StringUtils.isBlank(StartChargeSeq)) {
            return JsonResult.handleResult(JsonResult.RESULT_Null, JsonResult.MSG_Null, "", "", "").toString();
        }
        LOGGER.info("...............校验订单格式-begin........................");
        if (StringUtils.length(StartChargeSeq) != 27 || !StringUtils.substring(StartChargeSeq, 0, 9).equals(OperatorID)) {
            return JsonResult.handleResult(JsonResult.RESULT_Data, JsonResult.MSG_ChargeOrder_Error, "", "", "").toString();
        }
        LOGGER.info("...............校验订单格式-end........................");
        int count = ordService.checkChargeOrderIsExist(StartChargeSeq);
        if (count == 0) {
            return JsonResult.handleResult(JsonResult.RESULT_Data_Error, JsonResult.MSG_No_ChargeOrder, "", "", "").toString();
        }
        Map<String, Object> map = ordService.getChargingOrderState(StartChargeSeq);
        //充电订单状态
        int orderStatus = Integer.parseInt(map.get("orderStatus").toString());
        //桩体编码
        String epCode = map.get("epCode").toString();
        //枪头编号
        int ehId = Integer.parseInt(map.get("ehId").toString());
        //充电设备接口编码--桩体编码   + 枪头编号（两位）
        String ConnectorID = null;
        if (ehId < 10) {
            ConnectorID = epCode + "0" + ehId;
        } else {
            ConnectorID = epCode + ehId;
        }
        //充电设备接口状态
        String ehState = map.get("ehState").toString();
        String endSoc = map.get("endSoc").toString();
        BigDecimal soc = new BigDecimal(endSoc);
        String st = map.get("startTime").toString();
        String startTime = st.substring(0, 19);
        String et = map.get("endTime").toString();
        String endTime = et.substring(0, 19);
        //累计充电量
        String tp = map.get("totalPower").toString();
        BigDecimal totalPower = new BigDecimal(tp);
        //累计电费
        String em = map.get("elecMoney").toString();
        BigDecimal elecMoney = new BigDecimal(em);
        //累计服务费
        String sm = map.get("serviceMoney").toString();
        BigDecimal serviceMoney = new BigDecimal(sm);
        //累计总金额
        String tm = map.get("totalMoney").toString();
        BigDecimal totalMoney = new BigDecimal(tm);
        String chargeMode = map.get("chargeMode").toString();
        //响应数据
        Map<String, Object> data = new HashMap<String, Object>();
        if (orderStatus == 2 || orderStatus == 3) {
            LOGGER.info("....................查询充电状态-历史订单信息.......................");
            data.put("StartChargeSeq", StartChargeSeq);
            data.put("StartChargeSeqStat", 4);
            data.put("ConnectorID", ConnectorID);
            int conStatus = 0;
            if (ehState == "0") {
                conStatus = 1;
            }
            if (ehState == "17") {
                conStatus = 2;
            }
            if (ehState == "6") {
                conStatus = 3;
            }
            if (ehState == "3") {
                conStatus = 4;
            }
            if (ehState == "9") {
                conStatus = 255;
            }
            data.put("ConnectorStatus", conStatus);
            data.put("CurrentA", 0);
            data.put("CurrentB", 0);
            data.put("CurrentC", 0);
            data.put("VoltageA", 0);
            data.put("VoltageB", 0);
            data.put("VoltageC", 0);
            if ("5".equals(chargeMode)) {
                data.put("Soc", soc);
            } else {
                data.put("Soc", 0);
            }
            data.put("StartTime", startTime);
            data.put("EndTime", endTime);
            data.put("TotalPower", totalPower);
            data.put("ElecMoney", elecMoney);
            data.put("ServiceMoney", serviceMoney);
            data.put("TotalMoney", totalMoney);
            data.put("SumPeriod", 0);
            data.put("ChargeDetails", null);
        } else {
            LOGGER.info("....................查询充电状态-实时订单信息.......................");
            String chargingOrder = redisService.strGet(WanmaConstants.PREFIX_CHARGING_ORDER + StartChargeSeq);
            if (chargingOrder == null) {
                chargingOrder = "0|0|0|0";
            }
            String[] info = chargingOrder.split("\\|");
            //A相电流
            int outCurrent = Integer.parseInt(info[0]);
            BigDecimal CurrentA = new BigDecimal(outCurrent);
            CurrentA = CurrentA.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            //A相电压
            int outVol = Integer.parseInt(info[1]);
            BigDecimal VoltageA = new BigDecimal(outVol);
            VoltageA = VoltageA.divide(new BigDecimal(10), 2, BigDecimal.ROUND_HALF_UP);
            //剩余电量soc
            int cacheSoc = Integer.parseInt(info[2]);
            //充电量
            String chargeMeterNum = info[3];
            BigDecimal chargePower = new BigDecimal(chargeMeterNum);
            //充电量格式化
            chargePower = chargePower.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
            data.put("StartChargeSeq", StartChargeSeq);
            data.put("StartChargeSeqStat", 2);
            data.put("ConnectorID", ConnectorID);
            data.put("ConnectorStatus", 3);
            data.put("CurrentA", CurrentA);
            data.put("CurrentB", 0);
            data.put("CurrentC", 0);
            data.put("VoltageA", VoltageA);
            data.put("VoltageB", 0);
            data.put("VoltageC", 0);
            if ("5".equals(chargeMode)) {
                data.put("Soc", cacheSoc);
            } else {
                data.put("Soc", 0);
            }
            data.put("StartTime", startTime);
            Date date = new Date();
            String currentTime = fmt.format(date);
            data.put("EndTime", currentTime);
            data.put("TotalPower", chargePower);
            data.put("ElecMoney", 0);
            data.put("ServiceMoney", 0);
            data.put("TotalMoney", 0);
            data.put("SumPeriod", 0);
            data.put("ChargeDetails", null);
        }
        LOGGER.info("................查询充电状态-end..................");
        //数据加密
        String datas = AesCBC.getInstance().encrypt(new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
        String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + datas;
        return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, datas, key, tblPartner.getSigSecret()).toString();
    }

    /**
     * 请求设备认证
     *
     * @param params
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/query_equip_auth")
    public String getEquipAuth(@RequestBody Map<String, String> params) throws Exception {
        LOGGER.info("...................请求设备认证-begin........................");
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        String equipAuthSeq = jsonData.getString("EquipAuthSeq");
        String epheElectricpileHeadId = jsonData.getString("ConnectorID");
        LOGGER.info("请求设备认证的流水号EquipAuthSeq：" + equipAuthSeq + ",充电设备接口编码ConnectorID：" + epheElectricpileHeadId);
        if (StringUtils.isBlank(equipAuthSeq) || StringUtils.isBlank(epheElectricpileHeadId)) {
            return JsonResult.handleResult(JsonResult.RESULT_Null, JsonResult.MSG_Null, "", "", "").toString();
        }
        LOGGER.info("................校验设备流水号格式是否正确.......................");
        if (StringUtils.length(equipAuthSeq) != 27 || !StringUtils.substring(equipAuthSeq, 0, 9).equals(OperatorID)) {
            return JsonResult.handleResult(JsonResult.RESULT_Data, JsonResult.MSG_EquipAuthSeq_Error, "", "", "").toString();
        }
        String eleHead = StringUtils.substring(epheElectricpileHeadId, 0, 16);
        String headId = StringUtils.substring(epheElectricpileHeadId, 16, 18);
        LOGGER.info(".................检验是否是白名单-begin.......................");
        Map<String, Object> mapl = new HashMap<String, Object>();
        mapl.put("OperatorID", OperatorID);
        mapl.put("epCode", eleHead);
        int count = hService.checkEquipIsRela(mapl);
        if (count == 0) {
            return JsonResult.handleResult(JsonResult.RESULT_Data_Error, JsonResult.MSG_No_ConnectorID, "", "", "").toString();
        }
        LOGGER.info(".................检验是否是白名单-end.......................");
        int num = 0;
        try {
            Map<String, Object> maph = new HashMap<String, Object>();
            maph.put("headId", headId);
            maph.put("epCode", eleHead);
            num = hService.getEquipAuthByEleHead(maph);
        } catch (Exception e) {
            return JsonResult.handleResult(JsonResult.RESULT_Data_Error, JsonResult.MSG_No_ConnectorID, "", "", "").toString();
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("EquipAuthSeq", equipAuthSeq);
        data.put("ConnectorID", epheElectricpileHeadId);
        if (num == 0) {
            data.put("SuccStat", 0);
            data.put("FailReason", 0);
        } else if (num == 17) {
            data.put("SuccStat", 1);
            data.put("FailReason", 1);
        } else {
            data.put("SuccStat", 1);
            data.put("FailReason", 2);
        }

        LOGGER.info("................请求设备认证-end..................");
        //数据加密
        String datas = AesCBC.getInstance().encrypt(new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
        String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + datas;
        return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, datas, key, tblPartner.getSigSecret()).toString();
    }

    /**
     * 推送订单对账信息
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping("/check_charge_orders")
    @ResponseBody
    public String checkChargeOrder(@RequestBody Map<String, String> params) throws Exception {
        LOGGER.info("~~~~~~~~~~~~~开始进行推送-begin~~~~~~~~~~~~~~~");
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        String cpyNumber = jsonData.getString("OperatorID");
        Map<String, Object> map = new HashMap<>();
        map.put("cpyNumber", cpyNumber);
        Map<String, Object> maps = psService.getPartnerKeyList(map);
        Date lastTime = new Date();
        lastTime = DateUtil.addDateDays(lastTime, -1L);
        Date beginTime = DateUtil.getDailyStartTime(lastTime);
        Date endTime = DateUtil.getDailyEndTime(lastTime);
        recobcilicationService.PushChargeOrder(maps.get("cpyNumber").toString(), beginTime, endTime);
        LOGGER.info("~~~~~~~~~~~~对账订单推送-end~~~~~~~~~~~~~~~~");
        return "Success";
    }

    /**
     * 查询充电订单信息
     *
     * @param params
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/query_charge_order_info")
    public String getChargeOrderInfo(@RequestBody Map<String, String> params) throws Exception {
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        LOGGER.info("................查询充电订单信息-begin..................");
        String StartChargeSeq = jsonData.getString("StartChargeSeq");
        if (StringUtils.isBlank(StartChargeSeq)) {
            return JsonResult.handleResult(JsonResult.RESULT_Null, JsonResult.MSG_Null, "", "", "").toString();
        }
        LOGGER.info("...............校验订单格式-begin........................");
        if (StringUtils.length(StartChargeSeq) != 27 || !StringUtils.substring(StartChargeSeq, 0, 9).equals(OperatorID)) {
            return JsonResult.handleResult(JsonResult.RESULT_Data, JsonResult.MSG_ChargeOrder_Error, "", "", "").toString();
        }
        LOGGER.info("...............校验订单格式-end........................");
        int count = ordService.checkChargeOrderIsExist(StartChargeSeq);
        if (count == 0) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("SuccStat", 1);
            String datas = AesCBC.getInstance().encrypt(new JSONObject(result).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
            String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + datas;
            return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, datas, key, tblPartner.getSigSecret()).toString();
        }
        //查询结果
        Map<String, Object> map = ordService.getChargeOrderInfo(StartChargeSeq);
        //桩体编码
        String epCode = map.get("epCode").toString();
        //枪头编号
        int ehId = Integer.parseInt(map.get("ehId").toString());
        //充电设备接口编码--桩体编码   + 枪头编号（两位）
        String ConnectorID = null;
        if (ehId < 10) {
            ConnectorID = epCode + "0" + ehId;
        } else {
            ConnectorID = epCode + ehId;
        }
        String st = map.get("startTime").toString();
        String startTime = st.substring(0, 19);
        String et = map.get("endTime").toString();
        String endTime = et.substring(0, 19);
        String tp = map.get("totalPower").toString();
        BigDecimal totalPower = new BigDecimal(tp);
        String em = map.get("elecMoney").toString();
        BigDecimal elecMoney = new BigDecimal(em);
        String sm = map.get("serviceMoney").toString();
        BigDecimal serviceMoney = new BigDecimal(sm);
        String tm = map.get("totalMoney").toString();
        BigDecimal totalMoney = new BigDecimal(tm);
        String stopCause = map.get("stopCause").toString();
        Date date = sdf.parse(endTime);
        Calendar cld = Calendar.getInstance();
        cld.setTime(date);
        cld.add(Calendar.DATE, 1);
        String workDate = sdf.format(cld.getTime());
        Map<String, Object> data = new HashMap<>();
        data.put("StartChargeSeq", StartChargeSeq);
        data.put("ConnectorID", ConnectorID);
        data.put("StartTime", startTime);
        data.put("EndTime", endTime);
        data.put("TotalPower", totalPower);
        data.put("TotalElecMoney", elecMoney);
        data.put("TotalSeviceMoney", serviceMoney);
        data.put("TotalMoney", totalMoney);
        data.put("StopReason", stopCause);
        data.put("WorkDate", workDate);
        data.put("SuccStat", 0);
        LOGGER.info("................查询充电订单信息-end..................");
        String datas = AesCBC.getInstance().encrypt(new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
        String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + datas;
        return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, datas, key, tblPartner.getSigSecret()).toString();
    }

    /**
     * 查询业务策略信息
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping("/query_equip_business_policy")
    @ResponseBody
    public String queryBusiness(@RequestBody Map<String, String> params) throws Exception {
        String OperatorID = params.get("OperatorID");
        TblPartner tblPartner = partnerService.PartnerInfo(OperatorID);
        JSONObject jsonData = JSON.parseObject(AesCBC.getInstance().decrypt(params.get("Data"), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv()));
        String equipBizSeq = jsonData.getString("EquipBizSeq");
        String ConnectorID = jsonData.getString("ConnectorID");
        LOGGER.info("业务策略查询流水号EquipAuthSeq：" + equipBizSeq + ",充电设备接口编码ConnectorID：" + ConnectorID);
        if (StringUtils.isBlank(equipBizSeq) || StringUtils.isBlank(ConnectorID)) {
            return JsonResult.handleResult(JsonResult.RESULT_Null, JsonResult.MSG_Null, "", "", "").toString();
        }
        LOGGER.info("................校验业务策略流水号格式是否正确.......................");
        if (StringUtils.length(equipBizSeq) != 27 || !StringUtils.substring(equipBizSeq, 0, 9).equals(OperatorID)) {
            return JsonResult.handleResult(JsonResult.RESULT_Data, JsonResult.MSG_EquipAuthSeq_Error, "", "", "").toString();
        }
        String ePCode = StringUtils.substring(ConnectorID, 0, 16);
        LOGGER.info(".................校验是否是白名单-begin.......................");
        Map<String, Object> map = new HashMap<>();
        map.put("OperatorID", OperatorID);
        map.put("epCode", ePCode);
        int count = hService.checkEquipIsRela(map);
        if (count == 0) {
            LOGGER.info(".................校验此电桩不是白名单.......................");
            return JsonResult.handleResult(JsonResult.RESULT_Data_Error, JsonResult.MSG_No_ConnectorID, "", "", "").toString();
        }
        Map<String, Object> date = new HashMap<>();
        date.put("OperatorID", OperatorID);
        date.put("epCode", ePCode);
        TblRateInformation rate = rateService.queryByRateInfo(date);
        List<Map<String, Object>> rateMapList = new ArrayList<>();
        // 个性化费率
        if (rate != null) {
            rateMapList = RateinformationUtil.getPowerRateMarks(rate);
        } else {
            //电桩默认费率
            rate = rateService.queryByPriceId(date);
            rateMapList= RateinformationUtil.getPowerRateMarks(rate);
        }
            Map<String, Object> data = new HashMap<>();
            data.put("EquipBizSeq", equipBizSeq);
            data.put("ConnectorID", ConnectorID);
            data.put("SumPeriod", rateMapList.size());
            if (rate!=null ) {
                data.put("FailReason", 0);
                data.put("SuccStat", 0);
            } else {
                data.put("FailReason", 1);
                data.put("SuccStat", 1);
            }
            data.put("PolicyInfos", rateMapList);
            LOGGER.info("................请求业务策略-end..................");
            //数据加密
            String maps = AesCBC.getInstance().encrypt(new JSONObject(data).toString(), "utf-8", tblPartner.getAesSecret(), tblPartner.getAesIv());
            String key = JsonResult.RESULT_OK + JsonResult.MSG_Ok + maps;
            return JsonResult.handleResult(JsonResult.RESULT_OK, JsonResult.MSG_Ok, maps, key, tblPartner.getSigSecret()).toString();

        }

}
