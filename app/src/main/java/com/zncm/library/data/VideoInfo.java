package com.zncm.library.data;

/**
 * Created by MX on 2014/8/21.
 */
public class VideoInfo extends BaseData {

    public String title;
    public String url;
    public Long time;


    public VideoInfo(String title, String url) {
        this.title = title;
        this.url = url;
        this.time = System.currentTimeMillis();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
