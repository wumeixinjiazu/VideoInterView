package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.adapter.BusinessAdapter;
import com.videocomm.VideoInterView.utils.ToastUtil;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/15 0015]
 * @function[功能简介 选择办理业务界面]
 **/
public class ChooseBusinessActivity extends TitleActivity {

    private RecyclerView mRecyclerView;
    private BusinessAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_business);
        mTitleLayoutManager.setTitle(getString(R.string.choose_handler_business));

        mRecyclerView = findViewById(R.id.recy);
        adapter = new BusinessAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_business_next:
                if (adapter.getSelectIndex() == -1) {
                    ToastUtil.show(getString(R.string.choose_business_least));
                    return;
                }
                VideoApplication mVideoApplication = (VideoApplication) getApplication();
                mVideoApplication.setSelectBussiness(adapter.getSelectBusiness());//保存选择的业务
                Intent intent = new Intent(ChooseBusinessActivity.this, RecordActivity.class);
                intent.putExtra("businessType", adapter.getSelectBusiness());
                startActivity(intent);
                finish();
                break;
        }
    }

}
