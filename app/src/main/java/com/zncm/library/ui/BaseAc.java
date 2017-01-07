package com.zncm.library.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.malinskiy.materialicons.Iconify;
import com.umeng.analytics.MobclickAgent;
import com.zncm.library.R;
import com.zncm.library.utils.DoubleClickImp;
import com.zncm.library.utils.XUtil;


public abstract class BaseAc extends AppCompatActivity {
    Toolbar toolbar;

    public boolean swipeBackOn = true;

    public static void initMainToolBar(BaseAc activity) {
        activity.toolbar.setNavigationIcon(null);
        activity.getSupportActionBar().setTitle(" ");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (swipeBackOn) {
            SwipeBackHelper.onCreate(this);
        }
        if (setCV() != -1) {
            setContentView(setCV());
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        if (toolbar != null) {
            DoubleClickImp.registerDoubleClickListener(toolbar,
                    new DoubleClickImp.OnDoubleClickListener() {
                        @Override
                        public void OnSingleClick(View v) {
                        }

                        @Override
                        public void OnDoubleClick(View v) {
                            toTop();
                        }
                    });
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(XUtil.initIconWhite(Iconify.IconValue.md_arrow_back));
        }
    }

    public void myTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    protected abstract int setCV();

    protected void toTop() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return false;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (swipeBackOn) {
            SwipeBackHelper.onPostCreate(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (swipeBackOn) {
            SwipeBackHelper.onDestroy(this);
        }
    }

    public boolean isSwipeBackOn() {
        return swipeBackOn;
    }

    public void setSwipeBackOn(boolean swipeBackOn) {
        this.swipeBackOn = swipeBackOn;
    }
}
