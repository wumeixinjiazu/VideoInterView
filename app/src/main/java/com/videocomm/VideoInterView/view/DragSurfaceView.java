package com.videocomm.VideoInterView.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.videocomm.VideoInterView.utils.DisplayUtil;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/26 0026]
 * @function[功能简介]
 **/
public class DragSurfaceView extends SurfaceView {
    int mLastX = 0;
    int mLastY = 0;
    int screenWidth = DisplayUtil.getScreenWidth();
    int screenHeight = DisplayUtil.getScreenHeight();
    private String tag = getClass().getSimpleName();
    private int viewWidth;
    private int viewHeigh;
    private float downX;
    private float downY;

    public DragSurfaceView(Context context) {
        super(context);
    }

    public DragSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(tag, "left" + left);
        Log.d(tag, "top" + top);
        Log.d(tag, "right" + right);
        Log.d(tag, "bottom" + bottom);
        Log.d(tag, "changed" + changed);
        Log.d(tag, "getScreenWidth" + DisplayUtil.getScreenWidth());
        Log.d(tag, "getScreenHeight" + DisplayUtil.getScreenHeight());
        viewWidth = right - left;
        viewHeigh = bottom - top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }


    /**
     * 让view能够随手自由拖动
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        float distanceParentX = getX();//按下的触摸点X坐标 view的最左边距离父类最左边的距离
        float distanceParentY = getY();//按下的触摸点Y坐标 view的最上边距离父类最上边的具体
        float distanceSelfLeft = event.getX();//按下的触摸点X坐标 距离本身View最左边的距离
        float distanceSelfTop = event.getY();//按下的触摸点Y坐标 具体本身View最上边的具体
        int x = (int) event.getRawX(); //触摸点相对于屏幕的横坐标
        int y = (int) event.getRawY(); //触摸点相对于屏幕的纵坐标
        Log.d(tag, "x1:" + distanceParentX);
        Log.d(tag, "y1:" + distanceParentY);
        Log.d(tag, "mLastX" + mLastX);
        Log.d(tag, "mLastY" + mLastY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();//屏幕的X坐标
                downY = event.getRawY();//屏幕的Y坐标
                break;

            case MotionEvent.ACTION_MOVE: //当手势类型为移动时
                float curX = event.getRawX();
                float curY = event.getRawY();

                float cx = curX - downX;
                float cy = curY - downY;
                Log.d(tag, "cx" + cx);
                //边界限制 x
                if (cx > 3) {
                    //右滑
                    if (getX() + viewWidth >= screenWidth) {
                        return true;
                    }
                } else {
                    //左滑
                    if (getX() <= 0) {
                        return true;
                    }
                }
                downX = curX;

                //边界限制 y
                if (cy > 3) {
                    //向下
                    if (getY() + viewHeigh >= screenHeight) {
                        return true;
                    }
                } else {
                    //向上
                    if (getY() <= 0) {
                        return true;
                    }
                }
                downY = curY;

                int deltaX = x - mLastX; //两次移动的x距离差
                int deltaY = y - mLastY;//两次移动的y的距离差

                //重新设置此view相对父容器的偏移量
                int translationX = (int) getTranslationX() + deltaX;
                int translationY = (int) getTranslationY() + deltaY;
                setTranslationX(translationX);
                setTranslationY(translationY);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        //记录上一次移动的坐标
        mLastX = x;
        mLastY = y;

        return true;
    }

}
