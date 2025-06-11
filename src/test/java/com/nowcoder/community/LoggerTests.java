package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class) //测试时加载 Spring 应用上下文，从而可以注入 Spring Bean 以及访问 Spring 环境中的各种组件。
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //定义测试类中如何加载应用上下文的配置。
// [1.7  项目调试技巧]
public class LoggerTests {
    // 每个类单独实例化一个logger，设置static便于同个类所有对象共享，logger包 slf4j，以当前类名命名logger
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger() {
        System.out.println(logger.getName());

        logger.debug("debug log"); // 调试程序
        logger.info("info log"); // 启用线程池，定时任务等，记录特殊任务
        logger.warn("warn log");
        logger.error("error log"); // try catch捕获异常
        //下一步，配置启用日志级别，log文件保存路径，spring boot整合后 application.properties
        // 进阶配置，logback-spring.xml，控制台打印，根据日志级别分别存储，并设置文件大小后将日志进行拆分
    }
}
