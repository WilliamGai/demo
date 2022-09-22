package com.sonic.website.app.wx_gzh;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.support.HttpUtil;
import com.sonic.website.core.common.support.LogCore;

public class WechatManager extends ManagerBase {
    public static final String accessUrl = "https://api.weixin.qq.com/cgi-bin/token";

    public static WechatManager inst() {
        return ManagerBase.inst(WechatManager.class);
    }

    /* step2 */
    public String[] getAccessTokenAndOpenid(String appId, String secret, String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("secret", secret);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        String content = HttpUtil.get(url, params);
        JSONObject json = JSONObject.parseObject(content);
        return new String[] { json.getString("access_token"), json.getString("openid"),
                json.getString("refresh_token") };
    }

    /* step3 */
    public String[] getAccountProfile(String accessToken, String openid) {
        String url = "https://api.weixin.qq.com/sns/userinfo";
        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("openid", openid);
        params.put("lang", "zh_CN");
        String content = HttpUtil.get(url, params);
        JSONObject json = JSONObject.parseObject(content);
        if(LogCore.BASE.isDebugEnabled()){
            String prettyStr = JSON.toJSONString(json,SerializerFeature.PrettyFormat,SerializerFeature.WriteClassName, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
            LogCore.BASE.debug("req https:api.weixin.qq.com/sns/userinfo :{}", prettyStr);
        }
        return new String[] { json.getString("nickname"), json.getString("headimgurl"), json.toString() };
    }

    @Override
    public void init() {
        
    }
}
