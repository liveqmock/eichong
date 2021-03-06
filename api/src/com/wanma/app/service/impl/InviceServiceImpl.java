package com.wanma.app.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanma.app.dao.InvoiceMapper;
import com.wanma.app.dao.TblPurchasehistoryMapper;
import com.wanma.app.service.InvoiceService;
import com.wanma.model.TblInvoice;

@Service
public class InviceServiceImpl implements InvoiceService{
	@Autowired
	InvoiceMapper invoiceMapper;
	@Autowired
	TblPurchasehistoryMapper  tblPurchasehistoryMapper;
	
	public List<HashMap<String, Object>> enableInvoiceList(HashMap<String, Object> params){
		List<HashMap<String, Object>> qryMap = invoiceMapper.enableInvoiceList(params);
		for (HashMap<String, Object> map : qryMap) {
			if (map.get("pCouponMoney") != null) {
				map.put("pMoney", (Double.parseDouble(map.get("pMoney").toString()) - Double.parseDouble(map.get("pCouponMoney").toString())));
			}
		}
		
		return qryMap;
	}
	
	public List<HashMap<String, Object>> invoicedList(HashMap<String, Object> params){
		return invoiceMapper.invoicedList(params);
	}
	
	public HashMap<String, Object> invoiceDetail(long iId){
		return invoiceMapper.invoiceDetail(iId);
	}
	
	/**
	 * 添加用户查看开票流程记录
	 * @param uId 用户id
	 * @return
	 */
	public void addInvoiceCheck(int uId){
		invoiceMapper.addInvoiceCheck(uId);
	}
	
	/**
	 * 检查用户是否看过开票流程
	 * @param uId 用户id
	 * @return
	 */
	public HashMap<String, Integer> getInvoiceCheck(int uId){
		return invoiceMapper.getInvoiceCheck(uId);
	}
	
	/**
	 * 获取发票配置信息
	 * @param configType 配置类型:30发票内容31邮费金额
	 * @return
	 */
	public List<HashMap<String, Object>> invoiceConfig(HashMap<String, Object> params){
		return invoiceMapper.invoiceConfig(params);
	}
	/**
	 * 开发票
	 * @param 
	 * @return
	 */
	public Long saveInvoice(TblInvoice invoice){
		invoiceMapper.saveInvoice(invoice);
		long pkInvoice = invoice.getPkInvoice();
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("pkInvoice", pkInvoice);
		map.put("ivRecords", invoice.getIvRecords());
		tblPurchasehistoryMapper.updateInvoice(map);
		return pkInvoice;
	}
	
	/**
	 * 更新发票邮费支付方式
	 * @param payMode
	 */
	public void updatePayMode(int ivPayFreight,long ivId,BigDecimal ivFreightAmount,int ivStatus){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ivPayFreight", ivPayFreight);
		map.put("ivId", ivId);
		map.put("ivFreightAmount", ivFreightAmount);
		map.put("ivStatus", ivStatus);
		invoiceMapper.updatePayMode(map);
	}
	
}
