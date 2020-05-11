package com.videocomm.VideoInterView.utils;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.videocomm.VideoInterView.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/24 0024]
 * @function[功能简介 声音播放工具类]
 **/
public class SoundUtils implements SoundPool.OnLoadCompleteListener {

    private SoundPool pool;
    private List<Object> listPool = new ArrayList<Object>();
    private boolean isOpen;//是否播放声音
    private boolean isLoad;//是否加载完成，加载完成才能播放声音
    private int streamId;

    public void initPool(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = null;
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            pool = new SoundPool.Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else { // 5.0 以前
            pool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
        }
        //这里添加自己的需要的音频文件
//        listPool.add(pool.load(context, R.raw.alive_mouse, 0));
//        listPool.add(pool.load(context, R.raw.alive_eye, 0));
//        listPool.add(pool.load(context, R.raw.alive_head, 0));
        pool.setOnLoadCompleteListener(this);
    }

    public SoundPool getPool() {
        return pool;
    }

    private static SoundUtils instance = null;

    private SoundUtils() {
    }

    public static SoundUtils getInstance() {
        synchronized (SoundUtils.class) {
            if (instance == null) {
                instance = new SoundUtils();
            }
        }

        return instance;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    /**
     * 播放对应的声音
     * <p>
     * 参数soundID：指定播放哪个声音； 
     * 参数leftVolume、rightVolume：指定左、右的音量： 
     * 参数priority：指定播放声音的优先级，数值越大，优先级越高； 
     * 参数loop：指定是否循环，0：不循环，-1：循环，其他值表示要重复播放的次数；
     * 参数rate：指定播放的比率，数值可从0.5到2， 1为正常比率。
     *
     * @param flag
     */
    public void playVoice(int flag) {
        if (isLoad) {
            if (null != pool) {
                int index = (Integer) listPool.get(flag);
                streamId = pool.play(index, 1, 1, 0, 0, 1);
            }
        } else {
            onLoadComplete(pool, 0, 0);
        }
    }

    /**
     * 释放资源
     */
    private void releaseSoundPool() {
        if (pool != null) {
            pool.autoPause();
            pool.release();
            pool = null;
        }
    }

    public void pause() {
        if (pool != null) {
            pool.pause(streamId);
        }
    }


    /**
     * 资源加载完成回调
     *
     * @param soundPool
     * @param sampleId
     * @param status
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        isLoad = true;
    }
}

