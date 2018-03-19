package com.wanma.ims.common.domain.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wanma.ims.common.domain.IntegralRulesExtensionsDO;


/**
 * 积分活动和规则临时对象
 * @author bingo
 * @date 2017-08-23
 */
public class IntegralActivityAndRulesBO implements Serializable{
	private static final long serialVersionUID = -3269280109099277370L;

	private Long pkId;//积分活动ID
	//活动名称（0：充值送；1：消费送；2：每日领取；3：资料修改；4：分享；5：积分兑换；6：积分抽奖）
	private String activityName;
	private Integer direction;//积分方向（0：获取；1：消耗）
	private Long maxIntegrals;//活动上限积分
	private Long launchIntegrals;//活动送出积分
	private Long residuesIntegrals;//活动剩余积分
	private Long integralRulesId;//积分活动规则ID
	private Integer highestPriority;//最高优先级（0：否；1：是）
	private Integer activityStatus;//活动状态（0：开启；1：关闭）
	private Long fixedIntegralValue;//固定积分值
	private Long ratioIntegralValue;//比例积分值
	private Date startDate;//活动开始日期
	private Date endDate;//活动结束日期
	private Integer effectiveTimes;//有效次数（1：第一次时赠送积分；2：每次都赠送）
	private Integer isWhole;//是否全国有效（0：否；1：是）
	private String provinceId;//省ID
	private String cityId;//市ID
	private Long powerStationId;//电站ID
	private Long minValue;//充电消费满足最小金额，才开始赠送、可以积分抵扣
	private Integer isChoice;//按照充值/消费金额赠送抽奖机会（0：启用；1：未启用）
	private Long choiceMoney;//每消费多少金额赠送一次抽奖机会
	private Integer isShareChoice;//充电消费分享赠送积分、抽奖机会（0：不赠送；1：首次分享； 2：每次分享）
	private Long shareIntegralValue;//分享成功后赠送积分值
	private Integer shareChoice;//分享成功后赠送一次抽奖机会（0：是；1：否）
	private Long limitIntegral;//消费赠送的上限积分
	private String contents;//积分规则内容
	private Long electricPileId;//电桩ID
	private Integer couponVarietyId;//优惠券品种Id
	private Long integralValue;//消费的积分值
	private String directionName;//积分方向名称（0：获取；1：消耗）
	private String creator;//创建人
	private String modifier;//修改人
	private Date gmtCreate;//创建时间
	private Date gmtModified;//修改时间
	private Map<String, Object> map;
	private List<IntegralRulesExtensionsDO> extList;
	private String electricPileIds;//桩Id集合
	private String addressName;//地区全称
	private String strStartDate;
	private String strEndDate;
	private String userAccount;// 用户帐号

	public Long getPkId() {
		return pkId;
	}
	public void setPkId(Long pkId) {
		this.pkId = pkId;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public Integer getDirection() {
		return direction;
	}
	public void setDirection(Integer direction) {
		this.direction = direction;
	}
	public Integer getActivityStatus() {
		return activityStatus;
	}
	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}
	public Long getMaxIntegrals() {
		return maxIntegrals;
	}
	public void setMaxIntegrals(Long maxIntegrals) {
		this.maxIntegrals = maxIntegrals;
	}
	public Long getLaunchIntegrals() {
		return launchIntegrals;
	}
	public void setLaunchIntegrals(Long launchIntegrals) {
		this.launchIntegrals = launchIntegrals;
	}
	public Long getResiduesIntegrals() {
		return residuesIntegrals;
	}
	public void setResiduesIntegrals(Long residuesIntegrals) {
		this.residuesIntegrals = residuesIntegrals;
	}
	public Long getIntegralRulesId() {
		return integralRulesId;
	}
	public void setIntegralRulesId(Long integralRulesId) {
		this.integralRulesId = integralRulesId;
	}
	public Integer getHighestPriority() {
		return highestPriority;
	}
	public void setHighestPriority(Integer highestPriority) {
		this.highestPriority = highestPriority;
	}
	public Long getFixedIntegralValue() {
		return fixedIntegralValue;
	}
	public void setFixedIntegralValue(Long fixedIntegralValue) {
		this.fixedIntegralValue = fixedIntegralValue;
	}
	public Long getRatioIntegralValue() {
		return ratioIntegralValue;
	}
	public void setRatioIntegralValue(Long ratioIntegralValue) {
		this.ratioIntegralValue = ratioIntegralValue;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Integer getEffectiveTimes() {
		return effectiveTimes;
	}
	public void setEffectiveTimes(Integer effectiveTimes) {
		this.effectiveTimes = effectiveTimes;
	}
	public Integer getIsWhole() {
		return isWhole;
	}
	public void setIsWhole(Integer isWhole) {
		this.isWhole = isWhole;
	}
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public Long getPowerStationId() {
		return powerStationId;
	}
	public void setPowerStationId(Long powerStationId) {
		this.powerStationId = powerStationId;
	}
	public Long getMinValue() {
		return minValue;
	}
	public void setMinValue(Long minValue) {
		this.minValue = minValue;
	}
	public Integer getIsChoice() {
		return isChoice;
	}
	public void setIsChoice(Integer isChoice) {
		this.isChoice = isChoice;
	}
	public Long getChoiceMoney() {
		return choiceMoney;
	}
	public void setChoiceMoney(Long choiceMoney) {
		this.choiceMoney = choiceMoney;
	}
	public Integer getIsShareChoice() {
		return isShareChoice;
	}
	public void setIsShareChoice(Integer isShareChoice) {
		this.isShareChoice = isShareChoice;
	}
	public Long getShareIntegralValue() {
		return shareIntegralValue;
	}
	public void setShareIntegralValue(Long shareIntegralValue) {
		this.shareIntegralValue = shareIntegralValue;
	}
	public Integer getShareChoice() {
		return shareChoice;
	}
	public void setShareChoice(Integer shareChoice) {
		this.shareChoice = shareChoice;
	}
	public Integer getCouponVarietyId() {
		return couponVarietyId;
	}
	public void setCouponVarietyId(Integer couponVarietyId) {
		this.couponVarietyId = couponVarietyId;
	}
	public Long getIntegralValue() {
		return integralValue;
	}
	public void setIntegralValue(Long integralValue) {
		this.integralValue = integralValue;
	}
	public Long getLimitIntegral() {
		return limitIntegral;
	}
	public void setLimitIntegral(Long limitIntegral) {
		this.limitIntegral = limitIntegral;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public Long getElectricPileId() {
		return electricPileId;
	}
	public void setElectricPileId(Long electricPileId) {
		this.electricPileId = electricPileId;
	}
	public String getDirectionName() {
		return directionName;
	}
	public void setDirectionName(String directionName) {
		this.directionName = directionName;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public List<IntegralRulesExtensionsDO> getExtList() {
		return extList;
	}

	public void setExtList(List<IntegralRulesExtensionsDO> extList) {
		this.extList = extList;
	}

	public String getElectricPileIds() {
		return electricPileIds;
	}
	public void setElectricPileIds(String electricPileIds) {
		this.electricPileIds = electricPileIds;
	}
	public String getAddressName() {
		return addressName;
	}
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}
	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
}