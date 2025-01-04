package com.fly.demo.services.impl;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.dao.UserMapper;
import com.fly.demo.exception.ApplicationException;
import com.fly.demo.model.User;
import com.fly.demo.services.IUserService;
import com.fly.demo.utils.MD5Util;
import com.fly.demo.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public void createNormalUser(User user) {
        // 1、非空校验
        if (user == null || StringUtil.iSEmpty(user.getUsername())
                || StringUtil.iSEmpty(user.getNickname())
                || StringUtil.iSEmpty(user.getPassword())
                || StringUtil.iSEmpty(user.getSalt())) {
            // 打日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛异常
            throw  new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        // 2、按用户名查询用户信息
        User existsUser = userMapper.selectByUserName(user.getUsername());
        if (existsUser != null) {
            // 打印日志
            log.info(ResultCode.FAILED_USER_EXISTS.toString());
            // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
        }
        // 3、新增用户流程，设置默认值
        user.setGender((byte)2);
        user.setArticleCount(0);
        user.setIsAdmin((byte)0);
        user.setState((byte)0);
        user.setDeleteState((byte)0);
        // 当前时间
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);
        // 写入数据库
        int row = userMapper.insert(user);
        if (row != 1) {
            log.info(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        log.info("新增用户成功，username：" + user.getUsername());
    }

    @Override
    public User selectByUserName(String username) {
        if (StringUtil.iSEmpty(username)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 返回查询到的结果
        return userMapper.selectByUserName(username);
    }

    @Override
    public User login(String username, String password) {
        // 1、非空校验
        if (StringUtil.iSEmpty(username) || StringUtil.iSEmpty(password)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 2、按用户名查询用户信息
        User user = selectByUserName(username);
        // 3、对查询结果做 非空校验
        if(user == null) {
            log.warn(ResultCode.FAILED_LOGIN.toString() + ", username = " + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        // 4、对密码做校验
        String salt = user.getSalt();
        String ciphertext = MD5Util.md5Salt(password, salt); // 根据提供的密码制作密文，跟数据库的对比
        if (!ciphertext.equals(user.getPassword())) {
            log.warn(ResultCode.FAILED_LOGIN.toString() + ", 密码错误, username = " + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        log.info("登录成功, username = " + user.getUsername());
        // 登录成功
        return user;
    }

}
