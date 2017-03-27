package com.sincetimes.website.app.lunbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.redis.jedis.spring.JedisManagerBase;

@Component
public class MediaManager2 extends JedisManagerBase {
	
	private static final LunboGroupVO EMPTY_LUNBO_GROUP;
	public static final String[] LUNBO_NAMES = {"lunbo_1", "lunbo_2", "lunbo_3", "lunbo_4","lunbo_5","lunbo_6"};
	public Map<String, LunboGroupVO> LUNBO_MAP = new HashMap<>();
	static {
		EMPTY_LUNBO_GROUP = new LunboGroupVO("000","空");
		List<LunboVO> list = new ArrayList<>();
		list.add(new LunboVO("files/1.jpg", "", ""));
		list.add(new LunboVO("files/2.jpg", "", ""));
		list.add(new LunboVO("files/3.jpg", "", ""));
		list.add(new LunboVO("files/4.jpg", "", ""));
		EMPTY_LUNBO_GROUP.lunbos = list;
	}
	
	
	public static MediaManager2 inst() {
		return ManagerBase.inst(MediaManager2.class);
	}
	

	public LunboGroupVO getLunboGroupVO(String group_no){
		return LUNBO_MAP.get(group_no);
	}
	
	public LunboGroupVO getLunboGroupNotNull(String group_no){
		LunboGroupVO vo = LUNBO_MAP.get(group_no);
		return vo == null?getEmptyLunboGroupVO(group_no):vo;
	}
	
	public Map<String, LunboGroupVO> getAllLunboGroupVOs(){
		return LUNBO_MAP;
	}
	
	public LunboGroupVO getEmptyLunboGroupVO(){
		return EMPTY_LUNBO_GROUP;
	}
	
	@Override
	public void init() {
		loadLunbotu();
		
	}
	
	public void loadLunbotu() {
		Stream.of(LUNBO_NAMES).forEach(this::loadLunbo);
	}
	public void loadLunbo(String group_no) {
		String lunbo_str = get(group_no);
		LogCore.BASE.info("redis lunbo group infos =group_no{}, group_list= {}", group_no, lunbo_str);
		if(!Util.isEmpty(lunbo_str)){
			try {
				LunboGroupVO temp_vo = JSON.parseObject(lunbo_str, LunboGroupVO.class);
				LUNBO_MAP.put(group_no, temp_vo);
			} catch (Exception e) {
				LogCore.BASE.error("load lunbotu error", e);
				LUNBO_MAP.put(group_no, getEmptyLunboGroupVO(group_no));
			}
			return;
		}
		LUNBO_MAP.put(group_no, getEmptyLunboGroupVO(group_no));
	}
	public LunboGroupVO getEmptyLunboGroupVO(String group_no){
		LunboGroupVO  vo = new LunboGroupVO(group_no, "空的轮播图");
		List<LunboVO> list = new ArrayList<>();
		list.add(getEmptyLunboVO());
		list.add(new LunboVO("files/2.jpg", "", "https://www.baidu.com/"));
		list.add(getEmptyLunboVO());
		list.add(getEmptyLunboVO());
		vo.lunbos = list;
		return vo;
	}
	public LunboVO getEmptyLunboVO(){
		return new LunboVO( "files/1.jpg", "", "https://www.baidu.com/");
	}
	public void updateLunbo(String group_no, int id, Optional<String>desc, Optional<String> img_url_cdn, Optional<String> link_url,
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
		LUNBO_MAP.put(lunbo.group_no, lunbo);
		set(lunbo.group_no, JSON.toJSONString(lunbo));
	}
	public void resetLunboSize(String group_no, Integer size) {
		LunboGroupVO lunbo = getLunboGroupNotNull(group_no);
		List<LunboVO> lunbos = lunbo.lunbos;
		int diff = lunbos.size() - size;
		if(diff>0){
			while(diff-->0){
				lunbos.remove(lunbos.size()-1);
			}
		}
		else{
			while(diff++<0 ){
				lunbos.add(getEmptyLunboVO());
			}
		}
		resetLunboGroup(lunbo);
	}


	public void createLunboConfig(String string, String string2) {
		// TODO Auto-generated method stub
		
	}
}
