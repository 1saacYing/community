package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// [1.6 开发社区首页]
@Mapper
public interface DiscussPostMapper {

    //分页offset是起始行号，limit每页显示数量
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //获取所有数据行数,
    //如果需要<if>动态拼接sql条件，且只有一个参数时必须使用@Param(-parameters能解决吗？ -不行)
    int selectDiscussPostRows(@Param("userId") int userId);
}
