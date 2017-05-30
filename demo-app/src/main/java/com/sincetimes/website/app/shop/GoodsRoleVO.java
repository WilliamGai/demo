package com.sincetimes.website.app.shop;

import java.io.Serializable;
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
}
