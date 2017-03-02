package com.zncm.library.ui;


import android.os.Bundle;
import android.view.View;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.Lib;
import com.zncm.library.data.VideoInfo;
import com.zncm.library.utils.XUtil;

import java.io.Serializable;

public class PlayActivity extends BaseAc {

    StandardGSYVideoPlayer gsyVideoPlayer;

    VideoInfo videoInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        gsyVideoPlayer = (StandardGSYVideoPlayer) findViewById(R.id.videoPlayer);


        Serializable dataParam = getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA);
        if (dataParam != null && dataParam instanceof VideoInfo) {
            videoInfo = (VideoInfo) dataParam;
        }

//url
        final String url = videoInfo.getUrl();

        XUtil.debug("url===>>" + url);

//设置播放url，第一个url，第二个开始缓存，第三个使用默认缓存路径，第四个设置title
        gsyVideoPlayer.setUp(url, true, null, "这是title");


//非全屏下，不显示title
        gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);

//非全屏下不显示返回键
        gsyVideoPlayer.getBackButton().setVisibility(View.GONE);


//立即播放
        gsyVideoPlayer.startPlayLogic();


    }

    @Override
    protected int setCV() {
        return R.layout.activity_play;
    }


}