package com.fly.demo.dao;

import com.fly.demo.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message row);

    int insertSelective(Message row);

    Message selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Message row);

    int updateByPrimaryKey(Message row);

    // 根据用户id查询该用户未读的信息
    Integer selectUnreadCount(@Param("receiveUserId") Long receiveUserId);

    // 根据用户id 查询站内信
    List<Message> selectByReceiveUserId(@Param("receiveUserId") Long receiveUserId);
}