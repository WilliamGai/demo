package com.sonic.website.app;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
public class CompareFileLines {
	public static void main(String args[]) throws IOException {
		Function<String, String> filter1 = a->{
			String ss[] = a.split("\"");
			if(ss.length>1){
				return ss[1];
			}
			return null;
		};
		Set<String> set = readFile2Set("d1.txt", filter1);
		Set<String> set2 = readFile2Set("官网礼包-2W.txt",a->a);
		System.out.println("文件1 行数"+set.size());
		System.out.println("文件2 行数"+set2.size());
		System.out.println(set2.retainAll(set));
		System.out.println(set2.size());
//		Files.write(Paths.get("openids.txt"), null, Charset.defaultCharset(), StandardOpenOption.CREATE);
	}
	public static <T> Set<T> readFile2Set(String filepath, Function<String, T> filter) throws IOException{
		String contents = new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8);
		String ss[] = contents.split("\n");
		Set<T> set = new HashSet<>();
		for (int i = 0; i < ss.length; i++) {
			T line = filter.apply(ss[i].replaceAll("\r", ""));
			set.add(line);
		}
		return set;
	}
}
