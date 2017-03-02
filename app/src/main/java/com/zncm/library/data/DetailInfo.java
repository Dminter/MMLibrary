package com.zncm.library.data;

/**
 * Created by MX on 2014/8/21.
 */
public class DetailInfo extends BaseData {

    public String title;
    public String url;
    public String key;
    public Long time;


    public DetailInfo(String url, String key) {
        this.url = url;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
