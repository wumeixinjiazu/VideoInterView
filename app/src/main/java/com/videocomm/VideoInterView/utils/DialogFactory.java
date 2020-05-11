package com.videocomm.VideoInterView.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.videocomm.VideoInterView.R;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/5 0005]
 * @function[功能简介]
 **/
public class DialogFactory {
    private static DialogFactory mDialogFactory = new DialogFactory();
    private static Dialog mDialog;
    private Activity mContext;
    private LayoutInflater mLayoutInlater;
    private TextView mTextViewTitle;

    public static int mCurrentDialogId = 0;
    public static final int DIALOGID_ENDCALL = 5;
    public static final int DIALOGID_EXIT_YEWU = 8;
    public static final int DIALOGID_EXIT_QUEUE = 9;

    public static Dialog getDialog(int dwDialogId,
                                   Activity context, View.OnClickListener listener) {
        mDialogFactory.initDialog(dwDialogId, context, listener);
        return mDialog;
    }

    public void initDialog(int dwDialogId, Activity context, View.OnClickListener listener) {
        if (mContext != context) {
            mContext = context;
            mLayoutInlater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        mCurrentDialogId = dwDialogId;
        mDialog = new Dialog(mContext, R.style.CommonDialog);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        switch (dwDialogId) {
            case DIALOGID_EXIT_YEWU:
                initQuitDialg(mDialog, "您确定要退出队列吗？", listener);
                break;
            case DIALOGID_EXIT_QUEUE:
                initQuitDialg(mDialog, "您确定要退出当前排队吗？", listener);
                break;
            case DIALOGID_ENDCALL:
                initQuitDialg(mDialog, "您确定要结束当前服务吗？", listener);
                break;
            default:
                break;
        }
    }

    public void initQuitDialg(final Dialog dialog, String content, View.OnClickListener onClickListener) {
        View view = mLayoutInlater
                .inflate(R.layout.dialog_resumeorcancel, null);
        TextView buttonQuit = (TextView) view.findViewById(R.id.btn_resume);
        TextView buttonCancel = (TextView) view.findViewById(R.id.btn_cancel);
        buttonQuit.setText("退出");
        buttonCancel.setText("取消");

        buttonQuit.setOnClickListener(onClickListener);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        String strTitle = content;
        initDialogTitle(view, strTitle);
        dialog.setContentView(view);
    }

    private void initDialogTitle(View view, final String strTitle) {
        mTextViewTitle = (TextView) view.findViewById(R.id.txt_dialog_prompt);
        mTextViewTitle.setTextColor(Color.BLACK);
        mTextViewTitle.setText(strTitle);
    }
}
