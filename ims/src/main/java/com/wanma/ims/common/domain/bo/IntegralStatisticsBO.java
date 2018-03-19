package com.wanma.ims.common.domain.bo;

import com.wanma.ims.common.domain.base.BasicModel;


/**
 * 积分统计表
 * @author bingo
 * @date 2017-08-17
 */
public class IntegralStatisticsBO extends BasicModel{

	private static final long serialVersionUID = -1898577228719285970L;
	
	/** 总积分   */
	private Long totalIntegrals;
	
	/** 可用积分 */
	private Long availableIntegrals;
	
	/** 已用积分  */
	private Long usedIntegrals;

	/** 充值积分  */
	private Long czjf;
	
	/** 充值人数  */
	private Long czrs;
	
	/** 充值次数  */
	private Long czcs;
	
	/** 消费积分  */
	private Long xfjf;
	
	/** 消费人数   */
	private Long xfrs;
	
	/** 消费次数  */
	private Long xfcs;
	
	/** 每日领取积分  */
	private Long lqjf;
	
	/** 每日领取人数  */
	private Long lqrs;
	
	/** 每日领取次数  */
	private Long lqcs;
	
	/** 修改资料积分  */
	private Long xgjljf;
	
	/** 修改资料人数  */
	private Long xgjlrs;
	
	/** 分享积分  */
	private Long fxjf;
	
	/** 分享人数  */
	private Long fxrs;
	
	/** 积分兑换积分  */
	private Long jfdhjf;
	
	/** 积分兑换人数  */
	private Long jfdhrs;
	
	/** 积分兑换次数  */
	private Long jfdhcs;
	
	/** 积分抽奖积分  */
	private Long jfcjjf;
	
	/** 积分抽奖人数  */
	private Long jfcjrs;
	
	/** 积分抽奖次数  */
	private Long jfcjcs;
	
	/** 节假日积分  */
	private Long jjrjf;
	
	/** 节假日人数  */
	private Long jjrrs;
	
	/** 节假日次数  */
	private Long jjrcs;
	
	/** 生日积分  */
	private Long srjf;
	
	/** 生日人数  */
	private Long srrs;
	
	/** 生日次数  */
	private Long srcs;
	

	public Long getTotalIntegrals() {
		return totalIntegrals;
	}

	public void setTotalIntegrals(Long totalIntegrals) {
		this.totalIntegrals = totalIntegrals;
	}

	public Long getAvailableIntegrals() {
		return availableIntegrals;
	}

	public void setAvailableIntegrals(Long availableIntegrals) {
		this.availableIntegrals = availableIntegrals;
	}

	public Long getUsedIntegrals() {
		return usedIntegrals;
	}

	public void setUsedIntegrals(Long usedIntegrals) {
		this.usedIntegrals = usedIntegrals;
	}

	public Long getCzjf() {
		return czjf;
	}

	public void setCzjf(Long czjf) {
		this.czjf = czjf;
	}

	public Long getCzrs() {
		return czrs;
	}

	public void setCzrs(Long czrs) {
		this.czrs = czrs;
	}

	public Long getCzcs() {
		return czcs;
	}

	public void setCzcs(Long czcs) {
		this.czcs = czcs;
	}

	public Long getXfjf() {
		return xfjf;
	}

	public void setXfjf(Long xfjf) {
		this.xfjf = xfjf;
	}

	public Long getXfrs() {
		return xfrs;
	}

	public void setXfrs(Long xfrs) {
		this.xfrs = xfrs;
	}

	public Long getXfcs() {
		return xfcs;
	}

	public void setXfcs(Long xfcs) {
		this.xfcs = xfcs;
	}

	public Long getLqjf() {
		return lqjf;
	}

	public void setLqjf(Long lqjf) {
		this.lqjf = lqjf;
	}

	public Long getLqrs() {
		return lqrs;
	}

	public void setLqrs(Long lqrs) {
		this.lqrs = lqrs;
	}

	public Long getLqcs() {
		return lqcs;
	}

	public void setLqcs(Long lqcs) {
		this.lqcs = lqcs;
	}

	public Long getXgjljf() {
		return xgjljf;
	}

	public void setXgjljf(Long xgjljf) {
		this.xgjljf = xgjljf;
	}

	public Long getXgjlrs() {
		return xgjlrs;
	}

	public void setXgjlrs(Long xgjlrs) {
		this.xgjlrs = xgjlrs;
	}

	public Long getFxjf() {
		return fxjf;
	}

	public void setFxjf(Long fxjf) {
		this.fxjf = fxjf;
	}

	public Long getFxrs() {
		return fxrs;
	}

	public void setFxrs(Long fxrs) {
		this.fxrs = fxrs;
	}

	public Long getJfdhjf() {
		return jfdhjf;
	}

	public void setJfdhjf(Long jfdhjf) {
		this.jfdhjf = jfdhjf;
	}

	public Long getJfdhrs() {
		return jfdhrs;
	}

	public void setJfdhrs(Long jfdhrs) {
		this.jfdhrs = jfdhrs;
	}

	public Long getJfdhcs() {
		return jfdhcs;
	}

	public void setJfdhcs(Long jfdhcs) {
		this.jfdhcs = jfdhcs;
	}

	public Long getJfcjjf() {
		return jfcjjf;
	}

	public void setJfcjjf(Long jfcjjf) {
		this.jfcjjf = jfcjjf;
	}

	public Long getJfcjrs() {
		return jfcjrs;
	}

	public void setJfcjrs(Long jfcjrs) {
		this.jfcjrs = jfcjrs;
	}

	public Long getJfcjcs() {
		return jfcjcs;
	}

	public void setJfcjcs(Long jfcjcs) {
		this.jfcjcs = jfcjcs;
	}

	public Long getJjrjf() {
		return jjrjf;
	}

	public void setJjrjf(Long jjrjf) {
		this.jjrjf = jjrjf;
	}

	public Long getJjrrs() {
		return jjrrs;
	}

	public void setJjrrs(Long jjrrs) {
		this.jjrrs = jjrrs;
	}

	public Long getJjrcs() {
		return jjrcs;
	}

	public void setJjrcs(Long jjrcs) {
		this.jjrcs = jjrcs;
	}

	public Long getSrjf() {
		return srjf;
	}

	public void setSrjf(Long srjf) {
		this.srjf = srjf;
	}

	public Long getSrrs() {
		return srrs;
	}

	public void setSrrs(Long srrs) {
		this.srrs = srrs;
	}

	public Long getSrcs() {
		return srcs;
	}

	public void setSrcs(Long srcs) {
		this.srcs = srcs;
	}
}