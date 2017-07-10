package com.zncm.library.ft;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.malinskiy.materialicons.Iconify;
import com.melnykov.fab.FloatingActionButton;
import com.zncm.library.R;
import com.zncm.library.utils.MySp;
import com.zncm.library.utils.XUtil;
import com.zncm.library.view.loadmore.LoadMoreRecyclerView;


import java.util.Random;


public abstract class BaseListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreRecyclerView.LoadMoreListener {
    protected SwipeRefreshLayout swipeLayout;
    protected LoadMoreRecyclerView listView;
    protected View view;
    protected FloatingActionButton addButton;
    protected LayoutInflater mInflater;
//    protected LinearLayout headView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        view = inflater.inflate(R.layout.fragment_base2, null);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(
                MySp.getTheme()
        );
        swipeLayout.setBackgroundColor(getResources().getColor(R.color.white));
        listView = (LoadMoreRecyclerView) view.findViewById(R.id.listView);
//        headView = (LinearLayout) view.findViewById(R.id.headView);
        listView.setHasFixedSize(true);
        addButton = (FloatingActionButton) view.findViewById(R.id.button_floating_action);
        addButton.setImageDrawable(XUtil.initIconWhite(Iconify.IconValue.md_add));
        addButton.setColorNormal(MySp.getTheme());
        addButton.setColorPressed(MySp.getTheme() + new Random().nextInt(10000));
        listView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        listView.setAutoLoadMoreEnable(true);
        listView.setLoadMoreListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
