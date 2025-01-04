package com.fly.demo.services.impl;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.dao.ArticleMapper;
import com.fly.demo.exception.ApplicationException;
import com.fly.demo.model.Article;
import com.fly.demo.model.Board;
import com.fly.demo.model.User;
import com.fly.demo.services.IArticleService;
import com.fly.demo.services.IBoardService;
import com.fly.demo.services.IUserService;
import com.fly.demo.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements IArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private IUserService userService;
    @Resource
    private IBoardService boardService;
    @Override
    public void create(Article article) {
        if (article == null || article.getUserId() == null || article.getBoardId() == null
                || StringUtil.iSEmpty(article.getTitle())
                || StringUtil.iSEmpty(article.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        article.setVisitCount(0); // 访问量
        article.setReplyCount(0); // 回复数
        article.setLikeCount(0); // 点赞数
        article.setDeleteState((byte)0);
        article.setState((byte)0);
        Date date = new Date();
        article.setCreateTime(date);
        article.setUpdateTime(date);
        int row = articleMapper.insert(article);
        if (row <= 0) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        // 更新用户的发帖数
        User user = userService.selectById(article.getUserId());
        if (user == null) {
            log.warn(ResultCode.FAILED_CREATE.toString() + ", 发帖失败, user id = " + article.getUserId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        // 更新用户的发帖数
        userService.addOneArticleCountById(user.getId());
        // 获取板块信息
        Board board = boardService.selectById(article.getBoardId());
        if (board == null) {
            log.warn(ResultCode.FAILED_CREATE.toString() + ", 发帖失败, board id = " + article.getBoardId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        // 更新板块中的帖子数量
        boardService.addOneArticleCountById(board.getId());
        // 打印日志，发帖成功
        log.info(ResultCode.SUCCESS.toString() + ", user id = " + article.getUserId() +
                " , board id = " + article.getBoardId() + ", article id = " + article.getId() + " 发帖成功");
    }

    @Override
    public List<Article> selectAll() {
        List<Article> articles = articleMapper.selectAll();
        return articles; // 调用方自己做校验，不归services层来管
    }

    @Override
    public List<Article> selectAllByBoardId(Long boardId) {
        if(boardId == null || boardId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 校验 板块是否存在
        Board board = boardService.selectById(boardId);
        if (board == null) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        // 调用dao层 查询数据库
        List<Article> articles = articleMapper.selectAllByBoardId(boardId);
        return articles;
    }

    @Override
    public Article selectDetailById(Long id) {
        // 1、非空校验
        if (id == null || id <= 0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用dao层
        Article article = articleMapper.selectDetailById(id);
        // 判断结果是否为空
        if (article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        // 更新帖子的访问次数
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setVisitCount(article.getVisitCount() +1);
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        // 返回帖子详情
        article.setVisitCount(article.getVisitCount() + 1); // 返回的时候，帖子的访问数量+1
        return article;
    }
}
