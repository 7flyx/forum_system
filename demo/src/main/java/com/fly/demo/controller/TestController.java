package com.fly.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
public class TestController {
    @RequestMapping("/hello")
    public String hello() {
        return "hello, Spring boot...";
    }
}