package com.fly.demo.services;

import com.fly.demo.model.ArticleReply;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleReplyService {

    @Transactional
    void create(ArticleReply articleReply);
    List<ArticleReply> selectByArticleId(Long articleId);
}
