package com.sincetimes.website.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.app.security.SecurityManager;
import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.app.stats.StatsManager;
import com.sincetimes.website.core.common.support.ClassTool;
import com.sincetimes.website.core.common.support.DataVO;
import com.sincetimes.website.core.common.support.JSONBuilder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Result;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.interfaces.AccessSupport;
import com.sincetimes.website.manager.DataManager;
/**
 * 参考
 */
@Controller
@Order(value = 6)
@RequestMapping("/mg")
public class MGControllerExcample implements AccessSupport {

	/**
	 * 修改变量
	 */
	@RequestMapping("/mg")
	String gm(Model model, HttpServletRequest req) {
		Map<String, Object> map = ClassTool.getFields(DataManager.inst(), x -> x.getName().startsWith("VALUE"));
		List<DataVO> list = new ArrayList<>();
		map.values().forEach(x -> list.add(new DataVO(x + "", DataManager.inst().get(x + ""))));
		list.sort((x,y) -> x.name.compareTo(y.name));
		LogCore.BASE.debug("all constants :{}", list);
		
		List<String[]> bilist = DataManager.inst().getAllBiValues();
		model.addAttribute("constant_list", list);
		model.addAttribute("bilist", bilist);
		Object _old_user = req.getSession().getAttribute("user");
		if(_old_user instanceof UserVO && "admin".equals(((UserVO)_old_user).getName())){
			Collection<UserVO> user_list = SecurityManager.inst().getAllUsers();
			model.addAttribute("user_list", user_list);
		}
		return "mg";
	}

	@RequestMapping("/changevalue")
	@ResponseBody
	Object changevalue(String k, String v) {
		LogCore.BASE.info("change value, k={},v={}", k, v);
		if (Util.nonEmpty(k, v)) {
			DataManager.inst().set(k, v);
		}
		return JSONBuilder.creatJsonString("name", k, "newvalue", DataManager.inst().get(k));
	}

	@RequestMapping("/change_bivalue")
	@ResponseBody
	Object changevalue(String k, String n, String v) {
		LogCore.BASE.info("change value, k={}, n={}, v={}", k, n, v);
		if (Util.nonEmpty(k, n, v)) {
			DataManager.inst().setBiValue(k, n, v);
		}
		return JSONBuilder.creatJsonString("key", k, "bivalue", DataManager.inst().getBiValue(k));
	}

	@RequestMapping("/delete_bi")
	@ResponseBody
	Object delete_bi(String k) {
		LogCore.BASE.info("del, k={}", k);
		if (Util.nonEmpty(k)) {
			DataManager.inst().delBiValue(k);
		}
		return new Result().success();
	}

	public void setListSizesModel(Model model) {
		List<DataVO> size_list = StatsManager.inst().getListSizes();
		model.addAttribute("size_list", size_list);
	}
}
