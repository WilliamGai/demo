package com.sonic.website.core.common.extension.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class TestGetMethodParamName2 {
        public String test(String name, Integer age){
            return null;
        }
        public static String printMethods(){
            StringBuilder sb = new StringBuilder();
            for (Method m : TestGetMethodParamName2.class.getDeclaredMethods()) {
                sb.append(m.getReturnType().getSimpleName());
                sb.append("  ").append(m.getName());
                sb.append("(");
                Parameter[]  prams = m.getParameters();
                for (Parameter p : prams) {
                    sb.append(p.getType().getSimpleName()+" "+p.getName());
            }
                sb.append(")\n");
            }
            return sb.toString();
        }
        public static void main(String args[]){
            String s = printMethods();
            System.out.println(s);
        }
    }
