package com.videocomm.VideoInterView.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author[wengCJ]
 * @version[创建日期，2019/12/16 0016]
 * @function[功能简介 存放一些代码尺寸的工具类]
 **/

public class DisplayUtil {
    private static int screenWidth;
    private static int screenHeight;

    //获取屏幕的宽度
    public static int getScreenWidth() {
        if (screenWidth <= 0) {
            WindowManager manager = (WindowManager) AppUtil.getApp().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            if (manager != null) {
                manager.getDefaultDisplay().getMetrics(dm);
            }
            screenWidth = dm.widthPixels;
        }

        return screenWidth;
    }

    //获取屏幕的高度
    public static int getScreenHeight() {
        if (screenHeight <= 0) {
            WindowManager manager = (WindowManager) AppUtil.getApp().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            if (manager != null) {
                manager.getDefaultDisplay().getMetrics(dm);
            }
            screenHeight = dm.heightPixels;
        }

        return screenHeight;
    }

    public static int dp2px(float dpValue) {
        final float scale = AppUtil.getApp().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        final float scale = AppUtil.getApp().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = AppUtil.getApp().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
