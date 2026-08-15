package com.atguigu.meet.utils;

import java.security.SecureRandom;

/**
 * 邀请码生成工具
 * 生成 8 位随机码，区分大小写，由数字 + 字母组成
 * 排除了易混淆字符 0/O/o、1/I/l 以提升可读性
 */
public class InviteCodeUtil {

    /** 邀请码字符集（去除易混淆字符 0 O o 1 I l） */
    private static final char[] CHARS = "23456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz".toCharArray();

    /** 邀请码长度 */
    private static final int CODE_LENGTH = 8;

    private static final SecureRandom RANDOM = new SecureRandom();

    private InviteCodeUtil() {
    }

    /**
     * 生成 8 位随机邀请码
     */
    public static String generate() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS[RANDOM.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }
}
