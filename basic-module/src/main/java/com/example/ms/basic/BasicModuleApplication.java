package com.example.ms.basic;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan({"com.example.ms.basic.mapper", "com.example.ms.mapper", "org.mybatis.dynamic.sql.util.mybatis3"})
@ComponentScan({"com.example.tools.component", "com.example.ms", "com.example.ms.basic"})
@EnableMethodCache(basePackages = "com.example.ms.basic")
public class BasicModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicModuleApplication.class, args);
	}

}
