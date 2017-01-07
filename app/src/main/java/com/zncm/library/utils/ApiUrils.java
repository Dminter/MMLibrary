package com.zncm.library.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.zncm.library.data.ApiData.Joke;
import com.zncm.library.data.ApiData.News;
import com.zncm.library.data.ApiData.NewsUrls;
import com.zncm.library.data.ApiData.WxHot;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ui.ShareAc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import de.greenrobot.event.EventBus;


public class ApiUrils {

    public static void getApiInfo(String lib) {
        int pageCount = MySp.getapi_page();
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
            getDataWx(queue);
        } else if (lib.equals(Constant.SYS_NEWS)) {
            getDataNews(queue);
        } else if (lib.equals(Constant.SYS_MEIVI)) {
            getDataMV(queue);
        } else if (lib.equals(Constant.SYS_XH)) {
            getDataXh(queue);
        } else if (lib.equals(Constant.SYS_XH_PIC)) {
            getDataXhPic(queue);
        } else if (lib.equals(Constant.SYS_BAIDU_BK)) {
            getDataBdbk(queue);
        }
    }

    private static void getDataWx(final Queue<Integer> myQueue) {
        try {
            int page = myQueue.poll();
            //XUtil.tShort(Constant.SYS_WXHOT + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(MyApplication.getInstance().ctx);
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
                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.ITEMS.getValue()));
                            int pageNo = MySp.getapi_wx() + MySp.getapi_page();
                            MySp.setapi_wx(pageNo);
                            XUtil.tShort(Constant.SYS_WXHOT + "-第" + pageNo + "页-" + XUtil.getDateFull());
                        } else {
                            getDataWx(myQueue);
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

    private static void getDataNews(final Queue<Integer> myQueue) {
        try {
            int page = myQueue.poll();
            //XUtil.tShort(Constant.SYS_NEWS + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(MyApplication.getInstance().ctx);
            String url = "http://apis.baidu.com/showapi_open_bus/channel_news/search_news?page=" + page;
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        XUtil.debug("ret=" + ret);
                        org.json.JSONObject obj = new org.json.JSONObject(ret);
                        String item = obj.getString("showapi_res_body");
                        item = new org.json.JSONObject(item).getString("pagebean");
                        item = new org.json.JSONObject(item).getString("contentlist");
                        XUtil.debug("item=" + item);
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
                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.ITEMS.getValue()));
                            int pageNo = MySp.getapi_news() + MySp.getapi_page();
                            MySp.setapi_news(pageNo);
                            XUtil.tShort(Constant.SYS_NEWS + "-" + XUtil.getDateFull());
                        } else {
                            getDataNews(myQueue);
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


    private static void getDataMV(final Queue<Integer> myQueue) {
        try {
            int page = myQueue.poll();
            //XUtil.tShort(Constant.SYS_MEIVI + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(MyApplication.getInstance().ctx);
            String url = "http://apis.baidu.com/txapi/mvtp/meinv?num=12";
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        for (int i = 0; i < 10; i++) {
                            org.json.JSONObject obj = new org.json.JSONObject(ret);
                            XUtil.debug("ret==>" + ret);
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
                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.ITEMS.getValue()));
                            int pageNo = MySp.getapi_mv() + MySp.getapi_page();
                            MySp.setapi_mv(pageNo);
                            XUtil.tShort(Constant.SYS_MEIVI + "-" + XUtil.getDateFull());
                        } else {
                            getDataMV(myQueue);
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


    private static void getDataXh(final Queue<Integer> myQueue) {
        try {
            int page = myQueue.poll();
            //XUtil.tShort(Constant.SYS_XH + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(MyApplication.getInstance().ctx);
            //joke_text
            String joke = "joke_text";
            String url = "http://apis.baidu.com/showapi_open_bus/showapi_joke/" + joke + "?page=" + page;
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        XUtil.debug("ret=" + ret);
                        org.json.JSONObject obj = new org.json.JSONObject(ret);
                        String item = obj.getString("showapi_res_body");
                        item = new org.json.JSONObject(item).getString("contentlist");
                        XUtil.debug("item=" + item);
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
                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.ITEMS.getValue()));
                            int pageNo = MySp.getapi_xh() + MySp.getapi_page();
                            MySp.setapi_xh(pageNo);
                            XUtil.tShort(Constant.SYS_XH + "-" + XUtil.getDateFull());
                        } else {
                            getDataXh(myQueue);
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

    private static void getDataXhPic(final Queue<Integer> myQueue) {
        try {
            int page = myQueue.poll();
            //XUtil.tShort(Constant.SYS_XH_PIC + "-正在下载，第" + page + "页");
            RequestQueue mVolleyQueue = Volley.newRequestQueue(MyApplication.getInstance().ctx);
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
                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.ITEMS.getValue()));
                            int pageNo = MySp.getapi_xhpic() + MySp.getapi_page();
                            MySp.setapi_xhpic(pageNo);
                            XUtil.tShort(Constant.SYS_XH_PIC + "-" + XUtil.getDateFull());
                        } else {
                            getDataXhPic(myQueue);
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

    private static void getDataBdbk(final Queue<Integer> myQueue) {
        try {

            final int page = myQueue.poll();
            //XUtil.tShort(Constant.SYS_BAIDU_BK + "-正在下载，第" + page + "页");
            Tasks.executeInBackground(MyApplication.getInstance().ctx, new BackgroundWork<Boolean>() {
                @Override
                public Boolean doInBackground() throws Exception {
                    try {
                        String url = "http://wapbaike.baidu.com/view/" + page + ".htm";
                        Document doc = Jsoup.connect(url).timeout(1000).get();
                        Elements elements = doc.getElementsByClass("summary-content");
                        String mConent = elements.text().toString();
                        String mTitle = doc.title().toString();
                        mTitle = mTitle.replaceAll("_百度百科", "");
                        mConent = mConent.replaceAll("\\[.*?]", "").replaceAll("百科名片 ", "");
                        XUtil.debug("mTitle:" + mTitle + "  mConent:" + mConent);
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
                        EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.ITEMS.getValue()));
                        int pageNo = MySp.getapi_bdbk() + MySp.getapi_page();
                        MySp.setapi_bdbk(pageNo);
                        XUtil.tShort(Constant.SYS_BAIDU_BK + "-" + XUtil.getDateFull());
                    } else {
                        getDataBdbk(myQueue);
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


}
