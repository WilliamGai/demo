package com.sincetimes.website.app.code;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sincetimes.website.app.file.FileManager;
import com.sincetimes.website.app.file.function.FileConsumer;
import com.sincetimes.website.app.security.vo.UserVO;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.interfaces.AccessSupport;
/**
 * 激活码
 */
@RequestMapping("/mg/code")
@Controller
@Order(value = 5)
public class CodeMGController implements AccessSupport{
	@RequestMapping
	void pageTemplate(HttpServletRequest req, HttpServletResponse resp){
        redirectAppend(req, resp, "code");
	}
	@RequestMapping("/code")
	String  code(Model model, @RequestParam Optional<String> sn, HttpServletRequest req, HttpServletResponse resp){
		List<CodeInfoVO> code_infos = CodeManager.inst().getAllCodeInfo();
		Optional<CodeInfoVO> code_info;
		if(sn.isPresent()){
			code_info = code_infos.stream().filter(a->a.sn.equals(sn.get())).findFirst();
		}else{
			code_info = code_infos.stream().findFirst();
		}
		if(code_info.isPresent()){
			model.addAttribute("code_info", code_info.get());
			model.addAttribute("code_infos", code_infos);
		}else{
			CodeInfoVO vo = new CodeInfoVO();
			vo.name="例子演示";
			model.addAttribute("code_info", vo);
			model.addAttribute("code_infos", new CodeInfoVO[]{vo});
		}
		String user_name = getSessionAttrFncOrElse(req, "user", UserVO::getName, null);
		model.addAttribute("user_name", user_name);
		//微信部分
		if(sn.isPresent()){
			String titlekey = sn.get()+"titl_url";
			String imgkey = sn.get()+"img_url";
			String img_url = FileManager.inst().get(imgkey);
			String title = FileManager.inst().get(titlekey);
			if(!Util.isEmpty(img_url)){
				model.addAttribute("img_url", "data:image/jpeg;base64,"+img_url);
			}
			if(!Util.isEmpty(title)){
				model.addAttribute("title", title);
			}
		}
		return "jihuoma";
	}
	@RequestMapping("/add_code_cf")
	void  add_code(@RequestParam Optional<String> code_sn, 
			@RequestParam Optional<String> code_name, 
			@RequestParam Optional<String> code_desc, 
			@RequestParam Optional<Byte> fresh_type, 
			@RequestParam Optional<String> open_time, 
			@RequestParam Optional<String> close_time, 
			StandardMultipartHttpServletRequest req, 
			HttpServletResponse resp){
		
		List<String> codes = new ArrayList<>();
		FileConsumer consume = (m)-> codes.addAll(FileManager.inst().readFileLines(m));
		
		Map<String, FileConsumer> comsumeMap = new HashMap<>();
		comsumeMap.put("code_file", consume);
		FileManager.inst().handleMultiFile(comsumeMap, req);
		
		String create_by = getSessionAttrFncOrElse(req, "user", UserVO::getName, "unknown");
		CodeManager.inst().addCodeInfo(code_sn.get(), create_by, code_name.get(), code_desc.get(), fresh_type.get(), open_time.get(), close_time.get(), codes);
		
		redirect(resp, "code?sn="+code_sn.get());
	}
	/**
	 * 修改激活码配置，修改的时候不可以修改刷新类型
	 */
	@RequestMapping("/edit_code_cf")
	void  edit_code_cf(@RequestParam String code_sn, 
			@RequestParam Optional<String> open_time, 
			@RequestParam Optional<String> close_time, 
			StandardMultipartHttpServletRequest req, 
			HttpServletResponse resp){
		
		List<String> codes = new ArrayList<>();
		FileConsumer consume = (m)-> codes.addAll(FileManager.inst().readFileLines(m));
		
		Map<String, FileConsumer> comsumeMap = new HashMap<>();
		comsumeMap.put("code_file", consume);
		FileManager.inst().handleMultiFile(comsumeMap, req);
		
		CodeManager.inst().editCodeInfo(code_sn, open_time.get(), close_time.get(), codes);
		redirect(resp, "code?sn="+code_sn);
	}
	/**
	 * 删除激活码配置
	 */
	@RequestMapping("/delete_code_cf")
	void  delete_code_cf(@RequestParam Optional<String> sn, HttpServletResponse resp){
		sn.ifPresent(CodeManager.inst()::deleteCodeInfo);
		redirect(resp, "code");
	}
	@RequestMapping("/delete_code_list")
	void  delete_code_list(@RequestParam String sn, HttpServletResponse resp){
		CodeManager.inst().cleanCodes(sn);
		redirect(resp, "code?sn="+sn);
	}
}
