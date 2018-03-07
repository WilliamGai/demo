package com.sonic.website.core.common.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 参考md5
 * @See {@link MD5}
 */
public class SHA256 {
	public static String sha256(String s){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
		    byte[] data = md.digest(Util.getUtfBytes(s));
		    StringBuilder sb = new StringBuilder();
		    for (int i = 0; i < data.length; i++) {
		    	int v = ((int)data[i]) & 0xff;
				if(v < 16){
					sb.append("0");
				}
				sb.append(Integer.toHexString(v));
		    }
		    return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    return null;
	}
}
