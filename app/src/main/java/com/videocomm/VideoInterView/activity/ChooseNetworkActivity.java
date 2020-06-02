package com.videocomm.VideoInterView.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.videocomm.VideoInterView.Constant;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.VideoApplication;
import com.videocomm.VideoInterView.activity.base.ParamsActivity;
import com.videocomm.VideoInterView.activity.base.TitleActivity;
import com.videocomm.VideoInterView.adapter.NetworkAdapter;
import com.videocomm.VideoInterView.bean.NetworkBean;
import com.videocomm.VideoInterView.bean.TradeInfo;
import com.videocomm.VideoInterView.simpleListener.SimpleTextWatcher;
import com.videocomm.VideoInterView.utils.DialogFactory;
import com.videocomm.VideoInterView.utils.HttpUtil;
import com.videocomm.VideoInterView.utils.JsonUtil;
import com.videocomm.VideoInterView.utils.SpUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;

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
    private LocationClient mLocationClient;
    private String[] cityList;
    private ImageView ivNoData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_network);
        mTitleLayoutManager.setTitle(getString(R.string.choose_network));
        initView();
        //请求数据
        requestData(tvChooseCityIn.getText().toString());
        initLocationOption();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    /**
     * 请求网点数据
     */
    private void requestData(String city) {
        HttpUtil.requestNetwork(city, SpUtil.getInstance().getString(SpUtil.USERPHONE, ""), "10", "0", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(tag, e.getMessage());
                runOnUiThread(() -> ToastUtil.show(getString(R.string.request_fail)));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                Log.d(tag, content);
                runOnUiThread(() -> {
                    if (!content.contains("resultList")) {
                        ToastUtil.show(getString(R.string.request_fail));
                    } else {
                        networkBean = JsonUtil.jsonToBean(content, NetworkBean.class);
                        if (networkBean == null) {
                            return;
                        }
                        //第一次创建
                        if (adapter == null) {
                            List<NetworkBean.ContentBean.ResultListBean> resultList = networkBean.getContent().getResultList();
                            if (resultList.size() > 0) {
                                adapter = new NetworkAdapter(ChooseNetworkActivity.this, resultList);
                                adapter.setClickItem(0);//默认为0
                                lvNetWorkList.setAdapter(adapter);
                                ivNoData.setVisibility(View.INVISIBLE);
                            } else {
                                ivNoData.setVisibility(View.VISIBLE);
                            }

                        } else {
                            List<NetworkBean.ContentBean.ResultListBean> resultList = networkBean.getContent().getResultList();
                            //刷新数据
                            adapter.refreshData(networkBean.getContent().getResultList());
                            if (resultList.size() > 0 && adapter != null) {
                                adapter.setClickItem(0);//默认为0
                                ivNoData.setVisibility(View.INVISIBLE);
                            } else {
                                ivNoData.setVisibility(View.VISIBLE);
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
        ivNoData = findViewById(R.id.iv_no_data);
        Button btnNext = findViewById(R.id.btn_next);

        ivSearchClean.setOnClickListener(this);
        tvChooseCityIn.setOnClickListener(this);
        tvChooseCity.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        //初始化城市名 根据上次的选择
        cityList = getResources().getStringArray(R.array.city_list);
        String defaultCity = cityList[SpUtil.getInstance().getInt(SpUtil.CHOOSECITYPOSITION)];
        tvChooseCityIn.setText(defaultCity);

        //etSearch 文本监听
        etSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                ivSearchClean.setVisibility(0 == s.length() ? View.GONE : View.VISIBLE);
            }
        });

        //etSearch 焦点监听
        etSearch.setOnFocusChangeListener((v, hasFocus) -> ivSearchClean.setVisibility(hasFocus && etSearch.getText().length() > 0 ? View.VISIBLE : View.GONE));

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
        lvNetWorkList.setOnItemClickListener((parent, view, position, id) -> adapter.setClickItem(position));
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
                //保存数据
                List<TradeInfo.ExInfosBean> exInfos = new ArrayList<>();
                TradeInfo.ExInfosBean exInfosBean = new TradeInfo.ExInfosBean();
                exInfosBean.setExKey(adapter.getChooseNetworkName());
                exInfosBean.setExValue(adapter.getChooseNetworkAddr());
                exInfos.add(exInfosBean);
                mVideoApplication.setExInfos(exInfos);
                Log.d(tag, adapter.getChooseNetworkName());
                Log.d(tag, adapter.getChooseNetworkAddr());
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

    /**
     * 初始化定位参数配置
     */
    private void initLocationOption() {
//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        mLocationClient = new LocationClient(getApplicationContext());
//声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
//注册监听函数
        mLocationClient.registerLocationListener(myLocationListener);
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("BD09ll");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
//可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(locationOption);
//开始定位
        mLocationClient.start();
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();
            //获取地址
            String address = location.getAddrStr();
            //获取地址详细信息
            String addrDesc = location.getLocationDescribe();
            //获取城市名
            String city = location.getCity();
            if (errorCode == 161) {
                //定位成功
                city = city.replace("市", "");
                refeshChooseCIty(city);
                mVideoApplication.setLatitude(latitude);
                mVideoApplication.setLongitude(longitude);
                mVideoApplication.setAddress(address);
                mVideoApplication.setAddressDesc(addrDesc);

                mLocationClient.stop();
            } else {
                //定位失败 重新定位
                initLocationOption();
            }

            Log.d(tag, "latitude" + latitude);
            Log.d(tag, "longitude" + longitude);
            Log.d(tag, "address" + address);
            Log.d(tag, "LocationDescribe" + addrDesc);
            Log.d(tag, "city" + location.getCity());
            Log.d(tag, "radius" + radius);
            Log.d(tag, "getAddrStr" + location.getAddrStr());
            Log.d(tag, "coorType" + coorType);
            Log.d(tag, "errorCode" + errorCode);
        }
    }

    /**
     * 刷新选择的城市
     *
     * @param city
     */
    private void refeshChooseCIty(String city) {
        //如果一样则不用查询
        if (tvChooseCityIn.getText().toString().equals(city)) {
            return;
        }

        for (int i = 0; i < cityList.length; i++) {
            if (cityList[i].equals(city)) {
                tvChooseCityIn.setText(cityList[i]);
                requestData(cityList[i]);
                SpUtil.getInstance().saveInt(SpUtil.CHOOSECITYPOSITION, i);
                break;
            }
        }

        ToastUtil.show("暂无" + city + "网点");
    }

    @Override
    public void onBackPressed() {
        DialogFactory.getDialog(DialogFactory.DIALOGID_EXIT_ACT, this, v -> {
            finish();
        }).show();
    }
}
