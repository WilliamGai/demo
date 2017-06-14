package com.sincetimes.website.app.page;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.app.page.vo.Item;
import com.sincetimes.website.app.page.vo.ItemPage;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.interfaces.AccessSupport;
@Controller
@Order(value = 7)
@RequestMapping("/mg/page")
public class ItemPageTemplateMGController implements AccessSupport {
	@RequestMapping
	void pageTemplate(HttpServletRequest req, HttpServletResponse resp){
		redirect(resp, req.getRequestURL()+"/page_template");
	}
	@ResponseBody
	@RequestMapping("/items_reload")
	String reload(Model model, HttpServletRequest req){
		return "ok";
	}
	
	@RequestMapping("/page_template")
	String pageTemplate(Model model, HttpServletRequest req, String id){
		model.addAttribute("type_list", ItemType.values());
		ItemPage page = ItemPageTemplateManager.inst().getItemPageById(id);
		Collection<ItemPage> pages = ItemPageTemplateManager.inst().getAllItemPages().values();
		if(null == page){
			page = pages.stream().findFirst().orElse(new ItemPage());
		}
		LogCore.BASE.info("get itemPage={}", page);
		String fixedId = Objects.toString(page.getId(), id);
		List<Item> items= ItemPageTemplateManager.inst().getItemsWithSort(fixedId);
		model.addAttribute("page", page);
		model.addAttribute("items", items);
		model.addAttribute("pages", pages);
		model.addAttribute("id", fixedId);
		LogCore.BASE.debug("all itemPages={}", pages);
		LogCore.BASE.debug("all items={}", items);
		return "page_template";
	}
	
	@RequestMapping("/page_template_rst")
	@ResponseBody
	Object page_rst(String template_id){
		return ItemPageTemplateManager.inst().getItemPageById(template_id);
	}
	
	@RequestMapping("/add_item")
	void add_item_type(HttpServletRequest req, HttpServletResponse resp, String id, byte type_id, String key, String name){
		if(Util.isEmpty(id)){
			redirect(resp, "page_template");
			return;
		}
		ItemPage itemPage = ItemPageTemplateManager.inst().getItemPageById(id);
		if(null == itemPage){
			redirect(resp, "page_template");
			return;
		}
		ItemType tp = ItemType.getType(type_id);
		Item it = new Item(key, name, tp);
		it.setCreateTime(System.currentTimeMillis());
		itemPage.putIfAbsent(key, it);
		ItemPageTemplateManager.inst().saveOrUpdateItemPage(itemPage);
		redirect(resp, "page_template?id=" + id);
	}
	
	@RequestMapping("/delete_item")
	void delete_item(HttpServletResponse resp, String id, String key){
		if(Util.isEmpty(id)){
			redirect(resp, "page_template");
			return;
		}
		ItemPageTemplateManager.inst().removeItem(id, key);
		redirect(resp, "page_template?id=" + id);
	}
	@RequestMapping("/delete_template")
	void delete_template(HttpServletResponse resp, String id){
		if(Util.isEmpty(id)){
			redirect(resp, "page_template");
			return;
		}
		ItemPageTemplateManager.inst().removePage(id);
		redirect(resp, "page_template");
	}

	
	@RequestMapping("/add_template")
	void add_template(HttpServletRequest req, HttpServletResponse resp, String id, String name){
		if(Util.isEmpty(id)){
			redirect(resp, "page_template");
			return;
		}
		ItemPage itemPage = ItemPageTemplateManager.inst().getItemPageById(id);
		if(null != itemPage){
			redirect(resp, "page_template?id=" + id);
			return;
		}
		itemPage = new ItemPage(id, name);
		itemPage.setId(id);
		ItemPageTemplateManager.inst().saveOrUpdateItemPage(itemPage);
		redirect(resp, "page_template?id=" + id);
	}
}