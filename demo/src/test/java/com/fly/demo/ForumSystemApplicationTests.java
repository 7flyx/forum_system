package com.fly.demo;

import com.fly.demo.dao.UserMapper;
import com.fly.demo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class ForumSystemApplicationTests {
	@Resource
	private DataSource dataSource;

	@Resource
	private UserMapper userMapper;

	@Test
	void testConnection() throws SQLException {
		System.out.println("dataSource: "+dataSource.getClass());
		Connection connection = dataSource.getConnection();
		System.out.println("connection: "+connection);
		connection.close();
	}

	@Test
	void contextLoads() {
		System.out.println("hello world");
	}

	@Test
	void testMybatis() {
		User user = userMapper.selectByPrimaryKey(1L);
		System.out.println(user);
		System.out.println(user.getUsername());
	}
}
