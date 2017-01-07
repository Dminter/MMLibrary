package com.zncm.library.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.zncm.library.R;

import java.util.HashMap;


public class SoundPoolUtil {
    static SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    @SuppressLint("UseSparseArrays")
    static HashMap<Integer, Integer> spMap = new HashMap<Integer, Integer>();

    public static void playSound(Context ctx) {
        try {
            spMap.put(1, soundPool.load(ctx, R.raw.right, 1));
            AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            final float volumnRatio = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    soundPool.play(spMap.get(1),
                            volumnRatio,         //左声道
                            volumnRatio,         //右声道
                            1,             //优先级，0最低
                            0,         //循环次数，0是不循环，-1是永远循环
                            1.5f);            //回放速度，0.5-2.0之间。1为正常速度
                }
            });
        } catch (Exception e) {

        }


    }

}
