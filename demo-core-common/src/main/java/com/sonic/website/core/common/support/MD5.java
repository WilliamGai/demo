package com.sonic.website.core.common.support;

import java.security.MessageDigest;

import com.sonic.website.core.common.core.Utils;
/**
 * @See {@link Utils#md5(String)}
 */
public class MD5 {
      public static String md5(String source) {
            try {
              MessageDigest md = MessageDigest.getInstance("MD5");
              md.update(source.getBytes("UTF-8"));
              byte[] bytes = md.digest();

              char[] chars = new char[32];
              char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
              int k = 0;
              for (int i = 0; i < 16; i++) {
                byte byte0 = bytes[i];
                chars[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
                chars[(k++)] = hexDigits[(byte0 & 0xF)];
              }
              return new String(chars);
            }
            catch (Exception e) {
              e.printStackTrace();
            }return null;
          }
}
