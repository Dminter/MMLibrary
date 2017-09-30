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

import com.melnykov.fab.FloatingActionButton;
import com.zncm.library.R;
import com.zncm.library.data.Constant;
import com.zncm.library.utils.DoubleClickImp;
import com.zncm.library.utils.XUtil;
import com.zncm.library.view.LoadMoreListView;

import tr.xip.errorview.ErrorView;


public abstract class BaseListFt extends BaseFt implements SwipeRefreshLayout.OnRefreshListener, LoadMoreListView.OnLoadMoreListener {
    protected SwipeRefreshLayout swipeLayout;
    protected RecyclerView mRecyclerView;
    protected boolean onLoading = false;
    protected View view;
    protected FloatingActionButton addButton;
    protected LayoutInflater mInflater;
    protected ErrorView errorView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        view = inflater.inflate(R.layout.fragment_base, null);


        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(
                Color.RED, Color.YELLOW, Color.GREEN, Color.WHITE
        );
        swipeLayout.setBackgroundColor(getResources().getColor(R.color.white));
        // MySp.getTheme()

        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        errorView = (ErrorView) view.findViewById(R.id.errorView);

        addButton = (FloatingActionButton) view.findViewById(R.id.button_floating_action);

//        addButton.setColorNormal(MySp.getTheme());
//        addButton.setColorPressed(MySp.getTheme() + 10);




        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //UNDONE
//                mRecyclerView.setSelection(0);
                return true;
            }
        });


        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void onLoadMoreComplete() {
        swipeLayout.setRefreshing(false);
        onLoading = false;
//        listView.onLoadMoreComplete();

    }

    public void listToTop() {
        //UNDONE
//        listView.setSelection(0);
    }


}
