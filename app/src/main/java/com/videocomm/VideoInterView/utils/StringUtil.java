package com.videocomm.VideoInterView.utils;

import com.videocomm.VideoInterView.R;

import java.util.regex.Pattern;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/31 0031]
 * @function[功能简介 字符串工具]
 **/
public class StringUtil {
    private static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";

    /**
     * 判断是不是手机号
     *
     * @param input
     * @return
     */
    public static boolean isMobile(CharSequence input) {
        return isMatch(REGEX_MOBILE_SIMPLE, input);
    }

    private static boolean isMatch(String regex, CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }
}
