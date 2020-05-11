package com.videocomm.VideoInterView.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/16 0016]
 * @function[功能简介 适配SurfaceView全屏拉伸问题]
 **/

public class LocalFullScreenCameraPreview extends LinearLayout {
    private String tag = getClass().getSimpleName();
    private Context mContext;

    public LocalFullScreenCameraPreview(Context context) {
        this(context, null);
    }

    public LocalFullScreenCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        Log.d(tag, left + "-" + top + "-" + right + "-" + bottom);
        super.onLayout(changed, left, top, right, bottom);
        Log.d(tag, left + "-" + top + "-" + right + "-" + bottom);
        int screenRealWidth;
        int screenRealHeigh;
        if (isPortraitMode()) {
            screenRealWidth = getScreenRealWidth(mContext);
            screenRealHeigh = getScreenRealHeigh(mContext);
        } else {
            screenRealHeigh = getScreenRealWidth(mContext);
            screenRealWidth = getScreenRealHeigh(mContext);
        }
        Log.d(tag, "getScreenRealWidth" + getScreenRealWidth(mContext));
        Log.d(tag, "getScreenRealHeigh" + getScreenRealHeigh(mContext));

        Log.d(tag, "screenRealWidth" + screenRealWidth);
        Log.d(tag, "screenRealHeigh" + screenRealHeigh);

        final int layoutWidth = right - left;
        final int layoutHeight = bottom - top;
        Log.d(tag, "layoutWidth" + layoutWidth);
        Log.d(tag, "layoutHeight" + layoutHeight);


        //本地摄像头全屏展示适配(自适应展示会上下留白) 宽高比3:4
        int childWidth = layoutWidth;
        int childHeight = layoutWidth * 4 / 3;

        //本地摄像头全屏展示适配(画面进行裁剪铺满整个屏幕)
        childWidth = childWidth * screenRealHeigh / childHeight;
        childHeight = layoutHeight;
        Log.e("====", "screenRealWidth:" + screenRealWidth +
                " screenRealHeigh:" + screenRealHeigh +
                " layoutWidth:" + layoutWidth + " layoutHeight:" + layoutHeight +
                " childWidth:" + childWidth + "  childHeight:" + childHeight);
        //layoutWidth:1080  childWidth:1620
        int childLeft = (childWidth - layoutWidth) / 2;
        Log.e("====childLeft ", childLeft + "");
        for (int i = 0; i < this.getChildCount(); ++i) {
            this.getChildAt(i).layout(0, 0, childWidth, childHeight);//设置子View的位置
            this.getChildAt(i).setTranslationX(-childLeft);//设置X的偏移量
        }
    }

    /**
     * 是否是竖直模式
     */
    private boolean isPortraitMode() {
        final int orientation = this.mContext.getResources().getConfiguration().orientation;
        return orientation != Configuration.ORIENTATION_LANDSCAPE && orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 获取屏幕真实宽度
     */
    private int getScreenRealWidth(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
            return vh;
        }
        return vh;
    }

    /**
     * 获取屏幕真实高度(不包含底部虚拟返回键高度)
     */
    private int getScreenRealHeigh(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
//        DisplayMetrics dm = new DisplayMetrics();
        try {
//            @SuppressWarnings("rawtypes")
//            Class c = Class.forName("android.view.Display");
//            @SuppressWarnings("unchecked")
//            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
//            method.invoke(display, dm);
//            vh = dm.heightPixels;
            vh = windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
            return vh;
        }
        return vh;
    }

}