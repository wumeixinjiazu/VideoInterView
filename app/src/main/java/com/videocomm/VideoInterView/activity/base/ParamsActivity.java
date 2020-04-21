package com.videocomm.VideoInterView.activity.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.Constant;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.adapter.ParamsAdapter;
import com.videocomm.VideoInterView.utils.AppUtil;
import com.videocomm.VideoInterView.utils.SpUtil;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/1/17 0017]
 * @function[功能简介]
 **/
public class ParamsActivity extends TitleActivity implements View.OnClickListener {

    /**
     * 外部传进来的参数ID
     */
    private int recordParamsId;

    private ParamsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);
        Intent intent = getIntent();
        recordParamsId = intent.getIntExtra("type", 0);
        initView();

    }

    private void initView() {
        RelativeLayout barTitle = findViewById(R.id.bar_title);
        TextView tvTitleTitle = findViewById(R.id.tv_title_title);
        TextView tvTitleLeft = findViewById(R.id.tv_title_left);
        final ListView lvRecordParams = findViewById(R.id.lv_record_params);

        //设置标题栏各参数
        tvTitleLeft.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_back, 0, 0, 0);
        barTitle.setBackgroundColor(Color.WHITE);
        tvTitleTitle.setTextColor(Color.BLACK);
        tvTitleLeft.setOnClickListener(this);

        switch (recordParamsId) {
            case R.string.choose_city:
                mTitleLayoutManager.setTitle(R.string.choose_city).setBackgroundColor(R.color.white).setTitleGravity(Gravity.CENTER).setLeftIcon(R.drawable.ic_left_arrow_gray).showLeft(true);
                break;
        }

        lvRecordParams.setAdapter(adapter);
        lvRecordParams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //获取上次选中的View
                View preView = lvRecordParams.getChildAt(adapter.getCurPosition());
                CheckedTextView preCtv = preView.findViewById(R.id.ctv);
                preCtv.setTextColor(AppUtil.getColor(R.color.black));

                preCtv.setChecked(false);

                //设置当前的View选中
                CheckedTextView curCtv = view.findViewById(R.id.ctv);
                curCtv.setChecked(true);
                curCtv.setTextColor(AppUtil.getColor(R.color.blue_ctv));

                adapter.setCurPosition(position);
                adapter.notifyDataSetChanged();

                //把当前选中的位置根据类型保存下来
                switch (recordParamsId) {

                }
                //把数值传回去
                setResult(Constant.RESULT_CODE_PARAMS_ACT, new Intent().putExtra("content", adapter.getChooseData()));
                finish();
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_left:
                setResult(Constant.RESULT_CODE_PARAMS_ACT, new Intent().putExtra("content", adapter.getChooseData()));
                finish();
                break;
            default:
                break;
        }

    }

}
