package com.sonic.website.app.wx_gzh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sonic.website.core.common.support.DataResult;
import com.sonic.website.core.common.support.HttpUtil;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.spring.interfaces.AccessSupport;


/** 微信公众号 */
@RestController
@Order(value = 5)
public class GzhController implements AccessSupport {
	private static final String SNSAPI_USERINFO = "snsapi_userinfo";
	private static final String SNSAPI_BASE = "snsapi_base";

	private String WECHAT_SIGNON_CALL_BACK_URL ="http://service.sincetimes.com/signon";

	/** 微信相关 *****/
	/**
	 * https://open.weixin.qq.com/connect/oauth2/authorize?appid=
	 * wx520c15f417810387&redirect_uri=https%3A%2F%2Fchong.qq.com%2Fphp%2Findex.
	 * php%3Fd%3D%26c%3DwxAdapter%26m%3DmobileDeal%26showwxpaytitle%3D1%
	 * 26vb2ctag%3D4_2030_5_1194_60&response_type=code&scope=snsapi_base&state=
	 * 123#wechat_redirect
	 * @return
	 * 所有在微信访问此接口的都将对调给传过来的地址并自动带上openid参数
	 */
	@RequestMapping("/signon")
	public void sq(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// resp.setHeader("refresh", "10;url=http://www.baidu.com");
		String code = req.getParameter("code");// 如果无此参数，但是有state参数说明用户没有授权
		String back_url = req.getParameter("state");// 提前放在腾讯回调的参数,放的是发朋友圈者的openid
		LogCore.BASE.debug("code={},state={},code is null?{},back_url is null?{}", code, back_url, null == code,
				null == back_url);

		/** code非空说明是授权传腾讯后过来的 */
		if (Util.nonNull(code)) {
			String[] tokenOpenid = WechatManager.inst().getAccessTokenAndOpenid(GZHConsts.DEFAULT_APP_ID, GZHConsts.DEFAULT_APP_SECRET, code);
			String accTokn = tokenOpenid[0];
			String openidSq = tokenOpenid[1];// 授权后的自己的openid
			// 通过缓存实现减少授权请求
			String[] infos = GzhManager.inst().getNickNamePic(openidSq);
			if (Util.isEmpty(infos)) {// 缓存中没有
				/*String refresh_token = tokenOpenid[2];*/
				String[] accProfile = WechatManager.inst().getAccountProfile(accTokn, openidSq);
				// 授权失败的情况[null, null, {"errcode":48001,"errmsg":"api unauthorized, hints: [ req_id: EMvfbA0407ns86 ]"}]
				String profileSq = accProfile[2];// 全部信息的json字符串
				if (profileSq.contains("errcode")) { // 静默授权获取用户信息失败
					shouquan(resp, SNSAPI_USERINFO, back_url);// 强制授权
					LogCore.BASE.info("force the auth start!{}", openidSq);
					return;
				}
				GzhManager.inst().setNickNameAndPic(openidSq, accProfile);
				LogCore.BASE.debug("accessToken, openid and freshToken{}", Arrays.toString(tokenOpenid));
				LogCore.BASE.debug("accountFrofile{}", Arrays.toString(accProfile));
			}else{
				LogCore.BASE.info("hit the cache success!openid={},nickname={}", openidSq, infos[0]);
			}
			String new_url = null;
			if(back_url.contains("?")){
				new_url = Util.format(back_url + "&openid={}", openidSq);
			}else{
				new_url = Util.format(back_url + "?openid={}", openidSq);
			}
			LogCore.BASE.info("{}  -> {}",back_url, new_url);
			resp.sendRedirect(new_url);
			return;
		} 
		/*以下说明是客户端请求的授权 */
		String _back_url = req.getParameter("back_url");// 带此参数说明是客户端请求的，
		shouquan(resp, SNSAPI_BASE, _back_url);// openid可能为null,即用户点进空白页面后授权
	}
	/**
	 * 这一步也可以放在客户端
	 * @param resp
	 * @param scope_
	 * @param stateP
	 */
	private void shouquan(HttpServletResponse resp, String scope_, String stateP) {
		String redirect_uri = HttpUtil.urlEncode(WECHAT_SIGNON_CALL_BACK_URL);
		String response_type = "code";
		// String scope_ = "snsapi_userinfo";
		String path = null;
		String url = null;
		if (Util.isEmpty(stateP) || "null".equals(stateP)) {// 防止客户端获取空的openid后请求授权
			path = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={}&redirect_uri={}&response_type={}&scope={}#wechat_redirect";
			url = Util.format(path, GZHConsts.DEFAULT_APP_ID, redirect_uri, response_type, scope_);
		} else {
			path = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={}&redirect_uri={}&response_type={}&scope={}&state={}#wechat_redirect";
			url = Util.format(path, GZHConsts.DEFAULT_APP_ID, redirect_uri, response_type, scope_, stateP);
		}
		LogCore.USER.debug("<<<<<<<<<<<<<<<<<<<<<<<<shouquan,scope:{}stateP:{}stateP is null:{}>>>>>>>>>>>>>>>>>{}",
				scope_, stateP, (null == stateP), url);
		try {
			resp.sendRedirect(url);
		} catch (IOException e) {
			LogCore.BASE.error("shouquan,scope={},", scope_, e);
		}
	}
	/**
	 * 获取某个openid的头像和昵称信息
	 */
	@RequestMapping("/get_profile_infos")
	public Object get_gzh_infos(String openid) {
		String [] nic_pic = GzhManager.inst().getNickNamePicNotNull(openid);
		ArrayList<String> list = new ArrayList<>();
		list.addAll(Arrays.asList(nic_pic));
		return new DataResult(1, nic_pic);
	}
}
