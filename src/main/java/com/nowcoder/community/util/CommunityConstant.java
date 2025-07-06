package com.nowcoder.community.util;

// [2.2 开发注册功能 2] 邮件激活状态 /[2.5 开发登入、退出功能]
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

    /**
     * 默认状态的登录凭证的超时时间
     */
    // [2.5 开发登入、退出功能]
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住我状态的登录凭证的超时时间
     */
    // [2.5 开发登入、退出功能]
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
}
