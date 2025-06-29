package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

// [1.3 Spring入门]
@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    // [1.3 Spring入门]
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Come on, you can do it!";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    //[4. Spring MVC 入门]
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        // 获取请求头
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        // 获取请求体
        // 传参http://localhost:8080/community/alpha/http?code=123，控制台输出
        System.out.println(request.getParameter("code"));

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8");
        // try()中创建会自动close，前提writer必须有close方法
        try (
                PrintWriter writer = response.getWriter();
                ){
            // 输出流方法，向浏览器打印
            writer.write("<h1>牛牛网</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //[4. Spring MVC 入门]
    // GET请求处理，向服务器获取数据，默认请求方式
    // /students?current=1&limit=20
    // 限制只处理get请求
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            // 该注解对参数做更详细的说明
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    //[4. Spring MVC 入门]
    // 根据学生编号查询一个学生
    // /students/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //[4. Spring MVC 入门]
    // POST请求，向服务器提交数据，浏览器需要打开一个带有表单的网页，通过表单填写数据，最后提交给服务器
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    //入参和表单一致，会自动传参
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //[4. Spring MVC 入门]
    // 响应动态HTML数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "牛师傅");
        mav.addObject("age", 30);
        //获取模板路径和名字，templates可忽略，固定是html格式，所以文件名中.html可忽略
        mav.setViewName("/demo/view");
        // 把model 和 view装一个对象返回
        return mav;
    }

    //[4. Spring MVC 入门]
    // return view的路径
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    // model对象由前端处理器DispatcherServlet创建
    public String getSchool(Model model){
        model.addAttribute("name", "牛场");
        model.addAttribute("age", "100");
        // 把view返回给前端处理器，同时model的引用也被持有
        return "/demo/view";
    }

    //[4. Spring MVC 入门]
    // 响应JSON数据的情况：（异步请求） 当前网页不刷新，但是悄悄请求了服务器，得到了一个结果
    // Java对象 -> Json字符串 -> JS对象
    // 前端处理器DispatcherServlet调用方法时，识别到@ResponseBody注解，告诉spring返回值作为响应体，会将map转成json
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "牛厨子");
        emp.put("age", 23);
        emp.put("salary", 8000);
        return emp;
    }

    //[4. Spring MVC 入门]
    // 返回多组员工
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> emps = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "牛厨子");
        emp.put("age", 23);
        emp.put("salary", 8000);
        emps.add(emp);

        emp = new HashMap<>();
        emp.put("name", "牛保安");
        emp.put("age", 43);
        emp.put("salary", 4000);
        emps.add(emp);

        emp = new HashMap<>();
        emp.put("name", "牛主任");
        emp.put("age", 33);
        emp.put("salary", 14000);
        emps.add(emp);
        return emps;
    }

    //    [2.3 会话管理] Cookie是服务器向浏览器发送的临时凭证
    //    HttpServletResponse 响应用于存储cookie
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie生效范围，具体请求路径下有效
        cookie.setPath("/community/alpha");
        // 设置cookie的生效时间，默认是-1，即关闭浏览器就失效
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);

        return "set Cookie";
    }
    //    [2.3 会话管理] 浏览器保存Cookie，下次请求会带上
    // @CookieValue 通过cookie创建时的key 获取cookie
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get Cookie";
    }

    //    [2.3 会话管理] 用户在服务器创建Session
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    // 默认未设置maxAge，session存在内存中，浏览器关闭就消失
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "牛牛");
        return "set Session";
    }

    //     [2.3 会话管理] 获取Session
    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get Session";
    }

    // TODO token重构
}
