package com.zncm.library.ft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.malinskiy.materialicons.Iconify;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;
import com.zncm.library.R;
import com.zncm.library.adapter.LibAdapter;
import com.zncm.library.data.Constant;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.Options;
import com.zncm.library.ui.ItemsAc;
import com.zncm.library.ui.ItemsAddAc;
import com.zncm.library.ui.ItemsDetailsAc;
import com.zncm.library.ui.LibAc;
import com.zncm.library.ui.LibAddAc;
import com.zncm.library.ui.LocLibAc;
import com.zncm.library.ui.PhotoAc;
import com.zncm.library.ui.ShareAc;
import com.zncm.library.ui.WebViewActivity;
import com.zncm.library.utils.ApiUrils;
import com.zncm.library.utils.CSVUtils;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.DoubleClickImp;
import com.zncm.library.utils.XUtil;
import com.zncm.library.utils.saxrssreader.RssFeed;
import com.zncm.library.utils.saxrssreader.RssItem;
import com.zncm.library.utils.saxrssreader.RssReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import tr.xip.errorview.RetryListener;


public class ItemsFt extends BaseListFt {
    private LibAdapter mAdapter;
    private ItemsAc ctx;
    private List<Items> datas = null;
    private Lib lib;
    private List<Fields> fieldsList = null;
    ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    boolean canLoadMore = false;
    MaterialSearchView searchView;

    public String query;
    ItemsAc mCtx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (ItemsAc) getActivity();


        if (ctx instanceof ItemsAc) {
            mCtx = (ItemsAc) getActivity();
            if (XUtil.notEmptyOrNull(mCtx.key)) {
                query = mCtx.key;
            }
        }

        Serializable dataParam = ctx.getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA);
        if (dataParam != null && dataParam instanceof Lib) {
            lib = (Lib) dataParam;
            if (XUtil.notEmptyOrNull(lib.getLib_name())) {
                ctx.myTitle(lib.getLib_name());
            }
        }

        addButton.setVisibility(View.VISIBLE);
        datas = new ArrayList<Items>();

        fieldsList = Dbutils.getFields(lib.getLib_id());
        mAdapter = new LibAdapter(ctx) {
            @Override
            public void setData(final int position, final MyViewHolder holder) {
                final Items data = datas.get(position);
                String title = null;
                String content = null;
                String picUrl = null;


                if (XUtil.notEmptyOrNull(data.getItem_json())) {
                    Map<String, Object> map = JSON.parseObject(data.getItem_json());
                    int j = 0;
                    for (int i = 0; i < fieldsList.size(); i++) {
                        Fields tmp = fieldsList.get(i);
                        if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue()) {
                            String obj = (String) map.get(String.valueOf(tmp.getFields_id()));
                            if (XUtil.isPic(obj)) {
                                picUrl = obj;
                            } else {
                                if (XUtil.notEmptyOrNull(obj)) {
                                    title = obj;
                                    j = i;
                                    break;
                                }
                            }

                        }
                    }
                    for (int i = j + 1; i < fieldsList.size(); i++) {
                        Fields tmp = fieldsList.get(i);
                        if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue()) {
                            String obj = (String) map.get(String.valueOf(tmp.getFields_id()));
                            if (XUtil.isPic(obj) && !XUtil.notEmptyOrNull(picUrl)) {
                                picUrl = obj;
                            } else {
                                if (XUtil.notEmptyOrNull(obj)) {
                                    content = obj;
                                    break;
                                }
                            }
                        }
                    }

                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                    holder.tvContent.setVisibility(View.GONE);
                }
                if (XUtil.notEmptyOrNull(picUrl)) {
                    holder.ivIcon.setVisibility(View.VISIBLE);
                    MyApplication.imageLoader.displayImage(picUrl,
                            holder.ivIcon, new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {
                                    holder.ivIcon.setImageResource(R.drawable.ic_lib);
                                    super.onLoadingStarted(imageUri, view);
                                }
                            });
                    final String finalPicUrl = picUrl;
                    holder.ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ctx, PhotoAc.class);
                            intent.putExtra("url", finalPicUrl);
                            startActivity(intent);
                        }
                    });
                } else if (XUtil.notEmptyOrNull(title)) {
                    holder.ivIcon.setVisibility(View.VISIBLE);
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(title.substring(0, 1), mColorGenerator.getColor(title.substring(0, 1)));
                    holder.ivIcon.setImageDrawable(drawable);
                } else {
                    holder.ivIcon.setVisibility(View.GONE);
                }


                if (XUtil.notEmptyOrNull(title)) {
                    holder.tvTitle.setVisibility(View.VISIBLE);
                    holder.tvTitle.setText(title.trim());
                } else {
                    holder.tvTitle.setVisibility(View.GONE);
                }


                if (XUtil.notEmptyOrNull(content)) {
                    content = XUtil.subStr(content, 30);
                    holder.tvContent.setVisibility(View.VISIBLE);
                    if (lib.getLib_exi1() == Lib.libType.rss.value()) {
                        content = XUtil.replaceImageURLs(content);
                        holder.tvContent.setText(XUtil.stripHtml(content.trim()));
                    } else {
                        holder.tvContent.setText(XUtil.stripHtml(content.trim()));
//                        holder.tvContent.setText(content.trim());
                    }
                } else {
                    holder.tvContent.setVisibility(View.GONE);
                }


            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                            @SuppressWarnings({"rawtypes", "unchecked"})
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                int curPosition = position - listView.getHeaderViewsCount();
                                                if (XUtil.notEmptyOrNull(query)) {
                                                    Items data = datas.get(curPosition);
                                                    if (data == null) {
                                                        return;
                                                    }


                                                    Intent intent = new Intent(ctx, ItemsDetailsAc.class);
                                                    intent.putExtra(Constant.KEY_PARAM_DATA, lib);
                                                    intent.putExtra("item", data);
                                                    intent.putExtra("size", -1);
                                                    intent.putExtra(Constant.KEY_CURRENT_POSITION, curPosition);
                                                    startActivity(intent);

                                                } else {
                                                    Intent intent = new Intent(ctx, ItemsDetailsAc.class);
                                                    intent.putExtra(Constant.KEY_PARAM_DATA, lib);
                                                    intent.putExtra("size", datas.size());
                                                    intent.putExtra(Constant.KEY_CURRENT_POSITION, curPosition);
                                                    startActivity(intent);
                                                }

                                            }
                                        }

        );


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                int curPosition = position - listView.getHeaderViewsCount();
                final Items data = datas.get(curPosition);
                if (data == null) {
                    return true;
                }
                new MaterialDialog.Builder(ctx)
                        .items(new String[]{"编辑", "删除", "取消"})
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(ctx, ItemsAddAc.class);
                                        intent.putExtra(Constant.KEY_PARAM_DATA, lib);
                                        intent.putExtra(Constant.KEY_PARAM_DATA2, data);
                                        intent.putExtra(Constant.KEY_PARAM_BOOLEAN, true);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        new MaterialDialog.Builder(ctx)
                                                .title("删除")
                                                .content("确认删除" + data.getItem_json())
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
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        })
                        .show();
                return true;
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        getData();


        searchView = (MaterialSearchView) ctx.findViewById(R.id.search_view);
        searchView.setHint("搜索");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                if (XUtil.notEmptyOrNull(query)) {
                    if (query.startsWith("@")) {
                        String intStr = query.replace("@", "");
                        int page = 0;
                        try {
                            page = Integer.parseInt(intStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(ctx, ItemsDetailsAc.class);
                        intent.putExtra(Constant.KEY_PARAM_DATA, lib);
                        intent.putExtra("size", page);
                        intent.putExtra(Constant.KEY_CURRENT_POSITION, page);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ctx, ItemsAc.class);
                        intent.putExtra("key", query);
                        intent.putExtra(Constant.KEY_PARAM_DATA, lib);
                        startActivity(intent);
                    }


                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        return view;
    }

    private void addItem() {
        Intent intent = new Intent(ctx, ItemsAddAc.class);
        intent.putExtra(Constant.KEY_PARAM_DATA, lib);
        startActivity(intent);
    }

//
//    @Override
//    public void onResume() {
//        super.onResume();
//      refresh();
//    }

    private void getData() {
        try {
            List<Items> list = Dbutils.getItemsPage(query, lib.getLib_id(), datas.size());
            if (XUtil.listNotNull(list)) {
                canLoadMore = (list.size() == Constant.MAX_DB_QUERY);
                datas.addAll(list);
            } else {
                if (XUtil.notEmptyOrNull(query)) {
                    XUtil.tShort("无结果~~");
                }

                canLoadMore = false;
            }
            mAdapter.setItems(datas);
            listView.setCanLoadMore(canLoadMore);
            onLoadMoreComplete();
            if (!XUtil.listNotNull(datas)) {
                errorView.setVisibility(View.VISIBLE);
                errorView.setErrorImageDrawable(null);
                errorView.setErrorTitle("");
                errorView.setErrorSubtitle("没有记录");
                errorView.setRetryButtonText("添加");
                errorView.setOnRetryListener(new RetryListener() {
                    @Override
                    public void onRetry() {
                        addItem();
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
        query = "";
        if (onLoading) {
            return;
        }
        refresh();
    }

    private void refresh() {
        onLoading = true;
        swipeLayout.setRefreshing(true);
        datas = new ArrayList<>();


        getData();
    }

    private void getRssInfo() {

        try {
            URL url = new URL(lib.getLib_exs1());
            RssFeed feed = RssReader.read(url);
            ArrayList<RssItem> rssItems = feed.getRssItems();
            ArrayList<String> list;
            for (RssItem rssItem : rssItems) {
                list = new ArrayList<>();
                list.add(rssItem.getTitle());
                list.add(XUtil.getDateFull(rssItem.getPubDate()));
                list.add(rssItem.getLink());
                ShareAc.saveDataList(lib.getLib_id(), list);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    @Override
    public void onLoadMore() {
        getData();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        if (lib.getLib_exi1() == Lib.libType.rss.value() || lib.getLib_exi1() == Lib.libType.net.value()) {
            menu.add("md_refresh").setIcon(XUtil.initIconWhite(Iconify.IconValue.md_refresh)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        SubMenu sub = menu.addSubMenu("");
        sub.setIcon(XUtil.initIconWhite(Iconify.IconValue.md_more_vert));
        sub.add(0, 1, 0, "搜索");
        sub.add(0, 2, 0, "编辑库");
        sub.add(0, 3, 0, "导出CSV");
        sub.add(0, 4, 0, "分享库结构");
        sub.add(0, 5, 0, "分享库结构");
        sub.add(0, 6, 0, "发布库");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item == null || item.getTitle() == null) {
            return false;
        }

        if (item.getTitle().equals("md_refresh")) {
            if (lib.getLib_exi1() == Lib.libType.rss.value()) {
                if (!XUtil.notEmptyOrNull(lib.getLib_exs1())) {
                    XUtil.tShort("Rss源地址有误~");
                }
                XUtil.tShort("正在更新Rss源...");
                Tasks.executeInBackground(ctx, new BackgroundWork<Boolean>() {
                    @Override
                    public Boolean doInBackground() throws Exception {
                        getRssInfo();
                        return false;
                    }
                }, new Completion<Boolean>() {
                    @Override
                    public void onSuccess(Context context, Boolean result) {
                        getData();
                    }

                    @Override
                    public void onError(Context context, Exception e) {
                    }
                });
            }
            if (lib.getLib_exi1() == Lib.libType.net.value()) {
                XUtil.tShort("更新数据中，请耐心等待...");
                ApiUrils.getApiInfo(lib.getLib_name());
            }
        }

        switch (item.getItemId()) {
            case 1:
                searchView.showSearch(true);
                break;
            case 2:
                if (lib.getLib_exi1() == Lib.libType.user.value()
                        || lib.getLib_exi1() == Lib.libType.rss.value()) {
                    Intent intent = new Intent(ctx, LibAddAc.class);
                    intent.putExtra(Constant.KEY_PARAM_BOOLEAN, true);
                    intent.putExtra(Constant.KEY_PARAM_DATA, lib);
                    startActivity(intent);
                } else {
                    XUtil.tShort("系统库不允许编辑~");
                }
                break;
            case 3:
                CSVUtils.outCsvUtils(ctx, lib);
                break;

            case 4:
                String ret = libInfo();
                XUtil.sendTo(ctx, ret);
                break;
            case 5:
                ret = libInfo();
                XUtil.copyText(ctx, ret);
                XUtil.tLong("库结构已复制~");
                break;
            case 6:
                ret = libInfo();
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + Constant.QQ_LIB_SHARE;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                XUtil.copyText(ctx, ret);
                XUtil.tLong("已复制，请粘贴到对话框~");
                break;
        }


        return false;
    }

    @NonNull
    private String libInfo() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(lib.getLib_name()).append("|||");
        if (XUtil.listNotNull(fieldsList)) {
            for (Fields tmp : fieldsList
                    ) {
                if (XUtil.notEmptyOrNull(tmp.getFields_name())) {
                    if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_MANY.getValue() || tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_OPTIONS_ONE.getValue()) {
                        List<Options> datas = Dbutils.getOptions(tmp.getFields_id());
                        if (XUtil.listNotNull(datas)) {
                            String opStr = "";
                            for (Options op : datas) {
                                if (XUtil.notEmptyOrNull(op.getOptions_name())) {
                                    opStr = opStr + op.getOptions_name() + "|";
                                }
                            }
                            stringBuffer.append(tmp.getFields_name()).append(":").append(EnumData.FieldsTypeEnum.valueOf(tmp.getFields_type()).getStrName()).append("[").append(opStr).append("]").append("||");
                        }
                    } else {
                        stringBuffer.append(tmp.getFields_name()).append(":").append(EnumData.FieldsTypeEnum.valueOf(tmp.getFields_type()).getStrName()).append("||");
                    }
                }
            }
        }
        return stringBuffer.toString();
    }


}
