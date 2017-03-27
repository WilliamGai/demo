package com.sincetimes.website.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
public class ReadLine2 {
	public static void main(String args[]) throws IOException {
		String contents = new String(Files.readAllBytes(Paths.get("nine_house.html")), StandardCharsets.UTF_8);
		//System.out.println(contents);
		String ss[] = contents.split("\n");
		System.out.println(ss.length);
//		Files.write(Paths.get("openids.txt"), null, Charset.defaultCharset(), StandardOpenOption.CREATE);
	}
}
