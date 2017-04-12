package com.sincetimes.website.app.page;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sincetimes.website.core.common.support.JSONBuilder;
import com.sincetimes.website.core.spring.interfaces.ControllerInterface;

/**
 * 前端展示访问
 */
@RestController
@Order(value = 8)
public class PageController implements ControllerInterface {

	@RequestMapping("/page")
	Object article(HttpServletRequest req, HttpServletResponse resp, @RequestParam Integer id,  @RequestParam Optional<Integer> id_pre, @RequestParam Optional<Integer> id_next) {
		JSONBuilder jb = JSONBuilder.create();
		jb.append("article", new PageDetailVO(null));
		//LogCore.BASE.info("query article result:{}", article);
		//model.addAttribute("list", list);
		return jb.buildJSON();
	}
	
	@RequestMapping("/pages")
	Object articles(HttpServletRequest req, String type_ids, @RequestParam Optional<Integer> page_no, @RequestParam Optional<Integer> page_size) {
		List<String> type_ids_list = Arrays.asList(type_ids.split("_"));//magic string
		//文章类型过滤器
//		Predicate<EntityPage> typeFilter = (a->type_ids_list.contains(a.getType_id()+""));
		return "";
	}
}
