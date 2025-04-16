package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//@SpringBootApplication 是一个组合注解，它包括以下三个核心注解：
//@SpringBootConfiguration: 等价于 @Configuration，标识类是一个 Spring 配置类。
//@EnableAutoConfiguration: 启动 Spring Boot 的自动配置功能，根据项目的依赖自动加载相关配置。
//@ComponentScan: 扫描指定包及其子包下的 Spring 组件（包括 @Controller、@Service、@Repository 等）。

@SpringBootApplication
// [1.3 Spring入门]
// CommunityApplication类，底层启动Tomcat，并自动创建Spring容器
// Spring容器创建后自动扫描bean并装配到容器中，
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
