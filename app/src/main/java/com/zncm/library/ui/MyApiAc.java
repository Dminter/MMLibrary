package com.zncm.library.ui;

import android.os.Bundle;

import com.zncm.library.R;
import com.zncm.library.ft.ApiFt;


public class MyApiAc extends BaseAc {
    ApiFt apiFt = new ApiFt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackOn(false);
        toolbar.setNavigationIcon(null);
        getSupportActionBar().setTitle("发现");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, apiFt)
                .commit();
    }

    @Override
    protected int setCV() {
        return R.layout.activity_search;
    }


}
