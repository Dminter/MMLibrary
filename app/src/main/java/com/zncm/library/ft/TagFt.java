package com.zncm.library.ft;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zncm.library.R;
import com.zncm.library.data.MyApplication;
import com.zncm.library.ui.LocLibAc;


import me.gujun.android.taggroup.TagGroup;


public class TagFt extends BaseFt {
    private Activity ctx;
    private EditText editText;
    View view;
    TagGroup mTagGroup;

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
                Intent intent = new Intent(ctx, LocLibAc.class);
                intent.putExtra("key", s);
                startActivity(intent);
            }
        });
        getData();
        return view;
    }

    private void getData() {
        mTagGroup.setTags(MyApplication.tags);
    }


}
