package com.sonic.website.app.lunbo;
														
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sonic.website.app.file.FileManager;
import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;
import com.sonic.website.core.spring.interfaces.AccessSupport;
/**
 * 轮播图和视频等多媒体信息
 */
@Controller
@Order(value = 5)
@RequestMapping("/mg/lunbo")
public class MediaMGController implements AccessSupport {
	@RequestMapping
	void pageTemplate(HttpServletRequest req, HttpServletResponse resp){
		redirect(resp, req.getRequestURL()+"/upload_media");
	}
	@RequestMapping("/upload_media")
	String upload_media(Model model, @RequestParam Optional<String> group_no, @RequestParam Optional<Integer> num, HttpServletRequest req){
		List<LunboGroupVO> list = MediaManager.inst().getAllLunboGroupVO();
		List<String> infos = list.stream().map(LunboGroupVO::getInfo).collect(Collectors.toList());
		model.addAttribute("lunbo_nos", infos);
		String user_name = getSessionAttrFncOrElse(req, "user", UserVO::getName, null);
		model.addAttribute("user_name", user_name);
		
		LunboGroupVO lunbo = null;
		if(group_no.isPresent()){
			lunbo = MediaManager.inst().getLunboGroupVO(group_no.get());
			lunbo.checkUrl();
		}
		if(null == lunbo){
			lunbo = list.stream().findFirst().orElse(MediaManager.inst().getEmptyLunboGroupVO());
			lunbo.checkUrl();
		}
		model.addAttribute("lunbo", lunbo);
		LogCore.BASE.info("lunbo={}", lunbo);
		return "upload_media";
	}
	
	@RequestMapping("/edit_lunbo_empty_page")
	void upload_media_submit_lunbo(@RequestParam Optional<String>desc, @RequestParam Optional<String>img_url_cdn,  @RequestParam int id,@RequestParam String group_no, @RequestParam Optional<String> link_url, StandardMultipartHttpServletRequest freq, HttpServletResponse resp) throws IOException {
		LogCore.BASE.info("edit_lunbo_empty_page,desc={}, group_no={}, img_url_cdn={},id={},link_url={}",desc, group_no, img_url_cdn, id, link_url);
		String img_url = FileManager.inst().uploadFileSimple(freq, "lunbotu" + group_no);
		MediaManager.inst().editLunboPage(group_no, id, desc, img_url_cdn ,link_url, img_url);
		resp.sendRedirect("upload_media?group_no="+group_no);
	}
	/**
	 * 上传音视频
	 * @param id
	 * @param freq
	 * @param resp
	 * @throws IOException
	 */
	@RequestMapping("/upload_media_submit_video")
	void upload_media_submit_video(@RequestParam Optional<String> id,  StandardMultipartHttpServletRequest freq, HttpServletResponse resp) throws IOException {
		FileManager.inst().uploadFileSimple(freq, "/upload/video", Function.identity());
		resp.sendRedirect("upload_media");
	}
	/*创建一个轮播图*/
	@RequestMapping("/add_lunbo_cf")
	void add_lunbo_cf(@RequestParam Optional<String> group_no, @RequestParam Optional<String> desc, HttpServletResponse resp) throws IOException {
		if(group_no.isPresent() && desc.isPresent()){
			MediaManager.inst().createLunboConfig(group_no.get(), desc.get());
			resp.sendRedirect("upload_media?group_no="+group_no.get());
			return;
		}
		resp.sendRedirect("upload_media");
	}
	@RequestMapping("/add_lunbo_empty_page")
	void add_lunbo_empty_page(@RequestParam Optional<String> group_no, HttpServletResponse resp) throws IOException {
		if(group_no.isPresent()){
			MediaManager.inst().addLunboEmptyPage(group_no.get());
			resp.sendRedirect("upload_media?group_no="+group_no.get());
			return;
		}
		resp.sendRedirect("upload_media");
	}
	
	@RequestMapping("/up_lunbo_page")
	void up_lunbo_page(@RequestParam String group_no, @RequestParam Integer ordinal, HttpServletResponse resp) throws IOException {
		MediaManager.inst().upLunboEmptyPage(group_no, ordinal);
		resp.sendRedirect("upload_media?group_no="+group_no);
	}
	
	@RequestMapping("/delete_lunbo_cf")
	void delete_lunbo_cf(@RequestParam Optional<String> group_no, HttpServletResponse resp) throws IOException {
		if(group_no.isPresent()){
			MediaManager.inst().deleteLunboConfig(group_no.get());
			resp.sendRedirect("upload_media?group_no="+group_no.get());
			return;
		}
		resp.sendRedirect("upload_media");
	}
	@RequestMapping("/delete_lunbo_page")
	void delete_lunbo_page(@RequestParam Optional<String> group_no, @RequestParam Integer ordinal, HttpServletResponse resp){
		if(Util.isEmpty(group_no.orElse(""))){
			redirect(resp, "upload_media");
			return;
		}
		MediaManager.inst().deleteLunboPage(group_no.get(), ordinal);
		redirect(resp, "upload_media?group_no="+group_no.get());
	}
}
