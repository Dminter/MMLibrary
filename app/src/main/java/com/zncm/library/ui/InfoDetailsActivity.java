package com.zncm.library.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.malinskiy.materialicons.Iconify;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.DetailInfo;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.ft.InfoDetailsFragment;
import com.zncm.library.utils.XUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;


public class InfoDetailsActivity extends BaseAc {
    InfoDetailsFragment likeFragment;
    public DetailInfo info;
    private Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        toolbar.setTitle("收藏");
        ctx = this;
        likeFragment = new InfoDetailsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, likeFragment)
                .commit();
    }

    @Override
    protected int setCV() {
        return R.layout.activity_add;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item == null || item.getTitle() == null) {
            return false;
        }

        String url  =info.getUrl();

        switch (item.getItemId()) {
            case 1:
                XUtil.sendTo(ctx, url);
                break;
            case 2:
                ShareAc.initSave(Constant.SYS_COLLECT, url, info.getTitle());
                XUtil.tShort("已收藏网址");
                break;
            case 3:
                XUtil.copyText(ctx, info.getUrl());
                XUtil.tShort("已复制");
                break;
            case 4:
                if (XUtil.notEmptyOrNull(url) && !url.startsWith("file:///")) {
                    if (item.getTitle().equals("在浏览器中打开")) {
                        try {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(i);
                        } catch (Exception e) {

                        }
                    }
                }
                break;
            case 5:
                likeFragment.getData(true);
                break;
            case 6:
                ArrayList<String> strs = new ArrayList<>();
                if (XUtil.notEmptyOrNull(url)) {
                    strs.add(info.getTitle());
                    strs.add(url);
                }
                ShareAc.initSaveList(Constant.SYS_SC, Constant.SYS_SC_MK, strs, Lib.libType.sys.value());
                XUtil.tShort("已添加收藏");
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void geUrlHtml(final String url) {
        try {

            //XUtil.tShort(Constant.SYS_BAIDU_BK + "-正在下载，第" + page + "页");
            Tasks.executeInBackground(MyApplication.getInstance().ctx, new BackgroundWork<String>() {
                @Override
                public String doInBackground() throws Exception {
                    String htmlStr = "";
                    try {
                        if (XUtil.isEmptyOrNull(url)) {
                            return htmlStr;
                        }
                        Document doc = Jsoup.connect(url).timeout(3000).get();
                        htmlStr = doc.html();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return htmlStr;
                }
            }, new Completion<String>() {
                @Override
                public void onSuccess(Context context, String result) {
                    if (XUtil.notEmptyOrNull(result)) {
                        Intent newIntent = new Intent(context, ShowInfoActivity.class);
                        newIntent.putExtra("show", result);
                        context.startActivity(newIntent);
                    }
                }

                @Override
                public void onError(Context context, Exception e) {
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu sub = menu.addSubMenu("");
        sub.setIcon(XUtil.initIconWhite(Iconify.IconValue.md_more_vert));
        sub.add(0, 1, 0, "分享");
        sub.add(0, 2, 0, "收藏网页");
        sub.add(0, 3, 0, "复制链接");
        sub.add(0, 4, 0, "在浏览器中打开");
        sub.add(0, 5, 0, "刷新");
        sub.add(0, 6, 0, "收藏");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }
}
