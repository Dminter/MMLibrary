package com.zncm.library.ui;

import android.os.Bundle;

import com.zncm.library.R;
import com.zncm.library.ft.LikeFragment;


public class LikeActivity extends BaseAc {
    LikeFragment likeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle("收藏");
        likeFragment = new LikeFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, likeFragment)
                .commit();
    }

    @Override
    protected int setCV() {
        return R.layout.activity_add;
    }


}
