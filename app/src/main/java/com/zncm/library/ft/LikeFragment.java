package com.zncm.library.ft;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zncm.library.R;
import com.zncm.library.adapter.DetailsAdapter;
import com.zncm.library.data.Constant;
import com.zncm.library.data.DetailInfo;
import com.zncm.library.data.Info;
import com.zncm.library.data.MyApplication;
import com.zncm.library.ui.LikeActivity;
import com.zncm.library.ui.PhotoAc;
import com.zncm.library.utils.XUtil;
import com.zncm.library.utils.htmlbot.contentextractor.ContentExtractor;
import com.zncm.library.view.loadmore.MxItemClickListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class LikeFragment extends BaseListFragment {
    private DetailsAdapter mAdapter;
    private LikeActivity ctx;
    private boolean onLoading = false;
    DetailInfo info;
    ArrayList<Info> list = new ArrayList<>();
    Set<String> imgUrls = new HashSet<>();
    String content;

    //    MaterialEditText editText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ctx = (LikeActivity) getActivity();

        Serializable dataParam = ctx.getIntent().getSerializableExtra(Constant.KEY_PARAM_DATA);
        if (dataParam != null && dataParam instanceof DetailInfo) {
            info = (DetailInfo) dataParam;
        }

        ctx.myTitle(info.getTitle());


        listView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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


                if (XUtil.notEmptyOrNull(tmp.getContent())) {
                    holder.imageView.setVisibility(View.GONE);
                    holder.tvContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setText(tmp.getContent());
                } else {
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.tvContent.setVisibility(View.GONE);
                }


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

        swipeLayout.setEnabled(false);
        listView.setAdapter(mAdapter);
        getData(true);


//         editText = new MaterialEditText(ctx);
//        if (MySp.get(SpConstant.isShowTitle, Boolean.class, true)) {
//            editText.setFloatingLabelText("信息");
//
//        } else {
//            editText.setHideUnderline(true);
//        }
//        editText.setTextColor(getResources().getColor(R.color.black));
//        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
//        editText.setFloatingLabelAlwaysShown(true);
//        editText.setEnabled(false);
//        editText.setFocusable(false);
//        editText.setPaddings(XUtil.dip2px(10),XUtil.dip2px(10),XUtil.dip2px(10),XUtil.dip2px(10));
//        ScrollView scrollView = new ScrollView(ctx);
//        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
//        scrollView.setBackgroundColor(getResources().getColor(R.color.white));
//        scrollView.addView(editText);
//        listView.addView(scrollView);


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
                content = ContentExtractor.getContentByURL(info.getUrl());
                Document doc = Jsoup.connect(info.getUrl()).timeout(3000).get();
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
                if (params[0]) {
                    list = new ArrayList<Info>();
                }
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
                            if (XUtil.notEmptyOrNull(base) && base.endsWith("/")) {
                                base = base.substring(0, base.length() - 1);
                            }
                            _durl = base + _durl;
                        }
                        if (elementsKey.contains("img") && imgUrls.add(_durl)&&!_durl.endsWith(".gif")) {
                            tmp.setImg(_durl);
                            list.add(tmp);
                        }
                    }
                } else {
                    tmp.setContent(doc.html());
                }
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


            if (!XUtil.listNotNull(list) && XUtil.notEmptyOrNull(content) || list.size() <= 6 && XUtil.notEmptyOrNull(content) && content.length() > 80) {
                list = new ArrayList<>();
                listView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                list.add(new Info("", "", content));
            }


            mAdapter.setItems(list, listView);


            swipeLayout.setRefreshing(false);
            onLoading = false;
            listView.setCanLoadMore(canLoadMore);


//            if (XUtil.notEmptyOrNull(content)&&editText!=null){
//                editText.setText(content);
//            }

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
//        getData(true);
    }


    @Override
    public void onLoadMore() {

//        getData(false);

    }


}
