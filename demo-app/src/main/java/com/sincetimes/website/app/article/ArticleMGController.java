package com.sincetimes.website.app.article;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Objects;
import com.sincetimes.website.app.security.interfaces.SecureAccessSupport;
import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Result;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.manager.DataManager;

@Controller
@Order(value = 7)
@RequestMapping("/mg/article")
public class ArticleMGController implements SecureAccessSupport {
	
	private static final int PAGE_SIZE = 10;
	@RequestMapping
	void pageTemplate(HttpServletRequest req, HttpServletResponse resp){
		redirect(resp, req.getRequestURL()+"/articles");
	}
	
	@ResponseBody
	@RequestMapping("/articles_reload")
	String reload(){
		ArticleManager.inst().reloadAll();
		return "ok";
	}
	/**
	 * 主页
	 * @param type_id 类型,	0和空串都是全部文章
	 * @param page_no 第几页,	0为第一页,1为第二页
	 * @return
	 */
	@RequestMapping("/articles")
	String articles(Model model, HttpServletRequest req, HttpServletResponse rsp, @RequestParam Optional<String> type_id, @RequestParam Optional<Integer> page_no) {
		//文章类型
		Predicate<Article> typeFilter = Util.isEmpty(type_id.orElse(""))?(a)->true : (a)->Objects.equal(a.getType_id(), type_id.get());
		Collection<Article> articles = ArticleManager.inst().getArticles().values();
		//分页
		int sum = (int) articles.stream().filter(typeFilter).count();
		int pages_num = (int) Math.ceil((double)sum/(double)PAGE_SIZE);// magic num
		int skip_num = page_no.orElse(0) * PAGE_SIZE;
		
	    List<Article> list = articles.stream()
	    		.filter(typeFilter)
	    		.sorted(Comparator.comparing(Article::getId).reversed())
	    		.skip(skip_num)
	    		.limit(PAGE_SIZE)
	    		.map(ArticleManager::initArticle)
	    		.collect(Collectors.toList());
	    
//	    list.forEach(a->a.setRead_num(StatsManager.inst().getUriCount(req.getRequestURI())));
	    String yulan_url = Util.toEmpyDefalut(DataManager.inst().get(DataManager.VALUE_YULAN_URL), "/mg/article_yulan.html");
	    model.addAttribute("type_list", ArticleManager.inst().getArticleTypes().values());
		model.addAttribute("sum", sum);//总文章数
		model.addAttribute("pages_num", pages_num);//总页数
		model.addAttribute("page_no", page_no.orElse(0));//第几页
		model.addAttribute("page_size", PAGE_SIZE);//每页大小
		model.addAttribute("list", list);
		model.addAttribute("type_id", type_id.orElse(""));
		if(!Util.isEmpty(type_id.orElse(""))){
			ArticleType type = ArticleManager.inst().getArticleTypes().get(type_id.get());
			model.addAttribute("type_name", Util.toNullDefalut(type, ArticleType::getName, "未知"));
		}
		model.addAttribute("yulan_url", yulan_url);
		//页码
		List<Integer> page_nos = new ArrayList<Integer>();
		for(int i=0; i< pages_num; i++){
			page_nos.add(i);
		}
		model.addAttribute("page_nos", page_nos);
		LogCore.BASE.info("articles.size:{}", list.size());
		String user_name = getSessionAttrFncOrElse(req, "user", UserVO::getName, null);
		model.addAttribute("user_name", user_name);
		return "articles";
	}
	/**
	 * 新增和修改文章
	 * Optional对@PathVariable无效
	 */
	@RequestMapping("/article_alter")
	Object articleAlter(Model model, @RequestParam Optional<Integer> id) {
		Article article = ArticleManager.inst().getArticleNotNullById(id.orElse(0));
		Collection<ArticleType> type_list = ArticleManager.inst().getArticleTypes().values();
//		
		LogCore.BASE.info("query articleTypes result:{}", type_list);
		model.addAttribute("type_list", type_list);
		model.addAttribute("article", article);
		LogCore.BASE.info("return ed article:{}",article);
		return "article_alter";
	}
	/** 删除文章 */
	@ResponseBody
	@RequestMapping("/article_delete")
	Object article_delete(Model model, @RequestParam Optional<Integer> id, @RequestParam Optional<Integer> type_id) {
		if(!id.isPresent()){
			return new Result().failure().setDesc("参数'id不能为空！");
		}
		model.addAttribute("type_id", type_id.orElse(0));
		return ArticleManager.inst().deleteArticle(id.get());
	}
		
	@ResponseBody
	@RequestMapping("/article/{id}")
	Object article(HttpServletRequest req, @PathVariable Integer id) {
		return ArticleManager.inst().getArticleWithInit(id);
	}
	
	
	@RequestMapping("/sumit_article")
	void sumit_article(HttpServletRequest req, HttpServletResponse rsp, @RequestParam Optional<Integer> id) throws IOException{
		save_article_func(req, id);
		rsp.sendRedirect("articles");
	}
	/***
	 * js 保存修改文章
	 */
	@ResponseBody
	@RequestMapping("/save_article_func")
	Object save_article_func(HttpServletRequest req, @RequestParam Optional<Integer> id) {
		Article atc = ArticleManager.inst().getArticleNotNullById(id.orElse(0));
		atc.setId(id.orElse(0));//0为新文章
		atc.setContent(req.getParameter("content"));
		atc.setImg_url(req.getParameter("img_url"));
		atc.setLink_url(req.getParameter("link_url"));
		atc.setShort_title(req.getParameter("short_title"));
		atc.setSort(req.getParameter("sort"));
		atc.setTitle(req.getParameter("title"));
		atc.setType_id(req.getParameter("type_id"));
		UserVO user = getSessioUser(req);
		if(null != user){
			if(0 == id.orElse(0)){
				atc.setCreate_time(System.currentTimeMillis());
		    	atc.setCreated_by(user.getName());
			}else{
				atc.setUpdate_time(System.currentTimeMillis());
				atc.setUpdated_by(user.getName());
			}
		}
		Result rst = ArticleManager.inst().saveOrUpdateArticles(atc);
		LogCore.BASE.info("article saved, encoding={}, rst={}, article:{}",req.getCharacterEncoding(), rst, atc);
		return rst;
	}

	@RequestMapping("/add_article_type")
	void add_article_type(HttpServletRequest req, HttpServletResponse rsp, @RequestParam Optional<String> type_id, @RequestParam Optional<String> name) throws IOException{
		if(!type_id.isPresent()){
			rsp.sendRedirect("articles");
			return;
		}
		/*如果存在直接返回*/
		if(ArticleManager.inst().getArticleTypes().containsKey(type_id.get())){
			rsp.sendRedirect("articles?type_id="+type_id.get());
			return;
		}
		ArticleType tp = new ArticleType();
		tp.setId(type_id.get());
		tp.setName(name.orElse("未定义"));
		UserVO user = getSessioUser(req);
		if(null != user){
			tp.setCreated_by(user.getName());
			tp.setUpdated_by(user.getName());
			tp.setCreate_time(System.currentTimeMillis());
			tp.setUpdate_time(System.currentTimeMillis());
		}
		ArticleManager.inst().saveOrUpdateArticleType(tp);
		rsp.sendRedirect("articles?type_id="+type_id.get());
	}
	@RequestMapping("/delete_article_type")
	void delete_article_type(HttpServletRequest req, HttpServletResponse resp, @RequestParam Optional<String> type_id){
		if(!Util.isEmpty(type_id.orElse(""))){
			ArticleManager.inst().deleteArticleType(type_id.get());
		}
		redirect(resp, "articles");
	}
	@RequestMapping("/edit_article_type")
	void edit_article_type(HttpServletRequest req, HttpServletResponse resp, @RequestParam Optional<String> type_id, @RequestParam Optional<String> name){
		if(!type_id.isPresent()||!name.isPresent()){
			redirect(resp, "articles");
			return;
		}
		ArticleType tp = ArticleManager.inst().getArticleTypes().get(type_id.get());
		if(null == tp){
			redirect(resp, "articles");
			return;
		}
		tp.setName(name.get());
		UserVO user = getSessioUser(req);
		if(null != user){
			tp.setUpdated_by(user.getName());
			tp.setUpdate_time(System.currentTimeMillis());
		}
		ArticleManager.inst().saveOrUpdateArticleType(tp);
		redirect(resp, "articles?type_id="+type_id.get());
	}
	
}
