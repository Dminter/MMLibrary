package com.zncm.library.ft;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
    protected LoadMoreListView listView;
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
        listView = (LoadMoreListView) view.findViewById(R.id.listView);
        errorView = (ErrorView) view.findViewById(R.id.errorView);
        listView.setOnLoadMoreListener(this);

        addButton = (FloatingActionButton) view.findViewById(R.id.button_floating_action);

//        addButton.setColorNormal(MySp.getTheme());
//        addButton.setColorPressed(MySp.getTheme() + 10);


        addButton.attachToListView(listView);


        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listView.setSelection(0);
                return true;
            }
        });


        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void onLoadMoreComplete() {
        swipeLayout.setRefreshing(false);
        onLoading = false;
        listView.onLoadMoreComplete();

    }

    public void listToTop() {
        listView.setSelection(0);
    }


}
