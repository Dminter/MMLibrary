package com.zncm.library.ft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.zncm.library.R;
import com.zncm.library.data.ApiData.Feed;
import com.zncm.library.data.ApiData.Joke;
import com.zncm.library.data.ApiData.News;
import com.zncm.library.data.ApiData.NewsUrls;
import com.zncm.library.data.Constant;
import com.zncm.library.data.ApiData.WxHot;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Lib;
import com.zncm.library.data.LocLib;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ui.ItemsAc;
import com.zncm.library.ui.LibAddAc;
import com.zncm.library.ui.ShareAc;
import com.zncm.library.ui.WebViewActivity;
import com.zncm.library.utils.DbHelper;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.MyStringRequest;
import com.zncm.library.utils.XUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import de.greenrobot.event.EventBus;


public class ApiFt extends BaseFt {
    private Activity ctx;
    View view;
    int pageCount = MySp.getapi_page();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (Activity) getActivity();
        view = inflater.inflate(R.layout.activity_api, null);

        final Button btnWx = (Button) view.findViewById(R.id.btnWx);

        btnWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg(btnWx, Constant.SYS_WXHOT);
            }
        });
        final Button btnNews = (Button) view.findViewById(R.id.btnNews);

        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg(btnNews, Constant.SYS_NEWS);
            }
        });
        final Button btnMv = (Button) view.findViewById(R.id.btnMv);
        btnMv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg(btnMv, Constant.SYS_MEIVI);
            }
        });
        final Button btnXh = (Button) view.findViewById(R.id.btnXh);
        btnXh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg(btnXh, Constant.SYS_XH);
            }
        });
        final Button btnXhpic = (Button) view.findViewById(R.id.btnXhpic);
        btnXhpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg(btnXhpic, Constant.SYS_XH_PIC);
            }
        });
        final Button btnBaidubk = (Button) view.findViewById(R.id.btnBaidubk);
        btnBaidubk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg(btnBaidubk, Constant.SYS_BAIDU_BK);
            }
        });

        final Button btnSys = (Button) view.findViewById(R.id.btnSys);
        btnSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = new String[]{Constant.SYS_SHARE, Constant.SYS_CLIPBOARD, Constant.SYS_FILE_DOWNLOAD, Constant.SYS_COLLECT, Constant.SYS_NET_HISTORY, Constant.SYS_PICS};
                new MaterialDialog.Builder(ctx)
                        .items(items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Lib data = Dbutils.findSysLib(items[which], 0);
                                if (data == null) {
                                    XUtil.tShort("暂无数据~~");
                                    return;
                                }
                                Intent intent = new Intent(ctx, ItemsAc.class);
                                intent.putExtra(Constant.KEY_PARAM_DATA, data);
                                startActivity(intent);
                            }
                        })
                        .show();


            }
        });
        final Button btnNet = (Button) view.findViewById(R.id.btnNet);
        btnNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String items[] = new String[]{"天气", "pm2.5", "日历", "时间", "最新电影", "快递", "翻译", "计算器", "单位换算"};
                new MaterialDialog.Builder(ctx)
                        .items(items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                String url = Constant.NET_BAIDU + items[which];
                                if (XUtil.isEmptyOrNull(url)) {
                                    return;
                                }
                                Intent intent = new Intent(ctx, WebViewActivity.class);
                                intent.putExtra("url", url);
                                startActivity(intent);
                            }
                        })
                        .show();


            }
        });


        return view;
    }

    private void dlg(final Button button, final String lib) {
        new MaterialDialog.Builder(ctx)
                .items(new String[]{"打开", "下载", "重置页码"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                Lib data = Dbutils.findSysLib(lib, 0);
                                if (data == null) {
                                    return;
                                }
                                Intent intent = new Intent(ctx, ItemsAc.class);
                                intent.putExtra(Constant.KEY_PARAM_DATA, data);
                                startActivity(intent);
                                break;
                            case 1:
                                Queue<Integer> queue = new ArrayDeque<>();
                                int api_page = 0;
                                if (lib.equals(Constant.SYS_WXHOT)) {
                                    api_page = MySp.getapi_wx();
                                } else if (lib.equals(Constant.SYS_NEWS)) {
                                    api_page = MySp.getapi_news();
                                } else if (lib.equals(Constant.SYS_MEIVI)) {
                                    api_page = MySp.getapi_mv();
                                } else if (lib.equals(Constant.SYS_XH)) {
                                    api_page = MySp.getapi_xh();
                                } else if (lib.equals(Constant.SYS_XH_PIC)) {
                                    api_page = MySp.getapi_xhpic();
                                } else if (lib.equals(Constant.SYS_BAIDU_BK)) {
                                    api_page = MySp.getapi_bdbk();
                                }
                                for (int i = api_page; i < pageCount + api_page; i++) {
                                    queue.add(i);
                                }
                                if (lib.equals(Constant.SYS_WXHOT)) {
                                    getDataWx(queue, button);
                                } else if (lib.equals(Constant.SYS_NEWS)) {
                                    getDataNews(queue, button);
                                } else if (lib.equals(Constant.SYS_MEIVI)) {
                                    getDataMV(queue, button);
                                } else if (lib.equals(Constant.SYS_XH)) {
                                    getDataXh(queue, button);
                                } else if (lib.equals(Constant.SYS_XH_PIC)) {
                                    getDataXhPic(queue, button);
                                } else if (lib.equals(Constant.SYS_BAIDU_BK)) {
                                    getDataBdbk(queue, button);
                                }
                                break;
                            case 2:
                                if (lib.equals(Constant.SYS_WXHOT)) {
                                    MySp.setapi_wx(1);
                                } else if (lib.equals(Constant.SYS_NEWS)) {
                                    MySp.setapi_news(1);
                                } else if (lib.equals(Constant.SYS_MEIVI)) {
                                    MySp.setapi_mv(1);
                                } else if (lib.equals(Constant.SYS_XH)) {
                                    MySp.setapi_xh(1);
                                } else if (lib.equals(Constant.SYS_XH_PIC)) {
                                    MySp.setapi_xhpic(1);
                                } else if (lib.equals(Constant.SYS_BAIDU_BK)) {
                                    MySp.setapi_bdbk(1);
                                }
                                button.setText(lib);
                                break;
                        }
                    }
                })
                .show();
    }

    private void getDataWx(final Queue<Integer> myQueue, final Button btn) {
        try {
            int page = myQueue.poll();
            btn.setText(Constant.SYS_WXHOT + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(ctx);
            String url = "http://apis.baidu.com/txapi/weixin/wxhot?rand=1&num=10&page=" + page;
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        for (int i = 0; i < 10; i++) {
                            org.json.JSONObject obj = new org.json.JSONObject(ret);
                            String item = obj.getString(i + "");
                            WxHot tmp = JSON.parseObject(item, WxHot.class);
                            ArrayList<String> strs = new ArrayList<>();
                            if (tmp != null) {
                                strs.add(tmp.getTitle());
                                strs.add(tmp.getDescription());
                                strs.add(tmp.getPicUrl());
                                strs.add(tmp.getUrl());
                                strs.add(tmp.getHottime());
                                ShareAc.initSaveList(Constant.SYS_WXHOT, Constant.SYS_WXHOT_MK, strs);
                            }
                        }
                        if (myQueue.isEmpty()) {
                            int pageNo = MySp.getapi_wx() + pageCount;
                            MySp.setapi_wx(pageNo);
                            btn.setText(Constant.SYS_WXHOT + "-第" + pageNo + "页-" + XUtil.getDateFull());
                        } else {
                            getDataWx(myQueue, btn);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            myStringRequest.getHeaders().put("apikey", "1e08b47bc5fc83bccc9b6bfb3b4cf1df");
            mVolleyQueue.add(myStringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataNews(final Queue<Integer> myQueue, final Button btn) {
        try {
            int page = myQueue.poll();
            btn.setText(Constant.SYS_NEWS + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(ctx);
            String url = "http://apis.baidu.com/showapi_open_bus/channel_news/search_news?page=" + page;
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        org.json.JSONObject obj = new org.json.JSONObject(ret);
                        String item = obj.getString("showapi_res_body");
                        item = new org.json.JSONObject(item).getString("pagebean");
                        item = new org.json.JSONObject(item).getString("contentlist");
                        ArrayList<News> list = (ArrayList<News>) JSON.parseArray(item, News.class);
                        for (News tmp : list
                                ) {
                            ArrayList<String> strs = new ArrayList<>();
                            if (tmp != null) {
                                strs.add(tmp.getTitle());
                                strs.add(tmp.getDesc());
                                strs.add(tmp.getSource());
                                String picUrl = "";
                                try {
                                    ArrayList<NewsUrls> urls = (ArrayList<NewsUrls>) JSON.parseArray(tmp.getImageurls(), NewsUrls.class);
                                    if (XUtil.listNotNull(urls)) {
                                        picUrl = urls.get(0).url;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                strs.add(picUrl);
                                strs.add(tmp.getLink());
                                strs.add(tmp.getPubDate());
                                ShareAc.initSaveList(Constant.SYS_NEWS, Constant.SYS_NEWS_MK, strs);
                            }
                        }
                        if (myQueue.isEmpty()) {
                            int pageNo = MySp.getapi_news() + pageCount;
                            MySp.setapi_news(pageNo);
                            btn.setText(Constant.SYS_NEWS + "-" + XUtil.getDateFull());
                        } else {
                            getDataNews(myQueue, btn);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            myStringRequest.getHeaders().put("apikey", "1e08b47bc5fc83bccc9b6bfb3b4cf1df");
            mVolleyQueue.add(myStringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getDataMV(final Queue<Integer> myQueue, final Button btn) {
        try {
            int page = myQueue.poll();
            btn.setText(Constant.SYS_MEIVI + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(ctx);
            String url = "http://apis.baidu.com/txapi/mvtp/meinv?num=12";
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        for (int i = 0; i < 10; i++) {
                            org.json.JSONObject obj = new org.json.JSONObject(ret);
                            String item = obj.getString(i + "");
                            WxHot tmp = JSON.parseObject(item, WxHot.class);
                            ArrayList<String> strs = new ArrayList<>();
                            if (tmp != null) {
                                strs.add(tmp.getTitle());
                                strs.add(tmp.getDescription());
                                strs.add(tmp.getPicUrl());
                                strs.add(tmp.getUrl());
                                strs.add(XUtil.getDateFull());
                                ShareAc.initSaveList(Constant.SYS_MEIVI, Constant.SYS_MEIVI_MK, strs);
                            }
                        }
                        if (myQueue.isEmpty()) {
                            int pageNo = MySp.getapi_mv() + pageCount;
                            MySp.setapi_mv(pageNo);
                            btn.setText(Constant.SYS_MEIVI + "-" + XUtil.getDateFull());
                        } else {
                            getDataMV(myQueue, btn);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            myStringRequest.getHeaders().put("apikey", "1e08b47bc5fc83bccc9b6bfb3b4cf1df");
            mVolleyQueue.add(myStringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getDataXh(final Queue<Integer> myQueue, final Button btn) {
        try {
            int page = myQueue.poll();
            btn.setText(Constant.SYS_XH + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(ctx);
            //joke_text
            String joke = "joke_text";
            String url = "http://apis.baidu.com/showapi_open_bus/showapi_joke/" + joke + "?page=" + page;
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        org.json.JSONObject obj = new org.json.JSONObject(ret);
                        String item = obj.getString("showapi_res_body");
                        item = new org.json.JSONObject(item).getString("contentlist");
                        ArrayList<Joke> list = (ArrayList<Joke>) JSON.parseArray(item, Joke.class);
                        for (Joke tmp : list
                                ) {
                            ArrayList<String> strs = new ArrayList<>();
                            if (tmp != null) {
                                strs.add(tmp.getTitle());
                                strs.add(tmp.getText());
                                ShareAc.initSaveList(Constant.SYS_XH, Constant.SYS_XH_MK, strs);
                            }
                        }
                        if (myQueue.isEmpty()) {
                            int pageNo = MySp.getapi_xh() + pageCount;
                            MySp.setapi_xh(pageNo);
                            btn.setText(Constant.SYS_XH + "-" + XUtil.getDateFull());
                        } else {
                            getDataXh(myQueue, btn);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            myStringRequest.getHeaders().put("apikey", "1e08b47bc5fc83bccc9b6bfb3b4cf1df");
            mVolleyQueue.add(myStringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataXhPic(final Queue<Integer> myQueue, final Button btn) {
        try {
            int page = myQueue.poll();
            btn.setText(Constant.SYS_XH_PIC + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(ctx);
            String joke = "joke_pic";
            String url = "http://apis.baidu.com/showapi_open_bus/showapi_joke/" + joke + "?page=" + page;
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        org.json.JSONObject obj = new org.json.JSONObject(ret);
                        String item = obj.getString("showapi_res_body");
                        item = new org.json.JSONObject(item).getString("contentlist");
                        ArrayList<Joke> list = (ArrayList<Joke>) JSON.parseArray(item, Joke.class);
                        for (Joke tmp : list
                                ) {
                            ArrayList<String> strs = new ArrayList<>();
                            if (tmp != null) {
                                strs.add(tmp.getTitle());
                                strs.add(tmp.getImg());
                                ShareAc.initSaveList(Constant.SYS_XH_PIC, Constant.SYS_XH_PIC_MK, strs);
                            }
                        }
                        if (myQueue.isEmpty()) {
                            int pageNo = MySp.getapi_xhpic() + pageCount;
                            MySp.setapi_xhpic(pageNo);
                            btn.setText(Constant.SYS_XH_PIC + "-" + XUtil.getDateFull());
                        } else {
                            getDataXhPic(myQueue, btn);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            myStringRequest.getHeaders().put("apikey", "1e08b47bc5fc83bccc9b6bfb3b4cf1df");
            mVolleyQueue.add(myStringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataBdbk(final Queue<Integer> myQueue, final Button btn) {
        try {

            final int page = myQueue.poll();
            btn.setText(Constant.SYS_BAIDU_BK + "-正在下载，第" + page + "页");
            Tasks.executeInBackground(ctx, new BackgroundWork<Boolean>() {
                @Override
                public Boolean doInBackground() throws Exception {
                    try {
                        String url = "http://wapbaike.baidu.com/view/" + page + ".htm";
                        Document doc = Jsoup.connect(url).timeout(1000).get();
                        Elements elements = doc.getElementsByClass("card");
                        String mConent = elements.text().toString();
                        String mTitle = doc.title().toString();
                        mTitle = mTitle.replaceAll(" - 百度百科", "");
                        mConent = mConent.replaceAll("\\[.*?]", "").replaceAll("百科名片 ", "");
                        ArrayList<String> strs = new ArrayList<>();
                        if (XUtil.notEmptyOrNull(mConent)) {
                            strs.add(mTitle);
                            strs.add(mConent);
                            strs.add(url);
                        }
                        ShareAc.initSaveList(Constant.SYS_BAIDU_BK, Constant.SYS_BAIDU_BK_MK, strs);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }, new Completion<Boolean>() {
                @Override
                public void onSuccess(Context context, Boolean result) {
                    if (myQueue.isEmpty()) {
                        int pageNo = MySp.getapi_bdbk() + pageCount;
                        MySp.setapi_bdbk(pageNo);
                        ArrayList<String> list = new ArrayList<>();
                        list = new ArrayList<>();
                        list.add(XUtil.getDateFullSec());
                        ShareAc.saveDataList(Constant.SYS_BAIDU_BK, list);
                        btn.setText(Constant.SYS_BAIDU_BK + "-" + XUtil.getDateFull());
                    } else {
                        getDataBdbk(myQueue, btn);
                    }
                }

                @Override
                public void onError(Context context, Exception e) {
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getFeedsByQuery(final Context ctx, final String query) {
        try {
            RequestQueue mVolleyQueue = Volley.newRequestQueue(ctx);
            String url = "https://cloud.feedly.com/v3/search/feeds?query=" + query;
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        org.json.JSONObject obj = new org.json.JSONObject(ret);
                        String item = obj.getString("results");
//                        item = new org.json.JSONObject(item).getString("contentlist");
                        final ArrayList<Feed> list = (ArrayList<Feed>) JSON.parseArray(item, Feed.class);

                        final ArrayList<String> items = new ArrayList<>();
                        for (Feed tmp : list
                                ) {
                            items.add(tmp.getTitle());
                        }
                        new MaterialDialog.Builder(ctx)
                                .items(items)
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        String content = list.get(which).getFeedId();
                                        if (XUtil.notEmptyOrNull(content)) {
                                            if (content.startsWith("feed/")) {
                                                content = content.replaceFirst("feed/", "");
                                            }
                                            ShareAc.initLibRss(items.get(which), content);
                                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LIB.getValue()));
                                            XUtil.tShort("已添加" + items.get(which));
                                        }
                                    }
                                })
                                .autoDismiss(false)
                                .show();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            myStringRequest.getHeaders().put("apikey", "1e08b47bc5fc83bccc9b6bfb3b4cf1df");
            mVolleyQueue.add(myStringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
