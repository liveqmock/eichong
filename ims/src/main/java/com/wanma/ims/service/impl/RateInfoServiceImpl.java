package com.wanma.ims.service.impl;

import com.wanma.ims.common.domain.ElectricPileDO;
import com.wanma.ims.common.domain.LogCommitDO;
import com.wanma.ims.common.domain.RateInfoDO;
import com.wanma.ims.common.domain.UserDO;
import com.wanma.ims.common.dto.BaseResultDTO;
import com.wanma.ims.constants.ResultCodeConstants;
import com.wanma.ims.core.GlobalSystem;
import com.wanma.ims.mapper.ElectricPileMapper;
import com.wanma.ims.mapper.LogCommitMapper;
import com.wanma.ims.mapper.RateInfoMapper;
import com.wanma.ims.service.RateInfoService;
import com.wanma.ims.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("RateInfoService")
public class RateInfoServiceImpl implements RateInfoService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RateInfoMapper rateInfoMapper;

	@Autowired
	private LogCommitMapper logCommitMapper;
	
	@Autowired
	private ElectricPileMapper electricPileMapper;
	
	@Override
	public Long getRateInfoCount(RateInfoDO rateInfo) {
		return rateInfoMapper.getRateInfoCount(rateInfo);
	}
	
	@Override
	public List<RateInfoDO> getRateInfoList(RateInfoDO rateInfo) {
		return rateInfoMapper.getRateInfoList(rateInfo);
	}
	
	@Override
	@Transactional
	public BaseResultDTO addRateInfo(RateInfoDO rateInfo, UserDO loginUser) throws Exception{
		BaseResultDTO baseResultDTO = new BaseResultDTO();
		//验证费率信息
		if (rateInfo == null) {
			log.error(this.getClass() + ".addRateInfo() error : " + ResultCodeConstants.ERROR_MSG_EMPTY_RATEINFO);
			baseResultDTO.setSuccess(false);
			baseResultDTO.setErrorDetail(ResultCodeConstants.ERROR_MSG_EMPTY_RATEINFO);
			return baseResultDTO;
		}
		if (loginUser == null || loginUser.getUserId() == null) {
			log.error(this.getClass() + ".addRateInfo() error : " + loginUser.getUserId() + ResultCodeConstants.ERROR_MSG_EMPTY_USER_INFO);
			baseResultDTO.setSuccess(false);
			baseResultDTO.setErrorDetail(ResultCodeConstants.ERROR_MSG_EMPTY_USER_INFO);
			return baseResultDTO;
		}
		
		RateInfoDO qryRateInfoDO = new RateInfoDO();
		qryRateInfoDO.setRaIn_Name(rateInfo.getRaIn_Name());
		Long count = rateInfoMapper.getRateInfoCount(qryRateInfoDO);
		if (count != null && count > 0L) {
			log.error(this.getClass() + ".addRateInfo() error : 费率名称已经存在！");
			baseResultDTO.setSuccess(false);
			baseResultDTO.setErrorDetail("费率名称已经存在！");
			return baseResultDTO;
		}
		
		rateInfo.setCreator_id(loginUser.getUserId().toString());
		// 取系统当前时间
		Date now = DateUtil.parse(DateUtil.getNow(DateUtil.TYPE_COM_YMDHMS));
		rateInfo.setRaIn_Createdate(now);
		rateInfo.setRaIn_Updatedate(now);

		//告警金额使用默认值10元
		rateInfo.setRaIn_WarnMoney(new BigDecimal(10));
		//最小欲动结金额默认10元
		if(rateInfo.getRaIn_MinFreezingMoney()==null||rateInfo.getRaIn_MinFreezingMoney()+""==""){
			rateInfo.setRaIn_MinFreezingMoney(new BigDecimal(10));
		}
		
		rateInfo.setRaIn_EffectiveDates(new Date());
		rateInfo.setRaIn_ExpiryDate(DateUtil.parse(DateUtil.addYear(DateUtil.format(new Date()), 20)));
		
		rateInfo.setRaIn_StartQuantumDate(new Date());
		rateInfo.setRaIn_EndQuantumDate(DateUtil.parse(DateUtil.addYear(DateUtil.format(new Date()), 20)));
		
		rateInfo.setRaIn_area_id("");
		rateInfo.setRaIn_city_id("");
		rateInfo.setRaIn_province_id("");
		rateInfo.setRaIn_FreezingMoney(new BigDecimal(0));
		rateInfo.setRaIn_TimeMarker(new BigDecimal(0));
		rateInfo.setRaIn_ReservationRate(new BigDecimal(0.0));
		rateInfo.setRaIn_remarks("");
		
		//如果不是新账户，需要将尖、峰、平、谷的服务费置成0；如果是新账户，服务费置成0
		if (rateInfo.getRaIn_type() != 3) {
			rateInfo.setRaIn_TipTimeTariffMoney(BigDecimal.ZERO);
			rateInfo.setRaIn_PeakElectricityMoney(BigDecimal.ZERO);	
			rateInfo.setRaIn_UsualMoney(BigDecimal.ZERO);
			rateInfo.setRaIn_ValleyTimeMoney(BigDecimal.ZERO);
		}else{
			rateInfo.setRaIn_ServiceCharge(BigDecimal.ZERO);
		}
		
		int result = rateInfoMapper.addRateInfo(rateInfo);
		if (result == 0) {
			log.error(this.getClass() + ".addRateInfo() error : " + ResultCodeConstants.ERROR_MSG_ERROR_ADD);
			baseResultDTO.setSuccess(false);
			baseResultDTO.setErrorDetail(ResultCodeConstants.ERROR_MSG_ERROR_ADD);
		}
		
		return baseResultDTO;
	}
	
	@Override
	@Transactional
	public BaseResultDTO modifyRateInfo(RateInfoDO rateInfo,UserDO user) throws Exception{
		BaseResultDTO baseResultDTO = new BaseResultDTO();
		if (user == null) {
			log.error(this.getClass() + ".modifyRateInfo() error : " + ResultCodeConstants.ERROR_MSG_EMPTY_SESSION_USER);
			baseResultDTO.setSuccess(false);
			baseResultDTO.setErrorDetail(ResultCodeConstants.ERROR_MSG_EMPTY_SESSION_USER);
			return baseResultDTO;
		}
		
		Long rateInformation = rateInfo.getPk_RateInformation();
		RateInfoDO qryRateInfo = new RateInfoDO();
		qryRateInfo.setPk_RateInformation(rateInformation);
		Long qryResult = rateInfoMapper.getRateInfoCount(qryRateInfo);
		if (qryResult < 1) {
			log.error(this.getClass() + ".modifyRateInfo() error : " + ResultCodeConstants.ERROR_MSG_EMPTY_MODIFY_RATEINFO);
			baseResultDTO.setSuccess(false);
			baseResultDTO.setErrorDetail(ResultCodeConstants.ERROR_MSG_EMPTY_MODIFY_RATEINFO);
			return baseResultDTO;
		}

		rateInfo.setGmtModified(DateUtil.parse(DateUtil.getNow(DateUtil.TYPE_COM_YMDHMS)));

		//如果不是新账户，需要将尖、峰、平、谷的服务费置成0；如果是新账户，服务费置成0
		if (rateInfo.getRaIn_type() != 3) {
			rateInfo.setRaIn_TipTimeTariffMoney(BigDecimal.ZERO);
			rateInfo.setRaIn_PeakElectricityMoney(BigDecimal.ZERO);	
			rateInfo.setRaIn_UsualMoney(BigDecimal.ZERO);
			rateInfo.setRaIn_ValleyTimeMoney(BigDecimal.ZERO);
		}else{
			rateInfo.setRaIn_ServiceCharge(BigDecimal.ZERO);
		}
		int result = rateInfoMapper.modifyRateInfo(rateInfo);
		
		if (result > 0) {
			ElectricPileDO qryElectricPile = new ElectricPileDO();
			qryElectricPile.setRateInformationId(rateInformation);
			List<ElectricPileDO> list = electricPileMapper.selectElectricPileList(qryElectricPile);
			//将桩体编号拼成要发送的格式
			String sendStr = "";
			for(ElectricPileDO electricPileDO : list){
				sendStr += electricPileDO.getCode() + ",";
			}
			if(sendStr.length() > 0){
				sendStr = sendStr.substring(0, sendStr.length() - 1);
				sendStr += ":" + rateInformation;
				String apiBaseUrl = GlobalSystem.getConfig("apiRoot");
				String token = ApiUtil.getToken();
				//调用接口更新费率
				HttpsUtil.getResponseParam(apiBaseUrl + "/app/net/sendRate.do?paramStrs=" + sendStr + "&t=" + token, "status");
//				insertLogCommit("费率更新命令下发，主键：["+ rateInformation + "]",user);
			}
		}
		
		return baseResultDTO;
	}
	
	/**
	 * 加入日志列表
	 * 
	 * @param logContent
	 *            操作内容
	 * @return
	 */
	public void insertLogCommit(String logContent,UserDO loginUser) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		// 请求的IP
		String ip = request.getRemoteAddr();
		LogCommitDO logCommit = new LogCommitDO();
		logCommit.setLogName(ObjectUtil.nvlStrEmpty(loginUser.getUserAccount()));
		logCommit.setLogIpAddress(ip);
		logCommit.setLogContent(logContent);
		logCommit.setIsDel(0);
		logCommit.setCreatorId(loginUser.getUserId());
		logCommit.setGmtCreate(new Date());
		logCommit.setGmtModified(new Date());
		logCommitMapper.insertLogCommit(logCommit);
//		addHbaseLog(logCommit);
	}
	
	/**
	 * 添加日志到hbase
	 * @param logCommitDO
	 */
	private void addHbaseLog(LogCommitDO logCommitDO) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("logName", logCommitDO.getLogName());
		params.put("logIpAddress", logCommitDO.getLogIpAddress());
		params.put("logContent", logCommitDO.getLogContent());
		params.put("creatorId", String.valueOf(logCommitDO.getCreatorId()));
		HttpRequestUtil.post(GlobalSystem.getConfig("hbaseUrl")+"/addLogCommit", params);
	}
	
	@Override
	@Transactional
	public BaseResultDTO removeRateInfo(RateInfoDO rateInfo) throws Exception{
		BaseResultDTO baseResultDTO = new BaseResultDTO();
		
		if(rateInfo == null || rateInfo.getRateInfoIds() == null || rateInfo.getRateInfoIds().length() == 0){
			log.error(this.getClass() + ".removeRateInfo() error : 删除失败，费率不允许为空！");
			baseResultDTO.setSuccess(false);
			baseResultDTO.setErrorDetail("删除失败，费率不允许为空！");
			return baseResultDTO;
		}

		ElectricPileDO qryElectricPile;
		StringBuffer msgRateInfoId = new StringBuffer();
		
		String rateInfoIds = rateInfo.getRateInfoIds();
		String[] rateInfoId = rateInfoIds.split(",");
		for (String id : rateInfoId) {			
			qryElectricPile = new ElectricPileDO();
			qryElectricPile.setRateInformationId(Long.parseLong(id));
			List<ElectricPileDO> list = electricPileMapper.selectElectricPileList(qryElectricPile);
			if (list != null && list.size() > 0) {
				msgRateInfoId.append(id).append(",");
			}else{
				rateInfoMapper.removeRateInfo(Integer.parseInt(id));
			}
		}
		
		if (msgRateInfoId.length() > 0) {		
			log.error(this.getClass() + ".removeRateInfo() error : " + msgRateInfoId.toString() + ResultCodeConstants.ERROR_MSG_ROMOVE_RELATION_RATEINFO);
			baseResultDTO.setSuccess(false);
			baseResultDTO.setErrorDetail(msgRateInfoId.toString() + ResultCodeConstants.ERROR_MSG_ROMOVE_RELATION_RATEINFO);
		}
		
		return baseResultDTO;
	}

	@Override
	public RateInfoDO getRateInfoById(RateInfoDO rateInfo) {
		return rateInfoMapper.getRateInfoById(rateInfo);
	}
	
}
