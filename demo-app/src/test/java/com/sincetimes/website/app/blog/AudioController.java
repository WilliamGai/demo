package com.sincetimes.website.app.blog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sincetimes.website.app.file.FileManager;
import com.sincetimes.website.app.security.interfaces.SecureAccessSupport;
@Controller
@Order(value = 5)
@RequestMapping("/mg")
public class AudioController implements SecureAccessSupport{

	private static final String UPLOAD_AUDIO_PATH = "/upload/audio";

	@RequestMapping("/audio")
	public Object audio(Model model,String filter, HttpServletRequest req) {
		String rootPath = getRootFilePath(req);
	
		File dir = new File(rootPath + UPLOAD_AUDIO_PATH);
		if(dir.isDirectory()){
			List<FileInfo> list = Stream.of(dir.listFiles()).map(FileInfo::new).collect(Collectors.toList());
			model.addAttribute("list", list);
		}
		model.addAttribute("path", UPLOAD_AUDIO_PATH);
		return "audio";
	}
	@RequestMapping("/add_audio")
	void add_audio(@RequestParam Optional<String> id,  StandardMultipartHttpServletRequest freq, HttpServletResponse resp) throws IOException {
		FileManager.inst().uploadFileSimple(freq, UPLOAD_AUDIO_PATH, Function.identity());
		resp.sendRedirect("audio");
	}
	
	@ResponseBody
	@RequestMapping("/ad")
	public Object aa() {
		return "audio";
	}
}
