package com.zncm.library.ft;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.MyApplication;
import com.zncm.library.ui.LocLibAc;
import com.zncm.library.ui.WebViewActivity;
import com.zncm.library.utils.XUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.gujun.android.taggroup.TagGroup;


public class LinkFt extends BaseFt {
    private Activity ctx;
    View view;
    TagGroup mTagGroup;
    Map<String, String> mLinkMap;


    String tags[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (Activity) getActivity();
        view = inflater.inflate(R.layout.fragment_search_history, null);
        mTagGroup = (TagGroup) view.findViewById(R.id.tag_group);
        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String s) {
                if (s == null) {
                    return;
                }

                String url = mLinkMap.get(s);
                if (XUtil.isEmptyOrNull(url)) {
                    return;
                }
                Intent intent = new Intent(ctx, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            }
        });
        initLinks();
        return view;
    }

    public void initLinks() {


        mLinkMap = new LinkedHashMap<String, String>();


        mLinkMap.put("百度", "http://m.baidu.com/");
        mLinkMap.put("新浪", "http://sina.cn/");
        mLinkMap.put("腾讯", "http://info.3g.qq.com/");
        mLinkMap.put("网易", "http://3g.163.com/");
        mLinkMap.put("头条", "http://m.toutiao.com/");
        mLinkMap.put("好豆网", "http://m.haodou.com/");
        mLinkMap.put("酷安网", "http://www.coolapk.com/");


        mLinkMap.put("豆瓣电影", "http://movie.douban.com/");
        mLinkMap.put("小米应用商店", "http://m.app.mi.com  ");
        mLinkMap.put("小众软件", "http://www.appinn.com/");
        mLinkMap.put("新浪微博", "http://weibo.cn/");
        mLinkMap.put("互动百科", "http://hudong.cn/");
        mLinkMap.put("百度地图", "http://map.baidu.com/");
        mLinkMap.put("2345工具箱", "http://tools.2345.com/m/");
        mLinkMap.put("常用电话", "http://wap.hao123.com/n/v/dianhua");


        mLinkMap.put("天气", Constant.NET_BAIDU + "天气");
        mLinkMap.put("pm2.5", Constant.NET_BAIDU + "pm2.5");
        mLinkMap.put("日历", Constant.NET_BAIDU + "日历");
        mLinkMap.put("时间", Constant.NET_BAIDU + "时间");
        mLinkMap.put("最新电影", Constant.NET_BAIDU + "最新电影");
        mLinkMap.put("快递", Constant.NET_BAIDU + "快递");
        mLinkMap.put("翻译", Constant.NET_BAIDU + "翻译");
        mLinkMap.put("计算器", Constant.NET_BAIDU + "计算器");
        mLinkMap.put("单位换算", Constant.NET_BAIDU + "单位换算");


        try {
            ArrayList<String> list = new ArrayList(mLinkMap.keySet());

            if (XUtil.listNotNull(list)) {
                tags = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    tags[i] = list.get(i);
                }
                if (tags.length > 0) {
                    mTagGroup.setTags(tags);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
