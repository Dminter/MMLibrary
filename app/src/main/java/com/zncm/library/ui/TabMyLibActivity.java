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

import com.afollestad.materialdialogs.MaterialDialog;
import com.astuetz.PagerSlidingTabStrip;
import com.malinskiy.materialicons.Iconify;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Lib;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ft.LibFt;
import com.zncm.library.ft.LocLibFt;
import com.zncm.library.utils.XUtil;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class TabMyLibActivity extends BaseAc {
    private LibFt libFt;
    private LibFt libSysFt;
    private LibFt libRssFt;
    private LibFt libNetFt;
    private LibFt libApiFt;
    private ViewPager mViewPager;
    private String TITLES[] = new String[]{ "RSS", "网络库", "API","系统库", "我的库",};
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
                case 4:
                    libFt = new LibFt();
                    fragment = libFt;
                    break;
                case 3:
                    libSysFt = new LibFt();
                    bundle = new Bundle();
                    bundle.putInt("libType", Lib.libType.sys.value());
                    libSysFt.setArguments(bundle);
                    fragment = libSysFt;
                    break;
                case 0:
                    libRssFt = new LibFt();
                    bundle = new Bundle();
                    bundle.putInt("libType", Lib.libType.rss.value());
                    libRssFt.setArguments(bundle);
                    fragment = libRssFt;
                    break;
                case 1:
                    libNetFt = new LibFt();
                    bundle = new Bundle();
                    bundle.putInt("libType", Lib.libType.net.value());
                    libNetFt.setArguments(bundle);
                    fragment = libNetFt;
                    break;

                case 2:
                    libApiFt = new LibFt();
                    bundle = new Bundle();
                    bundle.putInt("libType", Lib.libType.api.value());
                    libApiFt.setArguments(bundle);
                    fragment = libApiFt;
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
            libFt.onRefresh2();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("qrcode").setIcon(getResources().getDrawable(R.drawable.qrcode)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
        if (item.getTitle().equals("qrcode")) {
            Intent intent = new Intent(this, QRActivity.class);
            startActivityForResult(intent, 1111);
        }
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

        }

        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == 1111) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    qrSuccess(ctx, result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    qrFailed();
                }
            }
        }
    }

    public static void qrFailed() {
        XUtil.tShort("解析二维码失败");
    }

    public static void qrSuccess(final Context ctx, final String result) {
        if (XUtil.isEmptyOrNull(result)) {
            return;
        }
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
        ArrayList<String> strs = new ArrayList<>();
        if (XUtil.notEmptyOrNull(result)) {
            strs.add(result);
            strs.add(XUtil.getDateFullSec());
        }
        ShareAc.initSaveList(Constant.SYS_QR, Constant.SYS_QR_MK, strs, Lib.libType.sys.value());
        XUtil.tShort(result);

        if (result.startsWith("http") || result.startsWith("www")) {
            Intent intent = new Intent(ctx, WebViewActivity.class);
            intent.putExtra("url", result);
            ctx.startActivity(intent);
        } else if (result.contains("|||")) {


            new MaterialDialog.Builder(ctx)
                    .title("导入库")
                    .content("结构:" + result)
                    .positiveText("导入")
                    .negativeText("取消")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            String content = result.replaceAll("，", ",").replaceAll("：", ":");
                            String arr[] = content.split("\\|\\|\\|");
                            if (arr.length > 1) {
                                LocLibFt.mkLib(arr[0], arr[1]);
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            showInfo(ctx, result);
                        }
                    }).show();
        } else {
            showInfo(ctx, result);
        }
    }

    private static void showInfo(Context ctx, String result) {
        Intent newIntent = new Intent(ctx, ShowInfoActivity.class);
        newIntent.putExtra("show", result);
        ctx.startActivity(newIntent);
    }
}