package com.videocomm.VideoInterView.dlgfragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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
import com.yalantis.ucrop.UCrop;

import java.io.File;

import static com.videocomm.VideoInterView.Constant.CAMERA_PERMISSION_CODR;
import static com.videocomm.VideoInterView.Constant.IDCARD_IMAGE_TYPE;
import static com.videocomm.VideoInterView.Constant.PHOTO_PERMISSION_CODR;
import static com.videocomm.VideoInterView.Constant.PHOTO_REQUEST_CODE;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/10 0010]
 * @function[功能简介]
 **/
public class PicChooseFragment extends BaseDlgFragment implements View.OnClickListener {
    /**
     * app需要用到的动态添加权限 6.0以上才会去申请
     */
    private String[] cameraPermission = new String[]{Manifest.permission.CAMERA};
    private String[] photoPermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Uri destinationUri;//保存裁剪后的destination URI
    private int requestCode;

    public PicChooseFragment(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_pic_choose;
    }

    @Override
    protected void initView(View view) {
        view.findViewById(R.id.tv_camera_capture).setOnClickListener(this);
        view.findViewById(R.id.tv_photo_capture).setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_camera_capture:
                if (PermissionUtil.checkPermission(this, cameraPermission, CAMERA_PERMISSION_CODR)) {
                    //成功 调用相机
                    openCamera();
                }
                break;
            case R.id.tv_photo_capture:
                if (PermissionUtil.checkPermission(this, photoPermission, PHOTO_PERMISSION_CODR)) {
                    //成功 调用相册
                    openSystemPhoto();
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(tag, "requestCode" + requestCode);

        switch (requestCode) {
            case CAMERA_PERMISSION_CODR:
                if (permissions.length == cameraPermission.length && grantResults[0] == 0) {
                    //成功 调用相机
                    openCamera();
                } else {
                    //判断用户拒绝权限是是否勾选don't ask again选项，若勾选需要客户手动打开权限
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), cameraPermission[0])) {
                        showWaringDialog();
                    }
                }
                break;

            case PHOTO_PERMISSION_CODR:
                Log.d(tag, "PHOTO_PERMISSION_CODR");
                if (permissions.length == photoPermission.length && grantResults[0] == 0) {
                    //成功 调用相册
                    openSystemPhoto();
                } else {
                    //某个权限拒绝
                    for (String s : photoPermission) {
                        //判断用户拒绝权限是是否勾选don't ask again选项，若勾选需要客户手动打开权限
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), s)) {
                            showWaringDialog();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(tag, "requestCode" + requestCode);
        switch (requestCode) {

            case PHOTO_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (null != data.getData()) {
                        dismiss();
                        //裁剪后保存到文件中
                        destinationUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "croppedImage"+this.requestCode+".jpg"));
                        //图片裁剪
                        UCrop.of(data.getData(), destinationUri)
                                .withAspectRatio(16, 9)
                                .withMaxResultSize(400, 300)
                                .start((AppCompatActivity) getActivity(),this.requestCode + Constant.OPEN_PHOTO);
                    } else {
                        ToastUtil.show("图片损坏，请重新选择");
                    }
                }
                break;
        }
    }

    /**
     * 提示用户自行打开权限
     */
    private void showWaringDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("警告！")
                .setMessage("请前往设置->应用->VideoTalk->权限中打开相关权限，否则功能无法正常运行！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).show();
    }

    /**
     * 获取系统图片
     */
    private void openSystemPhoto() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    PHOTO_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PHOTO_REQUEST_CODE);
        }
    }

    /**
     * 打开照相机
     */
    private void openCamera(){
        Intent intent = new Intent(getActivity(), CameraCaptureActivity.class);
        intent.putExtra(IDCARD_IMAGE_TYPE,this.requestCode);
        getActivity().startActivityForResult(intent, requestCode + Constant.OPEN_CAMERA);
        dismiss();
    }


}
