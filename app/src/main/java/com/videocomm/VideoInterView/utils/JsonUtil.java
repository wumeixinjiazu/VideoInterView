package com.videocomm.VideoInterView.utils;

import com.google.gson.Gson;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/13 0013]
 * @function[功能简介 Json 转换工具]
 **/
public class JsonUtil {

    /**
     *  Json 转 Bean
     * @param content
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T jsonToBean(String content, Class<T> clazz) {
        if (content == null) {
            return null;
        }
        Gson gson = new Gson();
        T bean = gson.fromJson(content, clazz);
        return bean;
    }

}
