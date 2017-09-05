package com.lwy.dat.pojo;

import org.springframework.stereotype.Component;

@Component
public class User {
    private Integer userId;

    private String userName;

    private String pwd;

    public User(Integer userId, String userName, String pwd) {
        this.userId = userId;
        this.userName = userName;
        this.pwd = pwd;
    }

    public User() {
        super();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd == null ? null : pwd.trim();
    }
}