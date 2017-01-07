package com.zncm.library.data;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zncm.library.ui.ShareAc;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.XUtil;


public class MyApplication extends Application {
    public Context ctx;
    public static MyApplication instance;
    public static ImageLoader imageLoader;
    public static String[] tags;
    ClipboardManager cb;

    @Override
    public void onCreate() {
        super.onCreate();
        this.ctx = this.getApplicationContext();
        instance = this;
        initImageLoader();
        tags = new String[]{
                "聊天", "收藏", "影视", "动漫", "书籍", "实用", "商业",
                "知识库", "游戏", "小说", "天谕", "数码", "日常", "漫画",
                "物品", "金融", "资源", "电脑", "维修", "图册", "健康",
                "记录", "计划", "贴吧", "书画", "手绘", "食品", "程序员",
                "管理", "论坛", "资讯", "植物", "动物", "学校",
                "学生", "相册", "其它",
        };


        cb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cb.setPrimaryClip(ClipData.newPlainText("", ""));
        cb.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                // 具体实现
                String content = cb.getText().toString();
                if (MySp.getClipboardListen()) {
                    ShareAc.initSave(Constant.SYS_CLIPBOARD, content, "");
                }
            }
        });

    }

    public static MyApplication getInstance() {
        return instance;


    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());
        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }


}
