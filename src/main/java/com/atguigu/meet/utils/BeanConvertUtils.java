package com.atguigu.meet.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * Bean 属性转换工具类
 * <p>
 * 在 Spring BeanUtils.copyProperties 基础上，额外支持：
 * <ul>
 *   <li>Integer (0/1) ↔ Boolean (false/true)</li>
 *   <li>String ("0"/"1") ↔ Boolean (false/true)</li>
 * </ul>
 * 适用于 Entity（Integer/String 状态字段）与 VO/DTO（Boolean 状态字段）之间的互转。
 */
public class BeanConvertUtils {

    private BeanConvertUtils() {
    }

    /**
     * 复制属性，自动处理 Integer/String ↔ Boolean 的类型转换
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
        convertSpecialTypes(source, target);
    }

    /**
     * 复制属性，忽略 source 中值为 null 的字段，自动处理 Integer/String ↔ Boolean
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        convertSpecialTypes(source, target);
    }

    /**
     * 复制属性，忽略指定的属性名 + 自动处理类型转换
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, ignoreProperties);
        convertSpecialTypes(source, target);
    }

    // ====================== 私有方法 ======================

    /**
     * 特殊类型转换：Integer/String ↔ Boolean
     * <p>
     * 遍历 target 的所有属性：
     * - 若 target 字段类型为 Boolean，source 对应字段为 Integer → 1=true, 0=false
     * - 若 target 字段类型为 Boolean，source 对应字段为 String  → "1"=true, "0"=false
     * - 若 target 字段类型为 Integer，source 对应字段为 Boolean → true=1, false=0
     * - 若 target 字段类型为 String， source 对应字段为 Boolean → true="1", false="0"
     */
    private static void convertSpecialTypes(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        BeanWrapper tgtWrapper = new BeanWrapperImpl(target);
        PropertyDescriptor[] targetPds = tgtWrapper.getPropertyDescriptors();

        for (PropertyDescriptor tgtPd : targetPds) {
            String propertyName = tgtPd.getName();
            if ("class".equals(propertyName)) {
                continue;
            }
            Class<?> tgtType = tgtPd.getPropertyType();
            if (tgtType == null || !srcWrapper.isReadableProperty(propertyName)) {
                continue;
            }
            Object srcValue = srcWrapper.getPropertyValue(propertyName);
            if (srcValue == null) {
                continue;
            }
            Class<?> srcType = srcValue.getClass();

            // --- target: Boolean ---
            if (Boolean.class.equals(tgtType) || boolean.class.equals(tgtType)) {
                if (Integer.class.equals(srcType)) {
                    Integer intVal = (Integer) srcValue;
                    tgtWrapper.setPropertyValue(propertyName, intVal == 1);
                } else if (String.class.equals(srcType)) {
                    String strVal = (String) srcValue;
                    if ("1".equals(strVal) || "0".equals(strVal)) {
                        tgtWrapper.setPropertyValue(propertyName, "1".equals(strVal));
                    }
                }
            }
            // --- target: Integer ---
            else if (Integer.class.equals(tgtType) || int.class.equals(tgtType)) {
                if (Boolean.class.equals(srcType) || boolean.class.equals(srcType)) {
                    Boolean boolVal = (Boolean) srcValue;
                    tgtWrapper.setPropertyValue(propertyName, boolVal ? 1 : 0);
                }
            }
            // --- target: String ---
            else if (String.class.equals(tgtType)) {
                if (Boolean.class.equals(srcType) || boolean.class.equals(srcType)) {
                    Boolean boolVal = (Boolean) srcValue;
                    tgtWrapper.setPropertyValue(propertyName, boolVal ? "1" : "0");
                }
            }
        }
    }

    /**
     * 获取对象中值为 null 的属性名数组
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
