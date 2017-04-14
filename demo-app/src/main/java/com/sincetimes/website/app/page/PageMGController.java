package com.sincetimes.website.app.page;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sincetimes.website.app.file.FileManager;
import com.sincetimes.website.app.page.builders.ItemPictureBuiler;
import com.sincetimes.website.app.page.vo.Item;
import com.sincetimes.website.app.page.vo.ItemPage;
import com.sincetimes.website.app.security.interfaces.SecureControllerInterface;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;

@Controller
@Order(value = 7)
@RequestMapping("/mg")
public class PageMGController implements SecureControllerInterface {
	/** 目录页面 */
	@RequestMapping("/page")
	String pageTemplate(Model model, HttpServletRequest req, String template_id){
		setUser(model, req);
		model.addAttribute("type_list", ItemType.values());
		ItemPage page = Util.isEmpty(template_id)?new ItemPage():PageTemplateManager.inst().getItemPageById(template_id);
		LogCore.BASE.info("get itemPage={}", page);
		Collection<ItemPage> pages = PageManager.inst().getAllItemPages(template_id).values();
		Collection<ItemPage> templatePages = PageTemplateManager.inst().getAllItemPages().values();
		model.addAttribute("page", page);//TODO: rmv
		model.addAttribute("pages", pages);
		model.addAttribute("templatePages", templatePages);
		model.addAttribute("template_id", template_id);
		LogCore.BASE.debug("all itemPages={}", pages);
		return "page";
	}
	/** 编辑页面*/
	@RequestMapping("/page_editor")
	String page_editor(Model model, HttpServletRequest req,String template_id, String id){
		setUser(model, req);
		model.addAttribute("type_list", ItemType.values());
		Collection<ItemPage> pages =  PageManager.inst().getAllItemPages(template_id).values();
		Collection<ItemPage> templatePages = PageTemplateManager.inst().getAllItemPages().values();
		if(Util.isEmpty(id)){
			id = PageManager.inst().applyItemPageId(template_id) + "";
		}
		List<Item> items= PageManager.inst().getItemsWithSort4Edit(template_id, id);
		ItemPage page = PageManager.inst().getItemPageById(template_id, id);//TODO: rmv
		model.addAttribute("page", page);
		model.addAttribute("pages", pages);
		model.addAttribute("items", items);
		model.addAttribute("id", id);
		model.addAttribute("template_id", template_id);
		model.addAttribute("templatePages", templatePages);
		LogCore.BASE.debug("all items={}", Util.prettyJsonStr(items));
		LogCore.BASE.debug("all itemPages={}", pages);
		return "page_editor";
	}
	/** 保存页面修改数据 */
	@RequestMapping("/page_save")
	void page_edit(StandardMultipartHttpServletRequest freq, HttpServletResponse resp, String template_id, String id, String key, byte type_id){
		Map<String, String[]> params = freq.getParameterMap();
		
		String fileUrl = FileManager.inst().uploadFileSimple(freq, "pages");
		if(!Util.isEmpty(fileUrl)){
			params.putIfAbsent(ItemPictureBuiler.FILE_PARAM_TAG, new String[]{fileUrl});
		}
		LogCore.BASE.debug("fileUrl={}", fileUrl);
		PageManager.inst().saveOrUpdatePageItem(template_id, id, key, type_id, params);
		redirect(resp, "page_editor?template_id=" + template_id + "&id=" + id);
	}
	/** 保存页面修改数据 */
	@RequestMapping("/delete_page_item")
	void item_remove(HttpServletResponse resp, String template_id, String id, String key){
		PageManager.inst().removePageItem(template_id, id, key);
		redirect(resp, "page_editor?template_id=" + template_id + "&id=" + id);
	}
	
	@ResponseBody
	@RequestMapping("/page_rst")
	public Object getPageByJson(String template_id, String id){
		return PageManager.inst().getItemPageById(template_id, id);
	}
	
	@RequestMapping("/add_page")
	void add_template(HttpServletRequest req, HttpServletResponse resp, String template_id, String id, String name){
		if(Util.isEmpty(id)){
			redirect(resp, "page_template");
			return;
		}
		ItemPage itemPage = PageManager.inst().getItemPageById(template_id, id);
		if(null != itemPage){
			redirect(resp, "page_template?id=" + id);
			return;
		}
		itemPage = new ItemPage(id, name);
		itemPage.setId(id);
		PageManager.inst().saveOrUpdateItemPage(template_id, itemPage);
		redirect(resp, "page_template?id=" + id);
	}
}