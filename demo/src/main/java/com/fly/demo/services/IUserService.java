package com.fly.demo.services;

import com.fly.demo.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

/**
 *  用户接口
 */
public interface IUserService {
    /**
     *  创建一个普通用户。没抛异常 就是正常的
     */
    void createNormalUser(User user);
    // 根据用户名查询用户信息
    User selectByUserName(String username);

    // 登录功能
    User login(String username, String password);

    // 根据id 查询用户信息
    User selectById(Long id);
}
