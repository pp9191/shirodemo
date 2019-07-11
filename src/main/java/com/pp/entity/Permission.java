package com.pp.entity;

import java.io.Serializable;

public class Permission implements Serializable{
    
	private static final long serialVersionUID = -3125446411610790381L;

	private Long id;

    private String urlMapping;

    private String remark;

    private String type;

    private Long pid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrlMapping() {
        return urlMapping;
    }

    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping == null ? null : urlMapping.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}