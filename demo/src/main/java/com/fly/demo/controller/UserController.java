package com.fly.demo.controller;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.config.AppConfig;
import com.fly.demo.model.User;
import com.fly.demo.services.IUserService;
import com.fly.demo.utils.MD5Util;
import com.fly.demo.utils.StringUtil;
import com.fly.demo.utils.UUIDUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Api(tags = "用户接口") // 对当前这个controller进行说明
@RequestMapping("/user")
@RestController // 返回一个数据（http body）的controller
public class UserController {
    @Resource
    private IUserService userService;
    /**
     * 用户注册功能
     * @param username 用户名
     * @param nickname 昵称
     * @param password 密码
     * @param passwordRepeat 重复密码
     * @return 是否注册成功
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public AppResult register(@ApiParam("用户名") @RequestParam("username") @NonNull String username,
                              @ApiParam("昵称") @RequestParam("nickname") @NonNull String nickname,
                              @ApiParam("密码") @RequestParam("password") @NonNull String password,
                              @ApiParam("确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat) {
        // 1、传统参数校验的方案
//        if (StringUtil.iSEmpty(username) || StringUtil.iSEmpty(nickname)
//                || StringUtil.iSEmpty(password) || StringUtil.iSEmpty(passwordRepeat)) {
//            // services层 需要抛异常，在controller层 只需要给前端返回错误信息即可
//            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
//        }

        // 2、spring系列使用 @NonNull注解进行校验. 如上

        // 校验两次密码是否相同
        if (!password.equals(passwordRepeat)) {
            log.warn(ResultCode.FAILED_TOW_PWD_NOT_SAME.toString());
            return AppResult.failed(ResultCode.FAILED_TOW_PWD_NOT_SAME);
        }
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        String salt = UUIDUtil.UUID_32(); // 盐
        String encryptPassword = MD5Util.md5Salt(password, salt); // 原密码+盐进行MD5加密
        user.setPassword(encryptPassword);
        user.setSalt(salt);
        userService.createNormalUser(user); // 调用services层代码

        return AppResult.success(ResultCode.SUCCESS);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AppResult login(HttpServletRequest req,
                           @ApiParam("用户名") @RequestParam("username") @NonNull String username,
                           @ApiParam("密码") @RequestParam("password") @NonNull String password) {
        // 1、调用Services层的登录代码，返回user对象
        User user = userService.login(username, password);
        // Services层已经判断 user==null，此处不做处理
        // 2、如果登录成功，把user对象设置到session作用域中
        HttpSession session = req.getSession(true); // true 表示没有session对象时，会自动创建一个
        session.setAttribute(AppConfig.USER_SESSION, user);
        // 3、返回结果
        return AppResult.success();
    }

    @ApiOperation("查询用户")
    @GetMapping("/info")
    public AppResult<User> getUserInfo(HttpServletRequest req,
                                       @ApiParam("用户Id") @RequestParam(value = "id", required = false) Long id) {
        User user = null;
        if (id == null) {
            // 1、如果id为空，从session中返回当前登录的用户信息
            HttpSession session = req.getSession(false);
            // 判断session和用户信息是否有效
            if (session == null || session.getAttribute(AppConfig.USER_SESSION) == null) {
                // 用户没有登录，返回错信息
                return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
            }
            user = (User)session.getAttribute(AppConfig.USER_SESSION);
        } else {
            // 2、id不为空，就从数据库中查询用户信息
            user = userService.selectById(id);
        }
        if (user == null) {
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        return AppResult.success(user);
    }


    @ApiOperation("退出登录")
    @GetMapping("/logout")
    public AppResult logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            // 用户已经登录，销毁session即可
            session.invalidate();
        }
        return AppResult.success("退出成功");
    }

}
