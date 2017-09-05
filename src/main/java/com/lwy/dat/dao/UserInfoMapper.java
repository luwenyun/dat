package com.lwy.dat.dao;

import com.lwy.dat.pojo.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoMapper {
    Integer deleteByPrimaryKey(Integer userInfoId);

    Integer insert(UserInfo record);

    Integer insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer userInfoId);
    Integer updateUserInfoById(UserInfo userInfo);

    UserInfo  getUserInfoById(Integer userId);
    Integer updateByPrimaryKey(UserInfo record);
}