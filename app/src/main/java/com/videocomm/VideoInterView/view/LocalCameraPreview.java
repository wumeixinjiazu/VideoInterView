package com.videocomm.VideoInterView.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.videocomm.VideoInterView.utils.DisplayUtil;

import java.lang.reflect.Method;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/3/16 0016]
 * @function[功能简介 适配SurfaceView全屏拉伸问题]
 **/

public class LocalCameraPreview extends LinearLayout {
    private String tag = getClass().getSimpleName();
    private Context mContext;
    private boolean isAuto = true;//设置是否自动调节
    private int layoutWidth;
    private int layoutHeight;
    private float downX;
    private float downY;
    int screenWidth = 0;
    int screenHeight = 0;
    int l, r, t, b; // 上下左右四点移动后的偏移量
    private boolean isDrag = true;

    /**
     * @param isDrag 是否可以拖动
     */
    public void setDrag(boolean isDrag) {
        this.isDrag = isDrag;
    }

    /**
     * @param isAuto 是否需要自动调节布局
     */
    public void setAuto(boolean isAuto) {
        this.isAuto = isAuto;
    }

    public LocalCameraPreview(Context context) {
        this(context, null);
    }

    public LocalCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        screenWidth = DisplayUtil.getScreenWidth();
        screenHeight = DisplayUtil.getScreenHeight();

        Log.e(tag, left + "-" + top + "-" + right + "-" + bottom);
        int screenRealWidth;
        int screenRealHeigh;
        if (isPortraitMode()) {
            screenRealWidth = getScreenRealWidth(mContext);
            screenRealHeigh = getScreenRealHeigh(mContext);
        } else {
            screenRealHeigh = getScreenRealWidth(mContext);
            screenRealWidth = getScreenRealHeigh(mContext);
        }

        layoutWidth = right - left;
        layoutHeight = bottom - top;

        //本地摄像头全屏展示适配(自适应展示会上下留白) 宽高比3:4
        int childWidth = layoutWidth;
        int childHeight = layoutWidth * 4 / 3;

        //本地摄像头全屏展示适配(画面进行裁剪铺满整个屏幕)
        childWidth = childWidth * screenRealHeigh / childHeight;
        childHeight = layoutHeight;
        Log.e(tag, "screenRealWidth:" + screenRealWidth +
                " screenRealHeigh:" + screenRealHeigh +
                " layoutWidth:" + layoutWidth + " layoutHeight:" + layoutHeight +
                " childWidth:" + childWidth + "  childHeight:" + childHeight);
        //layoutWidth:1080  childWidth:1620
        int childLeft = (childWidth - layoutWidth) / 2;
        Log.e("====childLeft ", childLeft + "");
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (isAuto) {
                this.getChildAt(i).layout(0, 0, childWidth, childHeight);//设置子View的位置
                this.getChildAt(i).setTranslationX(-childLeft);//设置X的偏移量
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Log.e(tag, "onTouchEvent layoutWidth:" + layoutWidth);

        float distanceParentLeft = getX();//按下的触摸点X坐标 自己（View）的最左边距离父类最左边的距离
        float distanceParentTop = getY();//按下的触摸点Y坐标 自己（View）的最上边距离父类最上边的具体
        float distanceSelfLeft = event.getX();//按下的触摸点X坐标距离自己（View）最左边的距离
        float distanceSelfTop = event.getY();//按下的触摸点Y坐标 具体自己（View）最上边的具体
        int x = (int) event.getRawX(); //触摸点相对于屏幕的横坐标
        int y = (int) event.getRawY(); //触摸点相对于屏幕的纵坐标

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();//屏幕的X坐标
                downY = event.getRawY();//屏幕的Y坐标
                break;

            case MotionEvent.ACTION_MOVE: //当手势类型为移动时

                Log.e(tag, "downX" + downX);
                Log.e(tag, "moveX" + event.getX());
                //记录最后的触摸位置
                float endX = event.getRawX();
                float endY = event.getRawY();
                //计算移动的距离
                float moveX = endX - downX;
                float moveY = endY - downY;
                //计算偏移量 设置偏移量 = 3 时 为判断点击事件和滑动事件的峰值

                if (Math.abs(moveX) > 3 || Math.abs(moveY) > 3) { // 偏移量的绝对值大于 3 为 滑动时间 并根据偏移量计算四点移动后的位置
                    l = (int) (getX() + moveX);
                    r = l + layoutWidth;
                    t = (int) (getY() + moveY);
                    b = t + layoutHeight;
                    //不划出边界判断,最大值为边界值
                    // 如果你的需求是可以划出边界 此时你要计算可以划出边界的偏移量 最大不能超过自身宽度或者是高度
                    // 如果超过自身的宽度和高度 view 划出边界后 就无法再拖动到界面内了 注意
                    if (l < 0) { // left 小于 0 就是滑出边界 赋值为 0 ; right 右边的坐标就是自身宽度 如果可以划出边界 left right top bottom 最小值的绝对值 不能大于自身的宽高
                        l = 0;
                        r = l + layoutWidth;
                    } else if (r > screenWidth) { // 判断 right 并赋值
                        r = screenWidth;
                        l = r - layoutWidth;
                    }
                    if (t < 0) { // top
                        t = 0;
                        b = t + layoutHeight;
                    } else if (b > screenHeight) { // bottom
                        b = screenHeight;
                        t = b - layoutHeight;
                    }
                    this.layout(l, t, r, b); // 重置view在layout 中位置
                    //更新位置
                    downX = endX;
                    downY = endY;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
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