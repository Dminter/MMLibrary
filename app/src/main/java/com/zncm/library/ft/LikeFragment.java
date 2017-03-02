package com.zncm.library.ft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zncm.library.R;
import com.zncm.library.adapter.DetailsAdapter;
import com.zncm.library.data.Constant;
import com.zncm.library.data.DetailInfo;
import com.zncm.library.data.EnumData;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Info;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.MyApplication;
import com.zncm.library.data.RefreshEvent;
import com.zncm.library.ui.PhotoAc;
import com.zncm.library.utils.Dbutils;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.XUtil;
import com.zncm.library.view.loadmore.MxItemClickListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.greenrobot.event.EventBus;


public class LikeFragment extends BaseListFragment {
    private DetailsAdapter mAdapter;
    private Activity ctx;
    private boolean onLoading = false;
    DetailInfo info;
    ArrayList<Info> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (Activity) getActivity();

        Serializable dataParam = ctx.getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA);
        if (dataParam != null && dataParam instanceof DetailInfo) {
            info = (DetailInfo) dataParam;
        }

        addButton.setVisibility(View.GONE);
        mAdapter = new DetailsAdapter(ctx) {
            @Override
            public void setData(final int position, final PjViewHolder holder) {
                Info tmp = list.get(position);
                if (tmp == null) {
                    return;
                }

                MyApplication.imageLoader.displayImage(tmp.getImg(),
                        holder.imageView, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                holder.imageView.setImageResource(R.drawable.ic_lib);
                                super.onLoadingStarted(imageUri, view);
                            }
                        });

                holder.setClickListener(new MxItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {


                        if (isLongClick) {


                        } else {
                            Intent intent = new Intent(ctx, PhotoAc.class);
                            intent.putExtra("url", list.get(position).getImg());
                            startActivity(intent);

                        }

                    }
                });
            }


        };
        listView.setAdapter(mAdapter);
        getData(true);
        return view;
    }


    private void getData(boolean bFirst) {
        GetData getData = new GetData();
        getData.execute(bFirst);
    }

    class GetData extends AsyncTask<Boolean, Integer, Boolean> {

        protected Boolean doInBackground(Boolean... params) {

            boolean canLoadMore = true;
            try {

                Document doc = Jsoup.connect(info.getUrl()).timeout(3000).get();
                XUtil.debug("getUrl==>>" + info.getUrl());
                String elementsKey = info.getKey();
                Elements elements = doc.select(elementsKey);
                if (elements == null || elements.size() == 0) {
                    elements = doc.getElementsByClass(elementsKey);
                }
                if (elements == null || elements.size() == 0) {
                    elements = doc.getElementsByTag(elementsKey);
                }
                if (elements == null || elements.size() == 0) {
                    elements = doc.getElementsByAttribute(elementsKey);
                }
                list = new ArrayList<Info>();

                Info tmp = new Info();

                if (XUtil.listNotNull(elements)) {
                    Collections.reverse(elements);
                    for (Element element : elements) {
                        tmp = new Info();
                        String _durl = element.select("a").attr("href");
                        if (elementsKey.contains("img")) {
                            _durl = element.attr("src");
                        }
                        if (XUtil.notEmptyOrNull(_durl) && _durl.startsWith("/")) {
                            String base = doc.baseUri();
//                                        String base = element.baseUri();
                            if (XUtil.notEmptyOrNull(base) && base.endsWith("/")) {
                                base = base.substring(0, base.length() - 1);
                            }
                            _durl = base + _durl;
                        }
                        if (elementsKey.contains("img")) {
                            tmp.setImg(_durl);
                        } else {
                            tmp.setContent(element.text());
                        }
                        XUtil.debug("  " + element.html() + "-- " + _durl);
                        list.add(tmp);
                    }
                } else {
                    tmp.setContent(doc.html());
                }


                XUtil.debug("list===>>" + list);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return canLoadMore;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(Boolean canLoadMore) {
            super.onPostExecute(canLoadMore);
            mAdapter.setItems(list, listView);
            swipeLayout.setRefreshing(false);
            onLoading = false;
            listView.setCanLoadMore(canLoadMore);
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
    public void onRefresh() {
        if (onLoading) {
            return;
        }
        refresh();
    }

    private void refresh() {
        onLoading = true;
        swipeLayout.setRefreshing(true);
        getData(true);
    }


    @Override
    public void onLoadMore() {

        getData(false);

    }


}
