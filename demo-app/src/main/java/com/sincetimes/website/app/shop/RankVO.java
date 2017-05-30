package com.sincetimes.website.app.shop;

import com.sincetimes.website.core.common.support.WeightSupport;
import com.sincetimes.website.core.common.vo.ToStringAbstract;


public class RankVO extends ToStringAbstract implements WeightSupport<String>{
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
	
}
