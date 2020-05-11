package com.videocomm.VideoInterView.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.videocomm.VideoInterView.R;

import java.util.ArrayList;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/17 0017]
 * @function[功能简介]
 **/
public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.Holder> {

    private ArrayList<String> data = new ArrayList<>();
    private int[] ids = {R.drawable.ic_business_car, R.drawable.ic_business_small, R.drawable.ic_business_house, R.drawable.ic_business_idcard};
    private int selectIndex = -1;//选择的item

    public int getSelectIndex() {
        return selectIndex;
    }

    public String getSelectBusiness() {
        if (selectIndex == -1) {
            return "";
        }
        return data.get(selectIndex);
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public BusinessAdapter() {
        data.add("汽车消费办理业务");
        data.add("小额消费贷款业务");
        data.add("房屋装修贷业务");
        data.add("信用卡面签业务业务");
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, null, false);
        Holder holder = new Holder(view, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.ivBusiness.setBackgroundResource(ids[position]);
        holder.tvName.setText(data.get(position));
        if (position == selectIndex) {
            holder.ivMask.setVisibility(View.VISIBLE);
            holder.ivChoose.setVisibility(View.VISIBLE);
        } else {
            holder.ivMask.setVisibility(View.GONE);
            holder.ivChoose.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class Holder extends RecyclerView.ViewHolder {


        private ImageView ivBusiness;
        private final ImageView ivMask;
        private final ImageView ivChoose;
        private final TextView tvName;

        public Holder(@NonNull View itemView, final BusinessAdapter businessAdapter) {
            super(itemView);
            ivBusiness = itemView.findViewById(R.id.iv_business);
            RelativeLayout rlItem = itemView.findViewById(R.id.rl_item);
            ivMask = itemView.findViewById(R.id.iv_business_mask);
            ivChoose = itemView.findViewById(R.id.iv_business_choose);
            tvName = itemView.findViewById(R.id.tv_business_name);
            rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    businessAdapter.setSelectIndex(getLayoutPosition());
                    businessAdapter.notifyDataSetChanged();
                }
            });


        }
    }
}
