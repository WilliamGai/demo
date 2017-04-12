package com.sincetimes.website.app.page;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PageDetailVO extends PageVO{
	public String content;
	
	public PageDetailVO() {
		super();
	}
	public PageDetailVO(EntityPage ar){
		super(ar);
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
