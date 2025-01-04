package com.fly.demo.controller;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.config.AppConfig;
import com.fly.demo.model.Article;
import com.fly.demo.model.Board;
import com.fly.demo.model.User;
import com.fly.demo.services.IArticleService;
import com.fly.demo.services.IBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "文章接口")
@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IBoardService boardService;
    @Resource
    private IArticleService articleService;
    /**
     * 发布新帖
     * @param boardId 板块id
     * @param title 文章标题
     * @param content 文章内容
     * @return 结果
     */
    @ApiOperation("发布新帖")
    @PostMapping("/create")
    public AppResult create( HttpServletRequest req,
                            @ApiParam("板块id") @RequestParam("boardId") @NonNull Long boardId,
                            @ApiParam("文章标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("文章内容") @RequestParam("content") @NonNull String content) {
        // 校验用户是否被禁言
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) { // 用户已禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED.toString());
        }
        Board board = boardService.selectById(boardId.longValue());
        if (board == null || board.getDeleteState() == 1 || board.getState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_BOARD_BANNED);
        }
        // 封装文章对象
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setBoardId(boardId);
        article.setUserId(user.getId());
        // 调用services层代码
        articleService.create(article);
        return AppResult.success();
    }

    /**
     *  如果BoardId=空，查询的是 全部板块的信息，即“首页”的信息
     *  如果boardId不为空，说明要查询的是具体某一个板块的信息，查询返回即可
     * @param boardId 板块id
     * @return 结果
     */
    @ApiOperation("获取帖子列表")
    @GetMapping("/getAllByBoardId")
    public AppResult<List<Article>> getAllByBoardId(@ApiParam("板块Id") @RequestParam(value = "boardId", required = false) Long boardId) {
        List<Article> articles = null;
        if (boardId == null) {
            articles = articleService.selectAll();
        } else {
            articles = articleService.selectAllByBoardId(boardId);
        }
        if (articles == null) { // 结果集为空的话，返回一个空数组
            articles = new ArrayList<>();
        }
        return AppResult.success(articles);
    }

    @ApiOperation("根据帖子id获取详情")
    @GetMapping("/details")
    public AppResult<Article> getDetails(@ApiParam("帖子id") @RequestParam("id") @NonNull Long id) {
        // 调用Services层，获取帖子详情
        Article article = articleService.selectDetailById(id);
        if (article == null) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
        }
        return AppResult.success(article);
    }

}
