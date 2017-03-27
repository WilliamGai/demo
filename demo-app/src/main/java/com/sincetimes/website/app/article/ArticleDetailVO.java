package com.sincetimes.website.app.article;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ArticleDetailVO extends ArticleVO{
	public String content;
	
	public ArticleDetailVO() {
		super();
	}
	public ArticleDetailVO(Article ar){
		super(ar);
		this.content = ar.getContent();
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
