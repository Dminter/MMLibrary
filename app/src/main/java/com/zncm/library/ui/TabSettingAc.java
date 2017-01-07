package com.zncm.library.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ft.LibFt;
import com.zncm.library.ft.SettingsFragment;
import com.zncm.library.utils.DbHelper;
import com.zncm.library.utils.XUtil;

import java.io.File;

import de.greenrobot.event.EventBus;


public class TabSettingAc extends BaseAc {

    public static String charset = "UTF-8";
    static MaterialDialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackOn(false);
        toolbar.setNavigationIcon(null);
        toolbar.setTitle("设置");
//        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment())
                        .commit();
            }
        });
    }

    @Override
    protected int setCV() {
        return R.layout.activity_setting;
    }

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
                            getData(TabSettingAc.this, path);
                        } else {
                            XUtil.tShort("格式不支持~");
                        }
                    }
                }
                break;
        }
    }


    public static void getData(Context ctx, String path) {
//        dlg = new MaterialDialog.Builder(ctx)
//                .title("请稍后...")
//                .autoDismiss(false)
//                .show();
        GetData getData = new GetData();
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

}
