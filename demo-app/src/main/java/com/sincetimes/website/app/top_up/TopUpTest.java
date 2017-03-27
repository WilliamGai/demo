package com.sincetimes.website.app.top_up;

import java.util.HashMap;
import java.util.Map;

import com.sincetimes.website.core.common.support.HttpUtil;
import com.sincetimes.website.core.common.support.MD5;


public class TopUpTest {
	//13522889046  13811279857 13811279857  
//	@org.junit.Test
	public void Test(){
		String url = "http://jztpy.sincetimes.com/cty_topup";
		String mobile = "18640217026";
		String openid="test";
		String sign = new StringBuilder(MD5.md5(mobile.concat(openid))).reverse().toString();
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("amount", "5000");
        map.put("openid", openid);
        map.put("sign", sign);
		String rst = HttpUtil.post(url, map);
		System.out.println(rst);
	}
}
