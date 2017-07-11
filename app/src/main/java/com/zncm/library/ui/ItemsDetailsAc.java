package com.zncm.library.ui;

import android.content.Intent;
import android.os.Bundle;

import com.zncm.library.R;
import com.zncm.library.data.BaseData;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.ObjEvent;
import com.zncm.library.ft.ItemsDetailsFt;
import com.zncm.library.utils.XUtil;

import java.io.Serializable;

import de.greenrobot.event.EventBus;


public class ItemsDetailsAc extends BaseAc {

    BaseData obj;
    ItemsDetailsFt ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackOn(false);
        ft = new ItemsDetailsFt();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, ft)
                .commit();
        EventBus.getDefault().register(this);
    }


    @Override
    protected int setCV() {
        return R.layout.activity_add;
    }

    public void onEvent(ObjEvent event) {
        int type = event.type;
        obj = event.obj;
        if (type == EnumData.RefreshEnum.ITEMS.getValue()) {
            Items items = (Items) obj;
            if (items != null) {
                ft.onRefresh(items);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
