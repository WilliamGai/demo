package com.sonic.website.app.wx_gzh;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.sonic.website.core.common.support.HttpUtil;
import com.sonic.website.core.common.support.LogCore;

public class GzhTest {
	@Test
	public void test(){
		String accessUrl = "https://api.weixin.qq.com/cgi-bin/token";
		Map<String,String> params =  new HashMap<String, String>();
		params.put("grant_type", "client_credential");
		params.put("appid", "wx9ae18f115734dc5f");
		params.put("secret", "db5b0cb86f1ff0ec6e1e0e5b5b190f20");
		String content = HttpUtil.get(accessUrl, params);
		JSONObject userJson = JSONObject.parseObject(content);
		String hz_wx_token =  userJson.getString("access_token");
		LogCore.BASE.info("hz_wx_token={}", hz_wx_token);
	}
}
