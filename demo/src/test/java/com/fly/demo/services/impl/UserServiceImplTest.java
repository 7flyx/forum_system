package com.fly.demo.services.impl;

import com.fly.demo.model.User;
import com.fly.demo.utils.MD5Util;
import com.fly.demo.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {
    @Resource
    private UserServiceImpl userService;
//    @Test
    void createNormalUser() {
        User user = new User();
        user.setUsername("admin1");
        user.setNickname("admin1");
        // 定义一个原始的密码
        String password = "123456";
        // 生成 盐
        String salt = UUIDUtil.UUID_32();
        // 生成密码的密文
        String ciphertext = MD5Util.md5Salt(password, salt);
        user.setPassword(ciphertext); // 加密后的密码
        user.setSalt(salt);
        userService.createNormalUser(user); // 调用services层的代码
        // 打印结果
        System.out.println(user);
    }

    @Transactional // 事务回滚，在测试完成之后，会回滚。不会污染数据库的数据
    @Test
    void selectByUserName() {
        User user = userService.selectByUserName("admin1");
        System.out.println(user);
    }

    @Test
    void login() {
        User user = userService.login("admin1", "222222");
        System.out.println(user);
    }

    @Test
    void selectById() {
        User user  = userService.selectById(1L);
        System.out.println(user);
    }
}