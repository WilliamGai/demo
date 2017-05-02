package com.sincetimes.website.core.common.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class IOTool {
	/** 低效率8Xmysql */
	@Deprecated
	public static void writeObject(Object obj, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream os= new ObjectOutputStream(fos);
			os.writeObject(obj);//java.io.NotSerializableException:
			os.close();
		} catch (IOException e) {//NotSerializableException
			LogCore.BASE.error("write file err:", e);
		}
	}
	/** 效率很低10Xmysql  */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <T> T readObject(String fileName) {
		try {
			 FileInputStream fis = new FileInputStream(fileName);
			 ObjectInputStream ois = new ObjectInputStream(fis);
			 Object obj = ois.readObject();
		     ois.close();
		     return (T)obj;
		} catch (FileNotFoundException e) {
			LogCore.BASE.warn("read warn:{} not found", fileName);
			return null;
		} catch (IOException |ClassNotFoundException e) {
			LogCore.BASE.error("read err:", e);
			return null;
		}
	}
	public static byte[] readAllBytes(InputStream is){
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
			e.printStackTrace();
			return data;
		}
	}
	public static byte[] nioRead(String file){ 
		byte[] b = null;
		try(FileInputStream in = new FileInputStream(file)) {
			FileChannel channel = in.getChannel(); //FileInputStream.getChannel() 
			ByteBuffer buffer = ByteBuffer.allocate(1024);  
			channel.read(buffer);  
			b = buffer.array();  
			return b;
		} catch (Exception e) {
			LogCore.BASE.error("nioRead err:{}", e);
			return b;
		}
	}  
	public static InputStream getInputStream(String fileName){
		try {
			return Files.newInputStream(Paths.get(fileName), StandardOpenOption.READ);
		} catch (IOException e) {
			LogCore.BASE.error("getData err:{}", e);
			return null;
		}
	}
	/** @since jdk1.7*/
	public static byte[] readAllData(String fileName){
		try {
			return Files.readAllBytes(Paths.get(fileName));
		} catch (IOException e) {
			LogCore.BASE.error("getData err:{}", e);
			return null;
		}
	}
	/** @since jdk1.7*/
	public static void writeData(byte[] data, String fileName, OpenOption... options){
		try {
			File dir = new File(fileName);
			if(!dir.exists()){
				dir.createNewFile();//如果没有要创建文件,否则抛出java.nio.file.NoSuchFileException
			}
			Files.write(Paths.get(fileName), data, options);
		} catch (IOException e) {
			LogCore.BASE.error("writeFile err:{}", e);
		}
	}
	public static void writeData(byte[] data, String fileName){
		writeData(data, fileName, StandardOpenOption.SYNC, StandardOpenOption.WRITE);
	}
	public static void appendData(byte[] data, String fileName){
		writeData(data, fileName, StandardOpenOption.SYNC, StandardOpenOption.APPEND);
	}
	public static void main(String args[]){
		writeData("avv".getBytes(), "monick");
		System.out.println("hi");
	}
	public static boolean existFile(String fileName){
		return Files.exists(Paths.get(fileName));
	}
}
