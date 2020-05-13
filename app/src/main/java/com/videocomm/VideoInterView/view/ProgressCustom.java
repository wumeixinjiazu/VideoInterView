package com.videocomm.VideoInterView.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 **/
public class ProgressCustom extends View {

    private String tag = getClass().getSimpleName();

    /**
     * 背景画笔
     */
    private Paint bgPaint;
    /**
     * 前景画笔
     */
    private Paint forePaint;
    /**
     * 选中画笔
     */
    private Paint selectPaint;
    /**
     * 未选中画笔
     */
    private Paint unselectPaint;
    /**
     * 描边画笔
     */
    private Paint strokePaint;

    /**
     * 文本选中和未选中画笔
     */
    private Paint textSelectPaint;
    private Paint textUnSelectPaint;

    /**
     * 文本画笔 用来写圆点中间的数字
     */
    private Paint textPaint;
    /**
     * 线的背景颜色 默认浅粉 #E7BFC1
     */
    private int lineColor = Color.parseColor("#E7BFC1");
    /**
     * 选中的颜色 默认白色 #ffffff
     */
    private int selectColor = Color.parseColor("#ffffff");
    /**
     * 未选中的颜色 默认红色 #AF292C
     */
    private int unSelectColor = Color.parseColor("#E2233E");
    /**
     * 背景颜色
     */
    private int bgColor = Color.parseColor("#E7BFC1");
    /**
     * 前景颜色
     */
    private int foreColor = Color.parseColor("#8A2BE2");
    /**
     * 默认高度
     */
    private int defaultHeight;
    /**
     * 节点文字
     */
    private List<String> nodeList;
    private List<String> nodeInList;
    private List<Rect> mBounds;
    private List<Rect> mInBounds;
    /**
     * 节点圆的半径
     */
    private int radius;
    /**
     * 文字和节点进度条的top
     */
    private int marginTop;
    /**
     * 两个节点之间的距离
     */
    private int dividWidth;
    /**
     * 选中位置
     */
    private int selectIndex;
    /**
     * 圆点外围的宽度
     */
    private int strokeWidth;
    private Bitmap selectBitmap;
    private Bitmap unselectBitmap;

    public ProgressCustom(Context context) {
        this(context, null);
    }

    public ProgressCustom(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCustom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Log.d(tag, "init");
        radius = DisplayUtil.dp2px(8);
        defaultHeight = DisplayUtil.dp2px(30);
        marginTop = DisplayUtil.dp2px(10);
        strokeWidth = +DisplayUtil.dp2px(2);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);

        forePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        forePaint.setColor(foreColor);
        forePaint.setStyle(Paint.Style.FILL);

        unselectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unselectPaint.setColor(unSelectColor);
        unselectPaint.setTextSize(DisplayUtil.sp2px(10));

        selectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectPaint.setColor(selectColor);
        selectPaint.setTextSize(DisplayUtil.sp2px(10));

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.GRAY);
        strokePaint.setStrokeWidth(strokeWidth);

        textSelectPaint = new Paint();
        textSelectPaint.setColor(selectColor);
        textSelectPaint.setTextSize(DisplayUtil.sp2px(10));

        textUnSelectPaint = new Paint();
        textUnSelectPaint.setColor(bgColor);
        textUnSelectPaint.setTextSize(DisplayUtil.sp2px(10));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(unSelectColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(DisplayUtil.sp2px(15));

//        selectBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_select_node);
//        unselectBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_unselect_node);
    }

    /**
     * 设置数据
     *
     * @param nodeDatas
     */
    public void setNodeList(List<String> nodeDatas) {
        if (nodeDatas != null) {
            nodeList = nodeDatas;
        }
        //测量文字所占用的空间
        measureText();
    }

    /**
     * 设置节点里面的数据
     *
     * @param nodeDatas
     */
    public void setNodeInList(List<String> nodeDatas) {
        if (nodeDatas != null) {
            nodeInList = nodeDatas;
        }

        //测量文字所占用的空间
        measureTextIn();
    }

    /**
     * 设置选中位置
     *
     * @param selectIndex
     */
    public void setSelectIndex(int selectIndex) {
        if (selectIndex > nodeList.size()) {
            return;
        }
        this.selectIndex = selectIndex;
        invalidate();
    }

    /**
     * 测量文字的长宽
     */
    private void measureText() {
        Log.d(tag, "measureText");
        mBounds = new ArrayList<>();
        for (int i = 0; i < nodeList.size(); i++) {
            Rect mBound = new Rect();
            unselectPaint.getTextBounds(nodeList.get(i), 0, nodeList.get(i).length(), mBound);
            mBounds.add(mBound);
        }
    }

    /**
     * 测量节点里面的文字长宽
     */
    private void measureTextIn() {
        Log.d(tag, "measureTextIn");
        mInBounds = new ArrayList<>();
        for (int i = 0; i < nodeInList.size(); i++) {
            Rect mBound = new Rect();
            unselectPaint.getTextBounds(nodeInList.get(i), 0, nodeInList.get(i).length(), mBound);
            mInBounds.add(mBound);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(tag, "onDraw" + getWidth());
        Log.d(tag, "onDraw" + getHeight());
        if (nodeList == null || nodeList.isEmpty()) {
            return;
        }
        bgPaint.setStrokeWidth(DisplayUtil.dp2px(2));

        //绘制灰色的背景线条
        canvas.drawLine(radius, radius + strokeWidth, getWidth() - radius, radius + strokeWidth, bgPaint);

        //画节点圆
        //每个圆相隔的距离  = （当前控件的宽度 - 半径 *2 - （圆点的描边宽度 * 数量） /  节点个数）
        dividWidth = (getWidth() - radius * 2 - (strokeWidth * nodeInList.size())) / (nodeList.size() - 1);
        forePaint.setStrokeWidth(radius / 2);
        for (int i = 0; i < nodeList.size(); i++) {
            if (i == selectIndex) {
                for (int j = 0; j <= i; j++) {
                    //花节点圆
                    canvas.drawCircle(radius + j * dividWidth + DisplayUtil.dp2px(2), radius + strokeWidth, radius + DisplayUtil.dp2px(1), selectPaint);
                    //描边
                    canvas.drawCircle(radius + j * dividWidth + DisplayUtil.dp2px(2), radius + strokeWidth, radius + DisplayUtil.dp2px(1), strokePaint);
                    //写入圆点中间文字
                    canvas.drawText(nodeInList.get(j), radius + j * dividWidth + DisplayUtil.dp2px(2), radius + mInBounds.get(j).height() / 2 + strokeWidth + 2, textPaint);
                }
            } else if (i > selectIndex) {
                canvas.drawCircle(radius + i * dividWidth + DisplayUtil.dp2px(2), radius + strokeWidth, radius + DisplayUtil.dp2px(1), unselectPaint);
                canvas.drawCircle(radius + i * dividWidth + DisplayUtil.dp2px(2), radius + strokeWidth, radius + DisplayUtil.dp2px(1), strokePaint);
            }
        }

        //绘制节点上的文字
        for (int i = 0; i < nodeList.size(); i++) {
            int currentTextWidth = mBounds.get(i).width();
            //绘制第一个文本
            if (i == 0) {
                if (i == selectIndex) {
                    canvas.drawText(nodeList.get(i), 0, radius * 2 + marginTop + mBounds.get(i).height() / 2 + strokeWidth, textSelectPaint);
                } else if (i > selectIndex) {
                    canvas.drawText(nodeList.get(i), 0, radius * 2 + marginTop + mBounds.get(i).height() / 2 + strokeWidth, textUnSelectPaint);
                }
                //绘制最后一个文本
            } else if (i == nodeList.size() - 1) {
                if (i == selectIndex) {
                    for (int j = 0; j <= i; j++) {
                        if (j == 0) {
                            canvas.drawText(nodeList.get(j), 0, radius * 2 + marginTop + mBounds.get(j).height() / 2 + strokeWidth, textSelectPaint);
                        } else if (j == i) {
                            canvas.drawText(nodeList.get(j), getWidth() - currentTextWidth, radius * 2 + marginTop + mBounds.get(j).height() / 2 + strokeWidth, textSelectPaint);
                        } else {
                            canvas.drawText(nodeList.get(j), radius + j * dividWidth - currentTextWidth / 2, radius * 2 + marginTop + mBounds.get(j).height() / 2 + strokeWidth, textSelectPaint);
                        }
                    }
                } else if (i > selectIndex) {
                    canvas.drawText(nodeList.get(i), getWidth() - currentTextWidth, radius * 2 + marginTop + mBounds.get(i).height() / 2 + strokeWidth, textUnSelectPaint);
                }
            } else {
                //绘制除了第一个和最后一个的文本
                if (i == selectIndex) {
                    //绘制在 i （包括i）位置后面的选中文本
                    for (int j = 0; j <= i; j++) {
                        if (j > 0) {
                            canvas.drawText(nodeList.get(j), radius + j * dividWidth - currentTextWidth / 2, radius * 2 + marginTop + mBounds.get(j).height() / 2 + strokeWidth, textSelectPaint);
                        } else {
                            canvas.drawText(nodeList.get(j), 0, radius * 2 + marginTop + mBounds.get(j).height() / 2 + strokeWidth, textSelectPaint);
                        }
                    }
                } else if (i > selectIndex) {
                    canvas.drawText(nodeList.get(i), radius + i * dividWidth - currentTextWidth / 2, radius * 2 + marginTop + mBounds.get(i).height() / 2 + strokeWidth, textUnSelectPaint);
                }
            }
        }
    }
}
