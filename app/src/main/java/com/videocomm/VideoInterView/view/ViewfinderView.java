package com.videocomm.VideoInterView.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/16 0016]
 * @function[功能简介 画面扫描线]
 **/
public class ViewfinderView extends View {

    private Paint paint;
    private static final int SPEEN_DISTANCE = 5;
    private boolean isFirst;
    private int slideTop;
    private static float density;
    private int ScreenRate;//线的长度
    private static final int CORNER_WIDTH = 10;//线的宽度
    private static final long ANIMATION_DELAY = 10L;

    public ViewfinderView(Context context) {
        super(context);
        init(context);
    }

    public ViewfinderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ViewfinderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        density = context.getResources().getDisplayMetrics().density;
        ScreenRate = (int) (30 * density);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isFirst) {
            isFirst = true;
            slideTop = 0;
        }
        paint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, 0 + ScreenRate,
                0 + CORNER_WIDTH, paint);
        canvas.drawRect(0, 0, 0 + CORNER_WIDTH,
                0 + ScreenRate, paint);
        canvas.drawRect(getWidth() - ScreenRate, 0, getWidth(),
                0 + CORNER_WIDTH, paint);
        canvas.drawRect(getWidth() - CORNER_WIDTH, 0, getWidth(),
                0 + ScreenRate, paint);
        canvas.drawRect(0, getHeight() - CORNER_WIDTH, 0
                + ScreenRate, getHeight(), paint);
        canvas.drawRect(0, getHeight() - ScreenRate, 0
                + CORNER_WIDTH, getHeight(), paint);
        canvas.drawRect(getWidth() - ScreenRate, getHeight()
                - CORNER_WIDTH, getWidth(), getHeight(), paint);
        canvas.drawRect(getWidth() - CORNER_WIDTH, getHeight()
                - ScreenRate, getWidth(), getHeight(), paint);


        //画扫描线
        slideTop += SPEEN_DISTANCE;
        if (slideTop >= getHeight()) {
            slideTop = 0;
        }
        Rect lineRect = new Rect();
        lineRect.left = 0;
        lineRect.right = getWidth();
        lineRect.top = slideTop;
        lineRect.bottom = slideTop + 18;
        canvas.drawBitmap(((BitmapDrawable) (getResources()
                        .getDrawable(R.drawable.fle))).getBitmap(), null, lineRect,
                paint);
        postInvalidateDelayed(ANIMATION_DELAY, 0, 0,
                getWidth(), getHeight());
    }
}
