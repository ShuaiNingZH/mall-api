package com.atguigu.meet.utils;

/**
 * 雪花算法分布式唯一ID生成工具（Twitter Snowflake）
 * <p>
 * 64 位长整型结构：
 * <pre>
 *  1 bit  符号位        = 0（正数）
 * 41 bit  毫秒时间戳    = 约可使用 69 年（相对于起始时间 twepoch）
 *  5 bit  数据中心ID    = 0~31
 *  5 bit  工作机器ID    = 0~31
 * 12 bit  毫秒内序列号  = 0~4095（同毫秒内最多产生 4096 个ID）
 * </pre>
 *
 * <h3>使用约定</h3>
 * <ul>
 *   <li>单实例部署：workerId / dataCenterId 直接用默认值 0 即可，100% 不重复</li>
 *   <li>多实例部署：每个实例必须配置不同的 (workerId, dataCenterId) 组合，否则会重复</li>
 *   <li>时钟回拨：回拨在容忍范围内则等待，超过则抛出异常，避免返回重复ID</li>
 * </ul>
 *
 * @see <a href="https://github.com/twitter-archive/snowflake">Twitter Snowflake 官方说明</a>
 */
public final class SnowflakeIdUtil {

    // ==================== 基础常量 ====================
    /** 起始时间戳（2025-01-01 00:00:00 UTC+8），尽量靠近项目上线时间以延长 69 年使用窗口 */
    private static final long TWEPOCH = 1735660800000L;

    /** 机器ID占位数 */
    private static final long WORKER_ID_BITS = 5L;
    /** 数据中心ID占位数 */
    private static final long DATA_CENTER_ID_BITS = 5L;
    /** 序列占位数 */
    private static final long SEQUENCE_BITS = 12L;

    /** 机器ID最大值 = 31 */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /** 数据中心ID最大值 = 31 */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    /** 机器ID左偏移 = 12 */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /** 数据中心ID左偏移 = 12 + 5 = 17 */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /** 时间戳左偏移 = 12 + 5 + 5 = 22 */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
    /** 序列号掩码 = 4095 */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /** 时钟回拨最大容忍毫秒（超过直接抛错，宁可拒绝服务也不产生重复ID） */
    private static final long MAX_BACKWARD_MS = 5L;

    // ==================== 运行时状态 ====================
    private final long workerId;
    private final long dataCenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /** 全局单例（默认 workerId=0, dataCenterId=0，单实例场景直接用） */
    private static final SnowflakeIdUtil INSTANCE = new SnowflakeIdUtil(0, 0);

    /**
     * 构造函数（外部自定义 workerId / dataCenterId 时使用）
     */
    public SnowflakeIdUtil(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format(
                    "workerId 不能大于 %d 或小于 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format(
                    "dataCenterId 不能大于 %d 或小于 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 获取全局单例生成的下一个唯一 ID（单实例部署直接用这个方法）
     *
     * @return 雪花ID（长整型，约 19 位十进制数字）
     */
    public static synchronized long nextId() {
        return INSTANCE.generate();
    }

    /**
     * 核心生成方法（线程安全：同一进程内串行生成保证序列号不重复）
     */
    private synchronized long generate() {
        long timestamp = System.currentTimeMillis();

        // 1. 时钟回拨处理
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= MAX_BACKWARD_MS) {
                // 可容忍范围内，等待时钟追上来
                try {
                    wait(offset << 1);
                    timestamp = System.currentTimeMillis();
                    if (timestamp < lastTimestamp) {
                        throw new IllegalStateException(String.format(
                                "时钟回拨 %d 毫秒，仍未恢复，拒绝生成ID", offset));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("时钟回拨等待被中断", e);
                }
            } else {
                throw new IllegalStateException(String.format(
                        "时钟回拨 %d 毫秒，超过容忍阈值 %d，拒绝生成ID", offset, MAX_BACKWARD_MS));
            }
        }

        // 2. 同一毫秒内，序列号递增
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 同毫秒序列号用完，等下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 跨毫秒，序列号重置为 0
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 3. 按位拼装最终 64bit ID
        return ((timestamp - TWEPOCH) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 等待直到下一毫秒
     */
    private long tilNextMillis(long lastTs) {
        long ts = System.currentTimeMillis();
        while (ts <= lastTs) {
            ts = System.currentTimeMillis();
        }
        return ts;
    }
}
