package com.sincetimes.website.core.common.support;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Test;
/**
 * 推荐的方法
 * readFileFast
 * writeFileFast
 * writeFileFastAppendSafe
 * @author BAO
 * jdk序列化的缺点 慢,发布后不能更改包名
 */
public class SerializeFileTool {
	public static boolean existFile(String fileName){
		return Files.exists(Paths.get(fileName));
	}
	/**
	 * 适合读数据少的文件
	 * <pre>same as{@code 
	 *	InputStream in = Files.newInputStream(Paths.get(fileName), StandardOpenOption.READ);
	 * }
	 * </pre>
	 * @param fileName
	 * @return
	 */
	public static <T> T readFile(String fileName){
		try (InputStream in = new FileInputStream(fileName);
			 DataInputStream stream = new DataInputStream(in)){
			return SerializeTool.read(stream);
		} catch (Exception e) {
			LogCore.BASE.error("read file err:{}", e);
			return null;
		}
	}
	public static <T> T readFileFast(String fileName){
		if(!existFile(fileName)){
			LogCore.BASE.warn("fileName{} not exitst", fileName);
			return null;
		}
		try{
			byte[] data =  Files.readAllBytes(Paths.get(fileName));
			return SerializeTool.deserilize(data);
		}catch (Exception e) {
			LogCore.BASE.error("read file err:{}", e);
			return null;
		}
	}
	/**
	 * 连续读取文件,在用append方式写入文件多个对象的时候有用
	 * @param fileName
	 * @return
	 */
	public static <T> List<T> readFile2List(String fileName){
		try (InputStream in = new FileInputStream(fileName);
			 DataInputStream stream = new DataInputStream(in)){
			List<T> list =  new ArrayList<>();
			while(stream.available() > 0){
				T t = SerializeTool.read(stream);
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			LogCore.BASE.error("read file err:{}", e);
			return null;
		}
	}
	public static <T> List<T> readFileFast2List(String fileName){
		return readFileFast2List(fileName, ArrayList::new);
	}
	/**
	 * Class<? extends List<T>> listClazz
	 */
	public static <T> List<T> readFileFast2List(String fileName, Supplier<List<T>> listCreate){
		List<T> list = listCreate.get();
		if(!existFile(fileName)){
			LogCore.BASE.warn("fileName{} not exitst", fileName);
			return list;
		}
		try {
			byte[] data =  Files.readAllBytes(Paths.get(fileName));
			return SerializeTool.deserilize2List(data, list);
		} catch (Exception e) {
			LogCore.BASE.error("read file err:{}", e);
			return null;
		}
	}
	/*写文件*/
	/**
	 * <pre>下面这种方式读取很慢:
	 *  FileOutputStream fileOut = new FileOutputStream(tempFileName);
	 *  DataOutputStream stream = new DataOutputStream(fileOut);
	 *	writeObject(obj, stream);
	 *  期中Files.newOutputStream(Paths.get(tempFileName))等同于new FileOutputStream(tempFileName)
	 *  详细代码:{@code
	   try(OutputStream fileOut = Files.newOutputStream(Paths.get(tempFileName));
			DataOutputStream stream = new DataOutputStream(fileOut);) {
			writeObject(obj, stream);
		} catch (IOException e) {
			LogCore.BASE.error("write file err:{}", e);
		}
		//将临时文件替换到目标文件
		try {
			Files.move(Paths.get(tempFileName), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LogCore.BASE.error("move file err:{}", e);
		}
	 * </pre>
	 * @param fileName
	 * @param obj
	 */
	public static void writeFile(String fileName, Object obj){
		String tempFileName = fileName.concat("_temp");
		try(OutputStream fileOut = Files.newOutputStream(Paths.get(tempFileName));
			DataOutputStream stream = new DataOutputStream(fileOut);) {
			SerializeTool.writeObject(obj, stream);
		} catch (IOException e) {
			LogCore.BASE.error("write file err:{}", e);
		}
		//将临时文件替换到目标文件
		try {
			Files.move(Paths.get(tempFileName), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LogCore.BASE.error("move file err:{}", e);
		}
	}
	/**
	 * 中间会产生临时文件
	 */
	public static void writeFileSafe(String fileName, Object value){
		try {
			String tempFileName = fileName.concat("_temp");
			byte[] data = SerializeTool.serlize(value);
			Files.write(Paths.get(tempFileName), data);
			Files.move(Paths.get(tempFileName), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LogCore.BASE.error("move file err:{}", e);
		}
	}
	/**
	 * 中间会产生临时文件,防止写的过程中意外中断而破坏原文件
	 * 超过2GB或者内存不足会报java.lang.OutOfMemoryError: Java heap space
	 * @see SerializeFileTool#writeFileFastAppend(String, Object)
	 */
	public static void writeFileFastAppendSafe(String fileName, Object value){
		if(existFile(fileName)){
			try {
				String tempFileName = getRandomTempFileName(fileName);
				byte[] data = Files.readAllBytes(Paths.get(fileName));
				byte[] append = SerializeTool.serlize(value);
				byte[] newData = Util.mergeBytes(data, append);
				Files.write(Paths.get(tempFileName), newData);
				Files.move(Paths.get(tempFileName), Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				LogCore.BASE.error("move file err:{}", e);
			}
			return;
		}
		writeFileSafe(fileName, value);
	}
	public static void writeFileFast(String fileName, Object value){
		try {
			byte[] data = SerializeTool.serlize(value);
			Files.write(Paths.get(fileName), data);
		} catch (IOException e) {
			LogCore.BASE.error("move file err:{}", e);
		}
	}
	/**
	 * 简单的将对象序列化后追加到文件CREATE决定了没有则创建文件,APPEND决定了写文件的方式
	 * @param fileName
	 * @param value
	 */
	public static void writeFileFastAppend(String fileName, Object value){
		try {
			byte[] data = SerializeTool.serlize(value);
			Files.write(Paths.get(fileName), data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			LogCore.BASE.error("move file err:{}", e);
		}
	}
	public static void writeFileAcessFast(String fileName, Object value) throws Exception{
//		RandomAccessFile file = new RandomAccessFile("file", "rw");
//		String tempFileName = fileName.concat("_temp");
//		SeekableByteChannel channel = Files.newByteChannel(Paths.get("a"));
	}
	@Test
	public void test() throws IOException{
		String fileName = "moddm";
		writeFileFastAppendSafe(fileName, 8L);
		writeFileFastAppendSafe(fileName, 2L);
//		Object list = readFileFast(fileName);
//		Map<Object,Object> map = new HashMap<>();
		List<Object> ll = new ArrayList<>();
		ll.add("a");
		ll.add(1);
//		map.put(ll, ll);
		ll.add(ll);
//		ll.add(map);
		byte[] data = SerializeTool.serlize(ll);
		System.out.println(ll);
		System.out.println(data.length);
	}
	public static String getRandomTempFileName(String fileName){
		return fileName + TimeTool.formatTime(System.currentTimeMillis(), "yyyy-MM-dd-HH_mm_ss") + new SecureRandom().nextInt(1000);
	}
}
