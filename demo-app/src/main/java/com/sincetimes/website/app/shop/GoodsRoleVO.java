package com.sincetimes.website.app.shop;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
/**
 * 客户端展示用
 */
public class GoodsRoleVO  extends GoodsVO implements Serializable {
	private static final long serialVersionUID = -5949501439682330869L;
	public int left_codes_num; 	//剩余激活码数量,前端显示
	public int status;			// 0未领取, 1领取过
	public GoodsRoleVO() {
	}
	
	public GoodsRoleVO(GoodsVO goods) {
		super(goods);
	}

	public GoodsRoleVO initLeftCodesNum(){
		this.left_codes_num = (int) ShopManager.inst().getLeftCodesNum(sn);
		return this;
	}
	
	public GoodsRoleVO initSatus(int status){
		this.status = status;
		return this;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
