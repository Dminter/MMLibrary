package com.zncm.library.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.malinskiy.materialicons.Iconify;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zncm.library.MainActivity;
import com.zncm.library.R;
import com.zncm.library.utils.ImageUtil;
import com.zncm.library.utils.MyPath;
import com.zncm.library.utils.XUtil;

import java.io.File;
import java.io.IOException;

/**
 * 定制化显示扫描界面
 */
public class QRActivity extends BaseAc {

    private CaptureFragment captureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("扫一扫");
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

    }

    @Override
    protected int setCV() {
        return R.layout.activity_second;
    }

    public static boolean isOpen = false;

    private void light() {
        if (!isOpen) {
            CodeUtils.isLightEnable(true);
            isOpen = true;
        } else {
            CodeUtils.isLightEnable(false);
            isOpen = false;
        }
    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            QRActivity.this.setResult(RESULT_OK, resultIntent);
            QRActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            QRActivity.this.setResult(RESULT_OK, resultIntent);
            QRActivity.this.finish();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu sub = menu.addSubMenu("");
        sub.setIcon(XUtil.initIconWhite(Iconify.IconValue.md_more_vert));
        sub.add(0, 1, 0, "从相册选取二维码");
        sub.add(0, 2, 0, "闪光灯");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item == null || item.getTitle() == null) {
            return false;
        }
        switch (item.getItemId()) {
            case 1:
                doPickPhotoFromGallery();
                break;
            case 2:
                light();
                break;
        }

        return false;
    }

    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case 100:
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            TabMyLibActivity.qrSuccess(QRActivity.this, result);
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            TabMyLibActivity.qrFailed();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }

    }
}
