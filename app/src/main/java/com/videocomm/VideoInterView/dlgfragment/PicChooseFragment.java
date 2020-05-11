package com.videocomm.VideoInterView.dlgfragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.videocomm.VideoInterView.Constant;
import com.videocomm.VideoInterView.R;
import com.videocomm.VideoInterView.activity.CameraCaptureActivity;
import com.videocomm.VideoInterView.dlgfragment.base.BaseDlgFragment;
import com.videocomm.VideoInterView.utils.PermissionUtil;
import com.videocomm.VideoInterView.utils.ToastUtil;

import java.io.File;

import static com.videocomm.VideoInterView.Constant.CAMERA_PERMISSION_CODE;
import static com.videocomm.VideoInterView.Constant.IDCARD_IMAGE_TYPE;
import static com.videocomm.VideoInterView.Constant.PHOTO_PERMISSION_CODE;
import static com.videocomm.VideoInterView.Constant.PHOTO_REQUEST_CODE;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/10 0010]
 * @function[功能简介]
 **/
public class PicChooseFragment extends BaseDlgFragment implements View.OnClickListener {

    //用来判断照相机拍照是正面还是背面
    private boolean isFront;

    public static PicChooseFragment newInstance(boolean isFront) {
        PicChooseFragment fragment = new PicChooseFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFront", isFront);
        fragment.setArguments(bundle);
        return fragment;
    }

    public PicChooseFragment() {

    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_pic_choose;
    }

    @Override
    protected void initView(View view) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            isFront = arguments.getBoolean("isFront");
        }
        view.findViewById(R.id.tv_camera_capture).setOnClickListener(this);
        view.findViewById(R.id.tv_photo_capture).setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_camera_capture:
                openCamera();
                dismiss();
                break;
            case R.id.tv_photo_capture:
                openLocalImage(getActivity());
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 打开本地相册
     *
     * @param activity
     */
    private void openLocalImage(final Activity activity) {
        Intent intent;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_PICK);
        }
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, PHOTO_REQUEST_CODE);
    }

    /**
     * 打开照相机
     */
    private void openCamera() {
        Intent intent = new Intent(getActivity(), CameraCaptureActivity.class);
        intent.putExtra(IDCARD_IMAGE_TYPE, this.isFront);
        getActivity().startActivityForResult(intent, Constant.OPEN_CAMERA);
    }

}
