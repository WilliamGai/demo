package com.sincetimes.website.app.page;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sincetimes.website.core.common.support.JSONBuilder;
import com.sincetimes.website.core.spring.interfaces.ControllerInterface;

/**
 * 前端展示访问
 */
@RestController
@Order(value = 8)
public class PageController implements ControllerInterface {

	@ResponseBody
	@RequestMapping("/page_rst")
	public Object getPageByJson(String template_id, String id){
		return PageManager.inst().getItemPageById(template_id, id);
	}
	
	@RequestMapping("/pages")
	Object articles(HttpServletRequest req, String type_ids, @RequestParam Optional<Integer> page_no, @RequestParam Optional<Integer> page_size) {
		List<String> type_ids_list = Arrays.asList(type_ids.split("_"));//magic string
		//文章类型过滤器
//		Predicate<EntityPage> typeFilter = (a->type_ids_list.contains(a.getType_id()+""));
		return "";
	}
}
