package com.zncm.library.ft;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.malinskiy.materialicons.Iconify;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zf.zson.ZSON;
import com.zf.zson.result.ZsonResult;
import com.zncm.library.R;
import com.zncm.library.adapter.LibAdapter;
import com.zncm.library.adapter.MyViewHolder;
import com.zncm.library.data.Constant;
import com.zncm.library.data.DetailInfo;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.Options;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.data.SpConstant;
import com.zncm.library.ui.InfoDetailsActivity;
import com.zncm.library.ui.ItemsAc;
import com.zncm.library.ui.ItemsAddAc;
import com.zncm.library.ui.ItemsDetailsAc;
import com.zncm.library.ui.LibAddAc;
import com.zncm.library.ui.PhotoAc;
import com.zncm.library.ui.ShareAc;
import com.zncm.library.ui.WebViewActivity;
import com.zncm.library.utils.ApiUrils;
import com.zncm.library.utils.CSVUtils;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.ImageUtil;
import com.zncm.library.utils.MyPath;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.MyStringRequest;
import com.zncm.library.utils.XUtil;
import com.zncm.library.utils.saxrssreader.RssFeed;
import com.zncm.library.utils.saxrssreader.RssItem;
import com.zncm.library.utils.saxrssreader.RssReader;

import de.greenrobot.event.EventBus;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        int lib_id = ctx.getIntent().getIntExtra("lib_id", 0);
        if (lib_id != 0) {
            lib = Dbutils.findLib(lib_id);
        } else {
            if (dataParam != null && dataParam instanceof Lib) {
                lib = (Lib) dataParam;

            }

        }
        if (lib != null && XUtil.notEmptyOrNull(lib.getLib_name())) {
            ctx.libId = lib.getLib_id();
            ctx.myTitle(lib.getLib_name());
        }
        addButton.setVisibility(View.VISIBLE);
        datas = new ArrayList<Items>();

        fieldsList = Dbutils.getFields(lib.getLib_id());
        mAdapter = new LibAdapter(ctx) {
            @Override
            public void setData(final int position, final MyViewHolder holder) {
                final Items data = datas.get(position);
                if (data == null) {
                    return;
                }
                String title = null;
                String content = null;
                String picUrl = null;


                if (data.isItem_exb1()) {
                    holder.tvIcon.setVisibility(View.GONE);
                } else {
                    holder.tvIcon.setVisibility(View.VISIBLE);
                }
//                if (data.isItem_exb1()) {
//                    holder.rlBg.setBackgroundResource(R.drawable.card_dark);
//                } else {
//                    holder.rlBg.setBackgroundResource(R.drawable.card);
//                }

                if (XUtil.notEmptyOrNull(data.getItem_json())) {
                    Map<String, Object> map = new HashMap<>();
                    try {
                        map = JSON.parseObject(data.getItem_json());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private void itemClick(int position) {
        Items item = datas.get(position);
        item.setItem_exb1(true);
        datas.set(position, item);
        mAdapter.setItems(datas);


        if (XUtil.notEmptyOrNull(query)) {
            Items data = datas.get(position);
            if (data == null) {
                return;
            }

            Intent intent = new Intent(ctx, ItemsDetailsAc.class);
            intent.putExtra(Constant.KEY_PARAM_DATA, lib);
            intent.putExtra("item", data);
            intent.putExtra("size", -1);
            intent.putExtra(Constant.KEY_CURRENT_POSITION, position);
            startActivity(intent);

        } else {
            Items tmp = datas.get(position);
            boolean flag = false;
            if (MySp.get(SpConstant.isOpenUrl, Boolean.class, false)) {
                if (lib.getLib_exi1() == Lib.libType.rss.value() || lib.getLib_exi1() == Lib.libType.net.value()
                        || lib.getLib_exi1() == Lib.libType.api.value()) {
                    if (XUtil.notEmptyOrNull(tmp.getItem_exs1())) {
                        flag = true;
                    }
                }
            }
            if (flag) {
                /**
                 *已读
                 */
                Dbutils.readItems(tmp.getItem_id(), true);
                if (lib.isLib_exb2()) {
                    Intent intent = new Intent(ctx, InfoDetailsActivity.class);
                    intent.putExtra(Constant.KEY_PARAM_DATA, new DetailInfo(tmp.getItem_exs1(), "img", XUtil.getChinese(tmp.getItem_json())));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ctx, WebViewActivity.class);
                    intent.putExtra("url", tmp.getItem_exs1());
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(ctx, ItemsDetailsAc.class);
                intent.putExtra(Constant.KEY_PARAM_DATA, lib);
                intent.putExtra("size", datas.size());
                intent.putExtra(Constant.KEY_CURRENT_POSITION, position);
                startActivity(intent);
            }


        }
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
            //UNDONE
//            mRecyclerView.setCanLoadMore(canLoadMore);
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
            if (XUtil.listNotNull(rssItems)) {
                Collections.reverse(rssItems);
                for (RssItem rssItem : rssItems) {
                    list = new ArrayList<>();
                    list.add(rssItem.getTitle());
                    list.add(XUtil.getDateFull(rssItem.getPubDate()));
                    list.add(rssItem.getLink());
                    Items items = new Items();
                    //item_exs1
                    items.setItem_exs1(rssItem.getLink());
                    ShareAc.saveDataList(lib.getLib_id(), list, items);
                }
            }
            list = new ArrayList<>();
            list.add(XUtil.getDateFullSec());
            ShareAc.saveDataList(lib.getLib_id(), list);


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void getApiByQuery(final Lib lib) {
        try {
            RequestQueue mVolleyQueue = Volley.newRequestQueue(ctx);
            final List<Fields> datas = Dbutils.getFields(lib.getLib_id());
            String baseUrl = "";
            for (int i = 0; i < datas.size(); i++) {
                Fields tmp = datas.get(i);
                if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue()) {
                    String name = tmp.getFields_name();
                    if (i == 0) {
                        baseUrl = name;
                    }

                }
            }
            if (XUtil.isEmptyOrNull(baseUrl)) {
                XUtil.tShort("检查URL");
                return;
            }
            MyStringRequest myStringRequest = new MyStringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String ret = response.toString();
                        String item = "";
                        String _url = "";
                        Map<Integer, String> map = new HashMap<Integer, String>();
                        Map<Integer, ArrayList<String>> mapOut = new HashMap<Integer, ArrayList<String>>();
                        for (int i = 0; i < datas.size(); i++) {
                            Fields tmp = datas.get(i);
                            if (tmp.getFields_type() == EnumData.FieldsTypeEnum.FIELDS_TEXT.getValue()) {
                                int _id = tmp.getFields_id();
                                String name = tmp.getFields_name();
                                if (i == 0) {
                                } else if (i == 1) {
                                    _url = name;
                                } else {
                                    map.put(_id, name);
                                }
                            }
                        }
                        try {
                            org.json.JSONObject obj = new org.json.JSONObject(ret);
                            if (XUtil.isEmptyOrNull(_url)) {
                                return;
                            }
                            if (_url.startsWith("zson ")) {
                                String path = _url.substring("zson ".length(), _url.length());
                                ZsonResult zr = ZSON.parseJson(ret);
                                List<Object> items = zr.getValues("//" + path);
                                String listStr = items.toString();
                                listStr = listStr.substring(1, listStr.length() - 1);
                                item = listStr;
                            } else {
                                if (_url.contains("/")) {
                                    String arr[] = _url.split("\\/");
                                    if (arr.length == 5) {
                                        item = obj.getJSONObject(arr[0]).getJSONObject(arr[1]).getJSONObject(arr[2]).getJSONObject(arr[3]).getString(arr[4]);
                                    } else if (arr.length == 4) {
                                        item = obj.getJSONObject(arr[0]).getJSONObject(arr[1]).getJSONObject(arr[2]).getString(arr[3]);
                                    } else if (arr.length == 3) {
                                        item = obj.getJSONObject(arr[0]).getJSONObject(arr[1]).getString(arr[2]);
                                    } else if (arr.length == 2) {
                                        item = obj.getJSONObject(arr[0]).getString(arr[1]);
                                    }

                                } else {
                                    item = obj.getString(_url);
                                }

                            }


                        } catch (Exception e) {
                            item = ret;
                            e.printStackTrace();
                        }
                        JSONArray list1 = JSON.parseArray(item);
                        if (XUtil.listNotNull(list1)) {
                            Collections.reverse(list1);
                        }
                        for (int i = 0; i < list1.size(); i++) {
                            Map<String, Object> tmpMap = new HashMap<>();
                            String link = "";
                            for (Integer in : map.keySet()) {
                                String value = map.get(in);
                                boolean isUrl = false;
                                if (XUtil.notEmptyOrNull(value) && value.startsWith("@")) {
                                    value = value.substring(1);
                                    isUrl = true;
                                }
                                String urlPre = "";
                                boolean isUrlStr = false;
                                if (XUtil.notEmptyOrNull(value) && value.contains("{") && value.contains("}")) {
                                    /**
                                     *匹配整个网址串  https://xxx.com/thread?id={}
                                     */
                                    isUrlStr = true;
                                    urlPre = value;
                                    value = urlPre.substring(urlPre.indexOf("{") + 1, urlPre.indexOf("}"));
                                }
                                String tmp = list1.getJSONObject(i).get(value) + "";
                                if (isUrlStr && XUtil.notEmptyOrNull(urlPre) & XUtil.notEmptyOrNull(tmp)) {
                                    tmp = urlPre.replace("{" + value + "}", tmp);
                                }
                                if (isUrl) {
                                    link = tmp;
                                } else {
                                    link = "";
                                }
                                tmpMap.put(in + "", tmp);
                            }
                            JSON json = new JSONObject(tmpMap);
                            if (map.size() > 0) {
                                Items items = new Items(lib.getLib_id(), json.toJSONString());
                                if (XUtil.notEmptyOrNull(link)) {
                                    items.setItem_exs1(link);
                                }
                                Dbutils.addItems(items);
                            }

                        }
                        ArrayList list = new ArrayList<>();
                        list.add(XUtil.getDateFullSec());
                        ShareAc.saveDataList(lib.getLib_id(), list);
                        EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.ITEMS.getValue()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            myStringRequest.getHeaders().put("apikey", "1e08b47bc5fc83bccc9b6bfb3b4cf1df");
            mVolleyQueue.add(myStringRequest);
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


        boolean isSys = false;

        if (lib.getLib_exi1() == Lib.libType.rss.value() || lib.getLib_exi1() == Lib.libType.net.value() || lib.getLib_exi1() == Lib.libType.api.value()
                || lib.getLib_name().startsWith(Constant.PRE_LIB)
                ) {
            isSys = true;
            menu.add("md_refresh").setIcon(XUtil.initIconWhite(Iconify.IconValue.md_refresh)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        SubMenu sub = menu.addSubMenu("");
        sub.setIcon(XUtil.initIconWhite(Iconify.IconValue.md_more_vert));
        sub.add(0, 1, 0, "搜索");
        sub.add(0, 2, 0, "编辑库");
        sub.add(0, 3, 0, "导出CSV");
        sub.add(0, 4, 0, "分享库结构");
        sub.add(0, 5, 0, "复制库结构");
        sub.add(0, 10, 0, "克隆库结构");
        sub.add(0, 6, 0, "发布库");
        sub.add(0, 8, 0, "二维码分享");
        if (isSys) {
            sub.add(0, 7, 0, "页码");
            if (lib.isLib_exb2()) {
                sub.add(0, 9, 0, "非网页详情页");
            } else {
                sub.add(0, 9, 0, "网页详情页");
            }
        }
        sub.add(0, 11, 0, "发送到桌面");
        sub.add(0, 12, 0, "清除库内容");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    void initEtDlg() {
        LinearLayout view = new LinearLayout(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        final EditText editText = new EditText(ctx);
        final MaterialEditText editText = new MaterialEditText(ctx);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.setFloatingLabelText("页码");
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        editText.setFloatingLabelAlwaysShown(true);
        XUtil.autoKeyBoardShow(editText);
        editText.setText(lib.getLib_exi2() + "");
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
                            lib.setLib_exi2(Integer.parseInt(content));
                            Dbutils.updateLib(lib);
//                            ctx.myTitle(content);
                        } catch (Exception e) {
                            e.printStackTrace();
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
                .build();
        md.setCancelable(false);
        md.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item == null || item.getTitle() == null) {
            return false;
        }

        if (item.getTitle().equals("md_refresh")) {
            refreshInfo();

        }

        switch (item.getItemId()) {
            case 1:
                searchView.showSearch(true);
                break;
            case 2:
                if (lib.getLib_exi1() != Lib.libType.sys.value()) {
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

            case 7:
                initEtDlg();
                break;

            case 8:
                String textContent = libInfo();


//                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(lib.getLib_name(), mColorGenerator.getColor(lib.getLib_name()));

                Bitmap logo = drawableToBitamp(drawable);
                Bitmap mBitmap = CodeUtils.createImage(textContent, 400, 400, logo);
//                imageView.setImageBitmap(mBitmap);
                String imgPath = MyPath.getPathImg() + "/qr_" + String.valueOf(System.currentTimeMillis()) + ".png";
                ImageUtil.saveMyBitmap(mBitmap, imgPath);
//                XUtil.tShort("二维码已生成至" + imgPath);
                XUtil.refreshGallery(ctx, imgPath);
                XUtil.shareImg(ctx, imgPath);
                break;

            case 9:
                lib.setLib_exb2(!lib.isLib_exb2());
                Dbutils.updateLib(lib);
                XUtil.tShort("已修改~");
                break;

            case 10:
                String content = libInfo();
                try {
                    content = content.replaceAll("，", ",").replaceAll("：", ":");
                    String arr[] = content.split("\\|\\|\\|");
                    if (arr.length > 1) {
//                        lib_exi1
                        LocLibFt.mkLib(arr[0], arr[1], lib.getLib_exi1());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                XUtil.tLong("库结构已克隆~");
                break;

            case 11:
                XUtil.sendToDesktop(ctx, lib);
                break;
            case 12:
                new MaterialDialog.Builder(ctx)
                        .title("清除库内容")
                        .content("确认清除库内容？")
                        .positiveText("清除")
                        .negativeText("取消")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                Dbutils.deleteLibItems(lib.getLib_id());
                                refresh();
                            }
                        }).show();
                break;
        }


        return false;
    }

    private void refreshInfo() {
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
                    datas = new ArrayList<>();
                    getData();
//                    EventBus.getDefault().post(new RefreshEvent(EnumData.RefreshEnum.ITEMS.getValue()));
                }

                @Override
                public void onError(Context context, Exception e) {
                }
            });
        }
        if (lib.getLib_exi1() == Lib.libType.net.value()) {
            XUtil.tShort("更新数据中，请耐心等待...");
            ApiUrils.getApiInfo(lib);
        }
        if (lib.getLib_exi1() == Lib.libType.api.value()) {
            XUtil.tShort("更新数据中，请耐心等待...");
            getApiByQuery(lib);
        }

        if (lib.getLib_name().startsWith(Constant.PRE_LIB)) {

            XUtil.tShort("更新数据中，请耐心等待...");

            ApiUrils.getApiInfo(lib);

        }
    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        int w = 120;
        int h = 120;
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
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

    private void itemLongClick(int position) {
        final Items data = datas.get(position);
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
    }
}
