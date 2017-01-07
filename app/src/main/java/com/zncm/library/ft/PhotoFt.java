package com.zncm.library.ft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.malinskiy.materialicons.Iconify;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.Options;
import com.zncm.library.ui.BaseAc;
import com.zncm.library.ui.OptionsAc;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.MyPath;
import com.zncm.library.utils.XUtil;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by MX on 3/11 0011.
 */
public class PhotoFt extends BaseFt {

    String url;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (BaseAc) getActivity();
        if (ctx.getIntent().getExtras() != null) {
            url = ctx.getIntent().getExtras().getString("url");
        }
        final PhotoView photoView = new PhotoView(ctx);
        MyApplication.imageLoader.displayImage(url,
                photoView, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        photoView
                                .setImageResource(R.drawable.ic_lib);
                        super.onLoadingStarted(imageUri, view);
                    }
                });
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View view, float x, float y) {
                ctx.finish();
            }
        });
        return photoView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add("md_save").setTitle("保存").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item == null || item.getTitle() == null) {
            return false;
        }

        if (item.getTitle().equals("保存")) {
            try {
                Bitmap bitmap = MyApplication.imageLoader.loadImageSync(url);
                MediaStore.Images.Media.insertImage(ctx.getContentResolver(), bitmap, "title", "description");
                XUtil.tShort("图片已保存至图库!");
            } catch (Exception e) {
            }
        }
        return false;
    }
}
