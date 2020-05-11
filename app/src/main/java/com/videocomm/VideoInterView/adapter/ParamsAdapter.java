package com.videocomm.VideoInterView.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.utils.AppUtil;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/1/19 0019]
 * @function[功能简介]
 **/
public class ParamsAdapter extends BaseAdapter {

    private Context mContext;
    private final LayoutInflater inflater;
    private String[] datas;
    private int mCurPosition;

    public ParamsAdapter(Context context, int recordParamsId, int curPosition) {
        mContext = context;
        mCurPosition = curPosition;
        inflater = LayoutInflater.from(mContext);
        switch (recordParamsId) {
            case R.string.choose_city:
                datas = mContext.getResources().getStringArray(R.array.city_list);
                break;
        }
    }

    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int getCurPosition() {
        return mCurPosition;
    }

    public void setCurPosition(int mCurPosition) {
        this.mCurPosition = mCurPosition;
    }

    public String getChooseData() {
        return datas[mCurPosition];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_params_list, null);
        CheckedTextView ctv = view.findViewById(R.id.ctv);
        ctv.setText(datas[position]);
        if (mCurPosition == position) {
            ctv.setChecked(true);
            ctv.setTextColor(AppUtil.getColor(R.color.blue_ctv));
        }
        return view;
    }


}
