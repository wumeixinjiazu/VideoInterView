package com.videocomm.VideoInterView.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.videocomm.VideoInterView.R;

import java.lang.reflect.Field;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/10 0010]
 * @function[功能简介  带清除按钮的EditText]
 **/
public class ClearEditText extends EditText implements View.OnFocusChangeListener, TextWatcher {
    private String tag = getClass().getSimpleName();
    private Drawable mClearDrawable;

    public ClearEditText(Context context) {
        super(context);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Log.d(tag, "init" );
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片,getCompoundDrawables()获取Drawable的四个位置的数组
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.ic_text_clean);
        }
        //设置图标的位置以及大小,getIntrinsicWidth()获取显示出来的大小而不是原图片的大小
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //默认不可见
        setIconVisible(false);
        //设置焦点改变监听
        setOnFocusChangeListener(this);
        //设置输入框文字内容改变监听
        addTextChangedListener(this);

    }

    /**
     * 设置清除按钮的可见状态
     *
     * @param visibility
     */
    private void setIconVisible(boolean visibility) {
        Drawable right = visibility ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(tag, "getWidth" + getWidth());
        Log.d(tag, "getHeight" + getHeight());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        setIconVisible(hasFocus && this.getText().length() > 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        setIconVisible(0 != s.length()&&hasFocus());
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //getTotalPaddingRight()图标左边缘至控件右边缘的距离
        //getWidth() - getTotalPaddingRight()表示从最左边到图标左边缘的位置
        //getWidth() - getPaddingRight()表示最左边到图标右边缘的位置
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                Log.d(tag, "getTotalPaddingRight" + getTotalPaddingRight());
                Log.d(tag, "getPaddingRight" + getPaddingRight());
                if (touchable) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

}
