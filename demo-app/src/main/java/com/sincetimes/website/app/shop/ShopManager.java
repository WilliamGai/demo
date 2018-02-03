package com.sincetimes.website.app.shop;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.util.StopWatch;

import com.sincetimes.website.app.wx_gzh.GzhManager;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.BiDataResult;
import com.sincetimes.website.core.common.support.DataResult;
import com.sincetimes.website.core.common.support.DataVO;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeUtil;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.redis.jedis.spring.JedisManagerBase;
/**
 * 商店
 * 商店数目比较少，几个，但是货架商品多，因此商店采用的数据结构为 一个hash,物品的激活码比较多,因此放在list
 * 商店信息比较多，所以商店只准创建和删除
 */
public class ShopManager extends JedisManagerBase {
	
	public static final String CODE_LIST_KEY_PREFIX ="codes_";//记录激活码队列的名字的前缀
	public static final String CODE_RMVD_LIST_KEY_PREFIX ="codes_rmvd_";//记录激活码队列的名字的前缀

	public static final String SHOP_HASH_KEY = "shops~keys";
	
	public static final String JIFEN_SET_KEY = "jifen~keys";
	
	public static ShopManager inst() {
		return ManagerBase.inst(ShopManager.class);
	}
	
	public String makeCodeKey(String goods_sn) {
		return CODE_LIST_KEY_PREFIX.concat(goods_sn);
	}
	public String makeRemovedKey(String goods_sn) {
		return CODE_RMVD_LIST_KEY_PREFIX.concat(goods_sn);
	}
	public long getLeftCodesNum(String goods_sn){
		return llen(makeCodeKey(goods_sn));
	}
	public long getRemovedCodesNum(String goods_sn){
		return llen(makeRemovedKey(goods_sn));
	}
	public void addShop(String shop_sn, String shop_name, String shop_desc, String open_time) {
		long openTime;
		try {
			openTime = TimeUtil.parseAndGetTime(open_time, "yyyy-MM-dd-HH:mm:ss");
		} catch (ParseException e) {
			LogCore.BASE.error("error time parameter={}", open_time);
			openTime = System.currentTimeMillis();
		}
		ShopVO shop = new ShopVO(shop_sn, shop_name, shop_desc, 1, openTime, null);
		LogCore.BASE.info("add_shop params, shop_sn={},shop_name={}, shop_desc={}, open_time={}",shop_sn, shop_name, shop_desc, open_time);
		hsetnx(SHOP_HASH_KEY, shop.sn, shop.toJSONString());
		LogCore.BASE.info("add_shop result ={}", hget(SHOP_HASH_KEY, shop.sn));
	}
	long deleteShop(String shop_sn){
		return hdel(SHOP_HASH_KEY, shop_sn);
	}
	void setShop(ShopVO shop){
		hset(SHOP_HASH_KEY, shop.sn, shop.toJSONString());
	}
	ShopVO getShop(String shop_sn){
		LogCore.BASE.info("get getShop shop_sn={}", shop_sn);
		return ShopVO.parseObject(hget(SHOP_HASH_KEY, shop_sn));
	}
	List<ShopVO> getAllShop(){
		return hgetall(SHOP_HASH_KEY).values().stream().map(ShopVO::parseObject).collect(Collectors.toList());
	}
	/**
	 * 总是成功
	 */
	public void addGoods(String shop_sn, String picUrl, String goods_name, String goods_desc, int point_need, List<String> codes) {
		String new_sn = incr("goods_sns")+"";
		String codes_list_key = makeCodeKey(new_sn);
		if(!Util.isEmpty(codes)){
			rpush(codes_list_key, codes.toArray(new String[]{}));
		}
		GoodsVO vo = new GoodsVO(new_sn, goods_name, picUrl, goods_desc, point_need);
		LogCore.BASE.info("add_goods params, shop_sn={},goods_name={}, goods_desc={}, point_need={}",shop_sn, goods_name, goods_desc, point_need);
		ShopVO shop = getShop(shop_sn);
		shop.addGoods(vo);
		setShop(shop);
		LogCore.BASE.info("add_shop result ={}", hget(SHOP_HASH_KEY, shop.sn));
	}
	public void editGoods(String shop_sn, String sn, String picUrl, String goods_name, String goods_desc, Optional<Integer> point_need, List<String> codes) {
		String codes_list_key = makeCodeKey(sn);
		if(!Util.isEmpty(codes)){
			rpush(codes_list_key, codes.toArray(new String[]{}));
		}
		LogCore.BASE.info("edit_goods params, shop_sn={},goods_name={}, goods_desc={}, point_need={}",shop_sn, goods_name, goods_desc, point_need);
		ShopVO shop = getShop(shop_sn);
		GoodsVO goods = shop.getGoods(sn);
		goods.sn = sn;
		goods.goods_name = goods_name;
		goods.goods_desc = goods_desc;
		if(!Util.isEmpty(picUrl)){
			goods.pic_url = picUrl;
		}
		point_need.ifPresent(n->goods.points_need=n);
		setShop(shop);
		LogCore.BASE.info("edit_shop result ={}", hget(SHOP_HASH_KEY, shop.sn));
	}
	public void deleteGoods(String goods_sn, String shop_sn){
		ShopVO shop = getShop(shop_sn);
		GoodsVO vo = shop.deleteGoods(goods_sn);
		if(null != vo){
			String codes_list_key = makeCodeKey(vo.sn);
			String codes_list_rmv_key = makeRemovedKey(vo.sn);
			del(codes_list_key);
			del(codes_list_rmv_key);
		}
		setShop(shop);
	}
	public void upGoods(String goods_sn, String shop_sn){
		ShopVO shop = getShop(shop_sn);
		shop.upGoods(goods_sn);
		setShop(shop);
	}
	/** 客户端接口 */
	@Override
	public void init() {
		
	}

	public DataResult qiandao(String shop_sn, String openid) {
		String jifenKey = mkJifenKey(shop_sn);
		Double score = zscore(jifenKey, openid);
		String date_tag = TimeUtil.formatTime(System.currentTimeMillis(), "yyyyMMdd");
		String tag_key = "sign_up".concat(date_tag).concat(openid);
		String tag_history = get(tag_key);
		LogCore.BASE.info("get sign_up_tag={},get value={}", tag_key, tag_history);
		if(!Util.isEmpty(tag_history)){
			return new BiDataResult(0, 0, score.intValue());
		}
		String expire_rst = setEX_NX_Exipire(tag_key, "1", TimeUtil.WEEK_SECONDS);
		LogCore.BASE.info("set expire_rst={}, ttl={}", expire_rst, ttl(tag_key));
		if(score == null){
			zincrby(jifenKey, 20, openid);
			return new BiDataResult(1, 20, 20);
		}else{
			int newScore = zincrby(jifenKey, 10, openid);
			return new BiDataResult(2, 10, newScore);
		}		
	}
	public int getScore(String shop_sn, String openid) {
		String jifenKey = mkJifenKey(shop_sn);
		Double score = zscore(jifenKey, openid);
		return null == score ? 0 : score.intValue();
	}
	public Long getScoreSetSize(String shop_sn){
		String jifenKey = mkJifenKey(shop_sn);
		return zcard(jifenKey);
	}
	public void delJifen(String shop_sn, String openid) {
		String jifenKey = mkJifenKey(shop_sn);
		zrem(jifenKey, openid);
	}
	/*public String mkJifenMemeberKey(String shop_sn, String openid) {
		Objects.requireNonNull(shop_sn, "shop_sn is null");
		Objects.requireNonNull(openid, "openid is null");
		return shop_sn.concat("_").concat(openid);
	}*/
	public String mkJifenKey(String shop_sn) {
		Objects.requireNonNull(shop_sn, "shop_sn is null");
		return JIFEN_SET_KEY.concat("_").concat(shop_sn);
	}

	public DataResult exchangeByPoints(String shop_sn, String goods_sn, String openid) {
		ShopVO shop = getShop(shop_sn);
		GoodsVO vo = shop.deleteGoods(goods_sn);
		int points_need = vo.points_need;
		int points_left = getScore(shop_sn, openid);
		int after_points = points_left - points_need;
		if(after_points < 0){
			return new DataResult(2, "points is not enough!");//积分不够
		}
		//因为每个商品只能兑换一次
		List<ExchangeResultVO> list = getExchangeList(shop_sn, openid);
		boolean has_exchanged = list.parallelStream().anyMatch(e -> Objects.equals(e.sn, goods_sn));
		LogCore.BASE.debug("buy identify, list={},goods_sn ={},has_exchanged={}",list, goods_sn, has_exchanged);
		if(has_exchanged){
			 return new DataResult(4, "u had exchanged it!");
		}
		
		String jifenKey = mkJifenKey(shop_sn);
		String codes_list_key = makeCodeKey(vo.sn);
		String codes_list_rmv_key = makeRemovedKey(vo.sn);
		String code = rpoplpush(codes_list_key, codes_list_rmv_key);
		if(null != code){
			zadd(jifenKey, after_points, openid);//减分数
			
			list.add(new ExchangeResultVO(vo.sn, code, vo.pic_url, vo.goods_name, vo.goods_desc));
			setExchangeList(shop_sn, openid, list);//记录
			return new DataResult(1, vo.init());
		}
		return new DataResult(3, "goods sold out!");
	}
	
	private void setExchangeList(String shop_sn, String openid, List<ExchangeResultVO> list) {
		String filed_key = "shop_exchange_reslut_".concat(shop_sn);
		String json = ExchangeResultVO.toJSONString(list);
		hset(openid, filed_key, json);
	}

	public List<ExchangeResultVO> getExchangeList(String shop_sn, String openid) {
		if(Util.isEmpty(openid)){
			return new ArrayList<ExchangeResultVO>();
		}
		String filed_key = "shop_exchange_reslut_".concat(shop_sn);
		String rstJson = hget(openid, filed_key);
		List<ExchangeResultVO> list = ExchangeResultVO.jsonToList(rstJson);
		LogCore.BASE.info("getExchangeResult key={}, rstJson={}",filed_key,  rstJson);
		return list;
	}
	/**客户端调用*/
	public BiDataResult rank(String shop_sn, String openid, int start, int end) {
		StopWatch stopWatch = new StopWatch("fenshu");
		stopWatch.start("redis rank");// 很快
		
		AtomicInteger rank_count = new AtomicInteger(0);
		List<RankVO> ranklist = new ArrayList<>();
		String jifenKey = mkJifenKey(shop_sn);
		
		//前10名
		zRevRangeWithScores(jifenKey, start, end).forEach((k) -> {
			String key = k.getElement();
			Double score = k.getScore();
			String[] nickname_pic = GzhManager.inst().getNickNamePicNotNull(key);
			RankVO vo = new RankVO(rank_count.incrementAndGet(), nickname_pic[0], nickname_pic[1],score.intValue());
			ranklist.add(vo);
		});
		
		ranklist.sort(Comparator.comparing(RankVO::mkWeight).reversed());//降序
		stopWatch.stop();
		
		RankVO vo = null;
		if(GzhManager.inst().existOpenid(openid)){
			String[] nickname_pic = GzhManager.inst().getNickNamePicNotNull(openid);
			Double score = zscore(jifenKey, openid);
			int _score = Util.toNullDefalut(score, Double::intValue, 0);
			Long ranking = zrevrank(jifenKey, openid);
			int _rank = Util.toNullDefalut(ranking, 0L).intValue();
			vo = new RankVO(_rank+1 , nickname_pic[0], nickname_pic[1], _score);
			LogCore.BASE.debug("me={}", vo);
		}
		
		LogCore.BASE.debug("zrevrangeWithScores ranklist.size={}", ranklist.size());
		LogCore.BASE.debug("get rank time used:{}", stopWatch.prettyPrint());
		return new BiDataResult(1, ranklist, vo);
	}
	/**mg调用,返回名次,积分以及是否微信授权*/
	public List<DataVO> rankList(String shop_sn, int start, int end) {
		StopWatch stopWatch = new StopWatch("mg fenshu");
		stopWatch.start("mg redis rank");// 很快
		
		List<DataVO> statslist = new ArrayList<>();
		String jifenKey = mkJifenKey(shop_sn);
		AtomicInteger count = new AtomicInteger(start);
		zRevRangeWithScores(jifenKey, start, end).forEach((k) -> {
			String key = k.getElement();
			Double score = k.getScore();
			Boolean existInWeixin = GzhManager.inst().existOpenid(key);
			Object goodsRsvd = getExchangeList(shop_sn, key);
			DataVO vo = new DataVO(key, count.incrementAndGet(), existInWeixin, score.intValue(), goodsRsvd);
			statslist.add(vo);
		});
		
		stopWatch.stop();
		
		LogCore.BASE.debug("mg zrevrangeWithScores ranklist.size={}", statslist.size());
		LogCore.BASE.debug("mg get rank time used:{}", stopWatch.prettyPrint());
		return statslist;
	}
}
