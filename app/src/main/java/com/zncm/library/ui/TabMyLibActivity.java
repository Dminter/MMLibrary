package com.zncm.library.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.astuetz.PagerSlidingTabStrip;
import com.malinskiy.materialicons.Iconify;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.zncm.library.R;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Lib;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ft.LibFt;
import com.zncm.library.utils.XUtil;

import de.greenrobot.event.EventBus;


public class TabMyLibActivity extends BaseAc {
    private LibFt libFt;
    private LibFt libSysFt;
    private LibFt libRssFt;
    private LibFt libNetFt;
    private ViewPager mViewPager;
    private String TITLES[] = new String[]{"我的库", "系统库", "RSS", "网络库"};
    MaterialSearchView searchView;

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackOn(false);
        initMainToolBar(this);

        toolbar.setTitle(getResources().getString(R.string.app_name));
        ctx = this;
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(TITLES.length);
        PagerSlidingTabStrip indicator = (PagerSlidingTabStrip) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        XUtil.initIndicatorTheme(indicator);
        EventBus.getDefault().register(this);


        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setHint("搜索");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(ctx, LibAc.class);
                intent.putExtra("key", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });


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
                    libFt = new LibFt();
                    fragment = libFt;
                    break;
                case 1:
                    libSysFt = new LibFt();
                    bundle = new Bundle();
                    bundle.putInt("libType", Lib.libType.sys.value());
                    libSysFt.setArguments(bundle);
                    fragment = libSysFt;
                    break;
                case 2:
                    libRssFt = new LibFt();
                    bundle = new Bundle();
                    bundle.putInt("libType", Lib.libType.rss.value());
                    libRssFt.setArguments(bundle);
                    fragment = libRssFt;
                    break;
                case 3:
                    libNetFt = new LibFt();
                    bundle = new Bundle();
                    bundle.putInt("libType", Lib.libType.net.value());
                    libNetFt.setArguments(bundle);
                    fragment = libNetFt;
                    break;
            }

            return fragment;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(RefreshEvent event) {
        int type = event.type;
        if (type == EnumData.RefreshEnum.LIB.getValue()) {
            XUtil.debug("Refresh LIb...");
            libFt.onRefresh();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lib_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        SubMenu sub = menu.addSubMenu("");
        sub.setIcon(XUtil.initIconWhite(Iconify.IconValue.md_more_vert));
        sub.add(0, 1, 0, "设置");
        sub.add(0, 2, 0, "模板");
        sub.add(0, 3, 0, "浏览器");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
////                bottomBar.selectTabAtPosition(0);
//                int tabId = bottomBar.getCurrentTabId();
//                tabSelected(tabId);
//                initTitle();
//                break;
//
//        }

//        if (AlipayZeroSdk.hasInstalledAlipayClient(ctx)) {
//            AlipayZeroSdk.startAlipayClient(this, "aex02461t5uptlcygocfsbc");
//        } else {
//            XUtil.tShort("请先安装支付宝~");
//        }
//        if (item.getTitle().equals("md_search")) {
//            searchView.showSearch(true);
//        }

        if (item == null || item.getTitle() == null) {
            return false;
        }
        switch (item.getItemId()) {
            case 1:
                startActivity(new Intent(ctx, SettingActivity.class));
                break;
            case 2:
                startActivity(new Intent(ctx, TabLibActivity.class));
                break;
            case 3:
                Intent intent = new Intent(ctx, WebViewActivity.class);
                intent.putExtra("url", "https://m.baidu.com/");
                startActivity(intent);
                break;
            case 4:

                break;

            case 5:

                break;
            case 6:
                break;
        }

        return false;
    }


}