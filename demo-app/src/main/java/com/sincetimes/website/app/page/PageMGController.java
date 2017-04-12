package com.sincetimes.website.app.page;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.app.page.vo.Item;
import com.sincetimes.website.app.page.vo.ItemPage;
import com.sincetimes.website.app.page.vo.ItemPageTemplateProvider;
import com.sincetimes.website.app.security.interfaces.SecureControllerInterface;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;

@Controller
@Order(value = 7)
@RequestMapping("/mg")
public class PageMGController implements SecureControllerInterface {
	
	@ResponseBody
	@RequestMapping("/items_reload")
	String reload(Model model, HttpServletRequest req){
		PageManager.inst().reloadAll();
		return "ok";
	}
	
	@RequestMapping("/page_template")
	String pageTemplate(Model model, HttpServletRequest req, String id){
		setUser(model, req);
		model.addAttribute("type_list", ItemType.values());
		ItemPage page = Util.isEmpty(id)?new ItemPage():ItemPageTemplateProvider.inst().getItemPageById(id);
		LogCore.BASE.info("get itemPage={}", page);
		Collection<ItemPage> pages = ItemPageTemplateProvider.inst().getAllItemPages().values();
		model.addAttribute("page", page);
		model.addAttribute("pages", pages);
		model.addAttribute("id", id);
		LogCore.BASE.debug("all itemPages={}", pages);
		return "page_template";
	}
	
	@RequestMapping("/add_item")
	void add_item_type(HttpServletRequest req, HttpServletResponse resp, String id, byte type_id, String key, String name){
		if(Util.isEmpty(id)){
			redirect(resp, "page_template");
			return;
		}
		ItemPage itemPage = ItemPageTemplateProvider.inst().getItemPageById(id);
		if(null == itemPage){
			redirect(resp, "page_template");
			return;
		}
		ItemType tp = ItemType.getType(type_id);
		Item it = new Item(key, name, tp);
		itemPage.putIfAbsent(key, it);
		ItemPageTemplateProvider.inst().saveOrUpdateItemPage(itemPage);
		redirect(resp, "page_template?id=" + id);
	}
	
	@RequestMapping("/delete_item")
	void delete_item(HttpServletResponse resp, String id, String key){
		if(Util.isEmpty(id)){
			redirect(resp, "page_template");
			return;
		}
		ItemPage itemPage = ItemPageTemplateProvider.inst().getItemPageById(id);
		if(null == itemPage){
			redirect(resp, "page_template");
			return;
		}
		itemPage.removeItem(key);
		ItemPageTemplateProvider.inst().saveOrUpdateItemPage(itemPage);
		redirect(resp, "page_template?id=" + id);
	}
	
	@RequestMapping("/add_template")
	void add_template(HttpServletRequest req, HttpServletResponse resp, String id, String name){
		if(Util.isEmpty(id)){
			redirect(resp, "page_template");
			return;
		}
		ItemPage itemPage = ItemPageTemplateProvider.inst().getItemPageById(id);
		if(null != itemPage){
			redirect(resp, "page_template?id=" + id);
			return;
		}
		itemPage = new ItemPage(id, name);
		itemPage.setId(id);
		ItemPageTemplateProvider.inst().saveOrUpdateItemPage(itemPage);
		redirect(resp, "page_template?id=" + id);
	}
}