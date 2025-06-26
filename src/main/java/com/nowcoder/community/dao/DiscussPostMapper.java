package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// [1.6 开发社区首页]
@Mapper
public interface DiscussPostMapper {

    //分页offset是起始行号，limit每页显示数量

    // 若需在 XML 中使用 <if> 动态拼接 SQL 条件，且有多个参数，
    // 必须使用 @Param 注解以匹配 XML 中的 #{xxx} 表达式。
    // 单个参数可省略 @Param，但为了代码规范和可维护性，推荐统一使用。
    List<DiscussPost> selectDiscussPosts(@Param("userId")int userId,
                                         @Param("offset")int offset,
                                         @Param("limit")int limit);

    //获取所有数据行数。
    int selectDiscussPostRows(@Param("userId") int userId);
}
