package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.TitleActivity;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/17 0017]
 * @function[功能简介 排队界面]
 **/
public class QueueActivity1 extends TitleActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_1);
        mTitleLayoutManager.setTitle(getString(R.string.queue)).setLeftListener(this).showLeft(true);
        initView();
    }

    private void initView() {

        String businessType = getIntent().getStringExtra("businessType");

        TextView tvPlanTime = findViewById(R.id.tv_plan_time);
        TextView tvQueueBusiness = findViewById(R.id.tv_queue_business);
        tvPlanTime.setText(Html.fromHtml(getResources().getString(R.string.black_red_black, "预计还需要等待", "  5  ", "分钟")));
        tvQueueBusiness.setText(Html.fromHtml(getResources().getString(R.string.black_red, "您正在办理", " " + businessType)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_left:
                finish();
                break;
            case R.id.btn_queue://排队
                startActivity(new Intent(QueueActivity1.this, RiskReportActivity.class));
                break;
        }
    }
}
