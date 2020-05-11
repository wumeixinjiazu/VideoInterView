package com.videocomm.VideoInterView.activity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.simpleListener.SimpleTextWatcher;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.SpUtil;
import com.videocomm.mediasdk.VComMediaSDK;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/30 0030]
 * @function[功能简介]
 **/
public class SettingActivity extends TitleActivity implements View.OnClickListener {

    private EditText etServerAddress;
    private EditText etServerPort;
    private ImageView ivAddressClean;
    private ImageView ivPortClean;
    private Switch swLivingCheck;
    private Switch swRiskReport;

    private boolean livingCheckState = false;//活体检测状态 默认关闭
    private boolean riskReportState = true;//风险播报状态 默认打开

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mTitleLayoutManager.setTitle(getString(R.string.setting)).setLeftIcon(R.drawable.ic_back).showLeft(true).setLeftListener(this);

        initView();
        initData();
    }

    private void initView() {

        TextView tvSdkVersion = findViewById(R.id.tv_sdk_version);
        etServerAddress = findViewById(R.id.et_server_address);
        etServerPort = findViewById(R.id.et_server_port);
        swLivingCheck = findViewById(R.id.switch_living_check);
        swRiskReport = findViewById(R.id.switch_risk_report);
        ivAddressClean = findViewById(R.id.iv_address_clean);
        ivPortClean = findViewById(R.id.iv_port_clean);

        ivAddressClean.setOnClickListener(this);
        ivPortClean.setOnClickListener(this);
        VComMediaSDK sdkUnit = VComMediaSDK.GetInstance();
        tvSdkVersion.setText("VideoComm SDK for Android " + JsonUtil.jsonToStr(sdkUnit.VCOM_GetSDKVersion(), "version"));
        initListener();
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        //etServerAddress 文本监听
        etServerAddress.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                ivAddressClean.setVisibility(0 == s.length() ? View.GONE : View.VISIBLE);
            }
        });

        //etServerPort 文本监听
        etServerPort.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                ivPortClean.setVisibility(0 == s.length() ? View.GONE : View.VISIBLE);
            }
        });

        //etServerAddress 焦点监听
        etServerAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivAddressClean.setVisibility(hasFocus && etServerAddress.getText().length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        //etAppId 焦点监听
        etServerPort.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivPortClean.setVisibility(hasFocus && etServerPort.getText().length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        //Switch 按钮监听
        swLivingCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //修改活体检测状态
                livingCheckState = isChecked;
                clearEditFocus();
            }
        });
        swRiskReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //修改风险播报状态
                riskReportState = isChecked;
                clearEditFocus();
            }
        });
    }

    private void initData() {
        etServerAddress.setText(SpUtil.getInstance().getString(SpUtil.SERVERADDR, getResources().getString(R.string.default_server_addr)));
        etServerPort.setText(SpUtil.getInstance().getString(SpUtil.SERVERPORT, getResources().getString(R.string.default_server_port)));
        swLivingCheck.setChecked(SpUtil.getInstance().getBoolean(SpUtil.LIVINGCHECKSTATE, true));
        swRiskReport.setChecked(SpUtil.getInstance().getBoolean(SpUtil.RISKREPORTSTATE, true));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_left:
                saveData();
                finish();
                break;
            case R.id.iv_address_clean:
                etServerAddress.setText("");
                break;
            case R.id.iv_port_clean:
                etServerPort.setText("");
                break;
        }
    }

    /**
     * 保存数据
     */
    private void saveData() {
        SpUtil.getInstance().saveString(SpUtil.SERVERADDR, etServerAddress.getText().toString());
        SpUtil.getInstance().saveString(SpUtil.SERVERPORT, etServerPort.getText().toString());
        SpUtil.getInstance().saveBoolean(SpUtil.LIVINGCHECKSTATE, livingCheckState);
        SpUtil.getInstance().saveBoolean(SpUtil.RISKREPORTSTATE, riskReportState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearEditFocus();
        ivAddressClean.setVisibility(View.INVISIBLE);
        ivPortClean.setVisibility(View.INVISIBLE);
    }

    private void clearEditFocus() {
        etServerAddress.clearFocus();
        etServerPort.clearFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
