package com.sincetimes.website.app.blog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sincetimes.website.app.file.FileManager;
import com.sincetimes.website.app.security.interfaces.SecureControllerInterface;
import com.sincetimes.website.core.common.core.SysException;
import com.sincetimes.website.core.common.core.Utils;
import com.sincetimes.website.core.common.port.PortInstance;
import com.sincetimes.website.core.common.support.HTMLFilter;
import com.sincetimes.website.core.common.support.HttpUtil;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.spring.interfaces.ControllerInterface;
import com.sincetimes.website.core.spring.manger.SpringManager;
import com.sincetimes.website.redis.jedis.excample.JedisServiceDemo;
@Controller
@Order(value = 5)
@RequestMapping("/mg")
public class AudioController implements SecureControllerInterface, ControllerInterface{

	@RequestMapping("/audio")
	public Object audio(Model model, HttpServletRequest req) {
		setUser(model, req);
		ArrayList<?> list = new ArrayList<>();
		model.addAttribute("list", list);
		return "audio";
	}
	@RequestMapping("/add_audio")
	void add_audio(@RequestParam Optional<String> id,  StandardMultipartHttpServletRequest freq, HttpServletResponse resp) throws IOException {
		FileManager.inst().uploadFileSimple(freq, "/upload/audio", Function.identity());
		resp.sendRedirect("audio");
	}
	
	@ResponseBody
	@RequestMapping("/ad")
	public Object aa() {
		return "audio";
	}
}
