package com.nowcoder.community.util;


import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

// [2.1 发送邮件]
// 启用Jasypt加密
public class Encryptor {
    public static void main(String[] args) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("yxj"); // 加密密钥
        encryptor.setAlgorithm("PBEWithMD5AndDES"); // 默认支持的加密算法
        String encrypted = encryptor.encrypt("ccbeb66f4c5e75d2");
        System.out.println("Encrypted password: ENC(" + encrypted + ")");

        String decrypted = encryptor.decrypt(encrypted);
        System.out.println("Decrypted password: " + decrypted);
    }
}
