package com.lwy.dat.service;/**
 * Created by lwy on 2017/4/30.
 */

import com.lwy.dat.dao.UserInfoMapper;
import com.lwy.dat.dao.UserMapper;
import com.lwy.dat.pojo.User;
import com.lwy.dat.pojo.UserInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 陆文云
 * @create 2017-04-30 14:05
 **/
@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    public int getUserIdByname(String username) {
        Integer id=this.userMapper.getUserIdByUserName(username);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public User getUserById(int userId) {
        return this.userMapper.getUserById(userId);
    }

    @Override
    public int insertUser(User user) {
        Integer id=this.userMapper.insert(user);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int insertUserInfo(UserInfo usrInfo) {
        Integer id=this.userInfoMapper.insert(usrInfo);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int updatePwdById(String pwd,int userId) {
        Integer id=this.userMapper.updatePwdById(pwd,userId);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public int updateUserInfoById(UserInfo userInfo) {
        Integer id=this.userInfoMapper.updateUserInfoById(userInfo);
        if(id==null){
            return 0;
        }
        return id.intValue();
    }

    @Override
    public UserInfo getUserInfoById(int userId) {
        return this.userInfoMapper.getUserInfoById(userId);
    }

}
