package org.Zewang.myBlog;
/**
 * @author Zewang
 * @date 2025.9.21
 * @description 启动类
 *
 */

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.Zewang.myBlog.dao")
public class MyBlogApplication {
	public static void main(String[] args) {

		SpringApplication.run(MyBlogApplication.class, args);
	}

}
