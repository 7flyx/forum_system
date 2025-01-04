package com.fly.demo.controller;

import com.fly.demo.common.AppResult;
import com.fly.demo.common.ResultCode;
import com.fly.demo.exception.ApplicationException;
import com.fly.demo.model.Board;
import com.fly.demo.services.IBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "板块接口")
@RestController
@RequestMapping("/board")
public class Boardcontroller {

    @Value("${fly-forum.index.board-num:9}") // 读取yml配置文件，如果没有 默认值是9
    private Integer indexBoardNum;

    @Resource
    private IBoardService boardService;
    @ApiOperation("获取板块内部信息")
    @GetMapping("/topList")
    public AppResult<List<Board>> topList() {
        log.info("首页板块个数为： " + indexBoardNum);
        // 调用Services 查询结果
        List<Board> boards = boardService.selectByNum(indexBoardNum);
        if (boards == null) {
            boards = new ArrayList<>();
        }
        // 返回结果
        return AppResult.success(boards);
    }

    @ApiOperation("获取板块信息")
    @GetMapping("/getById")
    public AppResult<Board> getById(@ApiParam("板块id") @RequestParam("id") @NonNull Long id) {
        // 调用Services
        Board board = boardService.selectById(id);
        if (board == null || board.getDeleteState() == 1){
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        // 返回结果
        return AppResult.success(board);
    }
}
