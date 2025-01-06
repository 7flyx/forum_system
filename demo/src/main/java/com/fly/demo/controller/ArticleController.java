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
    public AppResult<Article> getDetails(HttpServletRequest req ,
            @ApiParam("帖子id") @RequestParam("id") @NonNull Long id) {
        // 从session中获取当前的登录用户
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        // 调用Services层，获取帖子详情
        Article article = articleService.selectDetailById(id);
        if (article == null) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
        }
        // 判断当前登录用户与文章作者是否相同
        if (user.getId() == article.getUserId()) {
//            System.out.println("true hhhhhhhhhhh");
            article.setOwn(true);
        }
        return AppResult.success(article);
    }


    @ApiOperation("修改帖子")
    @PostMapping("/modify")
    public AppResult modify(HttpServletRequest req,
                            @ApiParam("帖子id") @RequestParam("id") @NonNull Long id,
                            @ApiParam("标题") @RequestParam("title") @NonNull String title,
                            @ApiParam("内容") @RequestParam("content") @NonNull String content) {
        // 获取当前登录的用户
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        // 检查用户状态
        if(user.getState() == 1) { // 禁言中，不能修改帖子
            return AppResult.failed(ResultCode.FAILED_USER_BANNED.toString());
        }
        // 查询帖子详情
        Article article = articleService.selectById(id);
        if (article == null) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
        }
        // 校验是不是作者
        if (user.getId() != article.getUserId()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN.toString());
        }
        // 判断帖子的状态
        if (article.getState() == 1 || article.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN.toString());
        }
        // 调用Services层代码
        articleService.modify(id, title, content);
        // 不抛异常，说明更新成功
        log.info("帖子更新成功, article id = " + article.getId());
        return AppResult.success();
    }

    @ApiOperation("点赞")
    @PostMapping("/thumbsUp")
    public AppResult thumbsUp(HttpServletRequest req,  @ApiParam("帖子id") @RequestParam("id") @NonNull Long id) {

        // 获取当前登录的用户
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) { // 判断用户是否禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 直接调用Services层
        articleService.thumbsUpById(id);
        return AppResult.success();
    }

    @ApiOperation("删除帖子")
    @PostMapping("/delete")
    public AppResult deleteById(HttpServletRequest req, @ApiParam("帖子id") @RequestParam("id") @NonNull Long id) {
        // 获取当前登录的用户
        HttpSession session = req.getSession(false);
        User user = (User)session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) { // 判断用户是否禁言
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        // 查询帖子详情
        Article article = articleService.selectById(id);
        if (article == null || article.getDeleteState() == 1) {
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        // 校验当前登录的用户是不是作者
        if (user.getId() != article.getUserId()) {
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        articleService.deleteById(id);
        return AppResult.success();
    }

    @ApiOperation("获取用户的帖子列表")
    @GetMapping("/getAllByUserId")
    public AppResult getAllByUserId(HttpServletRequest req,
                                    @ApiParam("用户id") @RequestParam(value = "userId", required = false) Long userId) {
        if (userId == null) {
            HttpSession session = req.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            userId = user.getId();
        }
        List<Article> articles = articleService.selectByUserId(userId);
        return AppResult.success(articles);
    }
}
