package com.zncm.library.ft;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.melnykov.fab.FloatingActionButton;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.utils.DoubleClickImp;
import com.zncm.library.utils.OnRefreshListener;
import com.zncm.library.utils.XUtil;
import com.zncm.library.view.LoadMoreListView;

import tr.xip.errorview.ErrorView;


public abstract class BaseListFt extends BaseFt implements OnRefreshListener {
    protected RecyclerView mRecyclerView;
    protected boolean onLoading = false;
    protected View view;
    protected FloatingActionButton addButton;
    protected LayoutInflater mInflater;
    protected ErrorView errorView;
    public TwinklingRefreshLayout refreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        view = inflater.inflate(R.layout.fragment_base, null);
        refreshLayout = (TwinklingRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                onRefresh2();
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                onLoadMore2();
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        errorView = (ErrorView) view.findViewById(R.id.errorView);
        addButton = (FloatingActionButton) view.findViewById(R.id.button_floating_action);
        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //UNDONE
//                mRecyclerView.setSelection(0);
                return true;
            }
        });
        addButton.setColorNormal(getResources().getColor(R.color.colorPrimary));
        addButton.setColorPressed(getResources().getColor(R.color.colorPrimaryDark));
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void onLoadMoreComplete() {
        refreshLayout.finishRefreshing();
        refreshLayout.finishLoadmore();
        onLoading = false;

    }

    public void listToTop() {
        //UNDONE
//        listView.setSelection(0);
    }


}
