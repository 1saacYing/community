package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

//[1.5 Mybatis入门]
//用于定义访问数据库的方法。它是与数据库交互的入口，声明一些方法来操作数据库，例如 select、insert、update 和 delete。
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int  insertUser(User user);

//    必须使用 @Param 注解以匹配 XML 中的 #{xxx} 表达式。
    int updateStatus(@Param("id") int id, @Param("status") int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

}
