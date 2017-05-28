package com.sincetimes.website.app.stats;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.core.common.support.DataVO;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.spring.interfaces.ControllerInterface;


@Controller
@Order(value = 7)
@RequestMapping("/mg")
public class StatsController implements ControllerInterface {

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
		List<DataVO> uri_db_list = StatsManager.inst().getUriStatsAllTime();
		List<DataVO> time_db_list = StatsManager.inst().getTimeStats();
		List<DataVO> constant_list = StatsManager.inst().getConstantStats();

		time_db_list.sort((x, y) -> {
			return x.name.compareTo(y.name);
		});

		model.addAttribute("base_info_list", base_info_list);
		model.addAttribute("uri_list", uri_list);
		model.addAttribute("uri_db_list", uri_db_list);
		model.addAttribute("time_db_list", time_db_list);
		model.addAttribute("constant_list", constant_list);
		return "all";
	}
	@RequestMapping("/clear_node_uri_stats")
	void clear_node_uri_stats(HttpServletResponse resp) throws IOException {
		StatsManager.inst().clearStatsUri();
		resp.sendRedirect("all");
	}

	@RequestMapping("/stats/{name}")
	@ResponseBody
	public String download(@PathVariable String name) {
		return StatsManager.inst().get(name);
	}
}
