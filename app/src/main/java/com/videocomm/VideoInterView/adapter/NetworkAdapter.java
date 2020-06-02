package com.videocomm.VideoInterView.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.bean.NetworkBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/21 0021]
 * @function[功能简介]
 **/
public class NetworkAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;

    /**
     * 存储listview数据 为搜索提供数据填充
     */
    private List<NetworkBean.ContentBean.ResultListBean> datas;
    private List<NetworkBean.ContentBean.ResultListBean> oldDatas;
    private List<NetworkBean.ContentBean.ResultListBean> newDatas;

    private int clickItem = -1;//记录被点击的Item

    public int getClickItem() {
        return clickItem;
    }

    public String getChooseNetworkName() {
        if (clickItem == -1) {
            return "";
        }
        return datas.get(clickItem).getName();
    }

    public String getChooseNetworkAddr() {
        if (clickItem == -1) {
            return "";
        }
        return datas.get(clickItem).getAddress();
    }

    public void setClickItem(int clickItem) {
        this.clickItem = clickItem;
        notifyDataSetChanged();
    }

    public NetworkAdapter(Context context, List<NetworkBean.ContentBean.ResultListBean> datas) {
        this.context = context;
        this.datas = datas;
        oldDatas = datas;
        inflater = LayoutInflater.from(context);
        newDatas = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_network, null);
            holder.tvName = convertView.findViewById(R.id.tv_network_name);
            holder.tvAddress = convertView.findViewById(R.id.tv_network_address);
            holder.tvTel = convertView.findViewById(R.id.tv_network_tel);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (clickItem == position) {
            convertView.setBackgroundResource(R.color.gray_cc);
        } else {
            convertView.setBackgroundResource(R.color.transparent);
        }
        holder.tvName.setText(datas.get(position).getName());
        holder.tvAddress.setText("地址：" + datas.get(position).getAddress());
        holder.tvTel.setText("电话：" + datas.get(position).getTelphone());
        return convertView;
    }

    /**
     * 刷新数据
     *
     * @param datas
     */
    public void refreshData(List<NetworkBean.ContentBean.ResultListBean> datas) {
        this.datas = datas;
        this.oldDatas = datas;
        clickItem = -1;
        notifyDataSetChanged();
    }

    /**
     * 刷新搜索数据
     *
     * @param datas
     */
    private void refreshSearchData(List<NetworkBean.ContentBean.ResultListBean> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    /**
     * 在oloDatas数据中检索有没有搜索的内容 并刷新
     *
     * @param key 搜索的文字
     */
    public void search(String key) {
        newDatas.clear();
        for (int i = 0; i < oldDatas.size(); i++) {
            if (oldDatas.get(i).getName().contains(key)) {
                newDatas.add(oldDatas.get(i));
            }
        }
        if (newDatas.size() > 0 || key.length() >= 1) {
            refreshSearchData(newDatas);
        } else {
            refreshSearchData(oldDatas);
        }
    }

    static class Holder {
        TextView tvName;
        TextView tvAddress;
        TextView tvTel;
    }
}
