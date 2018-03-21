package com.sonic.data.ivanka.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.sonic.website.core.common.support.JSONBuilder;
import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.TimeUtil;
import com.sonic.website.core.spring.HttpHeadUtil;
import com.sonic.website.core.spring.interfaces.AccessSupport;
@RestController
@Order(value = 6)
@RequestMapping("/mg/article")
/**
 * 富文本编辑器的上传文件请求和打开文件管理器的请求
 */
public class File4RichTextEditController implements AccessSupport {
	public static final HashMap<String, List<String>> EXT_MAP = new HashMap<>();//支持的文件类型，目前不限制
	public static final List<String> IMGE_TYPES =Arrays.asList("gif","jpg","jpeg","png","bmp");
	static{
		EXT_MAP.put("image", IMGE_TYPES);
		EXT_MAP.put("flash", Arrays.asList("swf","flv"));
		EXT_MAP.put("media", Arrays.asList("swf","flv","mp3","wav","wma","wmv","mid","avi","mpg","asf","rm","rmvb"));
		EXT_MAP.put("file", Arrays.asList("doc","docx","xls","xlsx","ppt","htm","html","txt","zip","rar","gz","bz2"));
	}
	/**
	 * 
	  <pre>下面的比较器等价于{@code
	  	public static Comparator<HashMap<String, Object>> TYPE_COMPARAOR_FUNC =(hashA, hashB)-> {
			int comparison = ((Boolean)hashB.get("is_dir")).compareTo((Boolean)hashA.get("is_dir"));
			if (comparison == 0) {
				comparison = ((String)hashA.get("filetype")).compareTo((String)hashB.get("filetype"));
			}
			return comparison;
		};}<pre>
	 */
	public static Comparator<HashMap<String, Object>> DIR_COMPARAOR_FUNC =(hashA, hashB)-> {
		return ((Boolean)hashB.get("is_dir")).compareTo((Boolean)hashA.get("is_dir"));
	};
	public static Comparator<HashMap<String, Object>> TYPE_COMPARAOR_FUNC =(hashA, hashB)-> {
		return ((String)hashA.get("filetype")).compareTo((String)hashB.get("filetype"));
	};
	
	public static Comparator<HashMap<String, Object>> NAME_COMPARAOR_FUNC = (hashA, hashB)-> {
		return ((String)hashA.get("filename")).compareTo((String)hashB.get("filename"));
	};
	
	public static Comparator<HashMap<String, Object>> SIZE_COMPARAOR_FUNC=(hashA, hashB)-> {
		return ((Long)hashA.get("filesize")).compareTo((Long)hashB.get("filesize"));
	};
	
	@RequestMapping("/file_manager_json")
	public Object fileManager(HttpServletRequest req, HttpServletResponse rsp){
		String dirPath = "upload/";
		String rootPath = getRootFilePath(req);
		rootPath += dirPath;
		
		//根目录URL，可以指定绝对路径，比如 http://www.yoursite.com/attached/
		String rootUrl  = req.getContextPath() + "/" + dirPath;
		LogCore.BASE.info("rootPath={}", rootPath);
		LogCore.BASE.info("rootUrl={}", rootUrl);
		
		String dirName = req.getParameter("dir");
		if (dirName != null) {
			if(!EXT_MAP.containsKey(dirName)){
				return "Invalid Directory name.";
			}
			rootPath += dirName + "/";
			rootUrl += dirName + "/";
			File saveDirFile = new File(rootPath);
			if (!saveDirFile.exists()) {
				saveDirFile.mkdirs();
			}
		}
		//根据path参数，设置各路径和URL
		String path = Objects.toString(req.getParameter("path"), "");
		String currentPath = rootPath + path;
		String currentUrl = rootUrl + path;
		String currentDirPath = path;
		String moveupDirPath = "";
		if (!"".equals(path)) {
			String str = currentDirPath.substring(0, currentDirPath.length() - 1);
			moveupDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : "";
		}

		//排序形式，name or size or type
		String order = Objects.toString(req.getParameter("order").toLowerCase(), "name");

		//不允许使用..移动到上一级目录
		if (path.indexOf("..") >= 0) {
			return "Access is not allowed.";
		}
		//最后一个字符不是/
		if (!"".equals(path) && !path.endsWith("/")) {
			return "Parameter is not valid.";
		}
		//目录不存在或不是目录
		File currentPathFile = new File(currentPath);
		if(!currentPathFile.isDirectory()){
			return "Directory does not exist.";
		}

		//遍历目录取的文件信息
		List<HashMap<String, Object>> fileList = new ArrayList<>();
		if(currentPathFile.listFiles() != null) {
			for (File file : currentPathFile.listFiles()) {
				HashMap<String, Object> hash = new HashMap<>();
				String fileName = file.getName();
				if(file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else if(file.isFile()){
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo", IMGE_TYPES.contains(fileExt));
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
				fileList.add(hash);
			}
		}

		if ("size".equals(order)) {
			Collections.sort(fileList, DIR_COMPARAOR_FUNC.thenComparing(SIZE_COMPARAOR_FUNC));
		} else if ("type".equals(order)) {
			Collections.sort(fileList, DIR_COMPARAOR_FUNC.thenComparing(TYPE_COMPARAOR_FUNC));
		} else {
			Collections.sort(fileList, DIR_COMPARAOR_FUNC.thenComparing(NAME_COMPARAOR_FUNC));
		}

		rsp.setContentType("application/json; charset=UTF-8");
		JSONObject json = JSONBuilder.create().append("moveup_dir_path", moveupDirPath)
				.append("current_dir_path", currentDirPath)
				.append("current_url", currentUrl)
				.append("total_count", fileList.size())
				.append("file_list", fileList).buildJSON();
		LogCore.BASE.info("filemanager json={}", json);
		return json;
	}
	
	@RequestMapping("/upload_file_json")
	Object upload_media(StandardMultipartHttpServletRequest req, HttpServletResponse rsp){
		try {
			LogCore.BASE.info("upload_file_json' invoked{}",HttpHeadUtil.getParamsMapLimit(req));
			String dirName = Objects.toString(req.getParameter("dir"), "image");
			
			//文件保存目录路径
			//rootPath/dirPath/dirName/ymdPath/fileName
			String ymdPath = TimeUtil.formatTime(System.currentTimeMillis(), "yyyy_MM_dd")+"/";
			String dirPath = "upload/";
			String rootPath = getRootFilePath(req);
			String filePath = dirPath + dirName+ "/"+ ymdPath;
			/*新文件名字的命名规则**/
			Function<String ,String> nameFunc = FileManager.inst()::randomFileName;

			rsp.setContentType("text/html; charset=UTF-8");

			if(!ServletFileUpload.isMultipartContent(req)){
				return getError("请选择文件。");
			}

			//检查目录
			File uploadDir = new File(rootPath);

			//检查目录写权限
			if(!uploadDir.canWrite()){
				return getError("上传目录没有写权限。");
			}

			String url = FileManager.inst().uploadSingleFile(rootPath, filePath, nameFunc, req);
			LogCore.BASE.info("file request={}, requestStr={}, url={}", req, req.toString(), url);
			LogCore.BASE.info("file requestjson={}", req.toString());

			return JSONBuilder.creatJsonString("error", 0, "url", url);
		} catch (Exception e) {
			LogCore.BASE.error("file upload err!{}", e);
			return getError("未知错误");
		}
	}
	
	private String getError(String message) {
		return JSONBuilder.creatJsonString("error", 1, "message", message);
	}
}
