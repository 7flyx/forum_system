package com.fly.demo.dao;

import com.fly.demo.model.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    int insert(Article row);

    int insertSelective(Article row);

    Article selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Article row);

    int updateByPrimaryKeyWithBLOBs(Article row);

    int updateByPrimaryKey(Article row);

    // 查询所有帖子列表
    List<Article> selectAll();
    // 根据板块号 查询 帖子列表
    List<Article> selectAllByBoardId(Long boardId);

    // 根据帖子id查询详情
    Article selectDetailById(@Param("id") Long id);

    // 根据用户id查询 帖子列表
    List<Article> selectByUserId(@Param("userId") Long userId);
}