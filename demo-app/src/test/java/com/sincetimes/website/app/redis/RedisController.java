package com.sincetimes.website.app.redis;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sincetimes.website.app.redis.RedisCacheService;
import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.core.common.support.TimeUtil;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.interfaces.AccessSupport;
@Order(value = 4)
@RestController
public class RedisController implements AccessSupport {

	@Autowired
	RedisCacheService redisCacheService;

	/**
	 * 测试延时响应
	 * @throws InterruptedException 
	 */
	@RequestMapping("/time")
	public Object time(long time, HttpServletRequest req) throws InterruptedException {
		String beginTime = TimeUtil.getLocalTime();
		Thread.sleep(time);
		String endTime = TimeUtil.getLocalTime();
		return Util.format("ok,starttime={},endtime={}", beginTime, endTime);
	}
	
	@RequestMapping("/test")
	public Object test(String name, String password, HttpServletRequest req) {
		String _name = Objects.toString(name, "testName");
		String _psw = Objects.toString(password, "testPsw");
		UserVO o = redisCacheService.getUser(_name, _psw);
//		Integer i = jdbcTemplate.queryForObject("select value from t_value where name='1'", int.class);
		String sid = req.getSession().getId();
		return   System.getProperty("user.dir") + o+",sessionid="+sid;
	}

	@RequestMapping("/redis/get")
	public UserVO getUser(String name, String password) {
		return redisCacheService.getUser(name, password);
	}

	@RequestMapping("/redis/clear")
	public Object clearUser(String name, String no) {
		redisCacheService.clearUser(no, name);
		return "clear";
	}

	@RequestMapping("/redis/put")
	public Object putUser(String name, String no) {
		String _name = Objects.toString(name, "testName");
		String _no = Objects.toString(no, "testNo");
		return redisCacheService.putUser(_no, _name);
	}
}
