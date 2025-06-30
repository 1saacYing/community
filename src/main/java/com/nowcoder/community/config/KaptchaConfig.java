package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;


//[2.4 生成验证码] kaptcha 配置类
@Configuration
public class KaptchaConfig {

    /**
     * [2.4 生成验证码]
     * 验证码生成器 Bean 配置方法
     *
     * 该方法创建并配置一个 Kaptcha 验证码生成器实例，用于在系统中生成图形验证码。
     * 配置内容包括验证码图片的尺寸、字体大小、颜色、字符范围、字符长度及干扰元素等。
     *
     *
     * @return Producer 返回一个已配置好的 Kaptcha 实例，供 Spring 容器管理使用
     */
    @Bean //创建 Bean 对象：被 @Bean 标注的方法会在 Spring 启动时执行一次（默认单例），并将返回值作为 Bean 存入 Spring 容器中。
    public Producer kaptchaProducer() {
        Properties properties = new Properties();

        // 设置验证码图片宽度
        properties.setProperty("kaptcha.image.width", "100");

        // 设置验证码图片高度
        properties.setProperty("kaptcha.image.height", "40");

        // 设置验证码文字字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "32");

        // 设置验证码文字颜色（RGB 黑色）
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");

        // 设置验证码字符范围：数字 + 大写字母
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        // 设置验证码字符数量
        properties.setProperty("kaptcha.textproducer.char.length", "4");

        // 设置干扰实现类，此处关闭干扰
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        // 创建 Kaptcha 实例并配置
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties); // 使用自定义配置
        kaptcha.setConfig(config);

        // 返回构建好的 Producer 实例
        return kaptcha;
}


}
