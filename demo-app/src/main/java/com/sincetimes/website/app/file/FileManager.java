package com.sincetimes.website.app.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sincetimes.website.app.file.function.FileCallBack;
import com.sincetimes.website.app.file.function.FileConsumer;
import com.sincetimes.website.app.file.interfaces.InputFileSupport;
import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeTool;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.core.spring.interfaces.FilePathInterface;
import com.sincetimes.website.redis.jedis.spring.JedisManagerBase;
/**
 * TODO:
 * replace JedisManagerBase by ManagerBase
 * @author BAO
 */
@Component
public class FileManager extends JedisManagerBase implements InputFileSupport, FilePathInterface{
	
	public static FileManager inst() {
		return ManagerBase.inst(FileManager.class);
	}

	@Override
	public void init() {
		
	}
	/*  上传或处理文件    */
	/**
	 * 上传一个文件并返回一个文件的新生成的用户名,路径名回带有日期目录
	 * @param req
	 * @param defalut_dir
	 * @return
	 */
	public String uploadFileSimple(StandardMultipartHttpServletRequest req, String defalut_dir) {
		String dirName = Objects.toString(req.getParameter("dir"), defalut_dir);
		String ymdPath = TimeTool.formatTime(System.currentTimeMillis(), "yyyy_MM_dd")+"/";
		String dirPath = "upload/";
		String filePath = dirPath + dirName+ "/"+ ymdPath;
		return uploadFileSimple(req, filePath, this::randomFileName);
	}
	
	/**
	 * 上传文件,执行文件命名方法
	 * @param filePath 文件路径,建议以'/upload/'开头
	 * @param nameFunc 为null的时候等价于{@code Function.identity()},表示维持之前的文件名
	 * <br>要使用时间的随机名请使用{@code this::randomFileName}
	 * <br>参考{@link InputFileSupport#randomFileName(String)}
	 * @return 文件uri
	 */
	public String uploadFileSimple(StandardMultipartHttpServletRequest req, String filePath, Function<String ,String> nameFunc) {
		String rootPath = getRootFilePath(req);
		return uploadSingleFile(rootPath, filePath, nameFunc, req);
	}
	
	/**
	 * 传入路径和命名规则，返回保存的文件全路径，特别适合富文本剪辑器以及其他上传图片的场景。
	 * <p>等价于<pre> {@code 
	 	String file_url[] = new String[1];
	 	freq.getMultiFileMap().values().forEach(files -> {
			files.forEach((MultipartFile file) -> {
				save(realpath, file, nameFunc).ifPresent(f->file_url[0] = saveUrl + f.getName());
			});
		});}</pre>
	 */
	public String uploadSingleFile(String rootPath, String filePath, Function<String, String> nameFunc, StandardMultipartHttpServletRequest freq) {
		if(Util.isEmpty(freq.getMultiFileMap())){
			return null;
		}
		StopWatch stopWatch = new StopWatch("upfile");
		stopWatch.start("save file");
		
		String saveUrl = freq.getContextPath() + "/" + filePath;
		String realpath = rootPath + filePath;
		LogCore.BASE.info("saveRootPath={}, filePath={}, realpath={}", rootPath, filePath, realpath);
		
		Optional<File> newFile = excuteFile((m)->save(realpath, m, nameFunc), freq);
		stopWatch.stop();
		LogCore.BASE.info("upfile used time:{},\n update newFile={}", stopWatch.prettyPrint(), newFile);
		if(null == newFile){//如果没有上传的文件回返回null,由于提前判断正常情况下所以不会发生
			return null;
		}
		if(newFile.isPresent()){
			return saveUrl + newFile.get().getName();
		}
		return null;
	}

	
	/**
	 * 处理单个文件
	 * 返回值可以是byte[]或者List<String>,File等等
	 * 示例:
	  <pre>
	  {@code byte[] data = FileManager.inst().excuteFile(FileManager.inst()::readFileBytes, req); }
	  {@code List<String> lines = FileManager.inst().excuteFile(FileManager.inst()::readFileLines, req);}
	  {@code Optional<File> newFile = excuteFile((m)->save(realpath, m, nameFunc), freq);}
	  </pre>
	 * @param callBack 
	 * @param freq
	 */
	public <T> T excuteFile(FileCallBack<T> callBack, StandardMultipartHttpServletRequest freq) {
		Map <String, List<MultipartFile>> map = freq.getMultiFileMap();
		for(List<MultipartFile> list:map.values()){
			for(MultipartFile m: list){
				LogCore.BASE.info("excuteFile filename={}", m.getName());
				return callBack.doInFile(m);
			}
		}
		return null;
	}
	/*   多个处理文件操作    */
	/**
	 * 这个接口看起来麻烦,但是处理多个文件参数的时候可以用。
	 * <p>例如上传的文件既要读取激活码又要存储图片返回uri
	 * <p>处理上传的文件,不同的文件根据不同的参数名做不同的处理
	 */
	public void handleMultiFile(Map<String, FileConsumer> comsumeMap, StandardMultipartHttpServletRequest freq) {
		StopWatch stopWatch = new StopWatch("handle multi files");
		stopWatch.start("handle multi files");
		Map <String, List<MultipartFile>> map = freq.getMultiFileMap();
		map.forEach((k,v)->{
			v.forEach((a) -> comsumeMap.get(k).consumeFile(a));
			LogCore.BASE.info("test{},{},{}", v.size(), k, comsumeMap.get(k));
		});
		stopWatch.stop();
		LogCore.BASE.info("up multi files used time:{}\n input file name={}", stopWatch.prettyPrint(), map.keySet());
	}
	/* 批量处理文件,暂未使用的方法*/
	/**
	 * 处理多个上传的文件,所有文件相同的处理
	 * @see  FileManager#excuteFile(FileCallBack, StandardMultipartHttpServletRequest)
	 */
	public void batchHandleFile(FileConsumer comsumer, StandardMultipartHttpServletRequest freq) {
		StopWatch stopWatch = new StopWatch("handle single file");
		stopWatch.start("batchUploadFile file");
		Map <String, List<MultipartFile>> map = freq.getMultiFileMap();
		map.forEach((k,v)->{
			v.forEach(comsumer::consumeFile);
		});
		stopWatch.stop();
		LogCore.BASE.info("batchUploadFile file used time:{}\n input file name={}", stopWatch.prettyPrint(), map.keySet());
	}
	/**
	 * 批量上传多个文件，返回文件的uri[]
	 * <p>TODO:上传文件大小限制
	 */
	public List<String> batchUploadFiles(String saveRootPath, String filePath, BiFunction<String, String, String> nameBiFunc, StandardMultipartHttpServletRequest freq) {
		StopWatch stopWatch = new StopWatch("up multi files");
		stopWatch.start("save multi files");
		String realpath = saveRootPath + filePath;
		LogCore.BASE.info("saveRootPath={}, filePath={}, realpath={}", saveRootPath, filePath, realpath);
		
		List<String> fileUris = new ArrayList<String>();
		FileConsumer comsumer = (MultipartFile m)->{
			save(realpath, m).ifPresent(f->fileUris.add(f.getName()));
		};
		batchHandleFile(comsumer, freq);
		stopWatch.stop();
		LogCore.BASE.info("up multi files used time:{}, fileUris={}", stopWatch.prettyPrint(), fileUris);
		return fileUris;
	}
	
}
