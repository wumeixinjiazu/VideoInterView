package com.videocomm.VideoInterView.utils;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.widget.Toast;

/**
 * @author[wengCJ]
 * @version[创建日期，2019/12/16 0016]
 * @function[功能简介 Toast的工具类]
 **/

public class ToastUtil {

    private static Toast toast;

    public static void show(CharSequence text) {
        if (text == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(AppUtil.getApp(), "", Toast.LENGTH_SHORT);
            toast.setText(text);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    @SuppressLint("ShowToast")
    static void show(int resId) {
        String text = AppUtil.getApp().getResources().getString(resId);
        if (text == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(AppUtil.getApp(), "", Toast.LENGTH_SHORT);
            toast.setText(text);
        } else {
            toast.setText(text);
        }
        toast.show();

    }

    public static void cancle() {
        if (toast != null) {
            toast.cancel();
        }
    }

}
