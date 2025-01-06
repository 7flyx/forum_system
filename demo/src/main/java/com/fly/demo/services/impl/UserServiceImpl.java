package com.fly.demo.services.impl;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.dao.UserMapper;
import com.fly.demo.exception.ApplicationException;
import com.fly.demo.model.User;
import com.fly.demo.services.IUserService;
import com.fly.demo.utils.MD5Util;
import com.fly.demo.utils.StringUtil;
import com.fly.demo.utils.UUIDUtil;
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
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
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
        user.setGender((byte) 2);
        user.setArticleCount(0);
        user.setIsAdmin((byte) 0);
        user.setState((byte) 0);
        user.setDeleteState((byte) 0);
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
        if (user == null) {
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

    @Override
    public User selectById(Long id) {
        // 1、非空校验
        if (id == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 2、调用dao层代码 进行查询
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if (id == null || id < 0) {
            log.info(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        // 查询用户
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString() + ", user id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        // 更新数据
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount() + 1);
        // 更新数据库
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数大于1.");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void subOneArticleCountById(Long id) {
        if (id == null || id < 0) {
            log.info(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        // 查询用户
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString() + ", user id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        // 更新数据
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount() - 1);
        if (updateUser.getArticleCount() < 0) { // 发帖数小于0，直接归0
            updateUser.setArticleCount(0);
        }
        // 更新数据库
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数大于1.");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyInfo(User user) {
        if (user == null || user.getId() == null || user.getId() <= 0) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        User existsUser = userMapper.selectByPrimaryKey(user.getId());
        if (existsUser == null) { // 校验数据库中的user
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        // 3、定义一个标志位
        boolean checkAttr = false; // false 表示没有 校验通过
        User updateUser = new User(); // 用来更新的对象。防止用户传入的User对象设置了其他的属性，当使用动态SQL进行更新的时候，覆盖了没有经过校验的字段
        updateUser.setId(user.getId());
        // 对每一个参数进行校验 并赋值
        if(!StringUtil.iSEmpty(user.getUsername())
            && !user.getUsername().equals(existsUser.getUsername())) {
            User checkUser = userMapper.selectByUserName(user.getUsername());
            if (checkUser != null) {
                // 用户已存在
                log.warn(ResultCode.FAILED_USER_EXISTS.toString());
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
            }
            // 数据库中没有找到相应的用户名，表示可以修改
            updateUser.setUsername(user.getUsername());
            checkAttr = true;
        }

        // 校验昵称
        if (!StringUtil.iSEmpty(user.getNickname())
                && !user.getNickname().equals(existsUser.getNickname())) {
            updateUser.setNickname(user.getNickname());
            checkAttr = true;
        }
        // 校验性别
        if(user.getGender() != null && existsUser.getGender() != user.getGender()) {
            updateUser.setGender(user.getGender());
            checkAttr = true;
        }
        // 校验邮箱
        if (!StringUtil.iSEmpty(user.getEmail())
            && !user.getEmail().equals(existsUser.getEmail())) {
            updateUser.setEmail(user.getEmail());
            checkAttr = true;
        }
        // 校验电话号码
        if (!StringUtil.iSEmpty(user.getPhoneNum())
            && !user.getPhoneNum().equals(existsUser.getPhoneNum())) {
            updateUser.setPhoneNum(user.getPhoneNum());
            checkAttr = true;
        }
        // 校验个人简介
        if (!StringUtil.iSEmpty(user.getRemark())
                && !user.getRemark().equals(existsUser.getRemark())) {
            updateUser.setRemark(user.getRemark());
            checkAttr = true;
        }
        if (!checkAttr) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用dao层
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void modifyPassword(Long id, String oldPassword, String newPassword) {
        if (id == null || id <= 0 || StringUtil.iSEmpty(oldPassword) || StringUtil.iSEmpty(newPassword)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        // 校验老密码是否正确
        String oldEncryptPassword = MD5Util.md5Salt(oldPassword, user.getSalt());
        if (!oldEncryptPassword.equals(user.getPassword())) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
        String salt = UUIDUtil.UUID_32();
        String password = MD5Util.md5Salt(newPassword, salt);
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setSalt(salt);
        updateUser.setPassword(password); // 新密码对应的密文
        updateUser.setUpdateTime(new Date());
        // dao层代码
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}
