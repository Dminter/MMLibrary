package com.zncm.library.data.ApiData;

import com.zncm.library.data.BaseData;

/**
 * Created by MX on 2014/8/21.
 */
public class Joke extends BaseData {

    public String title;
    public String img;
    public String text;
    public String ct;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }
}
