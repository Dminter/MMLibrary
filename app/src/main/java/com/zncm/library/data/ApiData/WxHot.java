package com.zncm.library.data.ApiData;

import com.zncm.library.data.BaseData;

/**
 * Created by MX on 2014/8/21.
 */
public class WxHot extends BaseData {

    public String hottime;
    public String title;
    public String description;
    public String picUrl;
    public String url;


    public String getHottime() {
        return hottime;
    }

    public void setHottime(String hottime) {
        this.hottime = hottime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
