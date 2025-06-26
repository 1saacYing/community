package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

//[2.2 开发注册功能]
public class CommunityUtil {

    //生成随机字符串，作为激活码或提供上传图像，文件的命名
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //MD5加密 每次加密结果不变，只能加密不能解密
    // 加密后添加随机字符串，salt
    public static String md5(String key) {
//        使用lang3判断空值
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
