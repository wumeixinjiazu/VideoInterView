package com.videocomm.VideoInterView.activity.base;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.utils.AppUtil;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/30 0030]
 * @function[功能简介 头部布局管理类]
 **/
public class TitleLayoutManager {

    public View batTitle;
    public TextView tvTitle;
    public TextView tvLeft;
    public TextView tvRight;

    public TitleLayoutManager(View barTitle) {
        if (barTitle == null) {
            return;
        }
        this.batTitle = barTitle;
        tvTitle = barTitle.findViewById(R.id.tv_title_title);
        tvLeft = barTitle.findViewById(R.id.tv_title_left);
        tvRight = barTitle.findViewById(R.id.tv_title_right);
    }

    //设置监听
    public TitleLayoutManager setRightListener(View.OnClickListener clickListener) {
        if (tvRight == null) {
            return this;
        }
        tvRight.setOnClickListener(clickListener);
        return this;
    }

    public TitleLayoutManager setLeftListener(View.OnClickListener clickListener) {
        if (tvLeft == null) {
            return this;
        }
        tvLeft.setOnClickListener(clickListener);
        return this;
    }

    //设置标题
    public TitleLayoutManager setTitle(int resId) {
        if (tvTitle == null) {
            return this;
        }
        tvTitle.setText(resId);
        return this;
    }

    public TitleLayoutManager setTitle(CharSequence title) {
        if (tvTitle == null) {
            return this;
        }
        tvTitle.setText(title);
        return this;
    }

    //是否展示控件
    public TitleLayoutManager showTitle(boolean isShow) {
        if (tvTitle == null) {
            return this;
        }
        tvTitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
        return this;
    }

    public TitleLayoutManager showLeft(boolean isShow) {
        if (tvLeft == null) {
            return this;
        }
        tvLeft.setVisibility(isShow ? View.VISIBLE : View.GONE);
        return this;
    }

    public TitleLayoutManager showRight(boolean isShow) {
        if (tvRight == null) {
            return this;
        }
        tvRight.setVisibility(isShow ? View.VISIBLE : View.GONE);
        return this;
    }

    //设置图标
    public TitleLayoutManager setRightIcon(int resId) {
        if (tvRight == null) {
            return this;
        }
        tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
        return this;
    }

    public TitleLayoutManager setLeftIcon(int resId) {
        if (tvLeft == null) {
            return this;
        }
        tvLeft.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        return this;
    }

    //设置背景
    public TitleLayoutManager setBackgroundColor(int color){
        if (batTitle == null){
            return this;
        }
        batTitle.setBackgroundColor(AppUtil.getColor(color));
        return this;
    }

    //设置标题的Gravity
    public TitleLayoutManager setTitleGravity(int gratity){
        if (tvTitle == null){
            return this;
        }
        tvTitle.setGravity(gratity);
        return this;
    }


}
