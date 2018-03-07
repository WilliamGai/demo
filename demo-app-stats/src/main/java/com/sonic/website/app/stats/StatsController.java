package com.sonic.website.app.stats;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sonic.website.core.common.support.DataVO;
import com.sonic.website.core.common.support.LogCore;


@Controller
@Order(value = 7)
@RequestMapping("/mg")
public class StatsController implements CommandLineRunner {

	@RequestMapping("/gclook")
	@ResponseBody
	String gclook(HttpServletRequest req, HttpServletResponse rsp) {
		Object _old_user = req.getAttribute("user");
		LogCore.BASE.info("gclook user:{}", _old_user);
		return System.getProperty("user.dir") + Runtime.getRuntime().freeMemory();
	}
	
	@RequestMapping("/all")
	String all(Model model) {
		List<DataVO> base_info_list = StatsManager.inst().getBaseInfos();
		List<DataVO> uri_list = StatsManager.inst().getUriStatsThisTime();
		model.addAttribute("base_info_list", base_info_list);
		model.addAttribute("uri_list", uri_list);
		return "all";
	}
	@RequestMapping("/clear_node_uri_stats")
	void clear_node_uri_stats(HttpServletResponse resp) throws IOException {
		StatsManager.inst().clearStatsUri();
		resp.sendRedirect("all");
	}

	@Override
	public void run(String... args) throws Exception {
		int orderValue = this.getClass().getAnnotation(Order.class).value();
		LogCore.BASE.info("{} init start! the order is {} !!! ", this.getClass().getName(), orderValue);
	}
}
