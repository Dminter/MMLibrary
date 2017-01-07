package com.zncm.library.ui;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.malinskiy.materialicons.Iconify;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.XUtil;

import java.util.ArrayList;


public class HomeTabActivity extends TabActivity {
    String template_version;
    boolean bUpdate = false;

    Drawable norDrawable[] = new Drawable[]{
            XUtil.initIconUnSelTheme(Iconify.IconValue.md_apps),
            XUtil.initIconUnSelTheme(Iconify.IconValue.md_perm_identity),
            XUtil.initIconUnSelTheme(Iconify.IconValue.md_settings),
    };
    Drawable norDrawableSel[] = new Drawable[]{
            XUtil.initIconThemeSel(Iconify.IconValue.md_apps),
            XUtil.initIconThemeSel(Iconify.IconValue.md_perm_identity),
            XUtil.initIconThemeSel(Iconify.IconValue.md_settings),
    };

    ArrayList<ImageView> imgs = new ArrayList<>();
    Activity ctx;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_main);
        ctx = this;
        setTabs();


        for (int i = 0; i < norDrawable.length; i++) {
            setIcon(i, false);
        }
        setIcon(0, true);

        TabHost tabHost = getTabHost();
        try {
            //不使用记住，首次启动显示模板界面，以后都显示为我的库界面
            tabHost.setCurrentTab(1);
        } catch (Exception e) {

        }
        template_version = OnlineConfigAgent.getInstance().getConfigParams(this, "template_version");
        try {
            if (XUtil.notEmptyOrNull(template_version)) {
                Integer ver = Integer.parseInt(template_version);
                if (ver > MySp.getTemplateVersion()) {
                    if (ver % 10 == 0) {
                        Dbutils.deleteLocLibAll();
                    }
                    bUpdate = true;
                    MySp.setTemplateVersion(ver);
                }
            }
        } catch (Exception e) {

        }

        if (bUpdate) {
            XUtil.tShort("发现新模板，正在更新...");
            WebViewActivity.downloadFile(this, Constant.LOCLIB_NET, true);
        }


    }

    private void setIcon(int i, boolean bSel) {
        Drawable drawable = norDrawable[i];
        if (bSel) {
            drawable = norDrawableSel[i];
        }
        if (drawable != null) {
            imgs.get(i).setVisibility(View.VISIBLE);
            imgs.get(i).setImageDrawable(drawable);
        } else {
            imgs.get(i).setVisibility(View.GONE);
        }
    }

    private void setTabs() {
        addTab(0, "模板", TabLibActivity.class);
        addTab(1, "我的库", TabMyLibActivity.class);
        addTab(3, "设置", TabSettingAc.class);
    }

    private void addTab(int id, String labelId, Class<?> c) {
        final TabHost tabHost = getTabHost();
        Intent intent = new Intent(this, c);
        TabHost.TabSpec spec = tabHost.newTabSpec(String.valueOf(id));
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        if (XUtil.notEmptyOrNull(labelId)) {
            title.setVisibility(View.VISIBLE);
            title.setText(labelId);
        } else {
            title.setVisibility(View.GONE);
        }
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        imgs.add(icon);
        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);

        tabHost.getTabWidget().setDividerDrawable(null);


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                int id = Integer.parseInt(tabId);


                for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
                    if (id == i) {
                        setIcon(i, true);
                    } else {
                        setIcon(i, false);
                    }
                }

            }
        });

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            if (XUtil.notEmptyOrNull(MySp.getPwd())) {
                finish();
            } else {
                this.moveTaskToBack(true);
            }
            String today = XUtil.getDateY_M_D();
            String todayDate = MySp.getTodayDate();
            if (XUtil.notEmptyOrNull(todayDate)) {
                if (today.equals(todayDate)) {
                    if (XUtil.notEmptyOrNull(MySp.getPwd())) {
                        finish();
                    } else {
                        this.moveTaskToBack(true);
                    }
                } else {
                    MySp.setTodayDate(today);
                    finish();
                }
            } else {
                MySp.setTodayDate(today);
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        final Intent intent = getIntent();
        if (intent != null) {
            if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction())) {
                // 得到文件地址,并且转换编码,不然会出现乱码
                Uri uri = intent.getData();
                if (uri != null) {
                    XUtil.debug("uri: " + uri);
                }
                intent.setData(null);
                intent.setAction(null);
            } else if (Intent.ACTION_SEND.equalsIgnoreCase(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if (extras.containsKey("android.intent.extra.STREAM")) {
                    Uri uri = (Uri) extras.get("android.intent.extra.STREAM");
                    if (uri != null) {
                        XUtil.debug("uri-STREAM: " + uri);
                    }
                }
                intent.setData(null);
                intent.setAction(null);
            }
        }
    }
}