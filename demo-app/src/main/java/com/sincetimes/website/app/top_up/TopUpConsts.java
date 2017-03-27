package com.sincetimes.website.app.top_up;

public interface TopUpConsts {
	/** 微信相关的常量 **/
	public static final String hz_OpenId="gh_435865907d4b";

	public static final String hz_Appid = "wx7998fd7b5413a0d9";

	public static final String hz_AppSecret = "26ae4c02f587e750f1a7ab027dc8979c";
	
	public String DEFAULT_APP_ID = "wx9ae18f115734dc5f"; 
	
	public String DEFAULT_APP_SECRET = "db5b0cb86f1ff0ec6e1e0e5b5b190f20"; 
	
	/*畅天游支付——华清飞扬
	提交KEY：asasa1
	回调KEY： zvzvz2
	公司ID:1069
	接口密码:f23385
	测试地址：http://test.800617.com:6001/submit.aspx 
	*/
	public static String CTY_PAY_URL_TEST = "http://test.800617.com:6001/submit.aspx";
	public static String CTY_CAMPANY_ID_TEST = "1069";
	public static String CTY_CALL_BACK_KEY_TEST = "zvzvz2";
	public static String CTY_SUBMIT_KEY_TEST = "asasa1";
	public static String CTY_INTERFACE_PSW_TEST = "f23385";
	
	/*畅天游支付——华清飞扬
	提交KEY：asasa1
	回调KEY： zvzvz2
	公司ID:1069
	接口密码:f23385
	测试地址：http://test.800617.com:6001/submit.aspx 
	*/
	
	/*以下是正式的,没上线请不要打开*/
	public static String CTY_PAY_URL = "http://wr.800617.com:6001/submit.aspx";
	public static String CTY_CAMPANY_ID = "1328";
	public static String CTY_CALL_BACK_KEY = "f8tptj";
	public static String CTY_SUBMIT_KEY = "x353d2";
	public static String CTY_INTERFACE_PSW = "4c799d";
}

