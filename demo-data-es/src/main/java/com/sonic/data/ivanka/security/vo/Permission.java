package com.sonic.data.ivanka.security.vo;

import java.io.Serializable;

import com.sonic.website.core.common.vo.ToStringAbstract;

public class Permission extends ToStringAbstract implements Serializable{
    private static final long serialVersionUID = 6075482255307965611L;
    private String name;
    private String uri;
    
    public Permission() {
        super();
    }
    
    public Permission(String name, String uri) {
        super();
        this.name = name;
        this.uri = uri;
    }
    
    public Permission(Permission permission) {
        super();
        this.name = permission.name;
        this.uri = permission.uri;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUri() {
        return uri;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }
}
