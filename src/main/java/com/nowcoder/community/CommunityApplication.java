package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//@SpringBootApplication 是一个组合注解，它包括以下三个核心注解：
//@SpringBootConfiguration: 等价于 @Configuration，标识类是一个 Spring 配置类。
//@EnableAutoConfiguration: 启动 Spring Boot 的自动配置功能，根据项目的依赖自动加载相关配置。
//@ComponentScan: 扫描指定包及其子包下的 Spring 组件（包括 @Controller、@Service、@Repository 等，因为他们包含@Component注解）。

@SpringBootApplication
// [1.3 Spring入门]
/*
CommunityApplication是个配置类，底层启动Tomcat，并自动创建Spring容器
Spring容器创建后自动扫描bean并装配到容器中
@SpringBootApplication:标识配置文件类,是一个组合注解，它包括以下三个核心注解：
    ->@SpringBootConfiguration:配置文件
    ->@EnableAutoConfiguration:自动配置
    ->@ComponentScan:组件扫描bean（1. 扫描配置类所在的包以及子包下的bean 2.bean需要包含 @Controller、@Service、@Repository、@Component注解，前三个注解由@Component组成）
*/
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
