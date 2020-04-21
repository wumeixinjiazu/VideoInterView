package com.videocomm.VideoInterView.dlgfragment.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.videocomm.VideoInterView.R;


/**
 * @author[wengCJ]
 * @version[创建日期，2020/1/2 0002]
 * @function[功能简介 DialogFragment的基类]
 **/
public abstract class BaseDlgFragment extends DialogFragment {

    protected String tag = getClass().getSimpleName();

    /**
     * 初始化布局
     *
     * @return 布局
     */
    protected abstract int initLayout();

    /**
     * 初始化view
     *
     * @param view 布局的view
     */
    protected abstract void initView(View view);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = initLayout();//获取子类定义的布局ID
        Window window = getDialog().getWindow();
        View view = inflater.inflate(layoutId, ((ViewGroup) window.findViewById(android.R.id.content)), false);//需要用android.R.id.content这个view
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(-1, -2);//这2行,和上面的一样,注意顺序就行
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.windowAnimations = R.style.HdSettingAnimation;//设置DialogFragment的动画
        window.setAttributes(lp);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
