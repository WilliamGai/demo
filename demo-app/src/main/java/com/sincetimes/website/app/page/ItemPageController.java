package com.sincetimes.website.app.page;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sincetimes.website.app.page.vo.ItemPage;
import com.sincetimes.website.core.spring.interfaces.AccessSupport;

/**
 * 前端展示访问
 */
@RestController
@Order(value = 8)
public class ItemPageController implements AccessSupport {

	@ResponseBody
	@RequestMapping("/page_rst")
	public Object getPageByJson(String template_id, String id){
		ItemPageManager.inst().visit(template_id, id);
		return ItemPageManager.inst().getItemPageById(template_id, id);
	}
	@RequestMapping("/pages_rst")
	Object articles(String template_id) {
		List<ItemPage> pages = ItemPageManager.inst().getAllItemPagesWithSort(template_id);
		return pages.stream().collect(Collectors.toMap(ItemPage::getId, Function.identity(), (oldValue, newValue)->newValue, LinkedHashMap::new));
	}
}
