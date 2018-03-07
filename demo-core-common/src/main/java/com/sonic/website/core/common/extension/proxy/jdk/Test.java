package com.sonic.website.core.common.extension.proxy.jdk;


public class Test {

	public static void main(String[] args) {
		Student student= new Student();
		ProxyFactory pf = new ProxyFactory();
		StudentInterface st = pf.createProxy(student);
		st.sayHello("william");
		
	}

}
