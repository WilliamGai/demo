package com.sincetimes.website.app.page;

import org.springframework.stereotype.Component;

import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.manager.annotation.ManangerInject;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Result;
@ManangerInject
@Component
public class PageManager extends ManagerBase {
	
	private static final String KEY_ARTICLE_BROWES_NUM = "article_browes_num";

	public static PageManager inst() {
		return ManagerBase.inst(PageManager.class);
	}

	@Override
	public void init() {
		reloadAll();
	}

	public void reloadAll() {
		LogCore.BASE.info("articleManager inited, articleTypes.size={}, articles.size={}");
	}
	
	
	/** article的id为0说明为新增 */
	public Result saveOrUpdateArticles(EntityPage atc){
		return new Result().success();
	}

	public Result deleteArticle(Integer id) {
		return new Result().failure().setDesc("db delete fail");
	}

	public void saveOrUpdateArticleType(PageType articleType) {
	}
	
	public void deleteArticleType(String type_id) {
	}
}