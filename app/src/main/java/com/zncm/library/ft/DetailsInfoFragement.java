package com.zncm.library.ft;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zncm.library.R;
import com.zncm.library.utils.XUtil;


public class DetailsInfoFragement extends Fragment {
    private View view;
    private String info;
    private TextView textView;
    private Activity ctx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ctx = (Activity) getActivity();
        view = inflater.inflate(R.layout.fragment_showinfo, null);
        textView = (TextView) view.findViewById(R.id.textView);
        info = ctx.getIntent().getExtras().getString("show");
        if (XUtil.notEmptyOrNull(info)) {
            textView.setText(info);
        }
        textView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                return false;
            }
        });
        int navId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int navHeight = getResources().getDimensionPixelSize(navId);
        textView.setMinHeight(XUtil.getDeviceHeight() - navHeight);
        float textWidth = XUtil.getTextLength(textView.getTextSize(), textView.getText().toString());
        if (textWidth > (XUtil.getDeviceWidth() - XUtil.dip2px(24))) {
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        } else {
            textView.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        }
        textView.setMovementMethod(new LinkMovementMethod());
        ScrollView rlShow = (ScrollView) view.findViewById(R.id.rlShow);
        rlShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctx.finish();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctx.finish();
            }
        });
        return view;
    }


}
