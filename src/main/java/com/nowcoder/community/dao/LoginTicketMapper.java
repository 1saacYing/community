package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

import java.util.Date;

//[2.5 开发登入、退出功能]
@Mapper
public interface LoginTicketMapper {

    // 注解方式定义 SQL 映射，不好调试，适合小型项目和简单的CRUD操作
    // 只有插入才需要 VALUES,INSERT INTO ... VALUES(...) 是 SQL 插入语句的标准语法
    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired)",
            "Values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    // 使用数据库自增主键，并将生成的值赋给 LoginTicket 对象的 id 属性
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id, user_id, ticket, status, expired",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    // 通过注解映射动态 SQL,需要将 <if> 标签包裹在 <script> 标签中
    @Update({
            "<script> ",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1 =1 ",
            "</if> ",
            "</script> "
    })
    int updateStatus(@Param("ticket")String ticket, @Param("status")int status); // 多个参数用 @Param 注解映射

    //通常不对数据库进行删除，而是把状态改为0，保留数据

}
