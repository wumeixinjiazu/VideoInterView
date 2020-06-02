package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.adapter.BusinessAdapter;
import com.videocomm.VideoInterView.bean.ProductsBean;
import com.videocomm.VideoInterView.bean.RouteBean;
import com.videocomm.VideoInterView.utils.DialogFactory;
import com.videocomm.VideoInterView.utils.HttpUtil;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

        requestData();
    }

    private void requestData() {
        HttpUtil.requestGetProducts(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                Log.d(tag, content);
                runOnUiThread(() -> {
                    ProductsBean productsBean = JsonUtil.jsonToBean(content, ProductsBean.class);
                    if (productsBean.getErrorcode() == 0) {
                        adapter = new BusinessAdapter(productsBean.getContent());
                        mRecyclerView.setAdapter(adapter);
                        mRecyclerView.setLayoutManager(new GridLayoutManager(ChooseBusinessActivity.this, 2));
                    }
                });
            }
        });
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
                mVideoApplication.setSelectCode(adapter.getSelectCode());//保存选择的业务代码
                //通过路由接口获取队列ID
                HttpUtil.requestGetRoute(adapter.getSelectCode(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            ToastUtil.show("获取失败，请重试");
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String content = response.body().string();
                        Log.d(tag, content);
                        runOnUiThread(() -> {
                            RouteBean routeBean = JsonUtil.jsonToBean(content, RouteBean.class);
                            if (routeBean.getErrorcode() == 0) {
                                mVideoApplication.setQueueId(routeBean.getContent().getQueueId());
                                Intent intent = new Intent(ChooseBusinessActivity.this, RecordActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }
    @Override
    public void onBackPressed() {
        DialogFactory.getDialog(DialogFactory.DIALOGID_EXIT_ACT, this, v -> {
            finish();
        }).show();
    }
}
