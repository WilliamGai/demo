package com.sonic.website.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
//nine_house.html
public class ReadLine {
    public static void main(String args[]) throws IOException {
        String contents = new String(Files.readAllBytes(Paths.get("a.log")), StandardCharsets.UTF_8);
        System.out.println(contents);
//        Files.write(Paths.get("openids.txt"), null, Charset.defaultCharset(), StandardOpenOption.CREATE);
    }
}
