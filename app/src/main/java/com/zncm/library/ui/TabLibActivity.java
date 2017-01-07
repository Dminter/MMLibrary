package com.zncm.library.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.zncm.library.R;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ft.LocLibFt;
import com.zncm.library.ft.TagFt;
import com.zncm.library.utils.XUtil;

import de.greenrobot.event.EventBus;


public class TabLibActivity extends BaseAc {
    private LocLibFt locLibFt;
    private TagFt tagFt;
    private ViewPager mViewPager;
    private String TITLES[] = new String[]{"模板", "分类"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setSwipeBackOn(false);
//        initMainToolBar(this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(TITLES.length);
        PagerSlidingTabStrip indicator = (PagerSlidingTabStrip) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        XUtil.initIndicatorTheme(indicator);
        myTitle("模板");
    }

    @Override
    protected int setCV() {
        return R.layout.activity_newmain;
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle bundle = null;
            switch (position) {
                case 0:
                    locLibFt = new LocLibFt();
                    fragment = locLibFt;
                    break;
                case 1:
                    tagFt = new TagFt();
                    fragment = tagFt;
                    break;
            }

            return fragment;
        }

    }


}