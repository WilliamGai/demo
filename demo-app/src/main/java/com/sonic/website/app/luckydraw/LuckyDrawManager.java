package com.sonic.website.app.luckydraw;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sonic.website.app.stats.StatsManager;
import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.support.BiDataResult;
import com.sonic.website.core.common.support.DataResult;
import com.sonic.website.core.common.support.DrawUtil;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Param;
import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.redis.jedis.spring.JedisManagerBase;
/**
 * 大转盘等权重抽奖
 */
public class LuckyDrawManager extends JedisManagerBase {
    
    private static final String CODE_USER_SET_KEY = "code_user_set";
    public static final String LUCKY_DRAW_HASH_KEY = "lucky_draw_keys";
    public static final String LUCKY_DRAW_LIST_KEY_PREFIX ="lucky_draw_";//记录激活码队列的名字的前缀
    public static final String LUCKY_DRAW_RMVD_LIST_KEY_PREFIX ="lucky_draw_rmvd_";//记录激活码队列的名字的前缀
    
    public final Map<String, ConcurrentHashMap<Object, Integer>> STATS_MAP = new HashMap<String, ConcurrentHashMap<Object,Integer>>();

    public static LuckyDrawManager inst() {
        return ManagerBase.inst(LuckyDrawManager.class);
    }
    
    public String makeCodeKey(String sn) {
        return LUCKY_DRAW_LIST_KEY_PREFIX.concat(sn);
    }
    public String makeRemovedKey(String sn) {
        return LUCKY_DRAW_RMVD_LIST_KEY_PREFIX.concat(sn);
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
    public void addCodeInfo(String sn, String create_by, String code_name, String code_desc,byte fresh_getType, String open_time, String close_time,int limit_num) {
        long openTime, closeTime;
        try {
            openTime = TimeUtil.parseAndGetTime(open_time, "yyyy-MM-dd-HH:mm:ss");
            closeTime = TimeUtil.parseAndGetTime(close_time, "yyyy-MM-dd-HH:mm:ss");
        } catch (ParseException e) {
            LogCore.BASE.error("error time parameter:open_time={},close_time={}", open_time);
            closeTime = openTime = System.currentTimeMillis();
        }
        LuckyDrawConfVO vo = new LuckyDrawConfVO(sn, create_by, code_name, code_desc,fresh_getType,  openTime, closeTime, limit_num);
        hsetnx(LUCKY_DRAW_HASH_KEY, vo.sn, vo.toJSONString());
        LogCore.BASE.info("add_code result ={}", hget(LUCKY_DRAW_HASH_KEY, vo.sn));
    }
    public void editLuckyDrawConf(String sn, String open_time, String close_time, Optional<Integer> limit_num) {
        long openTime, closeTime;
        try {
            openTime = TimeUtil.parseAndGetTime(open_time, "yyyy-MM-dd-HH:mm:ss");
            closeTime = TimeUtil.parseAndGetTime(close_time, "yyyy-MM-dd-HH:mm:ss");
        } catch (ParseException e) {
            LogCore.BASE.error("error time parameter:open_time={},close_time={}", open_time);
            closeTime = openTime = System.currentTimeMillis();
        }
        LuckyDrawConfVO vo = getCodeInfo(sn);
        vo.open_time = openTime;
        vo.close_time = closeTime;
        limit_num.ifPresent(v->vo.limit_num=v);
        hset(LUCKY_DRAW_HASH_KEY, vo.sn, vo.toJSONString());
        LogCore.BASE.info("exit_code result ={}", hget(LUCKY_DRAW_HASH_KEY, vo.sn));
    }
    
    long deleteCodeInfo(String sn){
        LuckyDrawConfVO vo = getCodeInfo(sn);
        vo.shelfs.forEach((item) -> clearCodesListByItem(item.sn));
        return hdel(LUCKY_DRAW_HASH_KEY, sn);
    }
    void clearCodesListByItem(String sn){
        String code_key = makeCodeKey(sn);
        String rmvd_key = makeRemovedKey(sn);
        del(code_key);
        del(rmvd_key);
    }
    void setCodeInfo(LuckyDrawConfVO vo){
        hset(LUCKY_DRAW_HASH_KEY, vo.sn, vo.toJSONString());
    }
    
    LuckyDrawConfVO getCodeInfo(String sn){
        return LuckyDrawConfVO.parseObject(hget(LUCKY_DRAW_HASH_KEY, sn));
    }
    List<LuckyDrawConfVO> getAllLuckyDrawConfs(){
        LogCore.BASE.info("all lucky_draw cf:{}", hgetall(LUCKY_DRAW_HASH_KEY));
        return hgetall(LUCKY_DRAW_HASH_KEY).values().stream().map(LuckyDrawConfVO::parseObject).map(LuckyDrawConfVO::init).collect(Collectors.toList());
    }
    /* ******客户端接口*********/
    /**
     * 本次启动全局的抽奖统计 
     * @param sn
     * @param who
     * @param count
     * @return 领取标志,激活码,0被抢光,1第一次领取,2已经领取,3超过系统对每个抽奖周期的限制,-1时间没到,-2时间过了
     */
    public DataResult draw(String sn, String who, Optional<Integer> count){
        BiConsumer<Object, Object> drawResultConsumser = (goods_sn, ordinal)->{
            STATS_MAP.computeIfAbsent(sn,k->new ConcurrentHashMap<Object,Integer>());
            STATS_MAP.get(sn).merge(ordinal, 1, Integer::sum);
        };
        return draw(sn, who, drawResultConsumser, false, count);
    }
    /**
     * @param sn
     * @param who
     * @param consumer 抽奖获得的sn,ordinal的consumer
     * @param count
     * @return 领取标志,激活码,0被抢光,1第一次领取,2已经领取,3超过系统对每个抽奖周期的限制,-1时间没到,-2时间过了
     */
    public DataResult draw(String sn, String who, BiConsumer<Object, Object> consumer,boolean test, Optional<Integer> count){
        Param avail_rst = get_code_available(sn);
        int available = avail_rst.get("result");
        if(1 != available){
            return new DataResult(available);
        }
        LuckyDrawConfVO codeCf= getCodeInfo(sn);
        String rsv_tag = "rsv" + codeCf.sn + codeCf.fresh_type.getNowTimeTag();//领取的关键字
        
        Map<String, List<LuckyDrawIResultVO>> map = getLuckyDrawMap(who, codeCf.sn);
        List<LuckyDrawIResultVO> reslut_before = getLuckyDrawResultNotNull(rsv_tag, map);

        if(count.orElse(1) <=  reslut_before.size()){
            return new BiDataResult(2, "抽奖机会已用完", reslut_before);
        }
        if(codeCf.limit_num <=  reslut_before.size()){
            return new BiDataResult(3, "已经没有抽奖机会", reslut_before);
        }
        
        //TODO 添加条件过滤
//        Map<LuckyDrawItemVO, Integer> weightMap = codeCf.shelfs.stream().collect(Collectors.toMap(LuckyDrawItemVO::generateKey, LuckyDrawItemVO::generateWeight));
        //过滤掉全局互斥和单轮互斥 begin
        List<LuckyDrawIResultVO> result_all = getLuckyDrawResultAll(map);
        
        /**这两个函数是根据转盘sn获取互斥标记,如果这个转盘的奖品被删了则标记为0*/
        Function<LuckyDrawIResultVO,Integer> all_f = v->Util.toNullDefalut(codeCf.getGoods(v.sn), (t->t.global_sn), 0);
        Function<LuckyDrawIResultVO,Integer> before_f = v->Util.toNullDefalut(codeCf.getGoods(v.sn), (t->t.current_turn_sn), 0);
        
        Set<Integer> set_all = result_all.stream().map(all_f).collect(Collectors.toSet());
        Set<Integer> set_before = reslut_before.stream().map(before_f).collect(Collectors.toSet()); 
        
        set_all.remove(0);
        set_before.remove(0);
        if(LogCore.BASE.isDebugEnabled()){
            LogCore.BASE.debug("set_all={}", set_all);
            LogCore.BASE.debug("set_before={}", set_before);
            LogCore.BASE.debug("codeCf.shelfs={}", codeCf.shelfs);
            LogCore.BASE.debug("filter set_all result={}", codeCf.shelfs.stream()
                    .filter(vo->!set_all.contains(vo.global_sn)).collect(Collectors.toList()));
            LogCore.BASE.debug("filter set_before result={}", codeCf.shelfs.stream()
                    .filter(vo->!set_before.contains(vo.current_turn_sn)).collect(Collectors.toList()));
        }
        Map<String, Integer> weightMap = codeCf.shelfs.stream()
            .filter(vo->!set_all.contains(vo.global_sn))
            .filter(vo->!set_before.contains(vo.current_turn_sn))
            .collect(Collectors.toMap(LuckyDrawItemVO::mkKey, LuckyDrawItemVO::mkWeight));
        LogCore.BASE.debug("sn={}, who={}, weightmap={}", sn, who, weightMap);
        //过滤互斥end
        if(Util.isEmpty(weightMap)){
            return new BiDataResult(0, "已经被抢光了", reslut_before);
        }
        String item_sn = DrawUtil.getOne(weightMap);
        LuckyDrawItemVO item_get = codeCf.getGoods(item_sn);
        int ordinal = codeCf.shelfs.indexOf(item_get);
        String code_key = makeCodeKey(item_sn);
        String rmvd_key = makeRemovedKey(item_sn);
        String code = rpoplpush(code_key, rmvd_key);
        if (Util.isEmpty(code)) {
            return new BiDataResult(0, item_sn+"已经被抢光", reslut_before);
        }
        LuckyDrawIResultVO result = new LuckyDrawIResultVO(item_sn, ordinal, code, item_get.pic_url);
        reslut_before.add(result);
        map.put(rsv_tag, reslut_before);
        if(!test){
            setLuckyDrawReslut(who, codeCf.sn, map);
            LogCore.BASE.info("save draw result:who={}, codeCf.sn={}, item_sn={}, ordinal={}", who, codeCf.sn, item_sn, ordinal);
        }
        if(consumer != null){
            consumer.accept(item_sn, ordinal);//检测结果的函数指针
        }
//        StatsManager.inst().countByDay(StatsManager.STATS_TG_code);//记录激活码的每日领取
        return new BiDataResult(1, result, reslut_before);
    }

    public void setLuckyDrawReslut(String who, String cf_sn, Map<String, List<LuckyDrawIResultVO>> map) {
        String key = "dzp_"+cf_sn;
        hset(who, key, LuckyDrawIResultVO.toJSONString(map));
        zadd(CODE_USER_SET_KEY+key, 0, who);
    }
    public Set<String> getAllOpenidByLuckyDrawSn(String cf_sn){
        String key = "dzp_" + cf_sn;
        String setKey = CODE_USER_SET_KEY + key;
        Set<String> openidSet = zrange(setKey, 0, -1);
        LogCore.BASE.info("get luckydraw all openids ,cf_sn={}, setKey={}, openidSet.size={}", cf_sn, setKey, openidSet.size());
        return openidSet;
    }
    private List<LuckyDrawIResultVO> getLuckyDrawResultNotNull(String rsv_tag, Map<String, List<LuckyDrawIResultVO>> map) {
        List<LuckyDrawIResultVO> reslut_before = map.get(rsv_tag);
        if(Util.isEmpty(reslut_before)){
            return new ArrayList<LuckyDrawIResultVO>();
        }
        return reslut_before;
    }
    private List<LuckyDrawIResultVO> getLuckyDrawResultAll(Map<String, List<LuckyDrawIResultVO>> map) {
        List<LuckyDrawIResultVO> list = new ArrayList<>();
        map.values().stream().filter(Objects::nonNull).forEach(list::addAll);
        return list;
    }
    public Map<String, List<LuckyDrawIResultVO>> getLuckyDrawMap(String who, String cf_sn) {
        String json = hget(who, "dzp_"+cf_sn);
        Map<String, List<LuckyDrawIResultVO>> _map = LuckyDrawIResultVO.jsonToMap(json);
        return _map;
    }
    /** 获得所有天的抽奖, 客户端展示用 */
    public List<LuckyDrawIResultVO> getLuckyDrawAllResultByCfSn(String who, String cf_sn) {
        LuckyDrawConfVO codeCf= getCodeInfo(cf_sn);
        if(null == codeCf){
            return null;
        }
        String json = hget(who, "dzp_"+cf_sn);
        if(Util.isEmpty(json)){
            return null;
        }
        
        Map<String, List<LuckyDrawIResultVO>> map = LuckyDrawIResultVO.jsonToMap(json);
        
        return getLuckyDrawResultAll(map);
    }
    /**
     * 本轮奖品(今天,本周,本月等)
     */
    public List<LuckyDrawIResultVO> getLuckyDrawResultCurrentTurnByCfSn(String who, String cf_sn) {
        LuckyDrawConfVO codeCf= getCodeInfo(cf_sn);
        String rsv_tag = "rsv" + codeCf.sn + codeCf.fresh_type.getNowTimeTag();//领取的关键字
        Map<String, List<LuckyDrawIResultVO>> map = getLuckyDrawMap(who, codeCf.sn);
        return getLuckyDrawResultNotNull(rsv_tag, map);
    }
    
    /**
     * @param sn
     * @return
     */
    public Param get_code_available(String sn) {
        LuckyDrawConfVO codeCf = getCodeInfo(sn);
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

    /* 总是成功 */
    public void addGoods(String cf_sn, String picUrl, String goods_name, String goods_desc,int weight, List<String> codes, int current_turn_sn,int global_sn) {
        String new_sn = incr("goods_sns")+"";
        String codes_list_key = makeCodeKey(new_sn);
        if(!Util.isEmpty(codes)){
            rpush(codes_list_key, codes.toArray(new String[]{}));
        }
        LuckyDrawItemVO vo = new LuckyDrawItemVO(new_sn, goods_name, goods_desc, picUrl, weight, current_turn_sn, global_sn);
        LuckyDrawConfVO cf = getCodeInfo(cf_sn);
        cf.addGoods(vo);
        setCodeInfo(cf);
        LogCore.BASE.info("add_shop result ={}", hget(LUCKY_DRAW_HASH_KEY, cf.sn));
    }
    public void deleteItem(String sn, String item_sn){
        LuckyDrawConfVO conf = getCodeInfo(sn);
        LuckyDrawItemVO vo = conf.deleteGoods(item_sn);
        if(null != vo){
            clearCodesListByItem(item_sn);
        }
        setCodeInfo(conf);
    }
    public void deleteItemCodes(String sn, String item_sn){
        LuckyDrawConfVO conf = getCodeInfo(sn);
        LuckyDrawItemVO vo = conf.getGoods(item_sn);
        if(null != vo){
            clearCodesListByItem(item_sn);
        }
    }
    public void upGoods(String sn, String item_sn){
        LuckyDrawConfVO conf = getCodeInfo(sn);
        conf.upGoods(item_sn);
        setCodeInfo(conf);
    }
    public void editGoods(String sn, 
            String item_sn, 
            Optional<String> picUrl, 
            String goods_name, 
            String goods_desc, 
            Optional<Integer> weight, 
            List<String> codes,
            Optional<Integer> current_turn_sn, 
            Optional<Integer> global_sn) {
        
        String codes_list_key = makeCodeKey(item_sn);
        if(!Util.isEmpty(codes)){
            rpush(codes_list_key, codes.toArray(new String[]{}));
        }
        LogCore.BASE.info("edit_goods params, sn={}, item_sn={}, goods_name={}, goods_desc={}, weight={}", sn, item_sn, goods_name, goods_desc, weight);
        LuckyDrawConfVO conf = getCodeInfo(sn);
        LuckyDrawItemVO goods = conf.getGoods(item_sn);
        
        goods.goods_desc = goods_desc;
        
        picUrl.ifPresent(n->goods.pic_url=n);
        weight.ifPresent(n->goods.weight=n);
        current_turn_sn.ifPresent(n->goods.current_turn_sn = n);
        global_sn.ifPresent(n->goods.global_sn = n);
        goods.init();
        setCodeInfo(conf);
        LogCore.BASE.debug("edit_shop result ={}", hget(LUCKY_DRAW_HASH_KEY, sn));
    }
}
