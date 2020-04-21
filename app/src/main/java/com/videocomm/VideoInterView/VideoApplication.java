package com.videocomm.VideoInterView;

import android.app.Application;

import com.videocomm.VideoInterView.utils.AppUtil;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/31 0031]
 * @function[功能简介]
 **/
public class VideoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.init(this);
    }
}
