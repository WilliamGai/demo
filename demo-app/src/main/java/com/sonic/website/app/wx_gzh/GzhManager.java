package com.sonic.website.app.wx_gzh;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sonic.website.app.redis.RedisService;
import com.sonic.website.core.common.cache.MyCache;
import com.sonic.website.core.common.manager.ManagerBase;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.common.support.Util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * GuavaCache + redis
 */
public class GzhManager extends ManagerBase {

    public static final String SET_KEY = "user~keys";

    private static final String FIELD_NICKNAME = "nickname";
    private static final String FIELD_SCORE = "score";
    private static final String FIELD_HEADPIC = "headpic";
    private static final String FIELD_CREATE_TIME = "create_time";

    private static final String[] EMPTY_NICKNAME_PIC = new String[2];// {null,null}
    // 本地缓存
    public MyCache<String, String[]> userBiInfoCache = new MyCache<String, String[]>(50000, 4);

    @Autowired
    private RedisService redisService;

    public static GzhManager inst() {
        return ManagerBase.inst(GzhManager.class);
    }

    /* 优化项, 费时操作 */
    @Override
    public void init() {}

    public boolean existOpenid(String openid) {
        String key = getKey(openid);
        return redisService.exist(key);
    }

    /**
     * 获取用户信息,昵称,头像,分数等 外部调用
     * @param openid
     * @return {nickname, headpic, score}
     */
    public String[] getUserInfo(String openid) {
        String[] nickname_pic = getNickNamePicNotNull(openid);
        String key = getKey(openid);
        String score = Objects.toString(redisService.hget(key, FIELD_SCORE), "0");
        int _score = Integer.parseInt(score);
        return new String[] { nickname_pic[0], nickname_pic[1], _score + "" };
    }

    /**
     * 外部调用 设置头像和昵称<br>
     */
    public void setNickNameAndPic(String openid, String[] nickname_pic) {
        LogCore.BASE.info("set nickname and pic {},{},{}", openid, nickname_pic[0], nickname_pic[1]);
        userBiInfoCache.putValue(openid, nickname_pic, this::putUserBaseBiInfo2Db);
        // 防止清空排行榜,使用zincry by而不是 zadd
        String key = getKey(openid);
        redisService.zincrby(SET_KEY, 0, key);
    }

    /**
     * 获取用户的昵称和头像
     * @param openid
     * @return {nickname, headpic}可能为null<br>
     */
    public String[] getNickNamePic(String openid) {
        return userBiInfoCache.getValue(openid, this::getUserBaseBiInfoFromDb);
    }

    /**
     * 获取用户的昵称和头像 如果缓存以及缓存加载失败则返回一个{null,null}
     */
    public String[] getNickNamePicNotNull(String openid) {
        String[] nic_pic = userBiInfoCache.getValue(openid, this::getUserBaseBiInfoFromDb);
        return Util.isEmpty(nic_pic) ? EMPTY_NICKNAME_PIC : nic_pic;
    }

    /* 缓存调用,查找失败需要返回null */
    private String[] getUserBaseBiInfoFromDb(String openid) {
        String key = getKey(openid);
        String nickname = redisService.hget(key, FIELD_NICKNAME);
        if (Util.isEmpty(nickname)) {
            return null;
        }
        String headpic = redisService.hget(key, FIELD_HEADPIC);
        return new String[] { nickname, headpic };
    }

    /* 缓存调用 */
    private void putUserBaseBiInfo2Db(String openid, String[] nickname_pic) {
        String key = getKey(openid);
        redisService.hset(key, FIELD_NICKNAME, nickname_pic[0]);
        redisService.hset(key, FIELD_HEADPIC, nickname_pic[1]);
        redisService.hsetNX(key, FIELD_CREATE_TIME,
                TimeUtil.formatTime(System.currentTimeMillis(), "yyyy-MM-dd-HH:mm:ss"));
    }

    /**
     * 结果包含域<br>
     * 仅供Robot使用
     */
    public Set<String> getAllOpenidKeys() {
        return redisService.zrange(SET_KEY);
    }

    public Map<String, Double> getAllOpenidKeysWithScore() {
        Map<String, Double> map = new HashMap<>();
        redisService.zRangeWithScores(SET_KEY).forEach((k) -> {
            map.put(Util.getUtf(k.getValue()), k.getScore());
        });
        LogCore.BASE.info("map size={}", map.size());
        return map;
    }

    public Map<String, Double> getRankOpenidKeysWithScore(int num) {
        Map<String, Double> map = new HashMap<>();
        redisService.zRevRangeWithScores(SET_KEY, 0, num).forEach((k) -> {
            map.put(Util.getUtf(k.getValue()), k.getScore());
        });
        LogCore.BASE.info("zrevrangeWithScores map size={}", map.size());
        return map;
    }

    /*
     * 此方法除了供外部查询,其他地方禁止调用
     */
    public Map<String, String> getUserInfoAll(String openid) {
        String key = getKey(openid);
        return redisService.hgetall(key);
    }

    public String hgetUserInfo(String openid, String field) {
        String key = getKey(openid);
        return redisService.hget(key, field);
    }

    public static String getKey(String openid) {
        // return "user:".concat(openid);
        return new StringBuilder("user:").append(openid).toString();
    }

    /** getKey的逆运算 */
//    private static String getOpenid(String key) {
//        return key.substring(5);
//    }
    
    /**
     * 测试将
     * 初始化用 parallelStream将提高10%, 但是并发流使用jedis将会出现异常
     */
    @Test
    public ConcurrentHashMap<String, Integer> getScoresMap() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(poolConfig, "23.56.13.70", 6379, 0, "foobared", 13);

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        try (Jedis jedis = pool.getResource()) {
            Set<String> set = jedis.zrange(SET_KEY, 0, -1);
            LogCore.BASE.info("all openids num={}", set.size());
            set.forEach((k) -> {
                String score = jedis.hget(k, FIELD_SCORE);
                map.put(k, score == null ? 0 : Integer.parseInt(score));
            });
        } catch (Exception e) {
            LogCore.BASE.info("manager fatal err,", e);
        }
        pool.close();
        LogCore.BASE.info("map size={}", map.size());
        return map;
    }
}
