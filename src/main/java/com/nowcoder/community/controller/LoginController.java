package com.nowcoder.community.controller;

import com.mysql.cj.x.protobuf.MysqlxExpr;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

// [2.2 开发注册功能]
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register.html";
    }

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

}
