package com.sincetimes.website.app.code;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sincetimes.website.app.stats.StatsManager;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Param;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.redis.jedis.spring.JedisManagerBase;
/**
 * 激活码，激活码是一个hash，其中有一个字段指向list
 * 领取的激活码在很多个hash, 总个激活码名单存在一个zset
 */
@Component
public class CodeManager extends JedisManagerBase {
	
	public static final String CODE_HASH_KEY = "codes_keys";
	public static final String CODE_LIST_KEY_PREFIX ="codes_";//记录激活码队列的名字的前缀
	public static final String CODE_RMVD_LIST_KEY_PREFIX ="codes_rmvd_";//记录激活码队列的名字的前缀
	
	public static CodeManager inst() {
		return ManagerBase.inst(CodeManager.class);
	}
	
	public String makeCodeKey(String sn) {
		String code_key= CODE_LIST_KEY_PREFIX.concat(sn);
		return code_key;
	}
	public String makeRemovedKey(String sn) {
		String code_key= CODE_RMVD_LIST_KEY_PREFIX.concat(sn);
		return code_key;
	}
	
	// 弹出一个激活码,并从持久化的列表中删除
	public String getAndRemoveCode(String sn, String who) {
		String code_key = makeCodeKey(sn);
		String rmvd_key = makeRemovedKey(sn);
		String code = rpoplpush(code_key, rmvd_key);
		// if (Util.isEmpty(code)) {
		StatsManager.inst().countByDay(StatsManager.STATS_TG_code+code_key);
		// }
		LogCore.BASE.info("{} get {} jihuoma:{}", who, code_key, code);
		return code;
	}

	// 往list里存入激活码
	private long putCodes(String sn, String... value) {
		String code_key = makeCodeKey(sn);
		return rpush(code_key, value);
	}

	// 清除激活码
	public long cleanCodes(String sn) {
		String code_key = makeCodeKey(sn);
		return del(code_key);
	}
	//领取剩余激活码数量
	public int getLeftCodesNum(String sn){
		String code_key = makeCodeKey(sn);
		return (int) llen(code_key);
	}
	//已经领取剩余激活码数量
	public int getRemovedCodesNum(String sn){
		String rmvd_key = makeRemovedKey(sn);
		return (int) llen(rmvd_key);
	}
	public void addCodeInfo(String sn, String create_by, String code_name, String code_desc,byte fresh_getType, String open_time, String close_time, List<String> codes) {
		long openTime, closeTime;
		try {
			openTime = TimeTool.parseAndGetTime(open_time, "yyyy-MM-dd-HH:mm:ss");
			closeTime = TimeTool.parseAndGetTime(close_time, "yyyy-MM-dd-HH:mm:ss");
		} catch (ParseException e) {
			LogCore.BASE.error("error time parameter:open_time={},close_time={}", open_time);
			closeTime = openTime = System.currentTimeMillis();
		}
		if(!Util.isEmpty(codes)){
			putCodes(sn, codes.toArray(new String[]{}));
		}
		CodeInfoVO vo = new CodeInfoVO(sn, create_by, code_name, code_desc,fresh_getType,  openTime, closeTime);
		hsetnx(CODE_HASH_KEY, vo.sn, vo.toJSONString());
		LogCore.BASE.info("add_code result ={}", hget(CODE_HASH_KEY, vo.sn));
	}
	public void editCodeInfo(String sn, String open_time, String close_time, List<String> codes) {
		long openTime, closeTime;
		try {
			openTime = TimeTool.parseAndGetTime(open_time, "yyyy-MM-dd-HH:mm:ss");
			closeTime = TimeTool.parseAndGetTime(close_time, "yyyy-MM-dd-HH:mm:ss");
		} catch (ParseException e) {
			LogCore.BASE.error("error time parameter:open_time={},close_time={}", open_time);
			closeTime = openTime = System.currentTimeMillis();
		}
		if(!Util.isEmpty(codes)){
			putCodes(sn, codes.toArray(new String[]{}));
		}
		CodeInfoVO vo = getCodeInfo(sn);
		vo.open_time = openTime;
		vo.close_time = closeTime;
		hset(CODE_HASH_KEY, vo.sn, vo.toJSONString());
		LogCore.BASE.info("exit_code result ={}", hget(CODE_HASH_KEY, vo.sn));
	}
	
	long deleteCodeInfo(String sn){
		String code_key = makeCodeKey(sn);
		String rmvd_key = makeRemovedKey(sn);
		del(code_key);
		del(rmvd_key);
		return hdel(CODE_HASH_KEY, sn);
	}
	
	void setCodeInfo(CodeInfoVO vo){
		hset(CODE_HASH_KEY, vo.sn, vo.toJSONString());
	}
	
	CodeInfoVO getCodeInfo(String sn){
		return CodeInfoVO.parseObject(hget(CODE_HASH_KEY, sn));
	}
	List<CodeInfoVO> getAllCodeInfo(){
		LogCore.BASE.info("all code cf:{}", hgetall(CODE_HASH_KEY));
		return hgetall(CODE_HASH_KEY).values().stream().map(CodeInfoVO::parseObject).map(CodeInfoVO::init).collect(Collectors.toList());
	}
	/* ******客户端接口*********/
	/**
	 * @param sn
	 * @param who
	 * @return 领取标志,激活码,0被抢光,1第一次领取，2已经领取,-1时间没到,-2时间过了,-3这个激活码地址不存在
	 */
	public Object[] getOneCode(String sn, String who){
		Param avail_rst = get_code_available(sn);
		int available = avail_rst.get("result");
		if(1 != available){
			return new Object[]{available, null};
		}
		CodeInfoVO codeCf= getCodeInfo(sn);
		String rsv_tag = "rsv" + codeCf.sn + codeCf.fresh_type.getNowTimeTag();//领取的关键字
		
		String user_code = hget(who, rsv_tag);
		if(!Util.isEmpty(user_code)){
			return new Object[]{2, user_code};
		}
		String code_key = makeCodeKey(sn);
		String rmvd_key = makeRemovedKey(sn);
		String code = rpoplpush(code_key, rmvd_key);
		if (Util.isEmpty(code)) {
			return new Object[]{0, null};
		}
		hset(who, rsv_tag, code);
		hset(who, rsv_tag+"time", System.currentTimeMillis()+"");
		zadd("code_user_set", 0, who);
//		StatsManager.inst().countByDay(StatsManager.STATS_TG_code);//记录激活码的每日领取
		return new Object[]{1, code};
	}
	/** 激活码是否能领取 */
	public Param get_code_available(String sn) {
		CodeInfoVO codeCf = getCodeInfo(sn);
		if(null == codeCf){
			return new Param("result", -3);//不存在
		}
		Param result = new Param("open_time", codeCf.open_time, "close_time", codeCf.close_time);
		long now = System.currentTimeMillis();
		if(codeCf.open_time > now){//时间没到
			return result.put("result", -1);
		}
		if(codeCf.close_time < now){//时间过了
			return result.put("result", -2);

		}
		return result.put("result", 1);
	}
	@Override
	public void init() {
		
	}
}
