package com.sonic.website.app.article;

import java.util.List;
import java.util.Map;

public interface ArticleService {
	/** 保存新文章，返回数据库自增ID*/
	int insertArtical(Article atc);
	/** 更改已有文章*/
	int updateArtical(Article atc);
	Map<Integer, Article> getAllArticles();

	int deleteArticle(int id);

	Article getArticleById(int id);

	List<ArticleType> getAllArticleTypes();
	int insertArticalType(ArticleType tp);
	int updateArticalType(ArticleType tp);
	int deleteArticleType(String id);

}
