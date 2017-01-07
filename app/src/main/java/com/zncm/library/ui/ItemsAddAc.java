package com.zncm.library.ui;

import android.os.Bundle;

import com.zncm.library.R;
import com.zncm.library.data.Fields;
import com.zncm.library.ft.FieldsFt;
import com.zncm.library.ft.ItemsAddFt;

import java.util.List;


public class ItemsAddAc extends BaseAc {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("添加数据");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new ItemsAddFt())
                .commit();
    }

    @Override
    protected int setCV() {
        return R.layout.activity_add;
    }


}
