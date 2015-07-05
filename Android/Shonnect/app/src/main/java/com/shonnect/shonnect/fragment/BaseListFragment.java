package com.shonnect.shonnect.fragment;

import com.shonnect.shonnect.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 7/4/15.
 */
public abstract class BaseListFragment<T> extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    protected RecyclerView rvList;
    protected SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    protected abstract <VH extends RecyclerView.ViewHolder> RecyclerView.Adapter<VH> provideAdapter();

    protected abstract void updateAdapter(List<T> data);

    @Override
    protected void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }
}
