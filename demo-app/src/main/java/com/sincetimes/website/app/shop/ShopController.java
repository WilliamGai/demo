package com.sincetimes.website.app.shop;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sincetimes.website.app.wx_gzh.GzhManager;
import com.sincetimes.website.core.common.support.BiDataResult;
import com.sincetimes.website.core.common.support.DataResult;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.interfaces.AccessSupport;
/**
 * 积分兑换商城
 */
@RestController
public class ShopController implements AccessSupport{

	@RequestMapping("/{shop_sn}/get_shop_infos")
	public Object get_shop_infos(String openid, @PathVariable String shop_sn, @RequestParam Optional<Integer> page_no) {
		String [] nic_pic = GzhManager.inst().getNickNamePicNotNull(openid);
		ShopVO shop =ShopManager.inst().getShop(shop_sn);
		if(null == shop){
			return new DataResult(0, "shop not exist!");
		}
		int _page_size = 5;
		int sum = shop.shelfs.size();
		int pages_num = (int) Math.ceil((double)sum/(double)_page_size);// magic num
		int skip_num = page_no.orElse(0) * _page_size;
		
	    Set<String> history_set = ShopManager.inst().getExchangeList(shop_sn, openid).stream().map(v->v.sn).collect(Collectors.toSet());
		Function<GoodsVO, GoodsRoleVO> role_goods_converter = g ->{
			int status = history_set.contains(g.sn)? 1:0;
			return new GoodsRoleVO(g).initLeftCodesNum().initSatus(status);
			
		};
		
	    List<GoodsRoleVO> list =  shop.shelfs.stream()
	    		.skip(skip_num)
	    		.limit(_page_size)
	    		.map(role_goods_converter)
	    		.collect(Collectors.toList());
	    

//	    list.parallelStream().anyMatch(e -> Objects.equals(e.sn, goods_sn));
	    
	    Map<String,Object> map = new HashMap<>();
	    map.put("goods_list", list);
	    map.put("pages_num", pages_num);
	    BiDataResult rst = new BiDataResult(1, nic_pic, map);
	    LogCore.BASE.debug("history_set ={}, skip_num = {},_page_size={}, list={}",history_set, skip_num, _page_size, list);
		if(LogCore.BASE.isDebugEnabled()){
			String prettyStr = JSON.toJSONString(rst,SerializerFeature.PrettyFormat,SerializerFeature.WriteClassName, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
			LogCore.BASE.debug("pretty rst = {}", prettyStr);
		}
		return rst;
	}
	
	@RequestMapping("/{shop_sn}/qiandao")
	public Object qiandao(@PathVariable String shop_sn, String openid) {
		DataResult rst = identifyShopAndOpenid(shop_sn, openid);
		if(rst.result != 1){
			return rst;
		}
		LogCore.BASE.info("qiandao openid={}, shop_sn={}", openid, shop_sn);
		return ShopManager.inst().qiandao(shop_sn, openid);
	}
	@RequestMapping("/{shop_sn}/qiantest")
	public Object qiantest(@PathVariable String shop_sn, String openid) {
		String jifenKey = ShopManager.inst().mkJifenKey(shop_sn);
		LogCore.BASE.info("qiantest openid={}, shop_sn={}", openid, shop_sn);
		return ShopManager.inst().zincrby(jifenKey, 10, openid);
	}

	@RequestMapping("/{shop_sn}/look_jifen")
	public Object get_jifen(@PathVariable String shop_sn, String openid) {
		DataResult rst = identifyShopAndOpenid(shop_sn, openid);
		if(rst.result != 1){
			return rst;
		}
		String [] nic_pic = GzhManager.inst().getNickNamePicNotNull(openid);
		int score = ShopManager.inst().getScore(shop_sn, openid);
		LogCore.BASE.info("shop_sn={}, openid={}, score={}", shop_sn, openid, score);
		return new BiDataResult(1, nic_pic, score);
	}
	//-3商店不存在;-2 openid为空;-1 openid,-4商品不存在,1成功,2积分不够,3没有库存
	@RequestMapping("/{shop_sn}/buy")
	public Object buy(@PathVariable String shop_sn, String goods_sn, String openid) {
		DataResult rst = identifyShopAndOpenidAndGoods(shop_sn, openid, goods_sn);
		LogCore.BASE.info("buy openid={}, shop_sn={}, goods_sn={}", openid, shop_sn, goods_sn);
		if(rst.result != 1){
			return rst;
		}
		return ShopManager.inst().exchangeByPoints(shop_sn, goods_sn, openid);
	}
	@RequestMapping("/{shop_sn}/buy_history")
	public Object buy_history(@PathVariable String shop_sn, String openid) {
		DataResult rst = identifyShopAndOpenid(shop_sn, openid);
		LogCore.BASE.info("buy_history openid={}, shop_sn={}", openid, shop_sn);
		if(rst.result != 1){
			return rst;
		}
		rst.data = ShopManager.inst().getExchangeList(shop_sn, openid);
		return rst;
	}
	
	@RequestMapping("/{shop_sn}/jifen_rank")
	public Object jifen_rank(@PathVariable String shop_sn, String openid) {
		ShopVO shop = ShopManager.inst().getShop(shop_sn);
		if(null == shop){
			return new DataResult(-3, "shop_sn not exist!");
		}
		LogCore.BASE.info("jifen_rank  shop_sn={},openid={}", shop_sn, openid);
		return ShopManager.inst().rank(shop_sn, openid, 0, 9);
	}
	
	/**
	 * 验证商店ID和Openid
	 * @return -3商店不存在;-2 openid为空;-1 openid不存在
	 */
	private DataResult identifyShopAndOpenid(String shop_sn, String openid) {
		ShopVO shop = ShopManager.inst().getShop(shop_sn);
		if(null == shop){
			return new DataResult(-3, "shop_sn not exist!");
		}
		if(Util.isEmpty(openid)){
			return new DataResult(-2, "openid empty!");
		}
		if(!GzhManager.inst().existOpenid(openid)){
			return new DataResult(-1, "openid not exitst!");
		}
		return new DataResult(1, null);
	}
	/**
	 * -3商店不存在;-2 openid为空;-1 openid,-4商品不存在
	 */
	private DataResult identifyShopAndOpenidAndGoods(String shop_sn, String openid, String goods_sn) {
		DataResult rst = identifyShopAndOpenid(shop_sn, openid);
		if(rst.result != 1){
			return rst;
		}
		ShopVO shop = ShopManager.inst().getShop(shop_sn);
		Object o = shop.getGoods(goods_sn);
		if(null == o){
			return new DataResult(-4, "goods_sn not exitst!");
		}
		return rst;
	}
}
