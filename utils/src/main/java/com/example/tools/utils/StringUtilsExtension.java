package com.example.tools.utils;

import org.apache.commons.lang3.StringUtils;

public class StringUtilsExtension {
    /**
     * 把字符串首字母改为小写
     *
     * @param src 原字符串
     * @return 转换后的字符串
     */
    public static String firstCharToLower(String src) {
        //如果字符串str为null和""则返回原数据
        if (StringUtils.isBlank(src)) return src;

        if (src.length() == 1) return src.toLowerCase();

        return src.substring(0, 1).toLowerCase() + src.substring(1);
    }
}
