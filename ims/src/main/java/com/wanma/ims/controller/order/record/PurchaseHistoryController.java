package com.wanma.ims.controller.order.record;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wanma.ims.common.domain.PurchaseHistoryDO;
import com.wanma.ims.common.domain.UserDO;
import com.wanma.ims.common.domain.base.Pager;
import com.wanma.ims.common.domain.bo.UserAccountBO;
import com.wanma.ims.common.dto.BaseResultDTO;
import com.wanma.ims.controller.BaseController;
import com.wanma.ims.controller.result.JsonResult;
import com.wanma.ims.service.PurchaseHistoryService;
import com.wanma.ims.util.ErrorHtmlUtil;

/**
 * 消费记录
 */
@RequestMapping("/manage/order/record")
@Controller
public class PurchaseHistoryController extends BaseController{
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PurchaseHistoryService purchaseHistoryService;
	
	/**
	* <p>Description: 获取消费记录列表</p>
	* @param
	* @author bingo
	* @date 2017-7-31
	 */
	@RequestMapping(value = "/getPurchaseHistory")
	@ResponseBody
	public void getPurchaseHistoryList(@ModelAttribute("pager") Pager pager,
			@ModelAttribute PurchaseHistoryDO purchaseHistory, Model model,HttpServletRequest request){
		JsonResult batchResult = new JsonResult();
		Long total = purchaseHistoryService.getPurchaseHistoryCount(purchaseHistory);
		if (total <= pager.getOffset()) {
			pager.setPageNo(1L);
		}
		pager.setTotal(total);
		purchaseHistory.setPager(pager);
		
		List<PurchaseHistoryDO> purchaseHistoryList = purchaseHistoryService.getPurchaseHistoryList(purchaseHistory);
		if (purchaseHistoryList == null) {
			purchaseHistoryList = new ArrayList<PurchaseHistoryDO>();
		}
		
		batchResult.setDataObject(purchaseHistoryList);
		batchResult.setPager(pager);
		
		responseJson(batchResult);
	}
	
	/**
	* <p>Description: 导出消费记录信息</p>
	* @param
	* @author bingo
	* @date 2017-7-31
	 */
    @RequestMapping(value = "/exportPurchaseHistory", method = RequestMethod.GET)
    @ResponseBody
    public void exportPurchaseHistory(HttpServletResponse response, PurchaseHistoryDO purchaseHistory) {
        UserDO loginUser = getCurrentLoginUser();
        try {
            BaseResultDTO resultDTO = purchaseHistoryService.exportPurchaseHistory(response, purchaseHistory, loginUser);
            if (resultDTO.isFailed()) {
                ErrorHtmlUtil.createErrorPage(response, resultDTO.getErrorDetail());
                return;
            }
        } catch (Exception e) {
        	log.error(this.getClass()+".exportPurchaseHistory() error:",e);
            ErrorHtmlUtil.createErrorPage(response, "导出消费记录信息失败！");
        }
    }
    
    /**
	* <p>Description: 用户首页中账单</p>
	* @param
	* @author bingo
	* @date 2017-7-31
	 */
	@RequestMapping(value = "/getUserAccount")
	@ResponseBody	
	public void getUserAccount(String userId, Model model,HttpServletRequest request) throws Exception{
		JsonResult batchResult = new JsonResult();
		
		UserAccountBO UserAccount = purchaseHistoryService.getUserAccount(userId);
		if (UserAccount == null) {
			UserAccount = new UserAccountBO();
		}
		batchResult.setDataObject(UserAccount);
		
		responseJson(batchResult);
	}
	
	/**
	* <p>Description: 卡首页中账单</p>
	* @param
	* @author bingo
	* @date 2017-08-01
	 */
	@RequestMapping(value = "/getCardAccount")
	@ResponseBody	
	public void getCardAccount(String cardId, Model model,HttpServletRequest request) throws Exception{
		JsonResult batchResult = new JsonResult();
		
		UserAccountBO UserAccount = purchaseHistoryService.getCardAccount(cardId);
		if (UserAccount == null) {
			UserAccount = new UserAccountBO();
		}
		batchResult.setDataObject(UserAccount);
		
		responseJson(batchResult);
	}
	
}
