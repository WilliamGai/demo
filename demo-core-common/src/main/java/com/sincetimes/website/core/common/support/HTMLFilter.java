package com.sincetimes.website.core.common.support;

import java.util.Objects;
import java.util.function.Function;

public final class HTMLFilter {

    public static String filter(String message) {
    	LogCore.BASE.info("{}",message);
        if (message == null)
            return (null);
        char content[] = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        StringBuffer result = new StringBuffer(content.length + 50);
        for (int i = 0; i < content.length; i++) {
            switch (content[i]) {
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '"':
                result.append("&quot;");
                break;
            default:
                result.append(content[i]);
            }
        }
        return (result.toString());
    }
    public static String printHtml(String message){
    	if(Util.isEmpty(message)){
    		return message;
    	}
    	return filter(message, (s)->s.replace("&lt;", "<br>&lt;"));
    }
    public static String filter(String message, Function<String, String> andthen_func){
    	Objects.requireNonNull(andthen_func, "function is null!");
    	return andthen_func.apply(filter(message));
    }
}