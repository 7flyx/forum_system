package com.fly.demo.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fly.demo.model.ArticleReply;
import com.fly.demo.services.IArticleReplyService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleReplyServiceImplTest {
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private IArticleReplyService articleReplyService;
    @Test
    @Transactional
    void selectByArticleId() throws JsonProcessingException {
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(3L);
        System.out.println(objectMapper.writeValueAsString(articleReplies));
    }
}