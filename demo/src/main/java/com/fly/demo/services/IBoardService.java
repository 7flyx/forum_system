package com.fly.demo.services;

import com.fly.demo.model.Board;

import java.util.List;

public interface IBoardService {
    // 查询 num条记录
    List<Board> selectByNum(Integer num);

    // 更新板块中的帖子数
    void addOneArticleCountById(Long id);
    // 根据板块id查询 板块信息
    Board selectById(Long id);

    // 板块中的帖子数量-1
    void subOneArticleCountById(Long id);
}
