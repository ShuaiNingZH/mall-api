package com.atguigu.meet.utils;

/**
 * 商品货号（goods_sn）生成工具
 * <p>
 * 编码格式：SP + Base32(雪花ID)
 * <ul>
 *   <li>SP：固定前缀，标识商品（SP = Stock Product）</li>
 *   <li>Base32(雪花ID)：基于 {@link SnowflakeIdUtil} 生成 64bit 唯一ID，按 5bit 一组映射为 Base32 字符</li>
 * </ul>
 *
 * <h3>Base32 字符集</h3>
 * 使用大写字母+数字，去除易混淆字符 0/O/1/I，共 32 个，正好对应 5bit 取值范围 0~31：
 * <pre>23456789ABCDEFGHJKLMNPQRSTUVWXYZ</pre>
 *
 * <h3>长度与可读性</h3>
 * 64bit / 5bit ≈ 13 个 Base32 字符，加上 SP 前缀总长度 <b>固定 15 位</b>，示例：
 * <pre>SP8Z2A7K3M9XQWE</pre>
 * 完全落在 goods_sn VARCHAR(64) 范围内。
 *
 * <h3>唯一性保证</h3>
 * 雪花算法在合理配置 workerId/dataCenterId 前提下 100% 不重复，无需查库；
 * 数据库 uk_goods_sn 唯一索引作为极端场景（时钟回拨超限/机器ID重复）下的最终兜底。
 * </p>
 */
public final class GoodsSnUtil {

    /** 商品编码前缀 */
    private static final String PREFIX = "SP";

    /**
     * Base32 字符集（去除易混淆字符 0 O 1 I，共 32 个，下标对应 5bit 值 0~31）
     * CHARS[0] = '2' 也是固定长度对齐时的填充字符
     */
    private static final char[] BASE32_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();

    /** long 64bit / 5bit = 12.8，向上取整 13 位即可完整表示所有雪花ID */
    private static final int BASE32_LENGTH = 13;

    /** 掩码 = 0b11111 = 31 */
    private static final long MASK = BASE32_CHARS.length - 1;

    private GoodsSnUtil() {
    }

    /**
     * 生成一个新的商品货号
     *
     * @return 形如 SP8Z2A7K3M9XQWE 的 15 位唯一编码
     */
    public static String generate() {
        long id = SnowflakeIdUtil.nextId();
        char[] buf = new char[BASE32_LENGTH];
        // 从最低 5bit 开始映射，结果是从右往左填充
        int idx = BASE32_LENGTH - 1;
        long value = id;
        while (idx >= 0) {
            buf[idx--] = BASE32_CHARS[(int) (value & MASK)];
            value >>>= 5;
        }
        return PREFIX + new String(buf);
    }
}
