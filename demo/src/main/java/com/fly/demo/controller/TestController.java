package com.fly.demo.controller;


import com.fly.demo.common.AppResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(tags = "测试类相关的接口")
@RequestMapping("/test")
@RestController
public class TestController {
    @ApiOperation("测试接口1，显示你好springBoot")
    @GetMapping ("/hello")
    public String hello() {
        return "hello, Spring boot...";
    }

    @ApiOperation("测试接口4，按传入的姓名显示 你好信息")
    @PostMapping("/helloByName")
    public String helloByName(@ApiParam("姓名") @RequestPart("name") String name) {
        return "hello: " + name;
    }

    @ApiOperation("测试接口2，显示抛出的异常信息")
    @GetMapping("/exception")
    public AppResult testException() throws Exception {
        throw new Exception("这是一个 Exception...");
    }
    @ApiOperation("测试接口3，显示抛出的异常信息")
    @GetMapping("appException")
    public AppResult testApplicationException() throws Exception {
        throw new Exception("这是一个 Application Exception...");
    }
}
