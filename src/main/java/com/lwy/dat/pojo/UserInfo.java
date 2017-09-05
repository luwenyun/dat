package com.lwy.dat.pojo;

import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class UserInfo {
    private Integer userInfoId;

    private String tel;

    private String email;

    private String work;

    private String sex;

    private Date ctime;

    private String company;

    private String nickName;

    private Integer userId;

    public UserInfo(Integer userInfoId, String tel, String email, String work, String sex, Date ctime, String company, String nickName, Integer userId) {
        this.userInfoId = userInfoId;
        this.tel = tel;
        this.email = email;
        this.work = work;
        this.sex = sex;
        this.ctime = ctime;
        this.company = company;
        this.nickName = nickName;
        this.userId = userId;
    }

    public UserInfo() {
        super();
    }

    public Integer getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Integer userInfoId) {
        this.userInfoId = userInfoId;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work == null ? null : work.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company == null ? null : company.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}