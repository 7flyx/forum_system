package com.fly.demo.services;

import com.fly.demo.model.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleService {
    /**
     * 发布帖子
     * @param article 要发布的帖子
     */
    @Transactional // 当前方法中的执行过程会被事务管理起来
    void create(Article article);

    // 查询所有帖子
    List<Article> selectAll();
    // 根据板块号 进行查询帖子
    List<Article> selectAllByBoardId(@Param("boardId") Long boardId);
    // 根据帖子id查询详情
    Article selectDetailById(Long id);

}