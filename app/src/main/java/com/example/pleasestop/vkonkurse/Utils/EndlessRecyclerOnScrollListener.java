package com.example.pleasestop.vkonkurse.Utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private int mPreviousTotal = 0;
    private boolean mLoading = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        if (mPreviousTotal > totalItemCount){
            mPreviousTotal = 0;
        }


        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }



        int visibleThreshold = 1;

        if (!mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

            onLoadMore();

            mLoading = true;
        }
    }



    public abstract void onLoadMore();

}
