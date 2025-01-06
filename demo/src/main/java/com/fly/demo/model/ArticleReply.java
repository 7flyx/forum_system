package com.fly.demo.model;

import lombok.Data;

import java.util.Date;
@Data
public class ArticleReply {
    private Long id; // 编号

    private Long articleId; // 帖子id，关联Article

    private Long postUserId; // 回复的用户编号

    private Long replyId; // 楼中楼的功能

    private Long replyUserId; // 回复的正文

    private String content; // 需求中的点赞功能

    private Integer likeCount; // 需求中的点赞功能

    private Byte state; // 状态 0正常，1禁用

    private Byte deleteState;// 状态 0正常，1删除

    private Date createTime;

    private Date updateTime;

    // 关联对象---回复的发布者
    private User user;
}