package com.fly.demo.services;

import com.fly.demo.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IMessageService {
    // 发送站内信
    void create(Message message);

    // 根据id查询站内信
    Message selectById(Long id);

    // 根据用户id查询该用户未读的信息
    Integer selectUnreadCount(Long receiveUserId);

    // 根据用户id 查询站内信
    List<Message> selectByReceiveUserId(Long receiveUserId);

    // 更新指定站内信的状态。id是 站内信的id
    void updateStateById(Long id, Byte state);

    // 回复私信
    @Transactional
    void reply(Long repliesId, Message message);
}
