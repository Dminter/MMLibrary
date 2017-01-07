package com.zncm.library.ui;

import android.os.Bundle;

import com.zncm.library.R;
import com.zncm.library.ft.FieldsFt;
import com.zncm.library.ft.OptionsFt;


public class OptionsAc extends BaseAc {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new OptionsFt())
                .commit();
    }


    public void myTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected int setCV() {
        return R.layout.activity_add;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }


}
