package com.fly.demo.dao;

import com.fly.demo.model.Board;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {
    int insert(Board row);

    int insertSelective(Board row);

    Board selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Board row);

    int updateByPrimaryKey(Board row);
}