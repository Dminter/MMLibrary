package com.zncm.library.ft;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.malinskiy.materialicons.Iconify;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.SpConstant;
import com.zncm.library.ui.BaseAc;
import com.zncm.library.ui.ItemsAc;
import com.zncm.library.ui.ItemsAddAc;
import com.zncm.library.ui.ItemsDetailsAc;
import com.zncm.library.ui.PhotoAc;
import com.zncm.library.ui.ShareAc;
import com.zncm.library.ui.WebViewActivity;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.FileMiniUtil;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.NotiHelper;
import com.zncm.library.utils.XUtil;
import com.zncm.library.view.HackyViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by MX on 3/24 0024.
 */
public class ItemsDetailsFt extends Fragment {
    private List<Fields> datas = null;
    private Items items;
    private BaseAc ctx;
    Map<String, Object> map;

    //    private ArrayList list;
    private int current = 0;
    private String title;
    private ArrayList<Items> itemsList;
    private ViewPager mViewPager;
    Lib lib;

    SamplePagerAdapter mAdapter;
    final ArrayList<String> images = new ArrayList<>();


    long totalSize = 0;

    boolean bSearch = false;

    int size;
    StringBuffer shareInfo = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ctx = (BaseAc) getActivity();
        map = new HashMap<String, Object>();

        Serializable dataParam = ctx.getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA);
        if (dataParam != null && dataParam instanceof Lib) {
            lib = (Lib) dataParam;
            if (XUtil.notEmptyOrNull(lib.getLib_name())) {
                title = lib.getLib_name();
            }
        }

        current = ctx.getIntent().getExtras().getInt(Constant.KEY_CURRENT_POSITION);
        size = ctx.getIntent().getExtras().getInt("size");

        itemsList = new ArrayList<>();

        if (size == -1) {
            bSearch = true;
            //搜索 -单个结果
            Serializable itemParam = ctx.getIntent().getSerializableExtra("item");
            if (itemParam != null && itemParam instanceof Items) {
                Items tmp = (Items) itemParam;
                if (tmp != null) {
                    totalSize = 1;
                    current = 0;
                    itemsList.add(tmp);
                }
            }
        } else {
            totalSize = Dbutils.getItemsSize(lib.getLib_id());
            itemsList = (ArrayList<Items>) Dbutils.getItemsPageMaxRows(lib.getLib_id(), size);
        }
        mViewPager = new HackyViewPager(getActivity());
        mAdapter = new SamplePagerAdapter(itemsList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                current = position;
                if (!bSearch && current == itemsList.size() - 1) {
                    size += Constant.MAX_DB_QUERY;
                    List<Items> items = new ArrayList<Items>();
                    items = Dbutils.getItemsPage(lib.getLib_id(), size);
                    if (XUtil.listNotNull(items)) {
                        itemsList.addAll(items);
                        mAdapter.setItems(itemsList);
                    }
                }
                ctx.myTitle((position + 1) + "/" + totalSize);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(current);
        ctx.myTitle((current + 1) + "/" + totalSize);
        return mViewPager;
    }

    class SamplePagerAdapter extends PagerAdapter {
        private List<Items> datas;


        public void setItems(List<Items> items) {
            this.datas = items;
            notifyDataSetChanged();
        }

        public SamplePagerAdapter(ArrayList<Items> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            if (datas == null) {
                return 0;
            } else {
                return datas.size();
            }

        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            items = itemsList.get(position);
            /**
             *已读
             */
            if (XUtil.listNotNull(itemsList)) {
                Dbutils.readItems(itemsList.get(mViewPager.getCurrentItem()).getItem_id(), true);
            }
            map = JSON.parseObject(items.getItem_json());
            ScrollView scrollView = initViews();
            container.addView(scrollView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return scrollView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


    private ScrollView initViews() {
        ScrollView scrollView = new ScrollView(ctx);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        scrollView.setBackgroundColor(getResources().getColor(R.color.white));
        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        int paddingPx = XUtil.dip2px(15);
        linearLayout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        datas = Dbutils.getFields(items.getLib_id());
        for (final Fields tmp : datas) {
            if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_INT.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DOUBLE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TIME.getValue()
                    || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATETIME.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()) {
                Object object = map.get(String.valueOf(tmp.getFields_id()));
                final String url = (String) object;
                if (XUtil.isPic(url)) {
                    final ImageView imageView = new ImageView(ctx);
                    imageView.setTag(tmp.getFields_id());
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    images.add(url);
                    MyApplication.imageLoader.displayImage(url,
                            imageView, new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    imageView
                                            .setImageResource(R.drawable.ic_lib);
                                    super.onLoadingStarted(imageUri, view);
                                }
                            });
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ctx, PhotoAc.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                    });
                    linearLayout.addView(imageView);
                } else {
                    MaterialEditText editText = new MaterialEditText(ctx);
                    editText.setAutoLinkMask(Linkify.ALL);
                    editText.setHint(tmp.getFields_name());
                    if (MySp.get(SpConstant.isShowTitle, Boolean.class, true)) {
                        editText.setFloatingLabelText(tmp.getFields_name());

                    } else {
                        editText.setHideUnderline(true);
                    }

                    editText.setTextColor(getResources().getColor(R.color.black));
                    editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
                    editText.setFloatingLabelAlwaysShown(true);
                    editText.setKeyListener(null);
                    editText.setFocusable(false);


                    if (object != null) {
                        String content = ((String) object).trim();
                        if (lib.getLib_exi1() == Lib.libType.rss.value()) {
                            content = XUtil.replaceImageURLs(content);
                            editText.setText(XUtil.fromHtml(content));
                        } else {
                            editText.setText(XUtil.fromHtml(content));

//                            editText.setText(content);
                        }
                        editText.setLineSpacing(1.1f, 1.1f);
                        XUtil.stripUnderlinesEditText(editText);
                        if (FileMiniUtil.isPathInDisk(content)) {
                            TextView textView = new TextView(ctx);
                            textView.setText("本地文件-打开\n");
                            textView.setTextColor(getResources().getColor(R.color.primary));
                            final String finalContent = content;
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    XUtil.openFile(ctx, finalContent);
                                }
                            });
                            linearLayout.addView(textView);

//                            strs.add("SYS-LIB-" + lib.getLib_id());
//                            strs.add("SYS-ITEM-" + items.getItem_id());
                        } else if (content.startsWith(Constant.SYS_PRE_ITEM)) {
                            try {
                                String idStr = content.replace(Constant.SYS_PRE_ITEM, "");
                                final int item_id = Integer.parseInt(idStr);
                                TextView textView = new TextView(ctx);
                                textView.setText("链接到原始记录\n");
                                textView.setTextColor(getResources().getColor(R.color.primary));
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Items data = Dbutils.findItems(item_id);
                                        Lib tmp = Dbutils.findLib(data.getLib_id());
                                        if (data == null) {
                                            return;
                                        }
                                        Intent intent = new Intent(ctx, ItemsDetailsAc.class);
                                        intent.putExtra(Constant.KEY_PARAM_DATA, tmp);
                                        intent.putExtra("item", data);
                                        intent.putExtra("size", -1);
                                        intent.putExtra(Constant.KEY_CURRENT_POSITION, 0);
                                        startActivity(intent);
                                    }
                                });
                                linearLayout.addView(textView);


                            } catch (Exception e) {

                            }
                        } else if (content.startsWith(Constant.SYS_PRE_LIB)) {
                            try {
                                String idStr = content.replace(Constant.SYS_PRE_LIB, "");
                                final int lib_id = Integer.parseInt(idStr);
                                final Lib tmp_lib = Dbutils.findLib(lib_id);
                                TextView textView = new TextView(ctx);
                                textView.setText(tmp_lib.getLib_name() + "\n");
                                textView.setTextColor(getResources().getColor(R.color.primary));
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (tmp_lib == null) {
                                            return;
                                        }
                                        Intent intent = new Intent(ctx, ItemsAc.class);
                                        intent.putExtra(Constant.KEY_PARAM_DATA, tmp_lib);
                                        startActivity(intent);
                                    }
                                });
                                linearLayout.addView(textView);


                            } catch (Exception e) {

                            }
                        } else {
                            XUtil.doubleTextPre(ctx, editText, object + "");
                            if (XUtil.notEmptyOrNull(editText.getText().toString().trim())) {
                                linearLayout.addView(editText);
                            }
                        }
                    }
                }
            } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_BOOLEAN.getValue()) {
                CheckBox checkBox = new CheckBox(ctx);
                checkBox.setText(tmp.getFields_name());
                checkBox.setEnabled(false);
                checkBox.setTextColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll_params.setMargins(-14, 10, 0, 10);
                checkBox.setLayoutParams(ll_params);
                Object obj = map.get(String.valueOf(tmp.getFields_id()));
                boolean flag = false;
                if (obj != null) {
                    flag = (Boolean) obj;
                }
                checkBox.setChecked(flag);
                linearLayout.addView(checkBox);
            } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_PICTURE.getValue()) {
                final ImageView imageView = new ImageView(ctx);
                imageView.setTag(tmp.getFields_id());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                final Object imgPath = map.get(String.valueOf(tmp.getFields_id()));
                if (imgPath != null) {
                    MyApplication.imageLoader.displayImage("file://" + imgPath.toString(),
                            imageView, new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    imageView
                                            .setImageResource(R.drawable.ic_lib);
                                    super.onLoadingStarted(imageUri, view);
                                }
                            });
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("file://" + imgPath.toString()), "image/*");
                            startActivity(intent);
                        }
                    });
                    linearLayout.addView(imageView);
                }
            } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_START.getValue()) {
                RatingBar ratingBar = new RatingBar(ctx);
                ratingBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                Object star = map.get(String.valueOf(tmp.getFields_id()));
                ratingBar.setEnabled(false);
                if (star != null) {
                    ratingBar.setRating((Integer) star);
                    linearLayout.addView(ratingBar);
                }
            } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_NET_PIC.getValue()) {
                Object object = map.get(String.valueOf(tmp.getFields_id()));
                final String url = (String) object;
                if (XUtil.notEmptyOrNull(url)) {
                    final ImageView imageView = new ImageView(ctx);
                    imageView.setTag(tmp.getFields_id());
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    imageView.setScaleType(ImageView.ScaleType.MATRIX);
                    images.add(url);
                    MyApplication.imageLoader.displayImage(url,
                            imageView, new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    imageView
                                            .setImageResource(R.drawable.ic_lib);
                                    super.onLoadingStarted(imageUri, view);
                                }
                            });
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ctx, PhotoAc.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                    });
                    linearLayout.addView(imageView);
                }
            }
        }

        if (MySp.get(SpConstant.isShowTime, Boolean.class, true)) {
            StringBuffer info = new StringBuffer();
            info.append("创建时间：").append(XUtil.getDateFull(items.getItem_time())).append("\n");
            info.append("修改时间：").append(XUtil.getDateFull(items.getItem_modify_time())).append("\n");
            if (XUtil.notEmptyOrNull(info.toString())) {
                MaterialEditText editText = new MaterialEditText(ctx);
//                editText.setAutoLinkMask(Linkify.ALL);
                if (MySp.get(SpConstant.isShowTitle, Boolean.class, true)) {
                    editText.setFloatingLabelText("信息");

                } else {
                    editText.setHideUnderline(true);
                }
                editText.setTextColor(getResources().getColor(R.color.black));
                editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
                editText.setFloatingLabelAlwaysShown(true);
                editText.setEnabled(false);
                editText.setFocusable(false);
                linearLayout.addView(editText);
                editText.setText(info.toString());

            }
        }


        if (XUtil.notEmptyOrNull(items.getItem_exs1())) {
            StringBuffer info = new StringBuffer();
            info.append(items.getItem_exs1()).append("\n");
            if (XUtil.notEmptyOrNull(info.toString())) {
                MaterialEditText editText = new MaterialEditText(ctx);
                editText.setAutoLinkMask(Linkify.WEB_URLS);
                if (MySp.get(SpConstant.isShowTitle, Boolean.class, true)) {
                    editText.setFloatingLabelText("阅读原文");
                } else {
                    editText.setHideUnderline(true);
                }
                editText.setTextColor(getResources().getColor(R.color.black));
                editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
                editText.setFloatingLabelAlwaysShown(true);
                editText.setEnabled(true);
                editText.setFocusable(false);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Items curItem = itemsList.get(mViewPager.getCurrentItem());
                        String url = curItem.getItem_exs1();
                        ItemsFt.openUrl(ctx,url);
                    }
                });

                linearLayout.addView(editText);
                editText.setText(info.toString());

            }
        }
        scrollView.addView(linearLayout);
        return scrollView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.add("md_star").setIcon(XUtil.initIconWhite(Iconify.IconValue.md_star)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("md_notifications").setIcon(XUtil.initIconWhite(Iconify.IconValue.md_notifications)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        SubMenu sub = menu.addSubMenu("");
        sub.setIcon(XUtil.initIconWhite(Iconify.IconValue.md_more_vert));
        sub.add(0, 1, 0, "编辑");
        sub.add(0, 2, 0, "分享");
//        sub.add(0, 3, 0, "收藏");
//        sub.add(0, 4, 0, "贴到通知栏");
        sub.add(0, 5, 0, "删除");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item == null || item.getTitle() == null) {
            return false;
        }


        if (item.getTitle().equals("md_star")) {
            infoSc();
        } else if (item.getTitle().equals("md_notifications")) {
            infoNoti();
        }


        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(ctx, ItemsAddAc.class);
                intent.putExtra(Constant.KEY_PARAM_DATA, lib);
                intent.putExtra(Constant.KEY_PARAM_DATA2, itemsList.get(mViewPager.getCurrentItem()));
                intent.putExtra(Constant.KEY_PARAM_BOOLEAN, true);
                startActivity(intent);
                break;
            case 2:
                infoShare();
                break;
            case 3:

                break;
            case 4:

                break;
            case 5:
                delDlg();
                break;
        }

        return false;
    }

    private void infoShare() {
        shareInfo = new StringBuffer();
        items = itemsList.get(mViewPager.getCurrentItem());
        map = JSON.parseObject(items.getItem_json());
        shareInfo.append("库名：" + lib.getLib_name()).append("\n");
        for (Fields tmp : datas) {
            if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_INT.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DOUBLE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TIME.getValue()
                    || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATETIME.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()
                    || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_START.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_BOOLEAN.getValue()
                    ) {
                Object object = map.get(String.valueOf(tmp.getFields_id()));
                String name = tmp.getFields_name();
                if (object != null) {
                    shareInfo.append(name).append("：");
                    shareInfo.append(object).append("\n");
                }
            }
        }
        if (XUtil.notEmptyOrNull(shareInfo.toString())) {
            XUtil.sendTo(ctx, shareInfo.toString());
        }
    }

    private void infoSc() {
        ArrayList<String> strs = new ArrayList<>();
        items = itemsList.get(mViewPager.getCurrentItem());
        map = JSON.parseObject(items.getItem_json());
        for (Fields tmp : datas) {
            Object object = map.get(String.valueOf(tmp.getFields_id()));
            String name = tmp.getFields_name();
            if (object != null) {
                strs.add(name + "：" + object);
            }
        }
        strs.add(Constant.SYS_PRE_LIB + lib.getLib_id());
        strs.add(Constant.SYS_PRE_ITEM + items.getItem_id());
        ShareAc.initSaveList(Constant.SYS_SC, Constant.SYS_SC_MK, strs, Lib.libType.sys.value());
        XUtil.tShort("已添加收藏");
    }

    private void infoNoti() {
        ArrayList<String> strs = new ArrayList<>();
        items = itemsList.get(mViewPager.getCurrentItem());
        map = JSON.parseObject(items.getItem_json());
        for (Fields tmp : datas) {
            Object object = map.get(String.valueOf(tmp.getFields_id()));
            String name = tmp.getFields_name();
            if (object != null) {
                strs.add(name + "：" + object);
            }
        }

        Lib curLib = Dbutils.findLib(items.getLib_id());
        Intent intent = new Intent(ctx, ItemsDetailsAc.class);
        intent.putExtra(Constant.KEY_PARAM_DATA, curLib);
        intent.putExtra("item", items);
        intent.putExtra("size", -1);
        intent.putExtra(Constant.KEY_CURRENT_POSITION, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String title = curLib.getLib_name();
        String content = "";
        if (XUtil.listNotNull(strs)) {
            if (strs.size() > 2) {
                title = strs.get(0);
                content = strs.get(1);
            } else {
                content = strs.get(0);
            }
        }

        NotiHelper.noti(title, content, title, intent, false, new Random().nextInt());
//        NotiHelper.noti(title, content, title, intent, false, items.getItem_id());
        XUtil.tShort("已贴到通知栏");
    }

    private void delDlg() {
        ArrayList<String> strs = new ArrayList<>();
        items = itemsList.get(mViewPager.getCurrentItem());
        new MaterialDialog.Builder(ctx)
                .title("删除")
                .content("确认删除" + items.getItem_json())
                .positiveText("删除")
                .negativeText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        Dbutils.deleteData(items);
                        XUtil.tShort("已删除~");
                    }
                }).show();
//        items = itemsList.get(mViewPager.getCurrentItem());
//        map = JSON.parseObject(items.getItem_json());
//        for (Fields tmp : datas) {
//            Object object = map.get(String.valueOf(tmp.getFields_id()));
//            String name = tmp.getFields_name();
//            if (object != null) {
//                strs.add(name + "：" + object);
//            }
//        }
//
//        XUtil.debug("strs.get(0)==>" + strs.get(0));
////        Intent intent = new Intent();
//
//
//        String intentStr = strs.get(0);
//
////        intentStr = "<a href=\"intent://scan/" + intentStr + "> Take a QR code </a>";
////        <a href="intent://scan/#Intent;scheme=zxing;package=com.google.zxing.client.android;end"> Take a QR code </a>
////        Intent intent = new Intent(Intent.ACTION_VIEW);
//////        i.setData(Uri.parse(url));
//////        startActivity(i);
////START u0 {act=com.tencent.mm.action.WX_SHORTCUT flg=0x14000000 pkg=com.tencent.mm cmp=com.tencent.mm/.plugin.base.stub.WXShortcutEntryActivity bnds=[596,801][810,1045] (has extras)} from uid 10269 on display 0
////
////        intent.setData(Uri.parse(intentStr));
////        startActivity(intent);
//
//        try {
//            Intent intent = UriUtils.parseIntentUri(intentStr, 0);
//
//            XUtil.debug("intent>>"+intent.getDataString());
//            startActivity(intent);
//
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }


    public void onRefresh(Items items) {
        itemsList.set(mViewPager.getCurrentItem(), items);
        mViewPager.getAdapter().notifyDataSetChanged();
    }


}
