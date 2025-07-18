package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// [1.6 开发社区首页]/[2.2 开发注册功能]/[2.2 开发注册功能-2]
@Service
public class UserService implements CommunityConstant {

    //[2.5 开发登入、退出功能]
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    // [2.2 开发注册功能]
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

//    map用于存储多种情况返回的报错
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }
        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }
        // 注册用户，密码加密，落表
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0); // 普通用户
        user.setStatus(0); // 未激活
        user.setActivationCode(CommunityUtil.generateUUID()); // 激活码
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000))); //设置随机头像
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送html激活邮件，配置模板templates/mail/activation.html

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        // mapper配置<insert id="insertUser" parameterType="com.nowcoder.community.entity.User" keyProperty="id">
        // 加 properties配置 mybatis.configuration.useGeneratedKeys=true
        // 插入数据后把数据库生成的主键设置到 User 类的 id 属性中
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;

    }
//    [2.2 开发注册功能-2] 激活业务逻辑->controller处理请求
    /**
     * 激活用户账户
     * 流程：查询用户 → 判断状态 → 更新状态（如匹配）
     * 学习点：
     * 1. 典型数据库操作流程：查询 → 判断 → 更新
     * 2. 使用常量统一管理状态码（ACTIVATION_SUCCESS 等）
     * 3. 注意空值处理和生产环境日志记录
     */
    public int activation(int id, String code) {
        User user = userMapper.selectById(id);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(id, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    //[2.5 开发登入、退出功能] 登录逻辑
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 验证账号-检查数据库中是否存在该用户
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }

        // 验证账号是否激活
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        // 验证成功，生成登录凭证，存入数据库
        // 类似与Cookie，但只保存在数据库，不保存在客户端
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        // NOTE expiredSeconds * 1000 超出了int范围得使用long 类型
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    //[2.5 开发登入、退出功能] 退出逻辑,ticket状态反转为无效
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }
}
