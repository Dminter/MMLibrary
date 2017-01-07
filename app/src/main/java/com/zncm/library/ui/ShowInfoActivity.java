package com.zncm.library.ui;

import android.os.Bundle;
import android.view.View;

import com.zncm.library.R;
import com.zncm.library.ft.ShowInfoFragement;


public class ShowInfoActivity extends BaseNoTitleAc {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new ShowInfoFragement())
                .commit();
    }

    @Override
    protected int setCV() {
        return R.layout.activity_ft_notitle;
    }


}
