package com.zncm.library.ui;

import android.os.Bundle;

import com.zncm.library.R;
import com.zncm.library.ft.FieldsAddFt;
import com.zncm.library.ft.ItemsAddFt;


public class FieldsAddAc extends BaseAc {
    private static FieldsAddAc instance;

    public static FieldsAddAc getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new FieldsAddFt())
                .commit();
    }

    @Override
    protected int setCV() {
        return R.layout.activity_add;
    }


}
