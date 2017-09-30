package com.zncm.library.ui;

import android.os.Bundle;
import android.view.MenuItem;

import com.zncm.library.R;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ft.LibFt;
import com.zncm.library.utils.XUtil;

import de.greenrobot.event.EventBus;


public class LibAc extends BaseAc {
    LibFt libFt = new LibFt();
    public String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        key = getIntent().getStringExtra("key");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, libFt)
                .commit();
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setCV() {
        return R.layout.activity_search;
    }


    public void onEvent(RefreshEvent event) {
        int type = event.type;
        if (type == EnumData.RefreshEnum.LIB.getValue()) {
            libFt.onRefresh2();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void toTop() {
        super.toTop();
        libFt.listToTop();
    }
}
