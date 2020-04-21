package com.videocomm.VideoInterView.activity.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/28 0028]
 * @function[功能简介 为其他Activity界面提供一个初始化的标题Activity]
 **/
public class TitleActivity extends BaseActivity {

    protected FrameLayout mContentView;
    protected TitleLayoutManager mTitleLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_title);//这里要引用父类的方法。
        mContentView = findViewById(R.id.content);
        mTitleLayoutManager = new TitleLayoutManager(findViewById(R.id.bar_title));
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, mContentView);
    }

    @Override
    public void setContentView(View view) {
        mContentView.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentView.addView(view, params);
    }
}
