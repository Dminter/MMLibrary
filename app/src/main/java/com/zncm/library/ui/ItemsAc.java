package com.zncm.library.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.zncm.library.R;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ft.ItemsFt;
import com.zncm.library.utils.XUtil;

import de.greenrobot.event.EventBus;


public class ItemsAc extends BaseAc {
    ItemsFt itemsFt = new ItemsFt();
    public String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getIntent().getStringExtra("key");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, itemsFt)
                .commit();
        EventBus.getDefault().register(this);
        initPreData();
    }

    private void initPreData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
                Bundle extras = intent.getExtras();
                String content = String.valueOf(extras.getString(Intent.EXTRA_TEXT));
                XUtil.debug("share: " + content);
            }
        }
    }


    @Override
    protected int setCV() {
        return R.layout.activity_search;
    }


    public void onEvent(RefreshEvent event) {
        int type = event.type;
        if (type == EnumData.RefreshEnum.ITEMS.getValue()) {
            itemsFt.onRefresh();
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
        itemsFt.listToTop();
    }
}
