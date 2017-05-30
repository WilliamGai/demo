package com.sincetimes.website.app.shop;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sincetimes.website.app.file.FileManager;
import com.sincetimes.website.app.file.function.FileConsumer;
import com.sincetimes.website.core.common.support.DataVO;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.HttpHeadUtil;
import com.sincetimes.website.core.spring.interfaces.AccessSupport;
/**
 * 积分兑换商城
 */
@RequestMapping("/mg/shop")
@Controller
@Order(value = 5)
public class ShopMGController implements AccessSupport{
	@RequestMapping
	void pageTemplate(HttpServletRequest req, HttpServletResponse resp){
		redirect(resp, req.getRequestURI()+"/point_shop_mg");
	}
	@RequestMapping("/point_shop_mg")
	String point_shop_mg(Model model, @RequestParam Optional<String> shop_sn){
		List<ShopVO> shop_list = ShopManager.inst().getAllShop();
		Optional<ShopVO> op_shop;
		ShopVO shop = new ShopVO();
		if(shop_sn.isPresent()){
			op_shop = shop_list.stream().filter(a->a.sn.equals(shop_sn.get())).findFirst();
		}else{
			op_shop = shop_list.stream().findFirst();
		}
		if(op_shop.isPresent()){
			shop = op_shop.get();
		}else{
		}
		
		model.addAttribute("shop_list", shop_list);
		model.addAttribute("shop", shop.init());
		return "point_shop";
	}
	/**
	 * 创建商店
	 * TODO：
	 * 提示成功和失败的状态
	 * 禁止覆盖旧的商店
	 */
	@RequestMapping("/add_shop")
	void add_shop(HttpServletResponse resp, String shop_sn, String shop_name, String shop_desc, String open_time){
		ShopManager.inst().addShop(shop_sn, shop_name, shop_desc, open_time);
		
		redirect(resp, "point_shop_mg?shop_sn=" + shop_sn);
	}
	@RequestMapping("/del_shop")
	void del_shop(HttpServletResponse resp, String shop_sn){
		ShopManager.inst().deleteShop(shop_sn);
		
		redirect(resp, "point_shop_mg");
	}
	/**
	 * 删除商品
	 */
	@RequestMapping("/del_goods")
	void del_goods(HttpServletRequest req, HttpServletResponse resp, String sn, String shop_sn){
		ShopManager.inst().deleteGoods(sn, shop_sn);
		forward(req, resp, "point_shop_mg");
	}
	/**
	 * 货架上新
	 */
	@RequestMapping("/add_goods")
	void add_goods(@RequestParam String shop_sn, @RequestParam String goods_name, @RequestParam String goods_desc, @RequestParam Optional<Integer> point_need, StandardMultipartHttpServletRequest req, HttpServletResponse resp){
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
		ShopManager.inst().addGoods(shop_sn, pic_url, goods_name, goods_desc, point_need.orElse(100), codes);
		redirect(resp, "point_shop_mg?shop_sn=" + shop_sn);
		LogCore.BASE.info("add goods file_url={},find codes anyone{}",names,codes.stream().findAny());
	}
	/**
	 * 修改
	 */
	@RequestMapping("/edit_goods")
	void edit_goods(@RequestParam String shop_sn, @RequestParam String sn, @RequestParam String goods_name, @RequestParam String goods_desc, @RequestParam Optional<Integer> point_need, StandardMultipartHttpServletRequest req, HttpServletResponse resp){
		LogCore.BASE.info("edit_goods point_need={},req={}", point_need, HttpHeadUtil.getParamsMap(req));
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
		ShopManager.inst().editGoods(shop_sn, sn, pic_url, goods_name, goods_desc, point_need, codes);
		redirect(resp, "point_shop_mg?shop_sn=" + shop_sn);
		LogCore.BASE.info("file_url={},pic_url={},find codes anyone{}",names,pic_url,codes.stream().findAny());
	}
	/**
	 * 上移
	 */
	@RequestMapping("/up_goods")
	void up_goods(@RequestParam String shop_sn, @RequestParam String sn, HttpServletRequest req, HttpServletResponse resp){
		LogCore.BASE.info("shop_sn={}, sn={}", shop_sn, sn);
		ShopManager.inst().upGoods(sn, shop_sn);
		redirect(resp, "point_shop_mg?shop_sn=" + shop_sn);
	}
	/**
	 * 查看积分榜和领奖情况,分页
	 * @param page_no 从0开始为第一页
	 */
	@RequestMapping("shop_debug_simple/{shop_sn}")
	String debug_simple(Model model, @PathVariable String shop_sn, @RequestParam Optional<Integer> page_no, @RequestParam Optional<Integer> page_size) throws IOException{
		//分页
		int _page_size = page_size.orElse(100);//每页大小
		int _page_no = page_no.orElse(0);
		int sum = ShopManager.inst().getScoreSetSize(shop_sn).intValue();//信息总条数
		int pages_num = (int) Math.ceil((double)sum/(double)_page_size);//页数
		int start = _page_no * _page_size;
		int end = start + _page_size -1;//注意下标
		List<DataVO> result = ShopManager.inst().rankList(shop_sn, start, end);
		
		List<Integer> page_nos = new ArrayList<Integer>();
		for(int i=0; i< pages_num; i++){
			page_nos.add(i);
		}
		model.addAttribute("page_nos", page_nos);
		model.addAttribute("sum", sum);//总文章数
		model.addAttribute("uri", "/mg/shop_debug_simple/"+shop_sn);
		model.addAttribute("pages_num", pages_num);//总页数
		model.addAttribute("page_no", page_no.orElse(0));//第几页
		model.addAttribute("page_size", _page_size);//每页大小
		//分页end
		model.addAttribute("title", "积分商城预览");
		model.addAttribute("tablename1", "积分商城排行榜");
		model.addAttribute("colums1", new String[]{"openid","名次","微信验证", "分数","领取奖励"});
		model.addAttribute("list1", result);
		return "bi_view5_pages";
	}
}
