package com.zncm.library.ft;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.analytics.MobclickAgent;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Lib;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.data.SpConstant;
import com.zncm.library.ui.ItemsAc;
import com.zncm.library.ui.ShareAc;
import com.zncm.library.ui.TabSettingAc;
import com.zncm.library.ui.WebViewActivity;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.MyPath;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.XUtil;
import com.zncm.library.utils.db.DatabaseHelper;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

import de.greenrobot.event.EventBus;


public class SettingsFragment extends PreferenceFragment {
    Preference pfCsv;
    Preference pfShare;
    Preference pfDb;
    Preference pfAbout;
    Preference pfQQ;
    TabSettingAc ctx;
    Preference pfNotifyKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        ctx = (TabSettingAc) getActivity();
        pfCsv = (Preference) findPreference("pfCsv");
        pfCsv.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(ctx)
                        .items(new String[]{"普通", "乱码点此"})
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        ctx.charset = "UTF-8";
                                        break;
                                    case 1:
                                        ctx.charset = "GBK";
                                        break;
                                }
                                ctx.showFileChooser();
                            }
                        })
                        .show();
                return false;
            }
        });


        pfShare = (Preference) findPreference("pfShare");
        pfShare.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                MobclickAgent.onEvent(ctx, "imp_net");
                Intent intent = new Intent(ctx, WebViewActivity.class);
                intent.putExtra("url", Constant.LIB_SHARE);
                intent.putExtra("isImport", true);
                startActivity(intent);
                return false;
            }
        });
        final Preference pfClipboardListen = (Preference) findPreference("pfClipboardListen");
        if (MySp.getClipboardListen()) {
            pfClipboardListen.setSummary("监听");
        } else {
            pfClipboardListen.setSummary("不监听");
        }
        pfClipboardListen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (MySp.getClipboardListen()) {
                    MySp.setClipboardListen(false);
                    pfClipboardListen.setSummary("不监听");
                } else {
                    MySp.setClipboardListen(true);
                    pfClipboardListen.setSummary("监听");
                }
                return false;
            }
        });


        final Preference pfNotifyListen = (Preference) findPreference("pfNotifyListen");
        if (MySp.get(SpConstant.isListenNotify, Boolean.class, true)) {
            pfNotifyListen.setSummary("监听");

        } else {
            pfNotifyListen.setSummary("不监听");
        }
        pfNotifyListen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (MySp.get(SpConstant.isListenNotify, Boolean.class, true)) {
                    MySp.put(SpConstant.isListenNotify, false);
                    pfNotifyListen.setSummary("不监听");
                } else {
                    MySp.put(SpConstant.isListenNotify, true);
                    pfNotifyListen.setSummary("监听");
                }
                return false;
            }
        });
        pfNotifyKey = (Preference) findPreference("pfNotifyKey");
        String key = MySp.get(SpConstant.notifyKey, String.class, "");
        if (XUtil.notEmptyOrNull(key)) {
            pfNotifyKey.setSummary(key);
        }
        pfNotifyKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                initNotifyEtDlg();
                return false;
            }
        });


        final Preference pfNotifyListenInto = (Preference) findPreference("pfNotifyListenInto");
        if (MySp.get(SpConstant.isListenNotifyInto, Boolean.class, true)) {
            pfNotifyListenInto.setSummary("打开");

        } else {
            pfNotifyListenInto.setSummary("关闭");
        }
        pfNotifyListenInto.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (MySp.get(SpConstant.isListenNotifyInto, Boolean.class, true)) {
                    MySp.put(SpConstant.isListenNotifyInto, false);
                    pfNotifyListenInto.setSummary("关闭");
                } else {
                    MySp.put(SpConstant.isListenNotifyInto, true);
                    pfNotifyListenInto.setSummary("打开");
                }
                return false;
            }
        });


        Preference pfShareApp = (Preference) findPreference("pfShareApp");
        pfShareApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                XUtil.sendTo(ctx, "我在玩“我的库”,实用的东西非常多，很不错，推荐一下。下载地址: " + Constant.SHARE_URL);
                return false;
            }
        });
//        Preference pfShareTemp = (Preference) findPreference("pfShareTemp");
//        pfShareTemp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Intent intent = new Intent(ctx, WebViewActivity.class);
//                intent.putExtra("url", Constant.LIB_SHARE_TMPLIB);
//                startActivity(intent);
//                return false;
//            }
//        });

        pfAbout = (Preference) findPreference("pfAbout");
        pfAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("开发者微信: xm0ff255").append("\n");
                stringBuffer.append("qq群: ").append(Constant.QQ_GROUP).append("\n");
                stringBuffer.append("库投稿: liuxingmin126@163.com").append("\n");
                stringBuffer.append("旨在灵活多变的管理任何类型的信息").append("\n");
                new MaterialDialog.Builder(ctx)
                        .title("关于 " + getResources().getString(R.string.app_name) + " " + XUtil.getVersionName(ctx))
                        .content(stringBuffer.toString())
                        .show();
                return false;
            }
        });


        pfDb = (Preference) findPreference("pfDb");
        pfDb.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                new MaterialDialog.Builder(ctx)
                        .items(new String[]{"备份（导出）", "恢复（导入）"})
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        backUpDbDo();
                                        break;
                                    case 1:
                                        ctx.showFileChooser();
                                        break;
                                }
                            }
                        })
                        .show();


                return false;
            }
        });
        pfQQ = (Preference) findPreference("pfQQ");
        pfQQ.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.QQ_GROUP_URL));
                startActivity(i);
                return false;
            }
        });
        Preference pfHelp = (Preference) findPreference("pfHelp");
        pfHelp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(ctx, WebViewActivity.class);
                intent.putExtra("url", "file:///android_asset/help/main.html");
                startActivity(intent);
                return false;
            }
        });
        final Preference pfApiPage = (Preference) findPreference("pfApiPage");
        pfApiPage.setSummary(MySp.getapi_page() + "页");
        pfApiPage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                new MaterialDialog.Builder(ctx)
                        .items(new String[]{"10", "20", "50", "100", "500"})
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        MySp.setapi_page(10);
                                        break;
                                    case 1:
                                        MySp.setapi_page(20);
                                        break;
                                    case 2:
                                        MySp.setapi_page(50);
                                        break;
                                    case 3:
                                        MySp.setapi_page(100);
                                        break;
                                    case 4:
                                        MySp.setapi_page(500);
                                        break;
                                }
                                pfApiPage.setSummary(MySp.getapi_page() + "页");
                            }
                        })
                        .show();

                return false;
            }
        });


        Preference pfUpdate = (Preference) findPreference("pfUpdate");
        pfUpdate.setSummary("当前版本:" + XUtil.getVersionName(ctx) + "(Build " + XUtil.getVersionCode(ctx) + ")");
        pfUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(ctx, WebViewActivity.class);
                intent.putExtra("url", "http://fir.im/lib/");
                startActivity(intent);
                return false;
            }
        });
    }


    private void backUpDbDo() {
        try {
            String newPath = MyPath.getPathData() + File.separator + XUtil.getDateY_M_D() + File.separator + DatabaseHelper.DATABASE_NAME;
            boolean flag = XUtil.copyFileTo(new File(Constant.DB_PATH), new File(newPath));
            if (flag) {
                XUtil.tShort("库已拷贝到" + newPath);
            } else {
                XUtil.tShort("库导出失败~");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void initNotifyEtDlg() {
        LinearLayout view = new LinearLayout(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final MaterialEditText editText = new MaterialEditText(ctx);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setFloatingLabelText("请输入需要监听的字符以|线分隔");
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText.setFloatingLabelAlwaysShown(true);
        XUtil.autoKeyBoardShow(editText);
        String nKey = MySp.get(SpConstant.notifyKey, String.class, "");
        editText.setText(nKey);
        view.addView(editText);
        MaterialDialog md = new MaterialDialog.Builder(ctx)
                .customView(view, true)
                .positiveText("保存")
                .negativeText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        try {
                            String content = editText.getText().toString().trim();
                            if (!XUtil.notEmptyOrNull(content)) {
                                XUtil.tShort("输入内容~");
                                XUtil.dismissShowDialog(dialog, false);
                                return;
                            }
                            MySp.put(SpConstant.notifyKey, content);
                            pfNotifyKey.setSummary(content);
                        } catch (Exception e) {
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog materialDialog) {

                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        XUtil.dismissShowDialog(materialDialog, true);
                    }
                })
                .autoDismiss(false)
                .build();
        md.setCancelable(false);
        md.show();
    }
}
