package com.videocomm.VideoInterView.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.bean.ChatMsg;
import com.videocomm.VideoInterView.utils.StringUtil;

import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/27 0027]
 * @function[功能简介 ]
 **/
public class ChatAdapter extends BaseAdapter {

    private final Context mContext;
    private List<ChatMsg> data;
    private final LayoutInflater mInflater;

    public ChatAdapter(Context context, List<ChatMsg> data) {
        this.mContext = context;
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    public void refreshData(List<ChatMsg> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = null;
        if (convertView == null) {
            holder = new Holder();
            view = mInflater.inflate(R.layout.item_chat, null);
            holder.leftLayout = view.findViewById(R.id.left_layout);
            holder.leftMsg = view.findViewById(R.id.left_msg);
            holder.leftTime = view.findViewById(R.id.left_time);
            holder.rightLayout = view.findViewById(R.id.right_Layout);
            holder.rightMsg = view.findViewById(R.id.right_msg);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (Holder) view.getTag();
        }
        ChatMsg chatMsg = (ChatMsg) getItem(position);
        if (chatMsg.getType() == ChatMsg.RECEIVED) {
            //如果是收到的消息，则显示左边消息布局，将右边消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftTime.setText(chatMsg.getTime());
            String frontContent = chatMsg.getUser() + ": ";
            holder.leftMsg.setText(Html.fromHtml(mContext.getResources().getString(R.string.blue_white, frontContent, chatMsg.getContent())));
        } else if (chatMsg.getType() == ChatMsg.SENT) {
            //如果是发出去的消息，显示右边布局的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(Html.fromHtml(mContext.getResources().getString(R.string.blue_white, chatMsg.getUser() + ": ", chatMsg.getContent())));
        }
        return view;
    }

    static class Holder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView leftTime;
        TextView rightMsg;
    }
}
