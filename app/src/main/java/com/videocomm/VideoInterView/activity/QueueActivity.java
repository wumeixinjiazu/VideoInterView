package com.videocomm.VideoInterView.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.TitleActivity;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/17 0017]
 * @function[功能简介]
 **/
public class QueueActivity extends TitleActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        mTitleLayoutManager.setTitle(getString(R.string.queue));

    }
}
