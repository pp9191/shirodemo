package com.pp.entity;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

public class Role implements Serializable{
    
	private static final long serialVersionUID = 145385866889782648L;

	private Long id;

	@Pattern(regexp="^\\w{4,20}$", message="角色名由6到20位字母或数字组成")
    private String rolename;

	@Length(max=100, message="描述超过最大限度100字符")
    private String remark;

    private Date createTime;

    private String createBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename == null ? null : rolename.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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