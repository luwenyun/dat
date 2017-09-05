package com.lwy.dat.service;

import com.lwy.dat.pojo.User;
import com.lwy.dat.pojo.UserInfo;

/**
 * Created by lwy on 2017/4/30.
 */
public interface UserService {
    public int  getUserIdByname(String username);
    public User getUserById(int userId);
    public int insertUser(User user);
    public int insertUserInfo(UserInfo usrInfo);
    public int updatePwdById(String pwd,int userId);
    public int updateUserInfoById(UserInfo userInfo);
    public UserInfo getUserInfoById(int userId);
}
