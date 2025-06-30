package com.nowcoder.community.util;

// [2.2 开发注册功能 2] 邮件激活状态
// todo 重构：把常量定义成枚举类
public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;
}
