package com.zncm.library.ui;

import android.os.Bundle;
import android.view.MenuItem;

import com.zncm.library.R;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ft.ApiFt;
import com.zncm.library.utils.XUtil;

import de.greenrobot.event.EventBus;


public class ApiAc extends BaseAc {
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
