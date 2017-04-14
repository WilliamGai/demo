package com.sincetimes.website.app.page;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sincetimes.website.core.spring.interfaces.ControllerInterface;

/**
 * 前端展示访问
 */
@RestController
@Order(value = 8)
public class ItemPageController implements ControllerInterface {

	@ResponseBody
	@RequestMapping("/page_rst")
	public Object getPageByJson(String template_id, String id){
		ItemPageManager.inst().visit(template_id, id);
		return ItemPageManager.inst().getItemPageById(template_id, id);
	}
	
	@RequestMapping("/pages_rst")
	Object articles(String template_id) {
		return  ItemPageManager.inst().getAllItemPages(template_id);
	}
}
