package com.zncm.library.ft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.malinskiy.materialicons.Iconify;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.zncm.library.R;
import com.zncm.library.adapter.LibAdapter;
import com.zncm.library.adapter.MyViewHolder;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Lib;
import com.zncm.library.data.LocLib;
import com.zncm.library.data.Options;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ui.LocLibAc;
import com.zncm.library.utils.ApiUrils;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.XUtil;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class LocLibFt extends BaseListFt {
    private LibAdapter mAdapter;
    private Activity ctx;
    private List<LocLib> datas = null;
    MaterialSearchView searchView;

    public String query;
    boolean canLoadMore = false;

    LocLibAc mCtx;
    ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (Activity) getActivity();
        if (ctx instanceof LocLibAc) {
            mCtx = (LocLibAc) getActivity();
            if (XUtil.notEmptyOrNull(mCtx.key)) {
                query = mCtx.key;
            }
        }
        addButton.setVisibility(View.GONE);
        datas = new ArrayList<LocLib>();
        mAdapter = new LibAdapter(ctx) {
            @Override
            public void setData(final int position, MyViewHolder holder) {
                final LocLib data = datas.get(position);
                if (data == null) {
                    return;
                }
                if (XUtil.notEmptyOrNull(data.getLoc_name())) {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(data.getLoc_name());
                    holder.ivIcon.setVisibility(View.VISIBLE);
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(data.getLoc_name().substring(0, 1), mColorGenerator.getColor(data.getLoc_name()));
                    holder.ivIcon.setImageDrawable(drawable);
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                    holder.ivIcon.setVisibility(View.GONE);
                }
                if (XUtil.notEmptyOrNull(data.getLoc_name())) {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(data.getLoc_name());
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                }

                if (XUtil.notEmptyOrNull(data.getLoc_desc())) {
                    holder.tvContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setText(data.getLoc_desc());
                } else {
                    holder.tvContent.setVisibility(View.GONE);
                }


                holder.btn.setVisibility(View.VISIBLE);
                if (data.getLoc_status() == 1) {
                    holder.btn.setEnabled(false);
                    holder.btn.setText("已添加");
                } else {
                    holder.btn.setEnabled(true);
                    holder.btn.setText("添加");
                }

                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mkLib(data.getLoc_name(), data.getLoc_content());
                        Dbutils.uploadLocLib(data.getLoc_id(), 1);
                        data.setLoc_status(1);
                        datas.set(position, data);
                        mAdapter.setItems(datas);
                    }
                });
            }

            @Override
            public void OnItemLongClickListener(int position, MyViewHolder holder) {
                itemLongClick(position);
            }

            @Override
            public void OnItemClickListener(int position, MyViewHolder holder) {
                itemClick(position);
            }

        };
        mRecyclerView.setAdapter(mAdapter);


        searchView = (MaterialSearchView) ctx.findViewById(R.id.search_view);
        searchView.setHint("搜索");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
//                LocLibFt.this.query = query;
//                datas = new ArrayList<>();
//                getData();
                Intent intent = new Intent(ctx, LocLibAc.class);
                intent.putExtra("key", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });


        if (mCtx != null) {
            swipeLayout.setEnabled(false);
        }


        return view;
    }

    private void itemLongClick(int position) {
        final LocLib data = datas.get(position);
        if (data == null) {
            return ;
        }
        new MaterialDialog.Builder(ctx)
                .items(new String[]{"复制", "分享", "取消"})
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                XUtil.copyText(ctx, data.getLoc_content());
                                XUtil.tLong("已复制~");
                                break;
                            case 1:
                                XUtil.sendTo(ctx, data.getLoc_content());
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .show();
    }

    private void itemClick(int position) {
        LocLib data = datas.get(position);
        if (data == null) {
            return;
        }
        String content = data.getLoc_content();
        content = content.replaceAll("\\|\\|", "\n").replaceAll(":", "").replaceAll("\\[", "").replaceAll("\\]", "");
        content = content.replaceAll("文本", " ").replaceAll("整数", " ").replaceAll("实数", " ").replaceAll("布尔", " ").replaceAll("日期/时间", " ").replaceAll("日期", " ").replaceAll("时间", " ");
        content = content.replaceAll("图像", " ").replaceAll("评级", " ").replaceAll("单选", " ").replaceAll("多选", " ");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(data.getLoc_name()).append("\n");
        stringBuffer.append(content).append("\n");
        stringBuffer.append(data.getLoc_desc()).append("\n");
        if (XUtil.notEmptyOrNull(stringBuffer.toString())) {
            new MaterialDialog.Builder(ctx)
                    .content(stringBuffer.toString())
                    .show();
        }
    }

    public static void mkLib(String name, String content) {
        mkLib(name, null, content, Lib.libType.user.value());
    }

    public static void mkLib(String name, String content, int libType) {
        mkLib(name, null, content, libType);
    }


    public static void mkLib(String name, String libDesc, String content, int libType) {
        try {


            if (content.contains("http://")) {
                content = content.replaceAll("http://", "http@//");
            }


            if (content.contains("https://")) {
                content = content.replaceAll("https://", "https@//");
            }
//
            String arr[] = content.split("\\|\\|");
            if (arr.length > 0) {
                int lib_id = Dbutils.addLib(new Lib(name, libDesc, libType));
                for (int i = 0; i < arr.length; i++) {
                    String value = arr[i];
                    if (XUtil.notEmptyOrNull(value)) {
                        String fields_content = value;
                        int fields_type = EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue();
                        ArrayList<String> optionsStr = null;
                        if (value.contains(":")) {
                            String fields_type_str = null;
                            String fields[] = value.split(":");
                            if (fields.length > 1) {
                                fields_content = fields[0];
                                if (fields_content.contains(Constant.NET_DIV)) {
                                    fields_content = fields_content.replace(Constant.NET_DIV, ":");
                                }
                                fields_type_str = fields[1];
                                if (XUtil.notEmptyOrNull(fields_type_str)) {
                                    if (fields_type_str.contains(EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getStrName()) || fields_type_str.contains(EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getStrName())) {
                                        optionsStr = new ArrayList<String>();
                                        if (fields_type_str.contains(EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getStrName())) {
                                            fields_type = EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue();
                                        } else if (fields_type_str.contains(EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getStrName())) {
                                            fields_type = EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue();
                                        }
                                        fields_type_str = fields_type_str.replaceAll("单选", "").replaceAll("多选", "").replaceAll("\\[", "").replaceAll("\\]", "");
                                        String op[] = fields_type_str.split("\\|");
                                        if (op.length > 0) {
                                            for (int j = 0; j < op.length; j++) {
                                                optionsStr.add(op[j]);
                                            }
                                        }
                                    } else {
                                        fields_type = EnumData.FieldsTypeEnum.nameOf(fields_type_str).getValue();
                                    }
                                }
                            }
                        } else {
                            fields_content = value;
                        }
                        Fields tmp = new Fields(fields_content, false, lib_id, fields_type);
                        int fId = Dbutils.addFieldsID(tmp);
                        if (XUtil.listNotNull(optionsStr)) {
                            for (String str : optionsStr
                                    ) {
                                Options options = new Options(lib_id, fId, str);
                                Dbutils.addOptions(options);
                            }
                        }
                    }
                }
            }


            EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.LIB.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        try {
            List<LocLib> list = Dbutils.getLocLib(query, datas.size());
            if (XUtil.listNotNull(list)) {
                canLoadMore = true;
                datas.addAll(list);
            } else {
                canLoadMore = false;
                if (XUtil.notEmptyOrNull(query)) {
                    XUtil.tShort("无结果~~");
                }
            }
            mAdapter.setItems(datas);
            onLoadMoreComplete();
            errorView.setVisibility(View.GONE);
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
        if (mCtx == null) {
            menu.add("md_search").setIcon(XUtil.initIconWhite(Iconify.IconValue.md_search)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item == null || item.getTitle() == null) {
            return false;
        }
        if (item.getTitle().equals("md_search")) {
            searchView.showSearch(true);
        }
        return false;
    }

    @Override
    public void onRefresh() {

        query = "";
        if (onLoading) {
            return;
        }
        refresh();
    }

    private void refresh() {
        datas = new ArrayList<>();
        onLoading = true;
        swipeLayout.setRefreshing(true);
        getData();
    }


    @Override
    public void onLoadMore() {
        getData();
    }


}
