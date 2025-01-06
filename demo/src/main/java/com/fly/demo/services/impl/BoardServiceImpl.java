package com.fly.demo.services.impl;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.dao.BoardMapper;
import com.fly.demo.exception.ApplicationException;
import com.fly.demo.model.Board;
import com.fly.demo.services.IBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class BoardServiceImpl implements IBoardService {
    @Resource
    private BoardMapper boardMapper;
    @Override
    public List<Board> selectByNum(Integer num) {
        // 1、非空校验
        if (num <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 2、调用DAO层查询数据库
        List<Board> result = boardMapper.selectByNum(num);
        return result; // 使用方自己做校验
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if (id == null || id < 0) {
            log.info(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
         // 查询对于的板块数据
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString() + ", board id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        // 更新帖子数量
        Board updateBoard = new Board(); // 重新创建一个用来更新的类
        updateBoard.setId(board.getId());
        updateBoard.setArticleCount(board.getArticleCount() + 1);
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));

        }
    }

    @Override
    public Board selectById(Long id) {
        if (id == null || id < 0) {
            log.info(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        // 调用dao层查询数据
        Board board = boardMapper.selectByPrimaryKey(id);
        return board;
    }

    @Override
    public void subOneArticleCountById(Long id) {
        // 非空校验
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        // 构造更新对象
        Board updateBoard = new Board();
        updateBoard.setId(board.getId());
        updateBoard.setArticleCount(board.getArticleCount() - 1);
        if (updateBoard.getArticleCount() < 0) {
            updateBoard.setArticleCount(0);
        }
        // dao层
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}
