package com.zncm.library.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.zncm.library.R;
import com.zncm.library.data.MyApplication;
import com.zncm.library.ui.WebViewActivity;


public class URLSpanUtils extends URLSpan {

    public URLSpanUtils(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(MyApplication.getInstance().ctx.getResources().getColor(R.color.action_bar));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        try {
            if (widget.getTag() != null) {
                widget.setTag(null);
                return;
            }
            Context context = widget.getContext();
            String url = getURL();
            if (XUtil.notEmptyOrNull(url)) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("url", getURL());
                    context.startActivity(intent);
                } else {
                    super.onClick(widget);
                }
            } else {
                super.onClick(widget);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
