package com.zncm.library.ft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zncm.library.adapter.LibAdapter;
import com.zncm.library.adapter.MyViewHolder;
import com.zncm.library.data.Constant;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Lib;
import com.zncm.library.data.Options;
import com.zncm.library.ui.BaseAc;
import com.zncm.library.ui.ItemsAc;
import com.zncm.library.ui.LibAddAc;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.XUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tr.xip.errorview.RetryListener;


public class OptionsFt extends BaseListFt {
    private LibAdapter mAdapter;
    private BaseAc ctx;
    private List<Options> datas = null;
    Fields fields;
    boolean bUpdate = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (BaseAc) getActivity();

        Serializable dataParam = ctx.getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA);
        if (dataParam != null && dataParam instanceof Fields) {
            fields = (Fields) dataParam;
        }

        ctx.myTitle(fields.getFields_name() + " 选项");


        addButton.setVisibility(View.VISIBLE);
        datas = new ArrayList<Options>();
        mAdapter = new LibAdapter(ctx) {
            @Override
            public void setData(final int position, MyViewHolder holder) {
                final Options data = datas.get(position);
                if (XUtil.notEmptyOrNull(data.getOptions_name())) {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(data.getOptions_name());
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                }
                holder.tvContent.setVisibility(View.GONE);
            }

            @Override
            public void OnItemLongClickListener(int position, MyViewHolder holder) {

            }

            @Override
            public void OnItemClickListener(int position, MyViewHolder holder) {
                itemClick(position);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOp();
            }
        });
        return view;
    }
    private void itemClick(int position) {
        final Options data = datas.get(position);
        if (data == null) {
            return;
        }
        new MaterialDialog.Builder(ctx)
                .items(new String[]{"编辑", "删除", "取消"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                bUpdate = true;
                                initEtDlg(data);
                                break;
                            case 1:
                                new MaterialDialog.Builder(ctx)
                                        .title("删除")
                                        .content("确认删除"+ data.getOptions_name())
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
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .show();
    }
    private void addOp() {
        bUpdate = false;
        initEtDlg(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        try {
            datas = Dbutils.getOptions(fields.getFields_id());
            mAdapter.setItems(datas);
            onLoadMoreComplete();

            if (!XUtil.listNotNull(datas)) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setErrorImageDrawable(null);
                errorView.setErrorTitle("");
                errorView.setErrorSubtitle("请为字段添选项");
                errorView.setRetryButtonText("添加");
                errorView.setOnRetryListener(new RetryListener() {
                    @Override
                    public void onRetry() {
                        addOp();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }


    @Override
    public void onRefresh2() {
        if (onLoading) {
            return;
        }
        refresh();
    }

    private void refresh() {
        onLoading = true;
        getData();
    }


    @Override
    public void onLoadMore2() {
        getData();
    }

    void initEtDlg(final Options options) {
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
            editText.setText(options.getOptions_name());
            editText.setSelection(options.getOptions_name().length());
        }
        view.addView(editText);
        MaterialDialog md = new MaterialDialog.Builder(ctx)
                .customView(view,true)
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
                                options.setOptions_name(content);
                                Dbutils.updateOptions(options);
                            } else {
                                Options tmp = new Options(fields.getLib_id(), fields.getFields_id(), content);
                                Dbutils.addOptions(tmp);
                            }
                            getData();

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
