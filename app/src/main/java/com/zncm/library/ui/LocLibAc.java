package com.zncm.library.ui;

import android.os.Bundle;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ft.LocLibFt;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.XUtil;

import de.greenrobot.event.EventBus;


public class LocLibAc extends BaseAc {
    LocLibFt loclibFt = new LocLibFt();
    public String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getIntent().getStringExtra("key");
        getSupportActionBar().setTitle(key);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, loclibFt)
                .commit();
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setCV() {
        return R.layout.activity_search;
    }


    public void onEvent(RefreshEvent event) {
        int type = event.type;
        if (type == EnumData.RefreshEnum.LOCLIB.getValue()) {
            loclibFt.onRefresh2();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
