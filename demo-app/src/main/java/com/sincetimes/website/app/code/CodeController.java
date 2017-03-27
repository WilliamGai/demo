package com.sincetimes.website.app.code;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.sincetimes.website.core.common.support.JSONBuilder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Param;
import com.sincetimes.website.core.spring.controller.ControllerInterface;
/**
 * 激活码
 */
@RestController
@Order(value = 5)
public class CodeController implements ControllerInterface{
	
	//领取标志,激活码,0被抢光,1第一次领取，2已经领取,-1时间没到,-2时间过了,-3这个激活码地址不存在
	@RequestMapping("/get_a_code")
	String  code(@RequestParam Optional<String> sn, @RequestParam Optional<String> who, HttpServletRequest req, HttpServletResponse resp){
		if(!sn.isPresent() ||!who.isPresent()){
			return JSONBuilder.creatJsonString("result", -3);
		}
		Object[] rst = CodeManager.inst().getOneCode(sn.get(), who.get());
		String result = JSONBuilder.creatJsonString("result", rst[0], "code", rst[1]);
		LogCore.BASE.info("get a code,sn={}, who={}, {}", sn, who, result);
		return result;
	}
	@RequestMapping("/if_code_fit")
	public Object get_code_available(@RequestParam Optional<String> sn) {
		if(sn.isPresent()){
			Param rst = CodeManager.inst().get_code_available(sn.get());
			return new JSONObject(rst.asMap());
		}
		return CodeManager.inst().getAllCodeInfo();
	}
}
