package com.videocomm.VideoInterView.utils;

import com.videocomm.VideoInterView.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

    /**
     * 日期格式化 yyyyMMddHHmmss
     */
    public static String getCurrentFormatTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return format.format(curDate);
    }
    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getTime() {
        Date date = new Date();// 创建一个时间对象，获取到当前的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置时间显示格式
        String CurrentTime = sdf.format(date);

        return CurrentTime;
    }

    public static String getTimeShowStringTwo(int Seconds) {

        String strtime = new String();
        int hour = Seconds / (60 * 60);
        int min = (Seconds / 60) % 60;
        int s = Seconds % 60;
        String hourStr = (hour >= 10) ? "" + hour : "0" + hour;
        String minStr = (min >= 10) ? "" + min : "0" + min;
        String seondStr = (s >= 10) ? "" + s : "0" + s;

        strtime = hourStr + "时" + minStr + "分" + seondStr + "秒";
        return strtime;
    }

    public static String getTimeShowString(int seconds) {
        String strShow = new String();
        int hour = seconds / (60 * 60);
        int min = (seconds / 60) % 60;
        int s = seconds % 60;
        String hourStr = (hour >= 10) ? "" + hour : "0" + hour;
        String minStr = (min >= 10) ? "" + min : "0" + min;
        String seondStr = (s >= 10) ? "" + s : "0" + s;
        strShow = hourStr + ":" + minStr + ":" + seondStr;
        return strShow;
    }

    /**
     * 替换字符
     *
     * @param content
     * @return
     */
    public static String replaceStr(String content) {
        if (content == null || content.length() == 0) {
            return "";
        }
        String substring = "";
        switch (content.length()) {
            case 1:
                return content;
            case 2:
                substring = content.substring(0, content.length() - 1);
                break;
            case 3:
                substring = content.substring(1, content.length() - 1);
                break;
            default:
                substring = content.substring(2, content.length() - 1);
                break;
        }
        //字符串截取
//        String substring = content.substring(0, content.length() - 1);
        //字符串替换
        return content.replace(substring, "*");
    }
}