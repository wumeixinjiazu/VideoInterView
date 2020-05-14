package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.videocomm.VideoInterView.Config;
import com.videocomm.VideoInterView.Constant;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.ParamsActivity;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.adapter.NetworkAdapter;
import com.videocomm.VideoInterView.bean.IdCardBackBean;
import com.videocomm.VideoInterView.bean.NetworkBean;
import com.videocomm.VideoInterView.bean.TradeInfo;
import com.videocomm.VideoInterView.simpleListener.SimpleTextWatcher;
import com.videocomm.VideoInterView.utils.HttpUtil;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.SpUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;
import com.videocomm.ai.baidu.ui.FaceLivenessActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/30 0030]
 * @function[功能简介 选择办理网点界面]
 **/
public class ChooseNetworkActivity extends TitleActivity implements View.OnClickListener {

    private ImageView ivSearchClean;
    private TextView tvChooseCityIn;
    private EditText etSearch;

    private NetworkAdapter adapter;
    private ListView lvNetWorkList;
    private VideoApplication mVideoApplication;
    private NetworkBean networkBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_network);
        mTitleLayoutManager.setTitle(getString(R.string.choose_network));

        initView();
        //请求数据
        requestData(tvChooseCityIn.getText().toString());

    }


    /**
     * 请求网点数据
     */
    private void requestData(String city) {

        HttpUtil.requestNetwork(city, SpUtil.getInstance().getString(SpUtil.USERPHONE, ""), "10", "0", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(tag, e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getString(R.string.request_fail));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                Log.d(tag, content);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!content.contains("resultList")) {
                            ToastUtil.show(getString(R.string.request_fail));
                        } else {
                            networkBean = JsonUtil.jsonToBean(content, NetworkBean.class);
                            if (networkBean == null) {
                                return;
                            }
                            if (adapter == null) {
                                List<NetworkBean.ContentBean.ResultListBean> resultList = networkBean.getContent().getResultList();
                                if (resultList.size() > 0) {
                                    adapter = new NetworkAdapter(ChooseNetworkActivity.this, resultList);
                                    adapter.setClickItem(0);//默认为0
                                    lvNetWorkList.setAdapter(adapter);
                                }

                            } else {
                                List<NetworkBean.ContentBean.ResultListBean> resultList = networkBean.getContent().getResultList();
                                if (resultList.size() > 0 && adapter != null) {
                                    adapter.setClickItem(0);//默认为0
                                }
                                if (adapter != null) {
                                    adapter.refreshData(networkBean.getContent().getResultList());
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        mVideoApplication = (VideoApplication) getApplication();

        etSearch = findViewById(R.id.et_search);
        ivSearchClean = findViewById(R.id.iv_search_clean);
        tvChooseCityIn = findViewById(R.id.tv_choose_city_in);
        TextView tvChooseCity = findViewById(R.id.tv_choose_city);
        lvNetWorkList = findViewById(R.id.lv_network_list);
        Button btnNext = findViewById(R.id.btn_next);

        ivSearchClean.setOnClickListener(this);
        tvChooseCityIn.setOnClickListener(this);
        tvChooseCity.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        //初始化城市名 根据上次的选择
        tvChooseCityIn.setText(getResources().getStringArray(R.array.city_list)[SpUtil.getInstance().getInt(SpUtil.CHOOSECITYPOSITION)]);

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

        etSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                Log.d(tag, s.toString());
                if (adapter != null) {
                    adapter.search(s.toString());
                }
            }
        });

        //listview 监听
        lvNetWorkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setClickItem(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_clean:
                etSearch.setText("");
                break;
            case R.id.tv_choose_city:
            case R.id.tv_choose_city_in:
                startRecordParamsActivity(R.string.choose_city);
                break;
            case R.id.btn_next:
                if (adapter == null || adapter.getClickItem() == -1 || networkBean == null) {
                    ToastUtil.show("请选择办理网点");
                    return;
                }
                //保存网点数据
                List<TradeInfo.ExInfosBean> exInfos = new ArrayList<>();
                List<NetworkBean.ContentBean.ResultListBean> resultList = networkBean.getContent().getResultList();
                NetworkBean.ContentBean.ResultListBean bean = resultList.get(adapter.getClickItem());
                TradeInfo.ExInfosBean exInfosBean = new TradeInfo.ExInfosBean();
                exInfosBean.setExKey(bean.getName());
                exInfosBean.setExValue(bean.getAddress());
                exInfos.add(exInfosBean);
                mVideoApplication.setExInfos(exInfos);

                startActivity(new Intent(ChooseNetworkActivity.this, IdentityVerifyActivity.class));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String city = data.getStringExtra("content");
            tvChooseCityIn.setText(city);
            requestData(city);
        }
    }
}
