package com.sincetimes.website.app.file.interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import org.springframework.web.multipart.MultipartFile;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;
/**
 * 用于‘继承’的接口, 在不修改原来类本身和父类的情况下增加方法,实现多重继承
 */
public interface InputFileSupport {
	public static int STATUS_OK = 1;
	public static int STATUS_FAIL = 0;
	/**
	 * 返回读取的行
	 */
	default List<String> readFileLines(MultipartFile m){
		List<String> codes = new ArrayList<>();
		try {
			BufferedReader br;
			br = new BufferedReader(new InputStreamReader(m.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				codes.add(line);
			}
			br.close();
		} catch (IOException e) {
			LogCore.BASE.error("readFileLines, err", e);
		}
		return codes;
	}
	/**
	 * 返回读取的byte[]
	 */
	default byte[] readFileBytes(MultipartFile m){
		InputStream is = getInputStream(m);
		return is == null? null : readFileBytes(is);
	}
	default byte[] readFileBytes(InputStream is){
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
	default InputStream getInputStream(MultipartFile m){
		try {
			return m.getInputStream();
		} catch (IOException e) {
			LogCore.BASE.error("getInputStream, err", e);
			return null;
		}
	}
	/**
	 * 
	 * @param realpath 保存路径
	 * @param nameFunc 根据原始文件名来重新命名,如果为空则保留原文件名
	 * @return 1成功, 0失败
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	default Optional<File> save(String realpath, MultipartFile file, Function<String, String> nameFunc){
		File dir = new File(realpath);
		LogCore.BASE.info("dir={}", dir);
		if(!dir.exists()) dir.mkdirs();
		String fileName = file.getOriginalFilename();
		if(Util.isEmpty(fileName)){
			LogCore.BASE.info("upload failed because of nofiles fileName={}", fileName);
			return Optional.empty();
		}
		LogCore.BASE.info("getOriginalFilename={},getContentType={},getSize={}", fileName, file.getContentType(), Util.getM(file.getSize()));
		String _new_file_original_name = fileName;
		if(null != nameFunc){
			_new_file_original_name = nameFunc.apply(fileName);
		}else{
			_new_file_original_name = fileName;
		}
		File newFile = new File(dir, _new_file_original_name);
		try {
			file.transferTo(newFile);
		} catch (IllegalStateException | IOException e) {
			LogCore.BASE.error("save file, err", e);
		}
		return Optional.ofNullable(newFile);
	}
	
	default Optional<File> save(String realpath, MultipartFile file){
		Function<String ,String> nameFunc = this::randomFileName;
		return save(realpath, file, nameFunc);
	}
	
	default String randomFileName(String fileName) {
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();//检查后缀名
		String newFileName = TimeTool.formatTime(System.currentTimeMillis(), "HH_mm_ss_") + new Random().nextInt(1000);
		return newFileName+"."+fileExt;
	}
}
 