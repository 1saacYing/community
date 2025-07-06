package com.nowcoder.community.controller;

import ch.qos.logback.core.OutputStreamAppender;
import com.google.code.kaptcha.Producer;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

// [2.2 开发注册功能]/[2.2 开发注册功能-2]
@Controller
public class LoginController implements CommunityConstant {

    //[2.4 生成验证码]
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    //[2.4 生成验证码]
    @Autowired
    private Producer kaptchaProducer;

    //[2.5 开发登入、退出功能] 注入配置文件中的路径
    @Value("${server.servlet.context-path}")
    private String contextPath;


    // [2.2 开发注册功能] GET显示注册页面,返回html页面
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register.html"; //1.不推荐加 .html 后缀 2. 路径错误（以 / 开头会被认为是绝对路径）如下 site/login
    }

    // [2.2 开发注册功能-2] GET显示注册页面,返回html页面
    //在 Spring Boot + Thymeleaf 项目中，控制器返回视图名时不要加 .html 后缀，由框架自动解析更规范、安全、易维护
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "site/login";
    }

    // [2.2 开发注册功能] POST处理用户提交的注册信息并反馈结果
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    // 如果页面传入的值与user的属性匹配，springMVC会自动将值注入到对应的属性
    // Model model：Spring MVC 提供的对象，用于向视图层传递数据（相当于把数据传到 HTML 页面
    // 注册账号 yxj 密码 yxj
    // [2.2 开发注册功能-2]
    // NOTE 入参是实体，不是基础数据类型的参数，会隐式调用model.addAttribute("user", user)，Spring MVC 的自动数据绑定机，
    //  Thymeleaf 页面仍可通过 ${user} 访问
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            // model.addAttribute(...)：将信息传递给页面，提示用户注册成功，提醒去邮箱激活账号。
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target", "/index");
            // 返回一个结果页面
            return "/site/operate-result";
        } else {
            // [2.2 开发注册功能-2] 建议显式添model.addAttribute("user", user)
            model.addAttribute("user", user);
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            // 报错内容发送会注册页面
            return "/site/register.html";
        }

    }


    // [2.2 开发注册功能-2] 用户账号激活接口 1. 接收路径参数 userId 和 code（来自激活邮件中的链接）2. 调用 service 层执行激活逻辑 3. 根据激活结果设置提示信息和跳转目标 4. 返回操作结果页面（operate-result.html）展示结果
    // - @PathVariable：用于从 URL 中提取动态参数（userId, code）
    // - model.addAttribute(...)：向页面传递提示信息和跳转链
    // 请求路径来自与注册发送的链接 http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        int result =  userService.activation(userId, code);

        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    // [2.4 生成验证码] 验证码生成接口（GET /kaptcha
    // - HttpServletResponse：用于向浏览器输出图片流
    // - HttpSession：用于存储验证码文本以防止伪造
    // - ImageIO.write(...)：将 BufferedImage 写入输出流
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 1. 使用 Kaptcha 工具生成验证码字符串（例如：4位数字或字母组合）
        String text = kaptchaProducer.createText();

        // 2. 根据该文本生成对应的图片（图形内容与文本一致）
        BufferedImage image = kaptchaProducer.createImage(text);

        // 3. 将验证码文本存入 Session，供后续登录/注册时比对验证
        //    选择 Session 是为了防止验证码被客户端篡改或伪造
        session.setAttribute("kaptcha", text);

        // 4. 设置响应类型为图片格式（PNG），告诉浏览器这是一个图片资源
        response.setContentType("image/png");

        // 5. 获取输出流并将图片写入响应体，返回给浏览器展示
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os); // 写出图片字节流到响应
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    //[2.5 开发登入、退出功能]
    // 1. 开发Controller post方法处理页面请求，获取表单传入的参数 2. 调用Service方法处理业务逻辑，返回结果 3. 根据结果重定向到相应的页面
    // 前端勾选按钮 记住我 Boolean rememberme
    // - HttpSession创建session，获取生成的验证码进行匹配
    // - HttpServletResponse创建cookie，将ticket发放给客户端，将ticket保存在cookie中
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    // NOTE 如果方法入参是实体类（如 User user），Spring MVC 会自动将请求参数绑定到实体属性，并存入 Model。
    //  当前方法使用了基本类型参数（String、Boolean），需要手动处理数据绑定和 Model 添加操作,eg.model.addAttribute("username", username);。
    public String login(Model model, String username, String password, String code, Boolean rememberme,
                        HttpSession session, HttpServletResponse response) {
        String kaptcha = (String) session.getAttribute("kaptcha");

        // 验证验证码
        if (StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !code.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "site/login";
        }

        // 验证账号，密码
        boolean isRememberMe = Boolean.TRUE.equals(rememberme); // 如果 rememberme 为 null，默认为 false，不然登入不勾选会报错
        int expiredSeconds = isRememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        // 登录成功后，创建一个 Cookie，保存用户登录的凭证，并返回给浏览器
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath); // 设置 Cookie 的作用域
            cookie.setMaxAge(expiredSeconds); // 设置 Cookie 的生效时间
            response.addCookie(cookie);
            return "redirect:/index"; // 重定向到首页
        } else {
            // 登录失败，将错误信息存到model中，返回登录页面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/login";
        }

        // 下一步，处理前端页面,展示错误信息
    }

    // [2.5 开发登入、退出功能] 用户退出登录 1. 从 Cookie 中获取登录凭证 ticket 2. 调用 service 层方法将该 ticket 标记为失效状态 3. 重定向到登录页面
    // - @CookieValue("ticket")：用于从客户端 Cookie 提取登录凭证
    // - redirect:/login：Spring MVC 的重定向语法，默认发送 GET 请求

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login"; // NOTE 重定向和非重定向得区别： service 层操作完，用 redirect；展示错，不用 redirect
    }


}
