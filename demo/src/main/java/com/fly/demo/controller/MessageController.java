package com.fly.demo.controller;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.config.AppConfig;
import com.fly.demo.model.Message;
import com.fly.demo.model.User;
import com.fly.demo.services.IMessageService;
import com.fly.demo.services.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Api(tags = "站内信接口")
@RequestMapping("/message")
public class MessageController {
    @Resource
    private IMessageService messageService;
    @Resource
    private IUserService userService;

    @ApiOperation("发送站内信")
    @PostMapping("/send")
    public AppResult send(HttpServletRequest req,
                          @ApiParam("接受者id") @RequestParam("receiveUserId") @NonNull Long receivedUserId,
                          @ApiParam("内容") @RequestParam("content") @NonNull String content) {
        // 1、当前登录用户的状态，如果是禁言状态 不能发站内信
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        if (user == null || user.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 2、不能给自己发站内信
        if(user.getId() == receivedUserId) {
            return AppResult.failed("不能给自己发送信息");
        }
        // 3、校验接受者是否存在
        User receivedUser = userService.selectById(receivedUserId);
        if (receivedUser == null || receivedUser.getDeleteState() == 1) {
            return AppResult.failed("接受者状态异常");
        }
        // 4、封装对象
        Message message = new Message();
        message.setReceiveUserId(receivedUserId);
        message.setPostUserId(user.getId());
        message.setContent(content);
        // 5、调用Services层接口
        messageService.create(message);
        // 6、返回结果
        return AppResult.success("发送成功");
    }

    @ApiOperation("获取站内信未读数")
    @GetMapping("/getUnreadCount")
    public AppResult<Integer> getUnreadCount(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        Integer count = messageService.selectUnreadCount(user.getId());
        return AppResult.success(count);
    }

    @ApiOperation("查询用户的所有站内信")
    @GetMapping("/getAll")
    public AppResult<List<Message>> getAll(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        List<Message> messages = messageService.selectByReceiveUserId(user.getId());
        return AppResult.success(messages);
    }

    @ApiOperation("更新为已读")
    @PostMapping("/markRead")
    public AppResult markRead( HttpServletRequest req,
            @ApiParam("站内信id") @RequestParam("id") @NonNull Long id) {
        // 1、根据id查询站内信
        Message message = messageService.selectById(id);
        // 2、站内信是否存在
        if (message == null || message.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }
        // 3、站内信是不是自己的
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        if(user.getId() != message.getReceiveUserId()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        // 调用Services
        messageService.updateStateById(id, (byte)1);
        return AppResult.success();
    }

    @ApiOperation("回复站内信")
    @PostMapping("/reply")
    public AppResult reply(HttpServletRequest req,
                           @ApiParam("要回复的站内信id") @RequestParam("repliedId") @NonNull Long repliesId,
                           @ApiParam("站内信的内容") @RequestParam("content") @NonNull String content) {
        // 校验当前登录用户的状态
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) { // 用户已经禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 校验要回复的站内信的状态
        Message existsMessage = messageService.selectById(repliesId);
        if (existsMessage == null || existsMessage.getDeleteState()== 1) {
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }
        // 不能给自己回复
        if(user.getId() == existsMessage.getPostUserId()) {
            return AppResult.failed("不能给自己的站内信回复");
        }
        // 构造对象
        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(existsMessage.getPostUserId());
        message.setContent(content);
        messageService.reply(repliesId, message);
        return AppResult.success();
    }
}
