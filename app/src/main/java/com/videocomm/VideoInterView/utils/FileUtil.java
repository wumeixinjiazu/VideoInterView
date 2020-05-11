package com.videocomm.VideoInterView.utils;

import android.text.TextUtils;

import java.io.File;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/9 0009]
 * @function[功能简介]
 **/
public class FileUtil {

    public static boolean deleteFile(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }

        return false;
    }
}
