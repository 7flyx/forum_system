package com.fly.demo.services.impl;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.dao.MessageMapper;
import com.fly.demo.dao.UserMapper;
import com.fly.demo.exception.ApplicationException;
import com.fly.demo.model.Message;
import com.fly.demo.model.User;
import com.fly.demo.services.IMessageService;
import com.fly.demo.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class IMessageServiceImpl implements IMessageService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private MessageMapper messageMapper;
    @Override
    public void create(Message message) {
        if (message == null || message.getPostUserId() == null || message.getReceiveUserId() == null || StringUtil.iSEmpty(message.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 校验接受者是否存在
        User user = userMapper.selectByPrimaryKey(message.getReceiveUserId());
        if (user == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 设置默认值
        message.setState((byte)0);
        message.setDeleteState((byte)0);
        Date date = new Date();
        message.setCreateTime(date);
        message.setUpdateTime(date);
        int row = messageMapper.insert(message);
        if(row != 1) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
    }

    @Override
    public Integer selectUnreadCount(Long receiveUserId) {
        if (receiveUserId == null || receiveUserId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用dao层
        Integer count = messageMapper.selectUnreadCount(receiveUserId);
        if (count == null) { // 正常的查询 是不可能出现null
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        return count;
    }

    @Override
    public List<Message> selectByReceiveUserId(Long receiveUserId) {
        if (receiveUserId == null || receiveUserId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        List<Message> messages = messageMapper.selectByReceiveUserId(receiveUserId);
        return messages;
    }

    @Override
    public void updateStateById(Long id, Byte state) {
        if (id == null || id <= 0 || state < 0 || state > 2) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 构造一个更新对象
        Message updateMessage = new Message();
        updateMessage.setId(id);
        updateMessage.setState(state);
        updateMessage.setUpdateTime(new Date());
        int row = messageMapper.updateByPrimaryKeySelective(updateMessage);
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public Message selectById(Long id) {
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Message message = messageMapper.selectByPrimaryKey(id);
        return message;
    }

    @Override
    public void reply(Long repliesId, Message message) {
        if (repliesId == null || repliesId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        // 校验repliesId对应的站内信状态
        Message existsMessage = messageMapper.selectByPrimaryKey(repliesId);
        if (existsMessage ==  null || existsMessage.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_MESSAGE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS));
        }
        // 更新状态
        updateStateById(repliesId, (byte)2);
        // 回复的内容写入数据库
        create(message);
    }
}



