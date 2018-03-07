package com.sonic.website.app.code;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sonic.website.app.file.FileManager;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.spring.interfaces.AccessSupport;
import com.sonic.website.redis.jedis.JedisPoolTemplate;
/**
 * 微信礼包激活码快捷生成<br>
 * 这个功能比较特殊,微信激活码的页面只有一张图和一个激活码<br>
 * 为了减少交互,决定只返回一次页面视图。包含激活码,不再请求图片地址也不再请求激活码。
 */
@Controller
public class CodeMGWXController implements AccessSupport{
	@Autowired
	public JedisPoolTemplate jedisTemplate;
	
	@RequestMapping("/jihuoma_wx")
	String  _jihuoma_wx(@RequestParam Optional<String> sn, Model model, HttpServletResponse resp){
		if(sn.isPresent()){
			String titlekey = sn.get()+"titl_url";
			String imgkey = sn.get()+"img_url";
			String img_url = FileManager.inst().get(imgkey);
			String title = FileManager.inst().get(titlekey);
			if(!Util.isEmpty(img_url)){
				model.addAttribute("img_url", "data:image/jpeg;base64,"+img_url);
			}
			if(!Util.isEmpty(title)){
				model.addAttribute("title", title);
			}
		}
		return "_jihuoma_wx";
	}
	/**
	 * 微信激活码页面生成
	 */
	@RequestMapping("/mg/code/add_wx_jhm_img")
	void  edit_code_cf＿wx(
			@RequestParam Optional<String> code_sn, 
			@RequestParam Optional<String> title, 
			StandardMultipartHttpServletRequest req,
			HttpServletResponse resp){
		if(!code_sn.isPresent()){
			LogCore.BASE.info("empty param: code_sn");
			return;
		}
		byte[] data = FileManager.inst().excuteFile(FileManager.inst()::readFileBytes, req);
		if(!Util.isEmpty(data)){
			String base64Str = new String(Base64.getEncoder().encodeToString(data));
			String imgkey = code_sn.get()+"img_url";
			FileManager.inst().set(imgkey, base64Str);
			LogCore.BASE.info("imgkey={},value={},,data.size={}", imgkey, base64Str,  data.length);

		}
		if(title.isPresent()){
			String titlekey = code_sn.get()+"titl_url";
			FileManager.inst().set(titlekey, title.get());
			LogCore.BASE.info("titlekey={},value={}", titlekey, title.get());
		}
		redirect(resp, "code?sn="+code_sn.get());
	}
}
