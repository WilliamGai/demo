package com.sonic.website.app.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.redis.StepMillisWatch;
import com.sonic.website.redis.jedis.annoation.RedisDataBaseSelect;
import com.sonic.website.redis.jedis.spring.JedisManagerBase;

/**
 * 测试各个搜索性能
 */
@RedisDataBaseSelect(value = 13)
public class SearchManager extends JedisManagerBase {

    public static final String CODE_HASH_KEY = "codes_keys";
    public static final String CODE_LIST_KEY_PREFIX = "codes_";// 记录激活码队列的名字的前缀
    public static final String CODE_RMVD_LIST_KEY_PREFIX = "codes_rmvd_";// 记录激活码队列的名字的前缀
    private static Map<String, List<String>> MAP = new ConcurrentHashMap<>();

    @Value("${search_init:false}")
    private boolean search_init;

    public static SearchManager inst() {
    return ManagerBase.inst(SearchManager.class);
    }

    public String makeCodeKey(String sn) {
    String code_key = CODE_LIST_KEY_PREFIX.concat(sn);
    return code_key;
    }

    // 往list里存入激活码
    private long putCodes(String sn, String... value) {
    String code_key = makeCodeKey(sn);
    MAP.computeIfAbsent(sn, (k) -> new ArrayList<>()).addAll(Arrays.asList(value));
    return rpush(code_key, value);
    }

    // 清除激活码
    public long cleanCodes(String sn) {
    String code_key = makeCodeKey(sn);
    MAP.remove(sn);
    return del(code_key);
    }

    // 领取剩余激活码数量
    public int getLeftCodesNum(String sn) {
    String code_key = makeCodeKey(sn);
    return (int) llen(code_key);
    }

    // 已经领取剩余激活码数量
    public int getRemovedCodesNum(String sn) {
    List<String> list = MAP.get(sn);
    return Util.size(list);
    }

    public void addCodeInfo(String sn, String create_by, String code_name, String code_desc, List<String> codes,
        int threads) {
    if (!Util.isEmpty(codes)) {
        putCodes(sn, codes.toArray(new String[] {}));
    }
    SearchInfoVO vo = new SearchInfoVO(sn, create_by, code_name, code_desc, threads);
    hsetnx(CODE_HASH_KEY, vo.sn, vo.toJSONString());
    LogCore.BASE.info("add_code result ={}", hget(CODE_HASH_KEY, vo.sn));
    }

    public void editCodeInfo(String sn, List<String> codes, Optional<Integer> threds) {
    if (!Util.isEmpty(codes)) {
        putCodes(sn, codes.toArray(new String[] {}));
    }
    SearchInfoVO vo = getCodeInfo(sn);
    vo.threds = threds.orElse(1);
    setCodeInfo(vo);
    LogCore.BASE.info("exit_code result ={}", hget(CODE_HASH_KEY, vo.sn));
    }

    long deleteCodeInfo(String sn) {
    String code_key = makeCodeKey(sn);
    del(code_key);
    MAP.remove(sn);
    return hdel(CODE_HASH_KEY, sn);
    }

    void setCodeInfo(SearchInfoVO vo) {
    hset(CODE_HASH_KEY, vo.sn, vo.toJSONString());
    }

    SearchInfoVO getCodeInfo(String sn) {
    return SearchInfoVO.parseObject(hget(CODE_HASH_KEY, sn));
    }

    List<SearchInfoVO> getAllCodeInfo() {
    LogCore.BASE.info("all code cf:{}", hgetall(CODE_HASH_KEY));
    return hgetall(CODE_HASH_KEY).values().stream().map(SearchInfoVO::parseObject).map(this::initConfigVO)
        .collect(Collectors.toList());
    }

    public SearchInfoVO initConfigVO(SearchInfoVO vo) {
    vo.left_codes_num = getLeftCodesNum(vo.sn);
    vo.rmv_codes_num = getRemovedCodesNum(vo.sn);
    vo.create_time_str = TimeUtil.formatTime(vo.create_time, "yyyy-MM-dd-HH:mm:ss");
    return vo;
    }

    /* ******客户端接口 *********/
    /**
     * @param sn
     * @param who
     * @return 领取标志,激活码,0被抢光,1第一次领取，2已经领取,-1时间没到,-2时间过了,-3这个激活码地址不存在
     */

    // public Object[] getOneCode(String sn, String who){
    // Param avail_rst = get_code_available(sn);
    // int available = avail_rst.get("result");
    // if(1 != available){
    // return new Object[]{available, null};
    // }
    // SearchInfoVO codeCf= getCodeInfo(sn);
    //
    // String user_code = hget(who, rsv_tag);
    // if(!Util.isEmpty(user_code)){
    // return new Object[]{2, user_code};
    // }
    // String code_key = makeCodeKey(sn);
    // String rmvd_key = makeRemovedKey(sn);
    // String code = rpoplpush(code_key, rmvd_key);
    // if (Util.isEmpty(code)) {
    // return new Object[]{0, null};
    // }
    // hset(who, rsv_tag, code);
    // hset(who, rsv_tag+"time", System.currentTimeMillis()+"");
    // zadd("code_user_set", 0, who);
    //// StatsManager.inst().countByDay(StatsManager.STATS_TG_code);//记录激活码的每日领取
    // return new Object[]{1, code};
    // }
    /** 激活码是否能领取 */
    // public Param get_code_available(String sn) {
    // SearchInfoVO codeCf = getCodeInfo(sn);
    // if(null == codeCf){
    // return new Param("result", -3);//不存在
    // }
    // Param result = new Param("open_time", codeCf.open_time, "close_time",
    // codeCf.close_time);
    // long now = System.currentTimeMillis();
    // if(codeCf.open_time > now){//时间没到
    // return result.put("result", -1);
    // }
    // if(codeCf.close_time < now){//时间过了
    // return result.put("result", -2);
    //
    // }
    // return result.put("result", 1);
    // }
    @Override
    public void init() {
    if (!search_init) {
        return;
    }
    List<SearchInfoVO> list = getAllCodeInfo();
    if (Util.isEmpty(list)) {
        return;
    }
    list.forEach(vo -> {
        String code_key = makeCodeKey(vo.sn);
        List<String> words = lrange(code_key, 0, -1);
        if (Util.isEmpty(words)) {
        return;
        }
        MAP.put(vo.sn, words);
        LogCore.BASE.info("sn = {}, words.size={}", vo.sn, words.size());
        SearchToolnstance.inst(vo.sn).init(words);

    });
    }

    public Object[] testWordsSearch(String code_sn, List<String> codes, Optional<Integer> threds) {
    List<String> list = MAP.get(code_sn);
    StepMillisWatch sw = new StepMillisWatch();
    sw.reset();
    AtomicInteger count = new AtomicInteger(0);
    for (String s : list) {
        for (String w : codes) {
        if (s.contains(w)) {
            count.incrementAndGet();
        }
        ;
        }
    }
    return new Object[] { sw.interval(), count.get() };
    }
}
