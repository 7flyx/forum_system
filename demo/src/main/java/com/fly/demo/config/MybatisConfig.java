package com.fly.demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置mybatis的扫描路径
 */
@Configuration
// 扫描路径
@MapperScan("com.fly.demo.dao")
public class MybatisConfig {

}
