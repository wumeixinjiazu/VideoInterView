package com.videocomm.VideoInterView.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.utils.DisplayUtil;

import java.util.ArrayList;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/16 0016]
 * @function[功能简介 多节点的数字圆]
 **/
public class NumberCircleProgress extends View {

    private Paint mPaint;
    private Paint mTextPaint;

    private int circleNum = 3;//小球的个数
    private int selectIndex = -1;//选中的号码
    private float radius;//小球的半径
    private int lineWidth;//圆的线宽度
    private ArrayList<String> numList;
    private ArrayList<Rect> rectNumList;
    private Bitmap bitmap;//绘制的图片

    public NumberCircleProgress(Context context) {
        super(context);
        init();
    }

    public NumberCircleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberCircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        radius = DisplayUtil.dp2px(20);
        lineWidth = DisplayUtil.dp2px(2);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);

        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.RED);
        mTextPaint.setTextSize(DisplayUtil.dp2px(20));

        //测量文字大小
        numList = new ArrayList<>();
        rectNumList = new ArrayList<>();
        numList.add("1");
        numList.add("2");
        numList.add("3");
        for (String text : numList) {
            Rect rect = new Rect();
            mTextPaint.getTextBounds(text, 0, text.length(), rect);
            rectNumList.add(rect);
        }

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_yes);
    }

    /**
     * 设置选中位置
     *
     * @param selectIndex
     */
    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        invalidate();
    }

    /**
     * 设置小球的个数
     *
     * @param num
     */
    public void setCircleNum(int num) {
        circleNum = num;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //真实的半径 圆的半径加线的宽度
        float realRadius = radius + lineWidth;
        //每个球之间的距离
        float averageCircle = (getWidth() - (realRadius * 2 * (circleNum))) / (circleNum - 1);

        //绘制节点圆 和 圆内文字
        for (int i = 0; i < circleNum; i++) {
            canvas.drawCircle(realRadius + (i * averageCircle) + (i * realRadius * 2), realRadius, radius, mPaint);
            if (i <= selectIndex) {
                canvas.drawBitmap(bitmap, realRadius + (i * averageCircle) + (i * realRadius * 2) - bitmap.getWidth() / 2, realRadius - bitmap.getHeight() / 2, mTextPaint);
            } else {
                canvas.drawText(numList.get(i), realRadius + (i * averageCircle) + (i * realRadius * 2), realRadius + rectNumList.get(i).height() / 2, mTextPaint);
            }
        }
    }
}
