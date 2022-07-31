package com.sonic.website.app.top_up;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sonic.website.core.common.support.HttpUtil;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.MD5;
import com.sonic.website.core.spring.HttpHeadUtil;
import com.sonic.website.core.spring.interfaces.AccessSupport;



/** 低配版第三方手机充值 */
@RestController
@Order(value = 5)
public class TopUpController implements AccessSupport {

	/**
	 * 畅天游接口的回调
	 */
	@RequestMapping("/cty_pay_back")
	String cty_pay_back(HttpServletRequest req) {
		LogCore.BASE.info("cty_pay_back=====", HttpHeadUtil.getParamsMapLimit(req));
		return "1";
	}

	@RequestMapping("    opup_back_test")
	String topup_back_test(HttpServletRequest req) {
		LogCore.BASE.info("topup_back_test=====", HttpHeadUtil.getParamsMapLimit(req));
		return "1";
	}

	/**
	 * 玩家充值
	 * 需要传入参数 mobile 手机号码，充值openid
	 */
	@RequestMapping("/cty_topup")
	public String topup(String mobile, String amount, String sign, String openid) {
		String sign0 = new StringBuilder(MD5.md5(mobile.concat(openid))).reverse().toString();
		if (!sign0.equals(sign)) {
			LogCore.USER.info("test{},{},need sign={},getsign ={}sign fail", mobile, amount, sign0, sign);
			return "err";
		}
		String url = TopUpConsts.CTY_PAY_URL;
		String CompanyID = TopUpConsts.CTY_CAMPANY_ID;
		String InterfacePwd = TopUpConsts.CTY_INTERFACE_PSW;
		String Mobile = mobile;
		String orderID = generateOderId();
		String RequestKey = TopUpConsts.CTY_SUBMIT_KEY;
		String Key = MD5.md5(CompanyID + InterfacePwd + Mobile + amount + orderID + RequestKey);
		Map<String, String> params = new HashMap<String, String>();
		params.put("CompanyID", CompanyID);
		params.put("InterfacePwd", InterfacePwd);
		params.put("Mobile", Mobile);
		params.put("Amount", amount);
		params.put("OrderID", orderID);
		params.put("Key", Key);
		String content = HttpUtil.get(url, params);
		if (content.contains("0000")) {
			LogCore.USER.info("{},{},{},success", mobile, amount, openid);
			return content;
		}
		LogCore.USER.info("{},{},{},fail", mobile, amount, openid);
		return content;
	}

	@RequestMapping("    opup_test")
	public String topuptest(String mobile, String amount, String openid) {
		String url = TopUpConsts.CTY_PAY_URL_TEST;
		String CompanyID = TopUpConsts.CTY_CAMPANY_ID_TEST;
		String InterfacePwd = TopUpConsts.CTY_INTERFACE_PSW_TEST;
		String Mobile = mobile;
		String orderID = generateOderId();
		String RequestKey = TopUpConsts.CTY_SUBMIT_KEY_TEST;
		String Key = MD5.md5(CompanyID + InterfacePwd + Mobile + amount + orderID + RequestKey);
		Map<String, String> params = new HashMap<String, String>();
		params.put("CompanyID", CompanyID);
		params.put("InterfacePwd", InterfacePwd);
		params.put("Mobile", Mobile);
		params.put("Amount", amount);
		params.put("OrderID", orderID);
		params.put("Key", Key);
		String content = HttpUtil.get(url, params);
		if (content.contains("0000")) {
			LogCore.USER.info("test{},{},{},success", mobile, amount, openid);
			return content;
		}
		LogCore.USER.info("test{},{},{},fail", mobile, amount, openid);
		return content;
	}

	private String generateOderId() {
		return UUID.randomUUID().toString();
	}
}
