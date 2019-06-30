package com.pp.entity;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

public class User implements Serializable{
	
	private static final long serialVersionUID = 5663648787359429182L;

	private Long id;
    
    @Length(min=4, max=20, message="账号长度为4-20位字符")
    private String account;

    @Pattern(regexp="^\\w{6,20}$", message="密码由6到20位字母或数字组成")
    private String password;

    private Long status;

    @Length(max=50, message="昵称超出长度50位字符")
    private String nickname;

    private Date birthday;

    @Pattern(regexp="^1[3-9]\\d{9}$", message="手机号不符合规则")
    private String phone;

    @Email
    private String email;

    @Length(max=250, message="地址超出长度250位字符")
    private String address;

    private String headImg;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createTime;

    @Length(max=200, message="签名超出长度200位字符")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg == null ? null : headImg.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}