package com.nowcoder.community.controller;

import com.mysql.cj.x.protobuf.MysqlxExpr;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

// [2.2 开发注册功能]/[2.2 开发注册功能-2]
@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    // [2.2 开发注册功能] GET显示注册页面
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register.html";
    }

    // [2.2 开发注册功能-2] GET显示注册页面
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login.html";
    }

    // [2.2 开发注册功能] POST处理用户提交的注册信息并反馈结果
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    // 如果页面传入的值与user的属性匹配，springMVC会自动将值注入到对应的属性
    // Model model：Spring MVC 提供的对象，用于向视图层传递数据（相当于把数据传到 HTML 页面
    // [2.2 开发注册功能-2]Spring MVC 的自动数据绑定机，会隐式调用model.addAttribute("user", user)，Thymeleaf 页面仍可通过 ${user} 访问
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

    /**
     * 用户激活方法
     * 功能：处理用户激活请求，根据激活码更新账户状态
     * 流程：
     * 1. 接收路径参数（userId, code）
     * 2. 调用 userService.activation 执行激活逻辑
     * 3. 根据返回结果设置提示信息和跳转目标
     * 学习点：
     * - Spring MVC 中 @PathVariable 的使用
     * - 控制器与服务层协作模式
     * - 使用 model.addAttribute 向页面传递数据
     */
    //    [2.2 开发注册功能-2] POST处理用户提交的注册信息并反馈结果
    //    请求路径来自与注册发送的链接
    //    http://localhost:8080/community/activation/101/code
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

}
