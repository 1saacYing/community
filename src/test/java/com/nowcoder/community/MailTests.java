package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class) //测试时加载 Spring 应用上下文，从而可以注入 Spring Bean 以及访问 Spring 环境中的各种组件。
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) //定义测试类中如何加载应用上下文的配置。
// [2.1 发送邮件] 邮件发送测试
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.password}")
    private String password;

    @Test
    public void testDecryptedPassword() {
        System.out.println("Mail 密码 = " + password);
    }


    @Test
    public void testTextMail() {
        mailClient.sendMail("yxj1saac@gmail.com", "TEST", "Mail Test Check");
    }

//    尝试发送html模板邮件，templates/mail/maildemo.html
    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "sunday");

        String content = templateEngine.process("/mail/maildemo", context);
        System.out.println(content);

        mailClient.sendMail("yxj1saac@gmail.com", "HTML TEST", content);
    }
}
