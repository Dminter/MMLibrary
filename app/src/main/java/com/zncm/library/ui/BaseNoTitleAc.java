package com.zncm.library.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.malinskiy.materialicons.Iconify;
import com.umeng.analytics.MobclickAgent;
import com.zncm.library.R;
import com.zncm.library.utils.XUtil;


public abstract class BaseNoTitleAc extends ActionBarActivity {

    public boolean swipeBackOn = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (swipeBackOn) {
            SwipeBackHelper.onCreate(this);
        }
        if (setCV() != -1) {
            setContentView(setCV());
        }
    }

    public void myTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    protected abstract int setCV();

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
