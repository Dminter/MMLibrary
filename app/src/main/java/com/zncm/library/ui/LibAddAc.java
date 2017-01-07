package com.zncm.library.ui;

import android.os.Bundle;

import com.zncm.library.R;
import com.zncm.library.ft.FieldsFt;


public class LibAddAc extends BaseAc {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new FieldsFt())
                .commit();
    }


    public void  myTitle(String title){
        getSupportActionBar().setTitle(title);
    }
    @Override
    protected int setCV() {
        return R.layout.activity_add;
    }

}
