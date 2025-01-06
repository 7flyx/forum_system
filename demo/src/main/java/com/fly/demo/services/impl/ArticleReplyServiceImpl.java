package com.fly.demo.services.impl;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.dao.ArticleReplyMapper;
import com.fly.demo.exception.ApplicationException;
import com.fly.demo.model.ArticleReply;
import com.fly.demo.services.IArticleReplyService;
import com.fly.demo.services.IArticleService;
import com.fly.demo.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArticleReplyServiceImpl implements IArticleReplyService {
    @Resource
    private ArticleReplyMapper articleReplyMapper;
    @Resource
    private IArticleService articleService;
    @Override
    public void create(ArticleReply articleReply) {
        if (articleReply == null || articleReply.getArticleId() == null
                || articleReply.getPostUserId() == null
                || StringUtil.iSEmpty(articleReply.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 设置默认值
        articleReply.setReplyId(null);
        articleReply.setReplyUserId(null);
        articleReply.setLikeCount(0);
        articleReply.setState((byte)0);
        articleReply.setDeleteState((byte)0);
        Date date = new Date();
        articleReply.setCreateTime(date);
        articleReply.setUpdateTime(date);
        // 写入数据库
        int row = articleReplyMapper.insertSelective(articleReply);
        if (row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        // 更新帖子表的回复数
        articleService.addOneReplyCountById(articleReply.getArticleId());
        log.info("回复成功, article id = " + articleReply.getArticleId() + ", user id = " + articleReply.getReplyUserId());
    }

    @Override
    public List<ArticleReply> selectByArticleId(Long articleId) {
        if (articleId == null || articleId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // dao层
        List<ArticleReply> result = articleReplyMapper.selectByArticleId(articleId);
        return result;
    }
}
