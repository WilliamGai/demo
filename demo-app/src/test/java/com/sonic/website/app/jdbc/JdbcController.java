package com.sonic.website.app.jdbc;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonic.website.core.common.support.LogCore;

/**
 * 测试,可以用来将mysql的数据转移到nosql
 */
@Controller
public class JdbcController{

	@Autowired
	private JdbcService jdbcService;
	
	@RequestMapping("    estjdbc")
	@ResponseBody
	public Object testjdbc() {
		return jdbcService.getStockValue("days");
	}
	
	public Object initRedis() {
		new Thread(() -> {
			StopWatch stopWatch = new StopWatch("initRedis");
			stopWatch.start("jdbc");
			Map<String, String> map = jdbcService.getAllDataValueMapByBytes();
			stopWatch.stop();
			stopWatch.start("redis");
			map.forEach((k, v) -> {
//				JSONObject json = JSONObject.parseObject(v);
//				String[] ss = {json.getString("a"),json.getString("b")};
			});
			stopWatch.stop();
			LogCore.BASE.info("map'size{},\n{}", map.size(), stopWatch.prettyPrint());
		}).start();
		return "just return";
	}
}
