package com.sincetimes.website.app.article;

public class ArticleDetailVO extends ArticleVO{
	public String content;
	
	public ArticleDetailVO() {
		super();
	}
	public ArticleDetailVO(Article ar){
		super(ar);
		this.content = ar.getContent();
	}
}
