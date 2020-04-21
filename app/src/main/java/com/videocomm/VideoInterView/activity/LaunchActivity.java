package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.BaseActivity;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/28 0028]
 * @function[功能简介 App启动页]
 **/
public class LaunchActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        setTheme(R.style.AppTheme_Launcher);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                finish();
            }
        },2000);
    }
}
