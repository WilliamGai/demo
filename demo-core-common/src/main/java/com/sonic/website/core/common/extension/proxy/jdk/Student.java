package com.sonic.website.core.common.extension.proxy.jdk;

public class Student implements StudentInterface {
    private String name;
    private int age;
    private String gender;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    @Override
    public void sayHello(String msg) {
        System.out.println("hello"+msg);
    }
    
}
