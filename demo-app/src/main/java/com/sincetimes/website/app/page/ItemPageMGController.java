package com.sincetimes.website.app.page;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.sincetimes.website.app.security.interfaces.SecureAccessSupport;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;

@Controller
@Order(value = 7)
@RequestMapping("/mg/page")
public class ItemPageMGController implements SecureAccessSupport {
	/** 目录页面 */
	@RequestMapping("/page")
	String pageTemplate(Model model, HttpServletRequest req, String template_id){
		model.addAttribute("type_list", ItemType.values());
		Collection<ItemPage> templatePages = ItemPageTemplateManager.inst().getAllItemPages().values();
		ItemPage templatePage = ItemPageTemplateManager.inst().getItemPageById(template_id);
		if(null == templatePage){
			templatePage = templatePages.stream().findFirst().orElse(new ItemPage());
		}
		String fixedTemplateId = Objects.toString(templatePage.getId(), template_id);
		Collection<ItemPage> pages = ItemPageManager.inst().getAllItemPagesWithSort(fixedTemplateId);
		LogCore.BASE.info("get templatePage={}", templatePage);
		model.addAttribute("page", templatePage);//TODO: rmv
		model.addAttribute("pages", pages);
		model.addAttribute("templatePages", templatePages);
		model.addAttribute("template_id", fixedTemplateId);
		LogCore.BASE.debug("all itemPages={}", pages);
		return "page";
	}
	/**增加一个空白页面*/
	@RequestMapping("/page_add")
	void page_add(HttpServletResponse resp, String template_id, String name){
		String id = ItemPageManager.inst().applyItemPageId(template_id);
		ItemPage itemPage = new ItemPage(id, name);
		ItemPageManager.inst().saveOrUpdateItemPage(template_id, itemPage);
		redirect(resp, "page_editor?template_id=" + template_id + "&id=" + id);
	}
	/**删除*/
	@RequestMapping("/page_delete")
	void page_delete(HttpServletResponse resp, String template_id, String id){
		ItemPageManager.inst().removePage(template_id, id);
		redirect(resp, "page?template_id=" + template_id);
	}
	
	/**
	 * 编辑页面
	 * @param template_id
	 * @param id 如果不传入此参数会申请一个新的
	 */
	@RequestMapping("/page_editor")
	String page_editor(Model model, HttpServletRequest req, HttpServletResponse resp, String template_id, String id){
		if(Util.isEmpty(template_id)){
			redirect(resp, "page?template_id=" + template_id);
			return null;
		}
		model.addAttribute("type_list", ItemType.values());
		Collection<ItemPage> pages =  ItemPageManager.inst().getAllItemPagesWithSort(template_id);
		Collection<ItemPage> templatePages = ItemPageTemplateManager.inst().getAllItemPages().values();
		if(Util.isEmpty(id)){
			id = ItemPageManager.inst().applyItemPageId(template_id);
		}
		List<Item> items= ItemPageManager.inst().getItemsWithSort4Edit(template_id, id);//fatal err!
		ItemPage page = ItemPageManager.inst().getItemPageById(template_id, id);//TODO: rmv 
		model.addAttribute("page", page);
		model.addAttribute("pages", pages);
		model.addAttribute("items", items);
		model.addAttribute("id", id);
		model.addAttribute("template_id", template_id);
		model.addAttribute("templatePages", templatePages);
		LogCore.BASE.debug("all items={}", Util.prettyJsonStr(items));
		LogCore.BASE.debug("all itemPages={}", Util.prettyJsonStr(pages.size()));
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
		ItemPageManager.inst().saveOrUpdatePageItem(template_id, id, key, type_id, params);
		redirect(resp, "page_editor?template_id=" + template_id + "&id=" + id);
	}
	/** 保存页面修改数据 */
	@RequestMapping("/delete_page_item")
	void delete_page_item(HttpServletResponse resp, String template_id, String id, String key){
		ItemPageManager.inst().removePageItem(template_id, id, key);
		redirect(resp, "page_editor?template_id=" + template_id + "&id=" + id);
	}
	
	@ResponseBody
	@RequestMapping("/page_rst")
	public Object getPageByJson(String template_id, String id){
		return ItemPageManager.inst().getItemPageById(template_id, id);
	}
}