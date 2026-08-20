package com.atguigu.meet.utils;

import com.alibaba.fastjson2.JSON;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * 时间范围查询工具类
 * <p>
 * 统一处理分页查询 DTO 中 "yyyy-MM-dd" 格式的日期范围字段（如 timeRange）。
 * <ul>
 *   <li>DTO 层：{@link #parseTimeRange(String)} 兼容 GET 请求多种字符串格式</li>
 *   <li>Service 层：{@link #toStartOfDay(String)} / {@link #toEndOfDay(String)} 转换为 LocalDateTime</li>
 * </ul>
 *
 * @Date 2026-08-20
 */
public final class TimeRangeUtils {

    /** 日期格式：yyyy-MM-dd */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private TimeRangeUtils() {
    }

    /**
     * 解析时间范围字符串为 List
     * <p>支持格式：</p>
     * <ol>
     *   <li>JSON 数组字符串，例如 ["2026-08-19", "2026-08-19"]</li>
     *   <li>逗号分隔的字符串，例如 2026-08-19,2026-08-19</li>
     * </ol>
     * 兼容前端可能误加的引号、空白字符。
     *
     * @param timeRangeStr 时间范围字符串
     * @return 解析后的日期字符串列表，入参为空时返回空列表
     */
    public static List<String> parseTimeRange(String timeRangeStr) {
        if (!StringUtils.hasText(timeRangeStr)) {
            return Collections.emptyList();
        }
        // 兼容前端可能误加的引号
        String cleaned = timeRangeStr.trim();
        if ((cleaned.startsWith("\"") && cleaned.endsWith("\""))
                || (cleaned.startsWith("'") && cleaned.endsWith("'"))) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        try {
            return JSON.parseArray(cleaned, String.class);
        } catch (Exception e) {
            return List.of(cleaned.split(","));
        }
    }

    /**
     * 将 "yyyy-MM-dd" 日期字符串转换为当天的 00:00:00
     *
     * @param dateStr 日期字符串
     * @return 当天起始时间；入参为空返回 null
     */
    public static LocalDateTime toStartOfDay(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay();
    }

    /**
     * 将 "yyyy-MM-dd" 日期字符串转换为当天的 23:59:59
     *
     * @param dateStr 日期字符串
     * @return 当天结束时间；入参为空返回 null
     */
    public static LocalDateTime toEndOfDay(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER).atTime(23, 59, 59);
    }
}
