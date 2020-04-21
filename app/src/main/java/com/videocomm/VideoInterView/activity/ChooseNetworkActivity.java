package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.Constant;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.base.ParamsActivity;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.simpleListener.SimpleTextWatcher;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/30 0030]
 * @function[功能简介 选择办理网点界面]
 **/
public class ChooseNetworkActivity extends TitleActivity implements View.OnClickListener {

    private ImageView ivSearchClean;
    private EditText etSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_network);
        mTitleLayoutManager.setTitle(getString(R.string.choose_network));

        initView();
    }

    private void initView(){
        etSearch = findViewById(R.id.et_search);
        ivSearchClean = findViewById(R.id.iv_search_clean);
        TextView tvChooseCityIn = findViewById(R.id.tv_choose_city_in);
        TextView tvChooseCity = findViewById(R.id.tv_choose_city);
        ListView lvNetWorkList = findViewById(R.id.lv_network_list);
        Button btnNext = findViewById(R.id.btn_next);

        ivSearchClean.setOnClickListener(this);
        tvChooseCityIn.setOnClickListener(this);
        tvChooseCity.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        //etSearch 文本监听
        etSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                ivSearchClean.setVisibility(0 == s.length() ? View.GONE : View.VISIBLE);
            }
        });

        //etSearch 焦点监听
        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivSearchClean.setVisibility(hasFocus && etSearch.getText().length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search_clean:
                etSearch.setText("");
                break;
            case R.id.tv_choose_city:
            case R.id.tv_choose_city_in:
                startRecordParamsActivity(R.string.choose_city);
                break;
            case R.id.btn_next:
                startActivity(new Intent(ChooseNetworkActivity.this,IdentityVerifyActivity.class));
                finish();
                break;
        }
    }

    /**
     * 启动参数Activity
     *
     * @param type 录制参数类型
     */
    public void startRecordParamsActivity(int type) {
        Intent intent = new Intent(ChooseNetworkActivity.this, ParamsActivity.class);
        intent.putExtra("type", type);
        this.startActivityForResult(intent, Constant.REQUEST_CODE_CHOOSE_ACT);
    }
}
