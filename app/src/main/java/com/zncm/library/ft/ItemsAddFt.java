package com.zncm.library.ft;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.j256.ormlite.stmt.QueryBuilder;
import com.malinskiy.materialicons.Iconify;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.umeng.analytics.MobclickAgent;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.ObjEvent;
import com.zncm.library.data.Options;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ui.ItemsDetailsAc;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.MyPath;
import com.zncm.library.utils.XUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * Created by MX on 3/24 0024.
 */
public class ItemsAddFt extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private List<Fields> datas = null;
    private Lib lib;
    private Activity ctx;
    Map<String, Object> map;
    Calendar calendar = Calendar.getInstance();
    private Fields mCurrentFields;
    String imgPath;
    boolean bUpdate = false;
    Map<String, Object> updateMap;
    private Items updateItems;
    private List<Options> mCurrentOptions = null;

    Long remindTime = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ctx = getActivity();
        map = new HashMap<String, Object>();
        updateMap = new HashMap<String, Object>();

        if (ctx.getIntent().getExtras() != null) {
            bUpdate = ctx.getIntent().getExtras().getBoolean(Constant.KEY_PARAM_BOOLEAN);
        }

        Serializable dataParam = ctx.getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA);
        if (dataParam != null && dataParam instanceof Lib) {
            lib = (Lib) dataParam;
        }

        Serializable dataItems = ctx.getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA2);
        if (dataItems != null && dataItems instanceof Items) {
            updateItems = (Items) dataItems;
            if (bUpdate && updateItems != null) {
                updateMap = JSON.parseObject(updateItems.getItem_json());
            }
        }

        ScrollView scrollView = new ScrollView(ctx);
        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        int paddingPx = XUtil.dip2px(15);
        linearLayout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        datas = Dbutils.getFields(lib.getLib_id());
        for (final Fields tmp : datas) {
            if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_INT.getValue()
                    || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DOUBLE.getValue()) {
                MaterialEditText editText = new MaterialEditText(ctx);
//                editText.setHint(tmp.getFields_name());
                editText.setFloatingLabelText(tmp.getFields_name());
                editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
                editText.setFloatingLabelAlwaysShown(true);
                editText.setTag(tmp.getFields_id());
//                if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_INT.getValue()) {
//                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DOUBLE.getValue()) {
//                    editText.setInputType(InputType.TYPE_CLASS_PHONE);
//                } else {
//                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//                    editText.setMaxLines(4);
////                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
//                }

                linearLayout.addView(editText);

                if (bUpdate) {
                    Object object = updateMap.get(String.valueOf(tmp.getFields_id()));
                    if (object != null) {
                        editText.setText((String) object);
                    }
                }
                XUtil.autoKeyBoardShow(editText);
            } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_BOOLEAN.getValue()) {
                CheckBox checkBox = new CheckBox(ctx);
                checkBox.setText(tmp.getFields_name());
                checkBox.setTag(tmp.getFields_id());
                checkBox.setTextColor(getResources().getColor(R.color.gray));
                LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll_params.setMargins(-14, 10, 0, 10);
                checkBox.setLayoutParams(ll_params);
                linearLayout.addView(checkBox);

                if (bUpdate) {
                    Object obj = updateMap.get(String.valueOf(tmp.getFields_id()));
                    boolean flag = false;
                    if (obj != null) {
                        flag = (Boolean) obj;
                    }
                    checkBox.setChecked(flag);
                }

            } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TIME.getValue()
                    || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATETIME.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()) {
                Button button = new Button(ctx);
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.card));
//                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_nor));
                button.setText(tmp.getFields_name());
                button.setTextColor(getResources().getColor(R.color.gray));
                button.setTag(tmp.getFields_id());
                button.setGravity(Gravity.LEFT);
                LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll_params.setMargins(0, 10, 0, 10);
                button.setLayoutParams(ll_params);
                linearLayout.addView(button);
                if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATE.getValue()) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCurrentFields = tmp;
                            initDatePicker();
                        }
                    });
                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TIME.getValue()) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCurrentFields = tmp;
                            initTimePickerDialog();
                        }
                    });
                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATETIME.getValue() ) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCurrentFields = tmp;
                            initDatePicker();
                        }
                    });
                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue()) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCurrentFields = tmp;
                            oneDlg();
                        }
                    });
                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCurrentFields = tmp;
                            manyDlg();
                        }
                    });
                }

                if (bUpdate) {
                    Object object = updateMap.get(String.valueOf(tmp.getFields_id()));
                    if (object != null) {
                        button.setText((String) object);
                    } else {
                        button.setText(tmp.getFields_name());
                    }
                }


            } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_PICTURE.getValue()) {
                final ImageView imageView = new ImageView(ctx);
                imageView.setTag(tmp.getFields_id());
                imageView.setImageResource(R.drawable.ic_lib);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                linearLayout.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentFields = tmp;
                        initImgDlg();
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (XUtil.notEmptyOrNull(imgPath)) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("file://" + imgPath.toString()), "image/*");
                            startActivity(intent);
                        } else {
                            XUtil.tShort("没有图片");
                        }
                        return true;
                    }
                });

                if (bUpdate) {
                    final Object imgPath = updateMap.get(String.valueOf(tmp.getFields_id()));
                    if (imgPath != null) {
                        imageView.setContentDescription(imgPath.toString());
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
                    }
                }


            } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_START.getValue()) {
                RatingBar ratingBar = new RatingBar(ctx);
                ratingBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ratingBar.setTag(tmp.getFields_id());
                ratingBar.setNumStars(5);
                ratingBar.setStepSize(1f);
                linearLayout.addView(ratingBar);

                if (bUpdate) {
                    Object star = updateMap.get(String.valueOf(tmp.getFields_id()));
                    if (star == null) {
                        star = 0;
                    }
                    ratingBar.setRating((Integer) star);
                }

            } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()) {
//                Button button = new Button(ctx);
//                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.card));
////                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_nor));
//                button.setText(tmp.getFields_name());
//                button.setTextColor(getResources().getColor(R.color.white));
//                button.setTag(tmp.getFields_id());
//                LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                ll_params.setMargins(0, 10, 0, 10);
//                button.setLayoutParams(ll_params);
//                linearLayout.addView(button);
//                if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue()) {
//                    button.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mCurrentFields = tmp;
//                            oneDlg();
//                        }
//                    });
//                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()) {
//                    button.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mCurrentFields = tmp;
//                            manyDlg();
//                        }
//                    });
//                }
//
//                if (bUpdate) {
//                    Object object = updateMap.get(String.valueOf(tmp.getFields_id()));
//                    if (object != null) {
//                        button.setText((String) object);
//                    } else {
//                        button.setText(tmp.getFields_name());
//                    }
//                }

            }


        }
        scrollView.addView(linearLayout);
        return scrollView;
    }


    private void oneDlg() {
        mCurrentOptions = Dbutils.getOptions(mCurrentFields.getFields_id());

        if (!XUtil.listNotNull(mCurrentOptions)) {
            XUtil.tShort("没有选项");
            return;
        }

        final String items[] = new String[mCurrentOptions.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = mCurrentOptions.get(i).getOptions_name();
        }

        new MaterialDialog.Builder(ctx)
                .title(mCurrentFields.getFields_name())
                .items(items)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Button button = (Button) getView().findViewWithTag(mCurrentFields.getFields_id());
                        button.setText(mCurrentFields.getFields_name() + " " + items[which]);
                        return false;
                    }
                })
                .positiveText("选择")
                .show();
    }

    private void manyDlg() {
        mCurrentOptions = Dbutils.getOptions(mCurrentFields.getFields_id());

        if (!XUtil.listNotNull(mCurrentOptions)) {
            XUtil.tShort("没有选项");
            return;
        }

        final String items[] = new String[mCurrentOptions.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = mCurrentOptions.get(i).getOptions_name();
        }

        new MaterialDialog.Builder(ctx)
                .title(mCurrentFields.getFields_name())
                .items(items)


                .itemsCallbackMultiChoice(new Integer[0], new MaterialDialog.ListCallbackMultiChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        Button button = (Button) getView().findViewWithTag(mCurrentFields.getFields_id());
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < which.length; i++) {
                            stringBuffer.append(items[i]).append(" ");
                        }
                        button.setText(mCurrentFields.getFields_name() + " " + stringBuffer.toString());
                        return false;
                    }
                })
                .positiveText("选择")
                .show();
    }


    private void initDatePicker() {
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setYearRange(2000, 2025);
        datePickerDialog.show(getActivity().getSupportFragmentManager(), "datepicker");
    }

    private void initTimePickerDialog() {
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        timePickerDialog.show(getActivity().getSupportFragmentManager(), "timepicker");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String tmp;
        if (mCurrentFields.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATETIME.getValue()) {
            calendar.set(year, month, day);
            initTimePickerDialog();
        } else {
            Button button = (Button) getView().findViewWithTag(mCurrentFields.getFields_id());
            tmp = mCurrentFields.getFields_name() + ": " + year + "年" + (month + 1) + "月" + day + "日";
            button.setText(tmp);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        Button button = (Button) getView().findViewWithTag(mCurrentFields.getFields_id());
        String tmp;
        if (mCurrentFields.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATETIME.getValue()) {
            tmp = mCurrentFields.getFields_name() + ": " + calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日 " + hourOfDay + ":" + minute;
        }else {
            tmp = mCurrentFields.getFields_name() + ": " + hourOfDay + ":" + minute;
        }


        button.setText(tmp);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add("save").setIcon(XUtil.initIconWhite(Iconify.IconValue.md_save)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item == null || item.getTitle() == null) {
            return false;
        }
        if (item.getTitle().equals("save")) {
            MobclickAgent.onEvent(ctx, "add_item");

            for (Fields tmp : datas) {
                if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_INT.getValue()) {
                    MaterialEditText editText = (MaterialEditText) getView().findViewWithTag(tmp.getFields_id());
                    String value = editText.getText().toString();
                    if (tmp.isFields_notnull() && !XUtil.notEmptyOrNull(value)) {
                        XUtil.tShort(tmp.getFields_name() + " 不能为空");
                        return false;
                    }
                    if (XUtil.notEmptyOrNull(value)) {
                        map.put(String.valueOf(tmp.getFields_id()), value);
                    }
                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATE.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TIME.getValue()
                        || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_DATETIME.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue()
                        || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()) {
                    Button button = (Button) getView().findViewWithTag(tmp.getFields_id());
                    String value = button.getText().toString();
                    if (tmp.isFields_notnull() && !XUtil.notEmptyOrNull(value)) {
                        XUtil.tShort(tmp.getFields_name() + " 不能为空");
                        return false;
                    }

                    if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue()
                            || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()) {
                        value = value.replaceAll(tmp.getFields_name(), "").trim();
                    }

                    if (XUtil.notEmptyOrNull(value)) {
                        map.put(String.valueOf(tmp.getFields_id()), value);
                    }
                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_BOOLEAN.getValue()) {
                    CheckBox checkBox = (CheckBox) getView().findViewWithTag(tmp.getFields_id());
                    Boolean value = checkBox.isChecked();
                    map.put(String.valueOf(tmp.getFields_id()), value);
                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_PICTURE.getValue()) {

                    ImageView imageView = (ImageView) getView().findViewWithTag(tmp.getFields_id());
                    CharSequence value = imageView.getContentDescription();
                    if (tmp.isFields_notnull() && value == null) {
                        XUtil.tShort(tmp.getFields_name() + " 不能为空");
                        return false;
                    }
                    if (value != null) {
                        map.put(String.valueOf(tmp.getFields_id()), value.toString());
                    }

                } else if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_START.getValue()) {
                    RatingBar ratingBar = (RatingBar) getView().findViewWithTag(tmp.getFields_id());
                    map.put(String.valueOf(tmp.getFields_id()), ratingBar.getRating());
                }
            }


            JSON json = new JSONObject(map);
            XUtil.debug("json:" + json + " " + map);
            if (map.size() > 0) {
                if (bUpdate) {
                    updateItems.setItem_json(json.toJSONString());
                    Dbutils.updateItems(updateItems);
                    EventBus.getDefault().post(new ObjEvent(EnumData.RefreshEnum.ITEMS.getValue(), updateItems));
                } else {
                    Items items = new Items(lib.getLib_id(), json.toJSONString());
                    Dbutils.addItems(items);
                }
                EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.ITEMS.getValue()));
            }


            ctx.finish();

        }
        return false;
    }


    void initImgDlg() {
        new MaterialDialog.Builder(ctx)
                .items(new String[]{"从相册选", "拍照", "取消"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                doPickPhotoFromGallery();
                                break;
                            case 1:
                                doTakePhoto();
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .show();

    }

    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
        }
    }

    private void doTakePhoto() {
        try {
            imgPath = MyPath.getPathImg() + "/" + String.valueOf(System.currentTimeMillis());
            File mCurrentPhotoFile = new File(imgPath);
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, 101);
        } catch (ActivityNotFoundException e) {
        }
    }

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case 100:
                String tmp = XUtil.getPathFromUri(ctx, data.getData());
                imgPath = MyPath.getPathImg() + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                try {
                    XUtil.copyFileTo(new File(tmp), new File(imgPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                showImg();
                break;
            case 101:
                showImg();
                break;

        }

    }

    private void showImg() {
        final ImageView imageView = (ImageView) getView().findViewWithTag(mCurrentFields.getFields_id());
        imageView.setContentDescription(imgPath);
        MyApplication.imageLoader.displayImage("file://" + imgPath,
                imageView, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        imageView.setImageResource(R.drawable.ic_lib);
                        super.onLoadingStarted(imageUri, view);
                    }
                });
    }
}
