package com.videocomm.VideoInterView.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.videocomm.VideoInterView.bean.ProductsBean;
import com.videocomm.VideoInterView.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/17 0017]
 * @function[功能简介]
 **/
public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.Holder> {
    private static final int HANDLER_FILE_DOWNLOAD_SUCCESS = 0;
    private static final int HANDLER_FILE_DOWNLOAD_FAILD = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_FILE_DOWNLOAD_SUCCESS:
                    byte[] bytes = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (holder != null) {
                        holder.ivBusiness.setBackground(null);
                        holder.ivBusiness.setImageBitmap(bitmap);
                    }
                    break;
                case HANDLER_FILE_DOWNLOAD_FAILD:
                    break;
                default:
                    break;
            }

        }
    };
    private List<ProductsBean.ContentBean> data;
    private int[] ids = {R.drawable.ic_business_car, R.drawable.ic_business_small, R.drawable.ic_business_house, R.drawable.ic_business_idcard};
    private int selectIndex = -1;//选择的item
    private Holder holder;
    private String tag = this.getClass().getSimpleName();

    public int getSelectIndex() {
        return selectIndex;
    }

    /**
     * @return 返回选择的业务
     */
    public String getSelectBusiness() {
        if (selectIndex == -1) {
            return "";
        }
        return data.get(selectIndex).getName();
    }

    /**
     * @return 返回选择的code 用来获取对应的队列
     */
    public String getSelectCode() {
        if (selectIndex == -1) {
            return "";
        }
        return data.get(selectIndex).getCode();
    }

    /**
     * @param selectIndex 选择的下标
     */
    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public BusinessAdapter(List<ProductsBean.ContentBean> data) {
        this.data = data;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, null, false);
        holder = new Holder(view, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tvName.setText(data.get(position).getName());
        if (position == selectIndex) {
            holder.ivMask.setVisibility(View.VISIBLE);
            holder.ivChoose.setVisibility(View.VISIBLE);
        } else {
            holder.ivMask.setVisibility(View.GONE);
            holder.ivChoose.setVisibility(View.GONE);
        }

        //获取图片刷新
        HttpUtil.requestFileDownload(data.get(position).getCoverUrl(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(HANDLER_FILE_DOWNLOAD_FAILD);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    return;
                }
                byte[] bytes = response.body().bytes();
                Message obtain = Message.obtain();
                obtain.what = HANDLER_FILE_DOWNLOAD_SUCCESS;
                obtain.obj = bytes;
                mHandler.sendMessage(obtain);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {


        private ImageView ivBusiness;
        private final ImageView ivMask;
        private final ImageView ivChoose;
        private final TextView tvName;

        Holder(@NonNull View itemView, final BusinessAdapter businessAdapter) {
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
