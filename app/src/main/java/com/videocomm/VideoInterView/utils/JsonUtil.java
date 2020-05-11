package com.videocomm.VideoInterView.utils;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/13 0013]
 * @function[功能简介 Json 转换工具]
 **/
public class JsonUtil {
    /**
     * Json 转 Bean
     *
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

    /**
     * 转成json
     */
    public static String beanToString(Object object) {
        String gsonString = null;
        Gson gson = new Gson();
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * @param src      源数据（只接收这个格式的数据解析） {"version":"V2.0.51", "build":"Build Time:Dec 19 2019 09:56:57"}
     * @param parseStr 需要解析的json字段 例如：version
     * @return
     */
    public static String jsonToStr(String src, String parseStr) {
        if (parseStr == null || parseStr.isEmpty() || src.isEmpty()) {
            return "";
        }
        Log.d("JsonUtil", src);
        Log.d("JsonUtil", "length:" + src.length());
        try {
            JSONObject jsonObject = new JSONObject(src);
            return jsonObject.getString(parseStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
