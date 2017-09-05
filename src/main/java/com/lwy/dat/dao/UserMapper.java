package com.lwy.dat.dao;

import com.lwy.dat.pojo.User;
import com.lwy.dat.pojo.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    Integer deleteByPrimaryKey(Integer userId);

    Integer insert(User record);

    Integer insertSelective(User record);
    Integer getUserIdByUserName(String username);
    User selectByPrimaryKey(Integer userId);
    User getUserById(Integer userId);
    Integer updateByPrimaryKeySelective(User record);
    Integer updatePwdById(@Param("password")String password,@Param("userId")Integer userId);
    Integer updateUserInfoById(UserInfo userInfo);
    Integer updateByPrimaryKey(User record);
}