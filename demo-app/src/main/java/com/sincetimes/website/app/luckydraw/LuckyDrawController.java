package com.sincetimes.website.app.luckydraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sincetimes.website.core.common.support.DataResult;
import com.sincetimes.website.core.common.support.JSONBuilder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Param;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.controller.ControllerInterface;

/**
 * 大转盘 TODO:设置奖品信息过期时间为半年
 */
@RestController
@Order(value = 5)
public class LuckyDrawController implements ControllerInterface {
	// 领取标志,激活码,0被抢光,1第一次领取，2已经领取
	@RequestMapping("/get_lucky_draw")
	Object code(@RequestParam Optional<String> sn, @RequestParam Optional<String> who,
			@RequestParam Optional<Integer> count, HttpServletRequest req, HttpServletResponse resp) {

		if (!sn.isPresent()) {
			return new DataResult(0, "sn 参数不能为空！");
		}
		if (!who.isPresent()) {
			return new DataResult(0, "who 参数不能为空！");
		}
		
		return LuckyDrawManager.inst().draw(sn.get(), who.get(), count);
	}

	/**
	 * 获取单个转盘的所有奖励列表
	 * @param sn
	 * @param who
	 * @return
	 */
	@RequestMapping("/get_lucky_draw_result")
	Object get_lucky_draw_result(@RequestParam Optional<String> sn, @RequestParam Optional<String> who) {
		if (!sn.isPresent()) {
			return new DataResult(0, "sn empty！");
		}
		if (!who.isPresent()) {
			return new DataResult(0, "who empty！");
		}
		Object obj = LuckyDrawManager.inst().getLuckyDrawAllResultByCfSn(who.get(), sn.get());
		return new DataResult(1, obj);
	}

	/**
	 * 按照时间排好序
	 * 
	 * @param sn
	 *            多个转盘信息以两个 _即 __相连
	 * @param who
	 * @param count
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/get_lucky_draw_result_by_sns")
	Object get_lucky_draw_result_by_sns(@RequestParam Optional<String> sn, @RequestParam Optional<String> who) {
		if (!sn.isPresent()) {
			return new DataResult(0, "sn 参数为空！");
		}
		if (!who.isPresent()) {
			return new DataResult(0, "who 参数为空！");
		}
		List<LuckyDrawIResultVO> list = new ArrayList<>();
		String[] _sns = sn.get().split("__");
		LogCore.BASE.info("sns={}", Arrays.toString(_sns));
		for (String _sn : _sns) {
			List<LuckyDrawIResultVO> _list = LuckyDrawManager.inst().getLuckyDrawAllResultByCfSn(who.get(), _sn);
			if (!Util.isEmpty(_list)) {
				list.addAll(_list);
			}
		}
		list.sort((a, b) -> Long.compare(a.time, b.time));
		return new DataResult(1, list);
	}

	/** 本轮(比如如果是每周刷新就是当前周的奖励)获取单个转盘的所有奖励列表 */
	@RequestMapping("/get_lucky_draw_result_current_turn")
	Object get_lucky_draw_result_current_turn(@RequestParam Optional<String> sn, @RequestParam Optional<String> who) {
		if (!sn.isPresent()) {
			return new DataResult(0, "sn empty！");
		}
		if (!who.isPresent()) {
			return new DataResult(0, "who empty！");
		}
		Object obj = LuckyDrawManager.inst().getLuckyDrawAllResultByCfSn(who.get(), sn.get());
		return new DataResult(1, obj);
	}

	@RequestMapping("/if_lucky_draw_fit")
	public Object get_code_available(@RequestParam Optional<String> sn) {
		if (sn.isPresent()) {
			Param rst = LuckyDrawManager.inst().get_code_available(sn.get());
			return new JSONObject(rst.asMap());
		}
		return LuckyDrawManager.inst().getAllLuckyDrawConfs();
	}

	@RequestMapping("/get_lucky_draw_info")
	Object get_lucky_draw_info(@RequestParam Optional<String> sn, @RequestParam Optional<String> who,
			HttpServletRequest req, HttpServletResponse resp) {
		LuckyDrawConfVO vo = LuckyDrawManager.inst().getCodeInfo(sn.get());

		JSONArray items = new JSONArray();
		for (LuckyDrawItemVO item : vo.shelfs) {
			JSONObject tmpObj = new JSONObject();
			tmpObj.put("goods_name", item.goods_name);
			tmpObj.put("goods_desc", item.goods_desc);
			tmpObj.put("pic_url", item.pic_url);
			items.add(tmpObj);
		}

		JSONObject json = JSONBuilder.create().append("name", vo.name).append("desc", vo.desc)
				.append("close_time", vo.close_time).append("open_time", vo.open_time).append("items", items)
				.buildJSON();
		return json;
	}

	@RequestMapping("/get_lucky_draw_info2")
	Object get_lucky_draw_info2(@RequestParam Optional<String> sn, @RequestParam Optional<String> who,
			HttpServletRequest req, HttpServletResponse resp) {
		allowAccess(resp);
		LuckyDrawConfVO vo = LuckyDrawManager.inst().getCodeInfo(sn.get());
		JSONArray items = new JSONArray();
		for (LuckyDrawItemVO item : vo.shelfs) {
			JSONObject tmpObj = new JSONObject();
			tmpObj.put("goods_name", item.goods_name);
			tmpObj.put("goods_desc", item.goods_desc);
			tmpObj.put("pic_url", item.pic_url);
			items.add(tmpObj);
		}

		JSONObject json = JSONBuilder.create().append("name", vo.name).append("desc", vo.desc)
				.append("close_time", vo.close_time).append("open_time", vo.open_time).append("items", items)
				.buildJSON();
		return json;
	}
}
