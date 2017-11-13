package com.sincetimes.website.app.article;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.sincetimes.website.core.common.support.JSONBuilder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.spring.interfaces.AccessSupport;

/**
 * 前端展示访问
 */
@RestController
@Order(value = 8)
public class ArticleController implements AccessSupport {

	@RequestMapping("/article")
	Object article(HttpServletRequest req, HttpServletResponse resp, @RequestParam Integer id,
	            @RequestParam Optional<Integer> id_pre, @RequestParam Optional<Integer> id_next) {
		JSONBuilder jb = JSONBuilder.create();
		Article article = ArticleManager.inst().getArticleWithRead(id);
		if (id_pre.isPresent()) {
			Article pre = ArticleManager.inst().getArticleWithRead(id_pre.get());
			jb.append("pre_id", pre.getId());
			jb.append("pre_title", pre.getTitle());
		}
		if (id_next.isPresent()) {
			Article next = ArticleManager.inst().getArticleWithRead(id_next.get());
			jb.append("next_id", next.getId());
			jb.append("next_title", next.getTitle());
		}
		jb.append("article", new ArticleDetailVO(article));
		// LogCore.BASE.info("query article result:{}", article);
		// model.addAttribute("list", list);
		return jb.buildJSON();
	}

	/**
	 * 根据文章类型的id来查询文章
	 * 
	 * @param type_ids 非空
	 * @param page_no 默认为0，即第一页
	 * @param page_size 每页几篇文章，默认为4
	 * @return
	 */
	@RequestMapping("/articles")
	Object articles(HttpServletRequest req, String type_ids, @RequestParam Optional<Integer> page_no,
	            @RequestParam Optional<Integer> page_size) {
		List<String> type_ids_list = Arrays.asList(type_ids.split("_"));// magic string
		// 文章类型过滤器
		Predicate<Article> typeFilter = (a -> type_ids_list.contains(a.getType_id() + ""));
		return paging(page_no, page_size, typeFilter);
	}

	/**
	 * 分页
	 * 
	 * @param page_no 当前页码
	 * @param page_size 每页几条数据
	 * @param typeFilter 过滤条件
	 * @return
	 */
	public JSONObject paging(Optional<Integer> page_no, Optional<Integer> page_size, Predicate<Article> typeFilter) {
		Collection<Article> articles = ArticleManager.inst().getArticles().values();
		// 分页
		int _page_size = page_size.orElse(4);
		int sum = (int) articles.stream().filter(typeFilter).count();
		int pages_num = (int) Math.ceil((double) sum / (double) _page_size);
		int skip_num = page_no.orElse(0) * _page_size;

		List<ArticleVO> list = articles.stream().filter(typeFilter)
		            .sorted(Comparator.comparing(Article::getId).reversed()).skip(skip_num).limit(_page_size)
		            .map(ArticleManager::initArticle).map(ArticleVO::new).collect(Collectors.toList());
		JSONBuilder jsonBuilder = JSONBuilder.create();
		jsonBuilder.append("sum", sum);// 总文章数
		jsonBuilder.append("pages_num", pages_num);// 总页数
		jsonBuilder.append("page_no", page_no.orElse(0));// 第几页
		jsonBuilder.append("page_size", _page_size);// 每页大小
		jsonBuilder.append("list", list);
		// 页码
		List<Integer> page_nos = new ArrayList<Integer>();
		for (int i = 0; i < pages_num; i++) {
			page_nos.add(i);
		}
		jsonBuilder.append("page_nos", page_nos);
		LogCore.BASE.info("articles.size:{}", list.size());
		return jsonBuilder.buildJSON();
	}
}
