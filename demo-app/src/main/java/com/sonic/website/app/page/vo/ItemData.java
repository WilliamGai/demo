package com.sonic.website.app.page.vo;

public class ItemData{

    private String name;
    private Object data;
    
    public ItemData() {
        super();
    }
    
    public ItemData(String name, Object data) {
        this.name = name;
        this.data = data;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T)data;
    }
    public void setData(Object data) {
        this.data = data;
    }
}
