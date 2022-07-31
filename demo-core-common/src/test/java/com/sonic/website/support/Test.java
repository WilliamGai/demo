package com.sonic.website.support;

import com.sonic.website.core.common.support.Util;

public class Test {
    public static void main(String args[]){
        System.out.println(Runtime.getRuntime()
                .availableProcessors());
        
        try {
            int a = 1/0;
            System.out.println(a);
        } catch (Exception e) {
            String s = Util.toString(e);
            System.out.println(s);
            e.printStackTrace();
        }
        
    }
}
