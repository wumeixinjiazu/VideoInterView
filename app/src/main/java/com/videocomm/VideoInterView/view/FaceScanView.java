package com.videocomm.VideoInterView.view;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/9 0009]
 * @function[功能简介]
 **/

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.utils.DisplayUtil;

/**
 * @describe: 人脸框View
 * @className: FaceScanView
 */
public class FaceScanView extends View {
    private int BORDER_RECT_COLOR = 0xfffd0303;
    private int BACKGROUND_COLOR = 0xFFAFAFAF;
    private int SCAN_COLOR = 0xffffff00;
    private int scanBg;
    private int borderRectColor;
    private int backgroundColor;
    private int scanColor;
    private float defaultBorderStrokeWidth;
    private Paint borderPaint;
    private Paint backgroundPaint;
    private Paint borderRectPaint;
    private Paint scanPaint;
    private Rect scanRect;
    private int leftWith;
    private int rightWith;
    private int topHeight;
    private int bottomHeight;
    private int width;
    private int height;
    int mDisplayWidth;
    int mDisplayHeight;
    private static final int SPEEN_DISTANCE = 10;
    private int slideTop;
    private static final long ANIMATION_DELAY = 10L;
    private Paint paint;

    public FaceScanView(Context context) {
        this(context, null);
    }

    public FaceScanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(dm);
        mDisplayWidth = dm.widthPixels;
        mDisplayHeight = dm.heightPixels;
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.FaceScanView);
        borderRectColor = t.getColor(R.styleable.FaceScanView_border_rect_color, BORDER_RECT_COLOR);
        backgroundColor = t.getColor(R.styleable.FaceScanView_background_color, BACKGROUND_COLOR);
        scanColor = t.getColor(R.styleable.FaceScanView_scan_color, SCAN_COLOR);
        defaultBorderStrokeWidth = t.getFloat(R.styleable.FaceScanView_borderStrokeWidth, 10f);
        scanBg = t.getResourceId(R.styleable.FaceScanView_scan_bg, 0);
        t.recycle();
        initPaint();
    }

    private void initPaint() {
        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(defaultBorderStrokeWidth);
        borderPaint.setAntiAlias(true);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        borderRectPaint = new Paint();
        borderRectPaint.setColor(borderRectColor);
        borderRectPaint.setStyle(Paint.Style.FILL);
        borderRectPaint.setAntiAlias(true);

        scanPaint = new Paint();
        scanPaint.setColor(scanColor);
        scanPaint.setStyle(Paint.Style.FILL);

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void drawScanRect(Canvas canvas) {
        scanRect = new Rect();
        width = canvas.getWidth();
        height = canvas.getHeight();
        leftWith = (int) (width * 0.1);
        rightWith = width - leftWith;
        topHeight = (int) (height * 0.1);
        bottomHeight = rightWith - leftWith + topHeight;
        scanRect.set(leftWith, topHeight, rightWith, bottomHeight);
    }

    public Rect getScanRect() {
        return scanRect;
    }

    private void drawBackground(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        canvas.drawRect(0.0F, 0.0F, (float) width, (float) scanRect.top, this.backgroundPaint);
        canvas.drawRect(0.0F, (float) scanRect.top, (float) scanRect.left, (float) (scanRect.bottom + 1), this.backgroundPaint);
        canvas.drawRect((float) (scanRect.right + 1), (float) scanRect.top, (float) width, (float) (scanRect.bottom + 1), this.backgroundPaint);
        canvas.drawRect(0.0F, (float) (scanRect.bottom + 1), (float) width, (float) height, this.backgroundPaint);
    }

    private void drawScanRectBg(Canvas canvas) {
        Bitmap bitmap = null;
        if (scanBg == 0) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ico_scan_bg);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), scanBg);
        }
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, src, scanRect, borderPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScanRect(canvas);
        drawBackground(canvas);
        drawScanRectBg(canvas);

        //画扫描线
        slideTop += SPEEN_DISTANCE;
        if (slideTop >= bottomHeight) {
            slideTop = 0;
        }
        if (slideTop <= topHeight) {
            slideTop = topHeight;
        }
        Rect lineRect = new Rect();
        lineRect.left = leftWith;
        lineRect.right = rightWith;
        lineRect.top = slideTop;
        lineRect.bottom = slideTop + 18;
        canvas.drawBitmap(((BitmapDrawable) (getResources()
                        .getDrawable(R.drawable.fle))).getBitmap(), null, lineRect,
                paint);
        postInvalidateDelayed(ANIMATION_DELAY, 0, 0,
                getWidth(), getHeight());
    }

    /**
     * 设置人脸识别框背景
     *
     * @param scanBg 资源文件 ：R.drawable.xxx
     */
    public void setScanBg(int scanBg) {
        this.scanBg = scanBg;
    }
}
