package com.zncm.library.ft;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.malinskiy.materialicons.Iconify;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Lib;
import com.zncm.library.data.Options;
import com.zncm.library.ui.BaseAc;
import com.zncm.library.ui.FieldsAddAc;
import com.zncm.library.ui.LibAddAc;
import com.zncm.library.ui.OptionsAc;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.XUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by MX on 3/11 0011.
 */
public class FieldsAddFt extends BaseFt {

    Fields fields;
    boolean bUpdate;
    CheckBox checkBox;
    MaterialEditText editText;
    private List<Options> datas = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (BaseAc) getActivity();
        Serializable dataParam = ctx.getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA);
        if (dataParam != null && dataParam instanceof Fields) {
            fields = (Fields) dataParam;
        }
        if (ctx.getIntent().getExtras() != null) {
            bUpdate = ctx.getIntent().getExtras().getBoolean(Constant.KEY_PARAM_BOOLEAN);
        }
        if (bUpdate) {
            ctx.myTitle("修改字段");
        } else {
            ctx.myTitle("新字段");
        }


        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int paddingPx = XUtil.dip2px(15);
        linearLayout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        editText = new MaterialEditText(ctx);
        editText.setHint("字段名");
        editText.setFloatingLabelText("字段名");
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText.setFloatingLabelAlwaysShown(true);
        linearLayout.addView(editText);
        XUtil.autoKeyBoardShow(editText);
        if (bUpdate) {
            editText.setText(fields.getFields_name());
            editText.setSelection(fields.getFields_name().length());
        }
        checkBox = new CheckBox(ctx);
        checkBox.setText("不为空");
        linearLayout.addView(checkBox);


        if (fields.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue() || fields.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()
                ) {
            if (fields.getFields_id() > 0) {
                Button button = new Button(ctx);
                button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_nor));
                button.setText("选项");
                button.setTextColor(getResources().getColor(R.color.white));
                LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll_params.setMargins(0, 10, 0, 10);
                button.setLayoutParams(ll_params);
                linearLayout.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ctx, OptionsAc.class);
                        intent.putExtra(Constant.KEY_PARAM_DATA, fields);
                        startActivity(intent);
                        ctx.finish();
                    }
                });


                datas = Dbutils.getOptions(fields.getFields_id());
                if (XUtil.listNotNull(datas)) {
                    for (Options tmp : datas) {
                        RadioButton radioButton = new RadioButton(ctx);
                        radioButton.setText(tmp.getOptions_name());
                        radioButton.setEnabled(false);
                        linearLayout.addView(radioButton);
                    }
                }
            }


        }


        return linearLayout;
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
                String content = editText.getText().toString().trim();
                if (!XUtil.notEmptyOrNull(content)) {
                    XUtil.tShort("输入内容~");
                    return false;
                }


                boolean notNull = checkBox.isChecked();
                if (bUpdate) {
                    fields.setFields_name(content);
                    fields.setFields_notnull(notNull);
                    Dbutils.updateFields(fields);
                } else {
                    Fields tmp = new Fields(content, notNull, fields.getLib_id(), fields.getFields_type());
                    Dbutils.addFields(tmp);
                    if (fields.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue() || fields.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue()
                            ) {
                        int _id = Dbutils.getFieldsSaveId();
                        tmp.setFields_id(_id);
                        fields = tmp;
                    }
                }
                ctx.finish();
            } catch (Exception e) {
            }
        }
        return false;
    }
}
