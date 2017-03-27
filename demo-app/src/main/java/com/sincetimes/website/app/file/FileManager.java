package com.sincetimes.website.app.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sincetimes.website.app.file.function.MutlipartFileConsumer;
import com.sincetimes.website.app.file.function.SingleFileCallBack;
import com.sincetimes.website.app.file.interfaces.InputFileSupport;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Sys;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.manger.SpringManager;
import com.sincetimes.website.redis.jedis.spring.JedisManagerBase;

@Component
public class FileManager extends JedisManagerBase implements InputFileSupport{
	
	public static FileManager inst() {
		return ManagerBase.inst(FileManager.class);
	}

	@Override
	public void init() {
		
	}
	
	//只是简单的的上传文件并返回一个文件的新生成的用户名
	public String upload_file_simple(StandardMultipartHttpServletRequest req, String defalut_dir) {
		String dirName = Objects.toString(req.getParameter("dir"), defalut_dir);
		String ymdPath = TimeTool.formatTime(System.currentTimeMillis(), "yyyy_MM_dd")+"/";
		String dirPath = "upload/";
		String rootPath = req.getServletContext().getRealPath("/");
		if(Sys.isLinux()){
			rootPath = SpringManager.inst().upload_path;
		}
		String filePath = dirPath + dirName+ "/"+ ymdPath;
		Function<String ,String> nameFunc = fileName ->{
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();//检查后缀名
			String newFileName = TimeTool.formatTime(System.currentTimeMillis(), "HH_mm_ss_") + new Random().nextInt(1000);
			return newFileName+"."+fileExt;
		};
		return upload_single_file(rootPath, filePath, nameFunc, req);
	}
	/**
	 * 这个接口看起来麻烦,但是处理多个文件参数的时候可以用
	 * 处理上传的文件,不同的文件根据不同的参数名做不同的处理
	 */
	public void handle_multi_file(Map<String, MutlipartFileConsumer> comsumeMap, StandardMultipartHttpServletRequest freq) {
		StopWatch stopWatch = new StopWatch("handle multi files");
		stopWatch.start("handle multi files");
		Map <String, List<MultipartFile>> map = freq.getMultiFileMap();
		map.forEach((k,v)->{
			v.forEach((a) -> comsumeMap.get(k).doUpload(a));
			LogCore.BASE.info("test{},{},{}", v.size(), k, comsumeMap.get(k));
		});
		stopWatch.stop();
		LogCore.BASE.info("up multi files used time:{}\n input file name={}", stopWatch.prettyPrint(), map.keySet());
	}
	/**
	 * 处理上传的文件,不同的文件做相同的处理
	 */
	@Deprecated
	public void handle_single_file(MutlipartFileConsumer comsume, StandardMultipartHttpServletRequest freq) {
		StopWatch stopWatch = new StopWatch("handle single file");
		stopWatch.start("handle single file");
		Map <String, List<MultipartFile>> map = freq.getMultiFileMap();
		map.forEach((k,v)->{
			v.forEach(comsume::doUpload);
		});
		stopWatch.stop();
		LogCore.BASE.info("hanle single file used time:{}\n input file name={}", stopWatch.prettyPrint(), map.keySet());
	}
	/**
	 * 推荐使用的方法
	 * 仅仅处理一个文件的返回值
	 * @param callBack
	 * @param freq
	 */
	public <T> T do_single_file(SingleFileCallBack<T> callBack, StandardMultipartHttpServletRequest freq) {
		Map <String, List<MultipartFile>> map = freq.getMultiFileMap();
		for(List<MultipartFile> list:map.values()){
			for(MultipartFile m: list){
				LogCore.BASE.info("filename={}", m.getName());
				return callBack.doInFile(m);
			}
		}
		return null;
	}
	/**
	 * 上传多个文件，返回文件的uri[]
	 * TODO:上传文件大小限制
	 */
	@Deprecated
	public List<String> upload_multi_file(String saveRootPath, String filePath, BiFunction<String, String, String> nameBiFunc, StandardMultipartHttpServletRequest freq) {
		StopWatch stopWatch = new StopWatch("up multi files");
		stopWatch.start("save multi files");
		String saveUrl = freq.getContextPath() + "/" + filePath;
		String realpath = saveRootPath + filePath;
		File dir = new File(realpath);
		LogCore.BASE.info("saveRootPath={}, filePath={}, realpath={}, dir={}", saveRootPath, filePath, realpath, dir);
		if(!dir.exists()) dir.mkdirs();
		
		List<String> file_url = new ArrayList<String>();
		try {
			freq.getMultiFileMap().values().stream().forEach(files -> {
				files.stream().forEach((MultipartFile file) -> {
					try {
						String paramName = file.getName();//表单参数
						String fileName = file.getOriginalFilename();
						if(Util.isEmpty(fileName)){
							LogCore.BASE.info("upload failed because of nofiles fileName={}", fileName);
							return;
						}
						LogCore.BASE.info("getOriginalFilename={},getContentType={},getSize={}", fileName, file.getContentType(), Util.getM(file.getSize()));
						String _new_file_original_name = fileName;
						if(null != nameBiFunc){
							_new_file_original_name = nameBiFunc.apply(paramName, fileName);
						}else{
							_new_file_original_name = fileName;
						}
						File newFile = new File(dir, _new_file_original_name);
						file.transferTo(newFile);
						file_url.add(saveUrl + newFile.getName());
						LogCore.BASE.info("file_url={}", file_url);
					} catch (IOException e) {
						LogCore.BASE.error("read file err:{}", e);
					}
				});
			});
		} catch (Exception e2) {
			LogCore.BASE.error("files upload err", e2);
			return file_url;
		}
		stopWatch.stop();
		LogCore.BASE.info("up multi files used time:{},\n update nums={}", stopWatch.prettyPrint());
		return file_url;
	}
	/*
	 * 传入路径和命名规则，返回保存的文件全路径，特别适合富文本剪辑器以及其他上传图片的场景
	 */
	public String upload_single_file(String rootPath, String filePath, Function<String, String> nameFunc, StandardMultipartHttpServletRequest freq) {
		StopWatch stopWatch = new StopWatch("upfile");
		stopWatch.start("save file");
		
		String saveUrl = freq.getContextPath() + "/" + filePath;
		
		String realpath = rootPath + filePath;
		LogCore.BASE.info("saveRootPath={}, filePath={}, realpath={}", rootPath, filePath, realpath);
		File dir = new File(realpath);
		LogCore.BASE.info("dir={}", dir);
		if(!dir.exists()) dir.mkdirs();
		
		String file_url[] = new String[1];
		freq.getMultiFileMap().values().forEach(files -> {
			files.forEach((MultipartFile file) -> {
				save(realpath, file, nameFunc).ifPresent(f->file_url[0] = saveUrl + f.getName());
//				file_url[0] = saveUrl + newFile.getName();/TU
			});
		});
		stopWatch.stop();
		LogCore.BASE.info("upfile used time:{},\n update nums={}", stopWatch.prettyPrint());
		return file_url[0];
	}
}
