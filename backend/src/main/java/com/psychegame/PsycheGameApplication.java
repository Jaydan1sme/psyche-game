package com.psychegame;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Psyche Game 后端主启动类
 *
 * @author Psyche Game Team
 * @since 2025-12-28
 */
@SpringBootApplication
@MapperScan("com.psychegame.mapper")
@ComponentScan(basePackages = "com.psychegame")
@EnableAsync
@EnableScheduling
public class PsycheGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsycheGameApplication.class, args);
        System.out.println("\n=======================================");
        System.out.println("  Psyche Game 后端服务已启动成功！");
        System.out.println("  本地访问地址：http://localhost:8080");
        System.out.println("  Swagger文档：http://localhost:8080/doc.html");
        System.out.println("  H2控制台：http://localhost:8080/h2-console");
        System.out.println("=======================================\n");
    }

}
