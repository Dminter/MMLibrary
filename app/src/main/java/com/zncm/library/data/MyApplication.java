package com.zncm.library.data;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

import com.idescout.sql.SqlScoutServer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zncm.library.ui.ShareAc;
import com.zncm.library.utils.MyPath;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.XUtil;

import java.io.File;


public class MyApplication extends Application {
    public Context ctx;
    public static MyApplication instance;
    public static ImageLoader imageLoader;
    public static String[] tags;
    ClipboardManager cb;
    private String myLastClipboard = "";

    @Override
    public void onCreate() {
        super.onCreate();
        this.ctx = this.getApplicationContext();
        instance = this;
        ZXingLibrary.initDisplayOpinion(this);
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
                    if (XUtil.isEmptyOrNull(myLastClipboard) || !myLastClipboard.equals(content)) {
                        myLastClipboard = content;
                        ShareAc.initSave(Constant.SYS_CLIPBOARD, content, "");
                    }
                }
            }
        });
        SqlScoutServer.create(this, getPackageName());
    }

    public static MyApplication getInstance() {
        return instance;


    }

    private void initImageLoader() {

        String cachePath = MyPath.getPathCache();
        File cachFile = null;
        if (XUtil.notEmptyOrNull(cachePath)) {
            cachFile = new File(cachePath);
        }
        int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 3);
        MemoryCache memoryCache;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            memoryCache =
                    new com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache(
                            memoryCacheSize);
        } else {
            memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
        }
        int maxMemory = ((int) Runtime.getRuntime().maxMemory()) / 1024 / 1024;
//        ImageLoaderConfiguration.Builder build =
//                new ImageLoaderConfiguration.Builder(ctx).memoryCacheExtraOptions(900, 1680)
//                        .threadPoolSize(6).threadPriority(Thread.NORM_PRIORITY - 2)
//                        .denyCacheImageMultipleSizesInMemory().memoryCache(memoryCache)
//                        .memoryCacheSize((maxMemory / 3) * 1024 * 1024)
//                        .diskCacheSize(500 * 1024 * 1024)
//                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                        .tasksProcessingOrder(QueueProcessingType.LIFO).diskCacheFileCount(5000)
//                        .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
//                        .memoryCache(new WeakMemoryCache());
//        if (cachFile != null) {
//            build.diskCache(new UnlimitedDiskCache(cachFile));
//        }


        ImageLoaderConfiguration.Builder build = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .memoryCacheExtraOptions(960, 1600) //即保存的每个缓存文件的最大长宽
                .threadPoolSize(6) //线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                //解释：当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
                .memoryCache(new WeakMemoryCache()) //缓存策略你可以通过自己的内存缓存实现 ，这里用弱引用，缺点是太容易被回收了，不是很好！
                .memoryCacheSize((maxMemory / 3) * 1024 * 1024) //设置内存缓存的大小
                .diskCacheSize(500 * 1024 * 1024) //设置磁盘缓存大小 500M
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO) //设置图片下载和显示的工作队列排序
                .diskCacheFileCount(1000) //缓存的文件数量
                .diskCache(new UnlimitedDiskCache(cachFile)) //自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) //显示图片的参数，默认：DisplayImageOptions.createSimple()
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                .writeDebugLogs(); // connectTimeout (5 s), readTimeout (30 s)超时时间

//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
//                .cacheOnDisk(true).cacheInMemory(true)
//                .bitmapConfig(Bitmap.Config.RGB_565).build();
//        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
//                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
//                new WeakMemoryCache());
        ImageLoaderConfiguration config = build.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }






}
