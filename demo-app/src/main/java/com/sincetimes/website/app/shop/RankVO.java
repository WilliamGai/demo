package com.sincetimes.website.app.shop;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.sincetimes.website.core.common.support.WeightSupport;


public class RankVO implements WeightSupport<String>{
	public int ranking;
	public String name;
	public String pic;
	public int score;
	
	
	public RankVO() {
		super();
	}

	public RankVO(int ranking, String name, String pic, int score) {
		super();
		this.ranking = ranking;
		this.name = name;
		this.pic = pic;
		this.score = score;
	}

	@Override
	public String mkKey() {
		return name;
	}

	@Override
	public int mkWeight() {
		return score;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
