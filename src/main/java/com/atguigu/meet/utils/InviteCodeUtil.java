package com.atguigu.meet.utils;

import java.util.Arrays;

/**
 * 邀请码编解码工具
 * <p>
 * 基于「自增序列号 seq + 54 进制编码」实现 <b>seq ↔ 邀请码 一一对应</b>的双向映射：
 * <ul>
 *   <li>字符集：{@code 23456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz}（54 个字符）
 *       <br>去除易混淆字符 0/O/o、1/I/l 以提升可读性</li>
 *   <li>8 位 54 进制容量 = 54^8 - 1 = 72,301,961,339,135（约 7.23 万亿）</li>
 *   <li>seq 由 Redis INCR 全局分配（见 {@code RedisInviteSeqGenerator}），保证进程间唯一</li>
 *   <li>seq ∈ [0, 54^8-1] 与 8 位邀请码严格一一对应，<b>数学上无碰撞</b></li>
 *   <li>编码后左侧用首字符 {@code '2'}（值为 0）补齐到固定 8 位，等价于十进制前导 0</li>
 * </ul>
 * <p>
 * 适用于「邀请码永久不变 + 可解码溯源 + 长期分佣结算」场景：
 * 正向 {@link #encode(long)} 生成；反向 {@link #decode(String)} 溯源。
 */
public class InviteCodeUtil {

    /** 邀请码字符集（去除易混淆字符 0 O o 1 I l），共 54 个字符 */
    private static final char[] CHARS = "23456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz".toCharArray();

    /** 自定义进制基数 */
    private static final int RADIX = CHARS.length; // 54

    /** 邀请码固定长度 */
    private static final int CODE_LENGTH = 8;

    /** 8 位 54 进制可表示的最大值 = 54^8 - 1 = 72,301,961,339,135 */
    private static final long MAX_SEQ = pow54(CODE_LENGTH) - 1;

    /** 字符 → 索引 反向映射表（ASCII 大小 128，覆盖所有可见字符） */
    private static final int[] CHAR_INDEX = buildCharIndex();

    private InviteCodeUtil() {
    }

    /**
     * 将自增序列号 seq 编码为 8 位邀请码
     *
     * @param seq 自增序列号，范围 [0, {@link #maxSeq()}]
     * @return 8 位邀请码（区分大小写，数字+字母）
     * @throws IllegalArgumentException seq 超出容量范围
     */
    public static String encode(long seq) {
        if (seq < 0 || seq > MAX_SEQ) {
            throw new IllegalArgumentException(
                    String.format("seq 超出邀请码容量范围 [0, %d]，当前 seq=%d", MAX_SEQ, seq));
        }

        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        long v = seq;
        do {
            sb.append(CHARS[(int) (v % RADIX)]);
            v /= RADIX;
        } while (v > 0);

        // 长度不足 8 位时左侧补首字符 '2'（值为 0，等价于十进制前导 0，不改变数值）
        while (sb.length() < CODE_LENGTH) {
            sb.append(CHARS[0]);
        }
        return sb.reverse().toString();
    }

    /**
     * 将 8 位邀请码解码为原始自增序列号 seq
     * <p>用于反向溯源、对账、分佣结算等场景
     *
     * @param code 8 位邀请码
     * @return 自增序列号 seq
     * @throws IllegalArgumentException 邀请码格式非法（长度/字符不合法）
     */
    public static long decode(String code) {
        if (code == null || code.length() != CODE_LENGTH) {
            throw new IllegalArgumentException("邀请码长度必须为 8 位");
        }

        long seq = 0L;
        for (int i = 0; i < CODE_LENGTH; i++) {
            int idx = charToIndex(code.charAt(i));
            if (idx < 0) {
                throw new IllegalArgumentException("邀请码包含非法字符: " + code.charAt(i));
            }
            seq = seq * RADIX + idx;
            if (seq > MAX_SEQ) {
                throw new IllegalArgumentException("邀请码解码后超出容量上限: " + code);
            }
        }
        return seq;
    }

    /**
     * 邀请码容量上限（54^8 - 1）
     */
    public static long maxSeq() {
        return MAX_SEQ;
    }

    /**
     * 字符 → 索引（O(1) 查表）
     */
    private static int charToIndex(char c) {
        if (c < 0 || c >= CHAR_INDEX.length) {
            return -1;
        }
        return CHAR_INDEX[c];
    }

    /**
     * 构建字符 → 索引 反向映射表
     */
    private static int[] buildCharIndex() {
        int[] table = new int[128];
        Arrays.fill(table, -1);
        for (int i = 0; i < CHARS.length; i++) {
            table[CHARS[i]] = i;
        }
        return table;
    }

    /**
     * 计算 54^n
     */
    private static long pow54(int n) {
        long result = 1L;
        for (int i = 0; i < n; i++) {
            result *= RADIX;
        }
        return result;
    }
}
