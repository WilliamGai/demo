package com.sonic.data.ivanka.security.vo;

public class RolePermission extends Permission {
    private static final long serialVersionUID = -5334280888581980324L;
    private String roleName;
    
    public RolePermission(){
        super();
    }
    public RolePermission(Permission permission, String roleName) {
        super(permission);
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
}
