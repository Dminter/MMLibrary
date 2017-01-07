package com.zncm.library.ui;

import android.os.Bundle;

import com.zncm.library.R;
import com.zncm.library.ft.LibFt;
import com.zncm.library.ft.PhotoFt;


public class PhotoAc extends BaseAc {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setNavigationIcon(null);
        getSupportActionBar().setTitle("");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new PhotoFt())
                .commit();
    }

    @Override
    protected int setCV() {
        return R.layout.activity_add;
    }
}
