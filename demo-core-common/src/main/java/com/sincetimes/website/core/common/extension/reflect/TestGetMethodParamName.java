package com.sincetimes.website.core.common.extension.reflect;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestGetMethodParamName {
	public String test(String name, Integer age) {
		return null;
	}

	public static String printMethods() {
		StringBuilder sb = new StringBuilder();
		for (Method m : TestGetMethodParamName.class.getDeclaredMethods()) {
			sb.append(m.getReturnType().getSimpleName());
			sb.append("  ").append(m.getName());
			sb.append("(");
			String paramString = Stream.of(m.getParameters())
			            .map(p -> p.getType().getSimpleName() + " " + p.getName())
			            .collect(Collectors.joining(", "));
			sb.append(paramString);
			sb.append(")\n");
		}
		return sb.toString();
	}

	public static void main(String args[]) {
		String s = printMethods();
		System.out.println(s);
	}
}
