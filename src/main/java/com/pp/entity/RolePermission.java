package com.pp.entity;

import java.util.Date;

public class RolePermission {
    private Long roleId;

    private Long permssionId;

    private Date createTime;

    private String createBy;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermssionId() {
        return permssionId;
    }

    public void setPermssionId(Long permssionId) {
        this.permssionId = permssionId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }
}