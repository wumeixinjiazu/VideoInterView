package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.mediasdk.VComMediaSDK;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/26 0026]
 * @function[功能简介 面签结果画面]
 **/
public class ResultActivity extends TitleActivity implements View.OnClickListener {

    private VComMediaSDK sdkUnit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mTitleLayoutManager.setTitle("面签结果");
        initView();

        if (sdkUnit == null) {
            sdkUnit = VComMediaSDK.GetInstance();
        }
    }

    private void initView() {
        ImageView ivResultIcon = findViewById(R.id.iv_result_icon);
        TextView tvResultText = findViewById(R.id.tv_result_text);
        Button btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
        //录制是否成功
        boolean isSuccess = getIntent().getBooleanExtra("isSuccess", false);
        if (isSuccess) {
            ivResultIcon.setBackgroundResource(R.drawable.ic_result_true);
            tvResultText.setText("双录结果：成功");
        } else {
            ivResultIcon.setBackgroundResource(R.drawable.ic_result_false);
            tvResultText.setText("双录结果：失败，用户主动退出");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sdkUnit != null) {
            sdkUnit.VCOM_LeaveConference();
            sdkUnit.VCOM_Logout();
        }
    }
}
