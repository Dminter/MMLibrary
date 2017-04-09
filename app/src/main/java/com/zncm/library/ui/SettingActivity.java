package com.zncm.library.ui;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kenumir.materialsettings.MaterialSettings;
import com.kenumir.materialsettings.items.CheckboxItem;
import com.kenumir.materialsettings.items.DividerItem;
import com.kenumir.materialsettings.items.HeaderItem;
import com.kenumir.materialsettings.items.TextItem;
import com.kenumir.materialsettings.storage.StorageInterface;
import com.malinskiy.materialicons.Iconify;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.data.SpConstant;
import com.zncm.library.utils.DbHelper;
import com.zncm.library.utils.MyPath;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.PhoneUtils;
import com.zncm.library.utils.XUtil;
import com.zncm.library.utils.db.DatabaseHelper;

import java.io.File;

import de.greenrobot.event.EventBus;

/**
 * Created by dminter on 2016/11/1.
 */

public class SettingActivity extends MaterialSettings {

    Activity ctx;
    private static SettingActivity instance;

    public static SettingActivity getInstance() {
        return instance;
    }


    private boolean isNeedUpdate = false;
    public static String charset = "UTF-8";

    public void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件导入"), 103);
        } catch (android.content.ActivityNotFoundException ex) {
            XUtil.tShort("没有找到文件管理器");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        ctx = this;
        instance = this;
        XUtil.verifyStoragePermissions(this);


        addItem(new TextItem(ctx, "").setTitle("导入CSV").setSubtitle("仅支持UTF-8编码").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                new MaterialDialog.Builder(ctx)
                        .items(new String[]{"普通", "乱码点此"})
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        charset = "UTF-8";
                                        break;
                                    case 1:
                                        charset = "GBK";
                                        break;
                                }
                                showFileChooser();
                            }
                        })
                        .show();
            }
        }));
        addItem(new TextItem(ctx, "").setTitle("导入剪切板数据").setSubtitle("复制数据，点击即可粘贴").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {

                String newPath = MyPath.getPathData() + File.separator + XUtil.getDateY_M_D() + File.separator + XUtil.getDateY_M_D_H_M_S() + ".txt";

                ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData abc = myClipboard.getPrimaryClip();
                ClipData.Item item = abc.getItemAt(0);
                String text = item.getText().toString();
                if (XUtil.notEmptyOrNull(text)) {
                    XUtil.contentToTxt(newPath, text);
                    getData(ctx, newPath);
                } else {
                    XUtil.tShort("剪切板没有数据~");
                }
            }
        }));

        addItem(new TextItem(ctx, "").setTitle("导入联系人/短信").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                new MaterialDialog.Builder(ctx)
                        .items(new String[]{Constant.SYS_CONTECT, Constant.SYS_SMS, Constant.SYS_CONTECT_NEAR})
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        PhoneUtils.readContect();
                                        break;
                                    case 1:
                                        PhoneUtils.getSMS();
                                        break;
                                    case 2:
                                        PhoneUtils.loadStrequent();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        }));


        addItem(new TextItem(ctx, "").setTitle("备份").setSubtitle("备份所有数据到存储卡").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
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
                                        showFileChooser();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        }));


        addItem(new TextItem(ctx, "").setTitle("导入网络库").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                Intent intent = new Intent(ctx, WebViewActivity.class);
                intent.putExtra("url", Constant.LIB_SHARE);
                intent.putExtra("isImport", true);
                startActivity(intent);
            }
        }));


        addItem(new DividerItem(ctx));
        addItem(new TextItem(ctx, "").setTitle("单次更新页数").setSubtitle(MySp.getapi_page() + "页").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(final TextItem textItem) {
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
                                textItem.updateSubTitle(MySp.getapi_page() + "页");
                            }
                        })
                        .show();
            }
        }));


        addItem(new TextItem(ctx, "").setTitle("分享给朋友").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                XUtil.sendTo(ctx, "我在玩“我的库”,实用的东西非常多，很不错，推荐一下。下载地址: " + Constant.SHARE_URL);
            }
        }));

        String buildDate = "2017-4-9 18:31:42";

        addItem(new TextItem(ctx, "").setTitle("检查更新").setSubtitle("当前版本:" + XUtil.getVersionName(ctx) + " (Build " + XUtil.getVersionCode(ctx) + ") @" + buildDate).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                Intent intent = new Intent(ctx, WebViewActivity.class);
                intent.putExtra("url", "http://fir.im/lib/");
                startActivity(intent);
            }
        }));


        addItem(new TextItem(ctx, "").setTitle("交流反馈").setSubtitle("开发者微信: xm0ff255").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("开发者微信: xm0ff255").append("\n");
                stringBuffer.append("qq群: ").append(Constant.QQ_GROUP).append("\n");
                stringBuffer.append("库投稿: liuxingmin126@163.com").append("\n");
                stringBuffer.append("旨在灵活多变的管理任何类型的信息").append("\n");
                new MaterialDialog.Builder(ctx)
                        .title("关于 " + getResources().getString(R.string.app_name) + " " + XUtil.getVersionName(ctx))
                        .content(stringBuffer.toString())
                        .show();
            }
        }));


        addItem(new TextItem(ctx, "").setTitle("加入QQ群").setSubtitle("qq群: " + Constant.QQ_GROUP).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.QQ_GROUP_URL));
                startActivity(i);
            }
        }));

        addItem(new TextItem(ctx, "").setTitle("帮助").setSubtitle("新手指导").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                Intent intent = new Intent(ctx, WebViewActivity.class);
                intent.putExtra("url", "file:///android_asset/help/main.html");
                startActivity(intent);
            }
        }));
        addItem(new HeaderItem(this).setTitle("TIPS!"));
        addItem(new TextItem(ctx, "").setTitle("搜索").setSubtitle("库里面搜索 @200 直接跳转到200页").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {

            }
        }));

        addItem(new DividerItem(ctx));

        addItem(new CheckboxItem(this, "").setTitle("监听剪切板").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                MySp.setClipboardListen(isChecked);
            }
        }).setDefaultValue(MySp.getClipboardListen()));


        addItem(new DividerItem(ctx));
        addItem(new CheckboxItem(this, "").setTitle("监听通知栏").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                MySp.put(SpConstant.isListenNotify, isChecked);
            }
        }).setDefaultValue(MySp.get(SpConstant.isListenNotify, Boolean.class, true)));

        addItem(new TextItem(ctx, "").setTitle("关键字提醒").setSubtitle(MySp.get(SpConstant.notifyKey, String.class, "")).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                initNotifyEtDlg(textItem);
            }
        }));


        addItem(new CheckboxItem(this, "").setTitle("展开通知").setSubtitle("通知含有关键字，自动打开").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                MySp.put(SpConstant.isListenNotifyInto, isChecked);
            }
        }).setDefaultValue(MySp.get(SpConstant.isListenNotifyInto, Boolean.class, true)));


        addItem(new HeaderItem(this).setTitle("其他"));
        addItem(new TextItem(ctx, "").setTitle("本软件已开源").setSubtitle(Constant.GITHUB_URL).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                Intent intent = new Intent(ctx, WebViewActivity.class);
                intent.putExtra("url", Constant.GITHUB_URL);
                startActivity(intent);
            }
        }));

        addItem(new CheckboxItem(this, "").setTitle("详情显示创建/修改时间").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                MySp.put(SpConstant.isShowTime, isChecked);
            }
        }).setDefaultValue(MySp.get(SpConstant.isShowTime, Boolean.class, true)));
        addItem(new CheckboxItem(this, "").setTitle("详情显示标题").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                MySp.put(SpConstant.isShowTitle, isChecked);
            }
        }).setDefaultValue(MySp.get(SpConstant.isShowTitle, Boolean.class, true)));


        addItem(new CheckboxItem(this, "").setTitle("网络库/Rss直接打开网页").setSubtitle("列表点击跳过详情页，直接打开链接").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                MySp.put(SpConstant.isOpenUrl, isChecked);
            }
        }).setDefaultValue(MySp.get(SpConstant.isOpenUrl, Boolean.class, false)));

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 103:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = XUtil.getPathFromUri(this, uri);
                    if (XUtil.notEmptyOrNull(path)) {
                        if (path.endsWith(".db")) {
                            importDb(path);
                        } else if (path.contains("csv") || path.contains("txt")) {
                            getData(ctx, path);
                        } else {
                            XUtil.tShort("格式不支持~");
                        }
                    }
                }
                break;
        }
    }


    void initNotifyEtDlg(final TextItem textItem) {
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
                            textItem.updateSubTitle(content);

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


    public static void getData(Context ctx, String path) {
//        dlg = new MaterialDialog.Builder(ctx)
//                .title("请稍后...")
//                .autoDismiss(false)
//                .show();
        TabSettingAc.GetData getData = new TabSettingAc.GetData();
        getData.execute(path);
    }

    static class GetData extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {
            DbHelper.importCsv(new File(params[0]), charset);
            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            XUtil.tShort("values: " + values[0]);
        }

        protected void onPostExecute(Boolean canLoadMore) {
            super.onPostExecute(canLoadMore);
            XUtil.tShort("数据已成功导入 ~");
            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LIB.getValue()));
//            dlg.dismiss();
        }
    }


    public File renameFile(String path) {
        File oldfile = new File(path);
        File newfile = new File(oldfile.getParent() + "/" + "lib.db");
        oldfile.renameTo(newfile);
        return newfile;
    }


    private void importDb(String path) {
        try {
            boolean flag = XUtil.copyFileTo(renameFile(path), new File(Constant.DB_PATH));
            if (flag) {
                XUtil.tShort("库导入成功~");
            } else {
                XUtil.tShort("库导入失败~");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public StorageInterface initStorageInterface() {
        return null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if (item == null || item.getTitle() == null) {
            return false;
        }

        if (item.getTitle().equals("back")) {
            backDo();
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        backDo();
    }


    private void backDo() {
        finish();
        if (isNeedUpdate) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("back").setIcon(XUtil.initIconWhite(Iconify.IconValue.md_arrow_back)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

}
