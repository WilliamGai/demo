package com.sonic.website.app.blog;

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
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.StandardServletEnvironment;

import com.sonic.website.core.common.core.SysException;
import com.sonic.website.core.common.core.Utils;
import com.sonic.website.core.common.port.PortInstance;
import com.sonic.website.core.common.support.HTMLFilter;
import com.sonic.website.core.common.support.HttpUtil;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.spring.interfaces.AccessSupport;
import com.sonic.website.core.spring.manger.SpringManager;
import com.sonic.website.redis.jedis.excample.JedisServiceDemo;
@Controller
@Order(value = 5)
public class BlogController implements AccessSupport{
	public static AtomicInteger count= new AtomicInteger();
	
	@Autowired
	JedisServiceDemo jedisService;
	
	@RequestMapping("/index")
	private String index(Model model) {
		model.addAttribute("data", "data_test");
		return "index";
	}
	
	@ResponseBody
	@RequestMapping("/read_html_res")
	public Object read_html_res(String text) {
		String rst = HttpUtil.sendGet(text);
		return HTMLFilter.printHtml(rst);
	}
	/**
	 * 这个方法是读取跟jar包平级的文件
	 */
	@ResponseBody
	@RequestMapping("/a")
	Object  a(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		return read1("app_nodes.properties");
	}
	
	@ResponseBody
	@RequestMapping("/aa")
	Object  aa(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		return read1("/app_nodes.properties");
	}
	
	@ResponseBody
	@RequestMapping("/b")
	Object  b(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		return read2("app_nodes.properties");
	}
	
	@ResponseBody
	@RequestMapping("/bb")
	Object  bb(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		return read2("/app_nodes.properties");
	}

	@ResponseBody
	@RequestMapping("/c")
	Object  c(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		return read3("app_nodes.properties");
	}
	
	@ResponseBody
	@RequestMapping("/cc")
	Object  cc(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		return read3("/app_nodes.properties");
	}
	
	@ResponseBody
	@CrossOrigin
	@RequestMapping("/d")
	String  d(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		 return read4("app_nodes.properties");
	}
	
	@ResponseBody
	@RequestMapping("/dd")
	String  dd(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		return read4("/app_nodes.properties");
	}
	
	@ResponseBody
	@RequestMapping("/e")
	String  e(int num, HttpServletRequest req, HttpServletResponse resp){
		long now = System.currentTimeMillis();
		while(num-->0){
			PortInstance.inst().addQueue((po)-> {
				count.incrementAndGet();
				while ((System.currentTimeMillis() - now)<100){
					LogCore.BASE.debug(count+"--"+(System.currentTimeMillis() - now));
				}
			});
		}
		return "e"+(System.currentTimeMillis() - now);
	}
	
	@ResponseBody
	@RequestMapping("/f")
	Object ef(HttpServletRequest req, HttpServletResponse resp){
		return read5("app_nodes.properties");
	}
	
	@ResponseBody
	@RequestMapping("/ff")
	Object ff(HttpServletRequest req, HttpServletResponse resp){
		return read5("/app_nodes.properties");
	}
	
	@ResponseBody
	@RequestMapping("/getenv")
	Object getenv(HttpServletRequest req, HttpServletResponse resp){
		StandardServletEnvironment en = null;
		return SpringManager.inst().getEnvironment().getClass().getName()+en;
	}
	
	@ResponseBody
	@RequestMapping("/lookenv")
	Object lookenv(HttpServletRequest req, HttpServletResponse resp){
		return SpringManager.inst().getEnvironmentJsonStr();
	}
	
	
	public Object read1(String path) throws IOException {
		String contents = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
		return contents;
	}
	public Object read2(String path) {
		return Utils.readProperties(path);
	}
	public Object read3(String path) throws FileNotFoundException, IOException {
		BufferedReader br;
		int count = 0;
		File file = new File(path);
		br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			 System.out.println(line);
			 count ++;
		}
		br.close();
		return count;
	}
	public String read4(String path) {
		try (FileInputStream in = new FileInputStream(path)){
			FileChannel channel = in.getChannel();  
			ByteBuffer buffer = ByteBuffer.allocate(1024);  
			channel.read(buffer);  
			byte[] b = buffer.array();  
			return new String(b);
		} catch (Exception e) {
			LogCore.BASE.error("read file err:{}", e);
			return null;
		}
	}
	
	public Object read5(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		byte[] data = null;
		try {
			if(is.available()==0){//严谨起见,一定要加上这个判断,不要返回data[]长度为0的数组指针
				return data;
			}
			data = new byte[is.available()];
			is.read(data);
			is.close();
			return data;
		} catch (IOException e) {
			LogCore.BASE.error("readFileBytes, err", e);
			return data;
		}
	}
	
	@ResponseBody
	@RequestMapping("/testerr")
	String  testerr(HttpServletRequest req, HttpServletResponse resp){
		throw new SysException("出错啦");
	}
}
