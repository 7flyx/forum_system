package com.fly.demo.controller;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.config.AppConfig;
import com.fly.demo.model.Article;
import com.fly.demo.model.ArticleReply;
import com.fly.demo.model.User;
import com.fly.demo.services.IArticleReplyService;
import com.fly.demo.services.IArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Api(tags = "回复接口")
@Slf4j
@RestController
@RequestMapping("/reply")
public class ArticleReplyController {
    @Resource
    private IArticleService articleService;
    @Resource
    private IArticleReplyService articleReplyService;

    @ApiOperation("回复帖子")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest req,
                            @ApiParam("帖子id") @RequestParam("articleId") @NonNull Long articleId,
                            @ApiParam("回复内容") @RequestParam("content") @NonNull String content) {
        // 获取当前登录的用户
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) { // 判断用户是否禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 获取帖子信息
        Article article = articleService.selectById(articleId);
        if (article == null || article.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        // 是否封贴
        if (article.getState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }
        // 构建回复帖子 对象
        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(articleId); // 帖子id
        articleReply.setPostUserId(user.getId()); // 回复发起人
        articleReply.setContent(content); // 回复内容
        // 调用Services层代码
        articleReplyService.create(articleReply);
        return AppResult.success();
    }

    @ApiOperation("获取回复列表")
    @GetMapping("/getReplies")
    public AppResult<List<ArticleReply>> getRepliesArticleId(@ApiParam("帖子id") @RequestParam("articleId") @NonNull Long articleId) {
        Article article = articleService.selectById(articleId);
        if (article == null || article.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        // 调用Services 获取结果
        List<ArticleReply> result = articleReplyService.selectByArticleId(articleId);
        return AppResult.success(result);
    }
}
