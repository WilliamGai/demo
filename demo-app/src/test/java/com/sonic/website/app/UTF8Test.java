package com.sonic.website.app;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;

public class UTF8Test {
    @SuppressWarnings("deprecation")
    public static void main(String args[]) throws UnsupportedEncodingException{
        String s = "拉拉";
        s = new String(s.getBytes(),"utf-8");
        System.out.println(s);
        System.getProperty("file.encoding");
        String s2 = URLEncoder.encode("嗯嗯");
        String s3 = URLDecoder.decode(s2);
        System.out.println(s2);
        System.out.println(s3);
//        String subjectg = URLDecoder.decode(request.getParameter("WIDsubject"));
//        subjectg = new String(subjectg.getBytes(), "utf-8");
        String str  ="好";
        System.out.println(str);
        str = new String(str.getBytes("ISO-8859-1"),"utf-8" );
        System.out.println(str);
        System.out.println(Arrays.toString(str.getBytes()));//[-27, -91, -67],[-26, -65, -126, -17, -65, -67]
        String csn = Charset.defaultCharset().name();
        System.out.println(csn);
//        URLEncoder.encode(str, enc)
    }
}
