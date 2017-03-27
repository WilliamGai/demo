package com.sincetimes.website.app.lunbo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.redis.jedis.spring.JedisManagerBase;

@Component
public class MediaManager extends JedisManagerBase {
	
	private static final LunboGroupVO EMPTY_LUNBO_GROUP;
	private static final String LUNBO_HASH_KEY = "lunbo_keys";
	static {
		EMPTY_LUNBO_GROUP = new LunboGroupVO("000","空");
	}
	
	public static MediaManager inst() {
		return ManagerBase.inst(MediaManager.class);
	}
	
	public LunboGroupVO getLunboGroupVO(String group_no){
		String jsonStr = hget(LUNBO_HASH_KEY, group_no);
		LogCore.BASE.debug("hget lunbo_keys {}:{}", group_no, jsonStr);
		return LunboGroupVO.parseObject(jsonStr);
	}
	
	public List<LunboGroupVO> getAllLunboGroupVO(){
		List<LunboGroupVO> list = new ArrayList<>();
		Map<String, String> map = hgetall(LUNBO_HASH_KEY);
		LogCore.BASE.debug("hget all lunbo:{}", map);
		if(Util.isEmpty(map)){
			return list;
		}
		map.forEach((k, v)->list.add(LunboGroupVO.parseObject(v)));
		list.sort(Comparator.comparing(LunboGroupVO::getTime));
		return list;
	}
	
	public LunboGroupVO getLunboGroupNotNull(String group_no){
		LunboGroupVO vo = getLunboGroupVO(group_no);
		return vo == null?getEmptyLunboGroupVO(group_no):vo;
	}
	
	public LunboGroupVO getEmptyLunboGroupVO(){
		return EMPTY_LUNBO_GROUP;
	}
	
	@Override
	public void init() {
		hgetall(LUNBO_HASH_KEY).forEach(this::checkLunbo);
	}
	
	public void checkLunbo(String name, String value) {
		LogCore.BASE.info("redis lunbo name={}, value={}", name, value);
	}
	public LunboGroupVO getEmptyLunboGroupVO(String group_no){
		LunboGroupVO  vo = new LunboGroupVO(group_no, "空的轮播图");
		List<LunboVO> list = new ArrayList<>();
		list.add(getEmptyLunboVO());
		list.add(new LunboVO(null, "", null));
		list.add(getEmptyLunboVO());
		list.add(getEmptyLunboVO());
		vo.lunbos = list;
		return vo;
	}
	public LunboVO getEmptyLunboVO(){
		return new LunboVO( "files/1.jpg", "", "https://www.baidu.com/");
	}
	public void editLunboPage(String group_no, int id, Optional<String>desc, Optional<String> img_url_cdn, Optional<String> link_url,
			String img_url) {
		LunboGroupVO lunbo = getLunboGroupNotNull(group_no);
		LunboVO vo = lunbo.lunbos.get(id);
		if(!Util.isEmpty(img_url)){
			vo.img_url = img_url;
		}
		if(img_url_cdn.isPresent()){
			vo.img_url_cdn = img_url_cdn.get();
		}
		if(desc.isPresent()){
			vo.desc = desc.get();
		}
		if(link_url.isPresent()){
			vo.link_url = link_url.get();
		}
		resetLunboGroup(lunbo);
	}
	public void resetLunboGroup(LunboGroupVO lunbo) {
		hset(LUNBO_HASH_KEY, lunbo.group_no, lunbo.toJSONString());
	}

	public void createLunboConfig(String group_no, String desc) {
		LunboGroupVO cfVO = new LunboGroupVO(group_no, desc);
		setLunboGroupIfAbsent(cfVO);
		LogCore.BASE.info("createLunboCf result ={}", hget(LUNBO_HASH_KEY, group_no));
	}

	public void setLunboGroupIfAbsent(LunboGroupVO cfVO) {
		if(null == cfVO){
			LogCore.BASE.warn("LunboGroupVO is null!");
		}
		hsetnx(LUNBO_HASH_KEY, cfVO.group_no, cfVO.toJSONString());
	}

	public void setLunboGroup(LunboGroupVO cfVO) {
		if(null == cfVO){
			LogCore.BASE.warn("LunboGroupVO is null!");
		}
		hset(LUNBO_HASH_KEY, cfVO.group_no, cfVO.toJSONString());
	}

	public void addLunboEmptyPage(String group_no) {
		LunboGroupVO cfVO = getLunboGroupVO(group_no);
		if(null == cfVO){
			return;
		}
		cfVO.lunbos.add(new LunboVO());
		setLunboGroup(cfVO);
	}

	public void deleteLunboConfig(String group_no) {
		hdel(LUNBO_HASH_KEY, group_no);
	}

	public void upLunboEmptyPage(String group_no, Integer ordinal) {
		LunboGroupVO cfVO = getLunboGroupVO(group_no);
		if(null == cfVO){
			return;
		}
		cfVO.upLunboVO(ordinal);
		setLunboGroup(cfVO);
	}

	public void deleteLunboPage(String group_no, Integer ordinal) {
		LunboGroupVO cfVO = getLunboGroupVO(group_no);
		if(null == cfVO){
			return;
		}
		cfVO.deleteLunboPage(ordinal);
		setLunboGroup(cfVO);
	}
}
