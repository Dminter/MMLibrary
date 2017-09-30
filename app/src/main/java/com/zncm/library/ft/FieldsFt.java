package com.zncm.library.ft;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zncm.library.adapter.LibAdapter;
import com.zncm.library.adapter.MyViewHolder;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Lib;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ui.FieldsAddAc;
import com.zncm.library.ui.LibAddAc;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.XUtil;

import de.greenrobot.event.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tr.xip.errorview.RetryListener;


public class FieldsFt extends BaseListFt {
    private LibAdapter mAdapter;
    private LibAddAc ctx;
    private List<Fields> datas = null;
    private Lib lib;
    private boolean bUpdate = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (LibAddAc) getActivity();
        addButton.setVisibility(View.GONE);
        datas = new ArrayList<Fields>();
        mAdapter = new LibAdapter(ctx) {
            @Override
            public void setData(final int position, MyViewHolder holder) {
                final Fields data = datas.get(position);

                if (XUtil.notEmptyOrNull(data.getFields_name())) {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(data.getFields_name());
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                }
                if (data.getFields_type() >= 0 && data.getFields_type() <= 10) {
                    holder.tvContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setText(EnumData.FieldsTypeEnum.valueOf(data.getFields_type()).getStrName());
                } else {
                    holder.tvContent.setVisibility(View.GONE);
                }
                holder.operate.setVisibility(View.VISIBLE);
                holder.operate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        opDlg(data);
                    }
                });

            }

            @Override
            public void OnItemLongClickListener(int position, MyViewHolder holder) {
                final Fields data = datas.get(position);
                opDlg(data);
            }

            @Override
            public void OnItemClickListener(int position, MyViewHolder holder) {
//                final Fields data = datas.get(position);
//                if (data == null) {
//                    return;
//                }
//                opDlg(data);
            }
        };
        mRecyclerView.setAdapter(mAdapter);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFieldType();
            }
        });


        Serializable dataParam = ctx.getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA);
        if (dataParam != null && dataParam instanceof Lib) {
            lib = (Lib) dataParam;
        }
        if (ctx.getIntent().getExtras() != null) {
            bUpdate = ctx.getIntent().getExtras().getBoolean(Constant.KEY_PARAM_BOOLEAN);
        }
        if (bUpdate) {
            ctx.myTitle("修改 " + lib.getLib_name());
            getData();
        } else {
            ctx.myTitle("新库");
            initEtDlg();
        }
        return view;
    }


    private void opDlg(final Fields data) {
        new MaterialDialog.Builder(ctx)
                .items(new String[]{"编辑", "删除", "上移", "下移", "取消"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(ctx, FieldsAddAc.class);
                                intent.putExtra(Constant.KEY_PARAM_DATA, data);
                                intent.putExtra(Constant.KEY_PARAM_BOOLEAN, true);
                                startActivity(intent);
                                break;
                            case 1:
                                new MaterialDialog.Builder(ctx)
                                        .title("删除")
                                        .content("确认删除" + data.getFields_name())
                                        .positiveText("删除")
                                        .negativeText("取消")
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                super.onPositive(dialog);
                                                Dbutils.deleteData(data);
                                                datas.remove(data);
                                                mAdapter.setItems(datas);
                                            }
                                        }).show();
                                break;
                            case 2:
                                data.setFields_exi1(data.getFields_exi1() + 1);
                                Dbutils.updateFields(data);
                                getData();
                                break;
                            case 3:
                                data.setFields_exi1(data.getFields_exi1() - 1);
                                Dbutils.updateFields(data);
                                getData();
                                break;
                            case 4:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .show();
    }

    private void initFieldType() {
        new MaterialDialog.Builder(ctx)
                .title("字段类型")
                .items(new String[]{EnumData.FieldsTypeEnum.FIELDS_TEXT.getStrName(), EnumData.FieldsTypeEnum.FIELDS_INT.getStrName(),
                        EnumData.FieldsTypeEnum.FIELDS_DOUBLE.getStrName(), EnumData.FieldsTypeEnum.FIELDS_BOOLEAN.getStrName(),
                        EnumData.FieldsTypeEnum.FIELDS_DATETIME.getStrName(), EnumData.FieldsTypeEnum.FIELDS_DATE.getStrName()
                        , EnumData.FieldsTypeEnum.FIELDS_TIME.getStrName()
                        , EnumData.FieldsTypeEnum.FIELDS_PICTURE.getStrName()
                        , EnumData.FieldsTypeEnum.FIELDS_START.getStrName()
                        , EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getStrName()
                        , EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getStrName()
                })
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                        initFieldsEtDlg(null, false, which);
//                        XUtil.debug("text==>" + text + "  " + which);
                        int fields_type = EnumData.FieldsTypeEnum.nameOf(text.toString()).getValue();
                        Fields tmp = new Fields(null, false, lib.getLib_id(), which);
                        Intent intent = new Intent(ctx, FieldsAddAc.class);
                        intent.putExtra(Constant.KEY_PARAM_DATA, tmp);
                        intent.putExtra(Constant.KEY_PARAM_BOOLEAN, false);
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void getData() {
        try {
            datas = Dbutils.getFields(lib.getLib_id());
            mAdapter.setItems(datas);
            onLoadMoreComplete();
            if (!XUtil.listNotNull(datas)) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setErrorImageDrawable(null);
                errorView.setErrorTitle("");
                errorView.setErrorSubtitle("请为库添加字段");
                errorView.setRetryButtonText("添加");
                errorView.setOnRetryListener(new RetryListener() {
                    @Override
                    public void onRetry() {
                        initFieldType();
                    }
                });
            } else {
                errorView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRefresh() {
        if (onLoading) {
            return;
        }
        refresh();
    }

    private void refresh() {
        onLoading = true;
        swipeLayout.setRefreshing(true);
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lib != null) {
            getData();
        }
    }

    @Override
    public void onLoadMore() {
        getData();
    }


    void initEtDlg() {
        LinearLayout view = new LinearLayout(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        final EditText editText = new EditText(ctx);
        final MaterialEditText editText = new MaterialEditText(ctx);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setFloatingLabelText("库名");
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText.setFloatingLabelAlwaysShown(true);
        XUtil.autoKeyBoardShow(editText);
        if (bUpdate) {
            editText.setText(lib.getLib_name());
            editText.setSelection(lib.getLib_name().length());
        }
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
                            if (bUpdate) {
                                lib.setLib_name(content);
                                Dbutils.updateLib(lib);
                                ctx.myTitle(content);
                            } else {
                                lib = new Lib(content);
                                lib.setLib_id(Dbutils.addLib(lib));
                                ctx.myTitle(content);
                                getData();
                            }


                            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LIB.getValue()));

                        } catch (Exception e) {
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog materialDialog) {

                    }

                    @Override
                    public void onNegative(MaterialDialog materialDialog) {
                        ctx.finish();
                        XUtil.dismissShowDialog(materialDialog, true);
                    }
                })
                .autoDismiss(false)
                .build();
        md.setCancelable(false);
        md.show();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (bUpdate) {
            menu.add("md_edit").setTitle("修改库名").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        menu.add("md_fields").setTitle("添加字段").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item == null || item.getTitle() == null) {
            return false;
        }

        if (item.getTitle().equals("修改库名")) {
            initEtDlg();
        } else if (item.getTitle().equals("添加字段")) {
            initFieldType();
        }
        return false;
    }


}
