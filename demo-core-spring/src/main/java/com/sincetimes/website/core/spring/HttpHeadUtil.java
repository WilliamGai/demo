package com.sincetimes.website.core.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sincetimes.website.core.common.support.Util;

public class HttpHeadUtil {
	private static final int MAX_PARAM_SIZE_DEFALUT_50 = 50;

	public static String SPLIT_TAG = "<+>";

	protected final Map<String, List<String>> headers;

	protected int localPort;

	protected String remoteAddr;

	protected String remoteHost;

	protected String scheme;

	protected boolean secure;

	protected int serverPort;

	public HttpHeadUtil(HttpServletRequest request) {
		this.localPort = request.getLocalPort();
		this.remoteAddr = request.getRemoteAddr();
		this.remoteHost = request.getRemoteHost();
		this.scheme = request.getScheme();
		this.secure = request.isSecure();
		this.serverPort = request.getServerPort();

		headers = new HashMap<>();
		for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
			String header = headerNames.nextElement();
			headers.put(header, Collections.list(request.getHeaders(header)));
		}
	}

	@Override
	public String toString() {
		return "HttpHead [headers=" + headers.toString() + "]";
	}

	/** 也可以使用@CrossOrigin注解 */
	public static void allowAccess(HttpServletResponse resp) {
		/* 跨域 begin */
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "GET");
		resp.setHeader("Allow", "GET");
		/* 跨域 end */
	}

	/**
	 * 获取真实的IP地址
	 */
	public static String getRealIpAddr(HttpServletRequest req) {
		String ip = req.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getRemoteAddr();
		}
		return ip;
	}

	public static String getCookieString(Cookie[] cookies) {
		if (cookies == null)
			return null;
		List<String> list = new ArrayList<>();
		for (Cookie c : cookies) {
			list.add("name=" + c.getName() + ",path=" + c.getPath() + ",value=" + c.getValue());
		}
		return list.toString();
	}

	public static String getCookieString(HttpServletRequest req) {
		return (getCookieString(req.getCookies()));
	}

	public static void setCookie(String name, String value, int age, HttpServletResponse resp) {
		Cookie c = new Cookie(name, value);
		c.setMaxAge(age);
		resp.addCookie(c);
	}

	public static boolean hasCookie(String name, HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		if (Util.isEmpty(cookies)) {
			return false;
		}
		for (Cookie c : cookies) {
			if (Objects.equals(c.getName(), name)) {
				return true;
			}
		}
		return false;
	}

	/** 静态方法,调试用,获取resp的请求头 */
	public static Map<String, Object> getRespHeads(HttpServletResponse resp) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("\nOrigin", resp.getHeader("Origin"));
		map.put("\nAccess-Control-Allow-Origin", resp.getHeader("Access-Control-Allow-Origin"));
		resp.getHeaderNames().forEach(
				x -> map.put("\nresp HeaderNames-<" + x, Util.join(resp.getHeaders(x), SPLIT_TAG)));
		return map;
	}

	/** 静态方法,调试用 */
	public static Map<String, String> getReqHeads(HttpServletRequest req) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("\nRequestURL", String.valueOf(req.getRequestURL()));
		Enumeration<String> names = req.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			map.put("\nHeaderNames->" + name, Util.join(req.getHeaders(name), SPLIT_TAG));
		}
		map.put("\nsimple heads", new HttpHeadUtil(req).toString());
		return map;
	}

	/** 静态方法,调试用 */
	public static Map<String, String> getReqFatHeads(HttpServletRequest req) {
		Map<String, String> map = new LinkedHashMap<>();
		/*
		 * map.put("\nauthType", req.getAuthType()); map.put("\nCharacterEncoding", req.getCharacterEncoding()); map.put("\nContentType",
		 * req.getContentType()); map.put("\nContextPath", req.getContextPath()); map.put("\nLocalAddr", req.getLocalAddr()); map.put("\nLocalName",
		 * req.getLocalName()); map.put("\nMethod", req.getMethod()); map.put("\nPathInfo", req.getPathInfo()); map.put("\nPathTranslated",
		 * req.getPathTranslated()); map.put("\nProtocol", req.getProtocol()); map.put("\nQueryString", req.getQueryString()); map.put("\nRemoteAddr",
		 * req.getRemoteAddr()); map.put("\nRemoteHost", req.getRemoteHost()); map.put("\nRemoteUser", req.getRemoteUser());
		 * map.put("\nRequestedSessionId", req.getRequestedSessionId()); map.put("\nRequestURI", req.getRequestURI()); map.put("\nScheme",
		 * req.getScheme()); map.put("\nServerName", req.getServerName()); map.put("\nServletPath", req.getServletPath()); map.put("\nContentLength",
		 * String.valueOf(req.getContentLength())); map.put("\nServerName", req.getServerName()); map.put("\nLocalPort",
		 * String.valueOf(req.getLocalPort())); map.put("\nRemotePort", String.valueOf(req.getRemotePort())); map.put("\nRequestURL",
		 * String.valueOf(req.getRequestURL())); map.put("\nServerPort", String.valueOf(req.getServerPort()));
		 */
		Enumeration<String> names = req.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			map.put("\nHeaderNames->" + name, Util.join(req.getHeaders(name), SPLIT_TAG));
		}
		map.put("\n\n simple heads", new HttpHeadUtil(req).toString());
		return map;
	}

	/**
	 * 调试用
	 */
	public static Map<String, String> getParamsMap(HttpServletRequest req, int size_limit) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> names = req.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String param = req.getParameter(name);
			if (param.length() >= size_limit) {
				param = param.substring(0, size_limit - 1);
			}
			map.put(name, req.getParameter(name));
		}
		return map;
	}

	public static Map<String, String> getParamsMapLimit(HttpServletRequest req) {
		return getParamsMap(req, MAX_PARAM_SIZE_DEFALUT_50);
	}

	public static Map<String, String> getParamsMap(HttpServletRequest req) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> names = req.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			map.put(name, req.getParameter(name));
		}
		return map;
	}

	/**
	 * 2017年08月07日23:37:17,这种方方式如果Controller被AOP后就无法获取requestMappings注解了,因为注入Spring的是代理类 TODO:反射获取
	 */
	public static Map<Object, Object> requestMappingsDetail(ApplicationContext context) {
		Map<String, Object> map = context.getBeansWithAnnotation(Controller.class);
		Map<Object, Object> classMap = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			// LogCore.BASE.info("look controller key= {}, v={}", key, value.getClass().getName());
			Map<Object, Object> mappingMap = new LinkedHashMap<>();
			String[] classMappingStrs = getReqMappingValues(value);
			ArrayList<String[]> list = new ArrayList<>();
			for (Method m : value.getClass().getDeclaredMethods()) {
				String[] methodStrs = getReqMappingValuesFromMethod(m);
				if (!Util.isEmpty(methodStrs)) {
					list.add(methodStrs);
				}
			}
			for (String str : classMappingStrs) {
				for (String[] ss : list) {
					for (String s : ss) {
						mappingMap.put(str + s, key);
					}
				}
			}
			classMap.put(key, mappingMap);
		}
		return classMap;
	}

	public static Map<Object, Object> requestMappings(ApplicationContext context) {
		Map<String, Object> map = context.getBeansWithAnnotation(Controller.class);
		Map<Object, Object> mappingsMap = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			// LogCore.BASE.info("look controller key= {}, v={}", key, value.getClass().getName());
			String[] classMappingStrs = getReqMappingValues(value);
			ArrayList<String[]> list = new ArrayList<>();
			for (Method m : value.getClass().getDeclaredMethods()) {
				String[] methodStrs = getReqMappingValuesFromMethod(m);
				if (!Util.isEmpty(methodStrs)) {
					list.add(methodStrs);
				}
			}
			for (String str : classMappingStrs) {
				for (String[] ss : list) {
					for (String s : ss) {
						mappingsMap.put(str + s, key);
					}
				}
			}
		}
		return mappingsMap;
	}

	/**
	 * 获取类的requestMaping注解的value值
	 * 
	 * @param value
	 * @return
	 */
	private static String[] getReqMappingValues(Object value) {
		String[] v3 = Util.toNullDefalut(value.getClass().getAnnotation(RequestMapping.class),
				RequestMapping::value, new String[] { "" });
		return v3;
	}

	private static String[] getReqMappingValuesFromMethod(Method method) {
		String[] v3 = Util.toNullDefalut(method.getAnnotation(RequestMapping.class), RequestMapping::value,
				new String[] {});
		// LogCore.BASE.info("====={},{},{}", method.getDeclaringClass().getName(), method.getName(),method.getAnnotation(RequestMapping.class));
		return v3;
	}

}