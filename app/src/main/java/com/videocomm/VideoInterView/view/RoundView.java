package com.videocomm.VideoInterView.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.videocomm.VideoInterView.utils.DisplayUtil;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/27 0027]
 * @function[功能简介]
 **/
public class RoundView extends View {

    private float cy;
    private float cx;
    private float radius;
    private Paint mBGPaint;
    private Paint mPathPaint;
    private Paint mFaceRoundPaint;
    private DashPathEffect mDashPathEffect;
    public static final int COLOR_BG = Color.parseColor("#2F2F33");
    public static final int COLOR_ROUND = Color.parseColor("#DB3333");

    public RoundView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDashPathEffect = new DashPathEffect(new float[]{DisplayUtil.dp2px(16), DisplayUtil.dp2px(16)}, 0);//虚线

        float pathWidth = DisplayUtil.dp2px(4);
        //禁用硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBGPaint.setColor(COLOR_BG);
        mBGPaint.setStyle(Paint.Style.FILL);
        mBGPaint.setAntiAlias(true);
        mBGPaint.setDither(true);

        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setColor(COLOR_ROUND);
        mPathPaint.setStrokeWidth(pathWidth);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setAntiAlias(true);
        mPathPaint.setDither(true);
        mPathPaint.setPathEffect(mDashPathEffect);

        mFaceRoundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFaceRoundPaint.setColor(COLOR_ROUND);
        mFaceRoundPaint.setStyle(Paint.Style.FILL);
        mFaceRoundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mFaceRoundPaint.setAntiAlias(true);
        mFaceRoundPaint.setDither(true);
    }

    public float getCy() {
        return cy;
    }

    public float getCx() {
        return cx;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
//        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawPaint(mBGPaint);

        cx = DisplayUtil.getScreenWidth() / 2.0f;
        cy = getHeight() / 3.0f;
        radius = DisplayUtil.getScreenWidth() / 2.5f;

        canvas.drawCircle(cx, cy, radius + 5, mPathPaint);//画虚线圆
        canvas.drawCircle(cx, cy, radius, mFaceRoundPaint);
    }


}

