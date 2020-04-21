package com.videocomm.VideoInterView.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/31 0031]
 * @function[功能简介 SP存储类]
 **/
public class SpUtil {

    private static SpUtil mSpUtil = null;
    private static SharedPreferences mSp = null;
    private static SharedPreferences.Editor mEdit = null;

    private static final String SPFILENAME = "_VideoInterView";//sp文件名

    public static final String SERVERADDR = "server_addr";//服务器地址
    public static final String SERVERPORT = "server_port";//服务器端口
    public static final String USER_MOBILE = "user_mobile";//用户手机号
    public static final String LIVINGCHECKSTATE = "living_check_state";//活体检测状态 默认关闭
    public static final String RISKREPORTSTATE = "risk_report_state";//风险播报状态 默认开启

    public static final String TOKEN = "token";
    public static final String USERPHONE = "userphone";


    private SpUtil() {

    }

    public static SpUtil getInstance() {
        if (mSpUtil == null || mSp == null || mEdit == null) {
            mSpUtil = new SpUtil();
            mSp = AppUtil.getApp().getSharedPreferences(SPFILENAME, Context.MODE_PRIVATE);
            mEdit = mSp.edit();
            return mSpUtil;
        }
        return mSpUtil;
    }

    public void saveString(String key, String value) {
        mEdit.putString(key, value).apply();
    }

    public void saveBoolean(String key, boolean value) {
        mEdit.putBoolean(key, value).apply();
    }

    public void saveInt(String key, int value) {
        mEdit.putInt(key, value);
    }

    public String getString(String key) {
        return mSp.getString(key, "");
    }

    public String getString(String key,String def) {
        return mSp.getString(key, def);
    }

    public boolean getBoolean(String key) {
        return mSp.getBoolean(key, false);
    }

    public boolean getBoolean(String key,boolean def) {
        return mSp.getBoolean(key, def);
    }

    private int getInt(String key) {
        return mSp.getInt(key, 0);
    }


}
