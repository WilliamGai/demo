package com.sincetimes.website.app.luckydraw;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sincetimes.website.app.file.FileManager;
import com.sincetimes.website.app.file.function.FileConsumer;
import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.app.wx_gzh.GzhManager;
import com.sincetimes.website.core.common.support.DataVO;
import com.sincetimes.website.core.common.support.JSONBuilder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.HttpHeadUtil;
import com.sincetimes.website.core.spring.interfaces.ControllerInterface;
/**
 * 激活码
 * TODO LRU缓存
 */
@RequestMapping("/mg")
@Controller
@Order(value = 5)
public class LuckyDrawMGController implements ControllerInterface{
	
	@RequestMapping("/lucky_draw")
	String  code(Model model, @RequestParam Optional<String> sn, HttpServletRequest req, HttpServletResponse resp){
		List<LuckyDrawConfVO> code_infos = LuckyDrawManager.inst().getAllLuckyDrawConfs();
		Optional<LuckyDrawConfVO> code_info;
		if(sn.isPresent()){
			code_info = code_infos.stream().filter(a->a.sn.equals(sn.get())).findFirst();
		}else{
			code_info = code_infos.stream().findFirst();
		}
		if(code_info.isPresent()){
			model.addAttribute("lucky_draw_info", code_info.get());
			model.addAttribute("lucky_draw_infos", code_infos);
		}else{
			LuckyDrawConfVO vo = new LuckyDrawConfVO();
			vo.name="例子演示";
			model.addAttribute("lucky_draw_info", vo);
			model.addAttribute("lucky_draw_infos", new LuckyDrawConfVO[]{vo});
		}
		String user_name = getSessionAttrFncOrElse(req, "user", UserVO::getName, null);
		model.addAttribute("user_name", user_name);
		model.addAttribute("type_list", LuckyDrawFreshType.values());
		return "lucky_draw";
	}
	@RequestMapping("/add_lucky_draw_cf")
	void  add_code(@RequestParam Optional<String> sn, 
			@RequestParam Optional<String> name, 
			@RequestParam Optional<String> desc, 
			@RequestParam Optional<Byte> fresh_type, 
			@RequestParam Optional<String> open_time, 
			@RequestParam Optional<String> close_time, 
			@RequestParam Optional<Integer> limit_num, 
			HttpServletRequest req, 
			HttpServletResponse resp){
		LogCore.BASE.info("params={}",HttpHeadUtil.getParamsMap(req));
		String create_by = getSessionAttrFncOrElse(req, "user", UserVO::getName, "unknown");
		LuckyDrawManager.inst().addCodeInfo(sn.get(), create_by, name.get(), desc.get(), fresh_type.get(), open_time.get(), close_time.get(), limit_num.get());
		redirect(resp, "lucky_draw?sn="+sn.get());
	}
	
	/**
	 * 修改大转盘配置，修改的时候不可以修改刷新类型
	 */
	@RequestMapping("/edit_lucky_draw_cf")
	void  edit_lucky_draw_cf(@RequestParam String sn, 
			@RequestParam Optional<String> open_time, 
			@RequestParam Optional<String> close_time,
			@RequestParam Optional<Integer> limit_num, 
			HttpServletRequest req, 
			HttpServletResponse resp){
		
		LuckyDrawManager.inst().editLuckyDrawConf(sn, open_time.get(), close_time.get(), limit_num);
		redirect(resp, "lucky_draw?sn="+sn);
	}
	
	/**
	 * 删除激活码配置
	 */
	@RequestMapping("/delete_lucky_draw_cf")
	void  delete_code_cf(@RequestParam Optional<String> sn, HttpServletResponse resp){
		sn.ifPresent(LuckyDrawManager.inst()::deleteCodeInfo);
		redirect(resp, "lucky_draw");
	}
	
	/**
	 * 删除商品
	 */
	@RequestMapping("/del_lucky_draw_item")
	@ResponseBody
	Object del_lucky_draw_item(HttpServletRequest req, HttpServletResponse resp, String sn, String item_sn){
		LuckyDrawManager.inst().deleteItem(sn, item_sn);
		return JSONBuilder.creatJsonString("result", 1);
	}
	/**
	 * 删除商品的激活码
	 */
	@RequestMapping("/delete_item_code_list")
	@ResponseBody
	Object delete_item_code_list(HttpServletRequest req, HttpServletResponse resp, String sn, String item_sn){
		LuckyDrawManager.inst().deleteItemCodes(sn, item_sn);
		return JSONBuilder.creatJsonString("result", 1);
	}
	/**
	 * 货架上新
	 */
	@RequestMapping("/add_lucky_draw_item")
	void add_goods(
			@RequestParam String sn, 
			@RequestParam String goods_name, 
			@RequestParam String goods_desc, 
			@RequestParam Optional<Integer> weight,  
			@RequestParam Optional<Integer> current_turn_sn, 
			@RequestParam Optional<Integer> global_sn, 
			StandardMultipartHttpServletRequest req, HttpServletResponse resp){
		String dirName = Objects.toString(req.getParameter("dir"), "image");
		String ymdPath = TimeTool.formatTime(System.currentTimeMillis(), "yyyy_MM_dd")+"/";
		String dirPath = "upload/";
		String rootPath = getRootFilePath(req);
		String filePath = dirPath + dirName+ "/"+ ymdPath;
		String saveUrl = req.getContextPath() + "/" + filePath;
		
		String realpath = rootPath + filePath;
		
		List<String> names = new ArrayList<>();
		
		FileConsumer consume = (m)->{
			FileManager.inst().save(realpath, m).ifPresent(f->names.add(f.getName()));
		};
		
		List<String> codes = new ArrayList<>();
		FileConsumer consume2 = (m)->{
			List<String> rst = FileManager.inst().readFileLines(m);
			codes.addAll(rst);
		};
		Map<String, FileConsumer> comsumeMap = new HashMap<>();
		comsumeMap.put("code_file", consume2);
		comsumeMap.put("file", consume);
		
		FileManager.inst().handleMultiFile(comsumeMap, req);
		String pic_url = null;
		if(!Util.isEmpty(names)){
			pic_url = saveUrl + names.get(0);
		}
		LuckyDrawManager.inst().addGoods(sn, pic_url, goods_name, goods_desc, weight.orElse(0), codes, current_turn_sn.orElse(0), global_sn.orElse(0));
		redirect(resp, "lucky_draw?sn=" + sn);
		LogCore.BASE.info("add goods file_url={},find codes anyone{}",names,codes.stream().findAny());
	}
	/**
	 * 修改
	 */
	@RequestMapping("/edit_lucky_draw_item")
	@ResponseBody
	void edit_goods(
			@RequestParam String sn, 
			@RequestParam String item_sn, 
			@RequestParam String goods_name, 
			@RequestParam String goods_desc, 
			@RequestParam Optional<Integer> weight, 
			@RequestParam Optional<Integer> current_turn_sn, 
			@RequestParam Optional<Integer> global_sn,
			StandardMultipartHttpServletRequest req, HttpServletResponse resp){
		LogCore.BASE.info("edit_lucky_draw_item weight={},req={}", weight, HttpHeadUtil.getParamsMap(req));
		String dirName = Objects.toString(req.getParameter("dir"), "image");
		String ymdPath = TimeTool.formatTime(System.currentTimeMillis(), "yyyy_MM_dd")+"/";
		String dirPath = "upload/";
		String rootPath = getRootFilePath(req);
		String filePath = dirPath + dirName+ "/"+ ymdPath;
		String saveUrl = req.getContextPath() + "/" + filePath;
		
		String realpath = rootPath + filePath;
		
		List<String> names = new ArrayList<>();
		
		Map<String, FileConsumer> comsumeMap = new HashMap<>();
		FileConsumer consume = (m)->{
			FileManager.inst().save(realpath, m).ifPresent(f->names.add(f.getName()));
		};
		List<String> codes = new ArrayList<>();
		FileConsumer consume2 = (m)->{
			List<String> rst = FileManager.inst().readFileLines(m);
			codes.addAll(rst);
		};
		
		comsumeMap.put("code_file", consume2);
		comsumeMap.put("file", consume);
		
		FileManager.inst().handleMultiFile(comsumeMap, req);
		String pic_url = null;
		if(!Util.isEmpty(names)){
			pic_url = saveUrl + names.get(0);
		}
		Optional<String> _pic_url = Optional.ofNullable(pic_url);
		LuckyDrawManager.inst().editGoods(sn, item_sn, _pic_url, goods_name, goods_desc, weight, codes, current_turn_sn, global_sn);
		LogCore.BASE.info("file_url={},pic_url={},find codes anyone{}",names,pic_url,codes.stream().findAny());
		redirect(resp, "lucky_draw?sn=" + sn);
	}
	/**
	 * 上移
	 */
	@RequestMapping("/up_lucky_draw_item")
	@ResponseBody
	Object up_goods(@RequestParam String sn, @RequestParam String item_sn, HttpServletRequest req, HttpServletResponse resp){
		LogCore.BASE.info("sn={}, item_sn={}", sn, item_sn);
		LuckyDrawManager.inst().upGoods(sn, item_sn);
		return JSONBuilder.creatJsonString("result", 1);
	}
	/**
	 * debug
	 */
	
	@RequestMapping("/lookdraw")
	@ResponseBody
	Object lookdraw(){
		LogCore.BASE.info("map={}", LuckyDrawManager.inst().STATS_MAP);
		return LuckyDrawManager.inst().STATS_MAP;
	}
	@RequestMapping("testdraw")
	String testdraw(Model model,
			@RequestParam String sn, 
			@RequestParam Optional<Integer> num){
		Map<Object, Integer> goods_sn_map = new HashMap<>();
		Map<Object, Integer> ordinal_map = new HashMap<>();
		BiConsumer<Object, Object> consumer = (goods_sn, ordinal)->{
			goods_sn_map.merge(goods_sn, 1, Integer::sum);
			ordinal_map.merge(ordinal, 1, Integer::sum);
		};
		int _num = num.orElse(100);
		while(_num-- >0){
			LuckyDrawManager.inst().draw(sn, "test"+_num, consumer, true, Optional.empty());
		}
		List<DataVO> sn_list = new ArrayList<>();
		List<DataVO> ordinal_list = new ArrayList<>();
		goods_sn_map.forEach((k, v)->sn_list.add(new DataVO(String.valueOf(k), v)));
		ordinal_map.forEach((k, v)->ordinal_list.add(new DataVO(String.valueOf(k), v)));
		model.addAttribute("title", "大转盘测试结果");
		model.addAttribute("tablename1", "物品sn结果");
		model.addAttribute("tablename2", "下标结果");
		model.addAttribute("colums1", new String[]{"sn","次数"});
		model.addAttribute("colums2",new String[]{"下标","次数"});
		model.addAttribute("list1", sn_list);
		model.addAttribute("list2", ordinal_list);
		return "bi_view";
	}
	/**
	 * 调试以及验证用
	 * @throws IOException 
	 */
	@RequestMapping("lucky_draw_debug/{sn}")
	String debug(Model model, @PathVariable  String sn) throws IOException{
		List<DataVO> result = new ArrayList<>();
		
		int count = 0;
		Set<String> openid_set = LuckyDrawManager.inst().getAllOpenidByLuckyDrawSn(sn);
		for (String openid : openid_set) {
			if(GzhManager.inst().existOpenid(openid)){
				count++;
			}
			List<LuckyDrawIResultVO> list = LuckyDrawManager.inst().getLuckyDrawAllResultByCfSn(openid, sn);
			if(!Util.isEmpty(list)){
				list.sort((a, b) -> Long.compare(a.time, b.time));
			}
			String nikcname_pic[] = GzhManager.inst().getNickNamePicNotNull(openid);
			result.add(new DataVO(nikcname_pic[0], nikcname_pic[1], openid, list));
		}

		result.add(new DataVO("总测试人数", null, openid_set.size()));
		result.add(new DataVO("经过服务号验证的人数",null,  count));
		model.addAttribute("title", "大转盘结果");
		model.addAttribute("tablename1", "物品sn结果");
		model.addAttribute("colums1", new String[]{"sn","奖品列表"});
		model.addAttribute("list1", result);
		return "bi_view_name_pic";
	}
	
	/** 无图片昵称 */
	@RequestMapping("lucky_draw_debug_simple/{sn}")
	String debug_simple(Model model, @PathVariable  String sn) throws IOException{
		List<DataVO> result = new ArrayList<>();
		int count = 0;
		Set<String> openid_set = LuckyDrawManager.inst().getAllOpenidByLuckyDrawSn(sn);
		for (String openid : openid_set) {
			if(GzhManager.inst().existOpenid(openid)){
				count++;
			}
			List<LuckyDrawIResultVO> list = LuckyDrawManager.inst().getLuckyDrawAllResultByCfSn(openid, sn);
			if(!Util.isEmpty(list)){
				list.sort((a, b) -> Long.compare(a.time, b.time));
			}
			result.add(new DataVO(openid, list));
		}

		result.add(new DataVO("总测试人数", openid_set.size()));
		result.add(new DataVO("经过服务号验证的人数", count));
		model.addAttribute("title", "大转盘结果");
		model.addAttribute("tablename1", "物品sn结果");
		model.addAttribute("colums1", new String[]{"sn","奖品列表"});
		model.addAttribute("list1", result);
		return "bi_view";
	}
	/**
	 * 调试用
	 * 查看某个激活码是那个转盘哪个物品的
	 */
	@RequestMapping("/lookdraw_debug")
	@ResponseBody
	Object lookdraw(String code){
		List<LuckyDrawConfVO> list = LuckyDrawManager.inst().getAllLuckyDrawConfs();
		for (LuckyDrawConfVO vo: list) {
			for(LuckyDrawItemVO ivo :vo.shelfs){
				String code_key = LuckyDrawManager.inst().makeCodeKey(ivo.sn);
				List<String> codes = LuckyDrawManager.inst().lrange(code_key, 0, -1);
				if(codes.contains(code)){
					return vo.name+vo.sn+"_avail_"+ivo.goods_name+ivo.sn;
				}
				String rmvd_key = LuckyDrawManager.inst().makeRemovedKey(ivo.sn);
				codes = LuckyDrawManager.inst().lrange(rmvd_key, 0, -1);
				if(codes.contains(code)){
					return vo.name+vo.sn+"_rmvd_"+ivo.goods_name+ivo.sn;
				}
			}
		}
		return LuckyDrawManager.inst().STATS_MAP;
	}
}
