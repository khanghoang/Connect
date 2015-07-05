package com.shonnect.shonnect.control;

import com.shonnect.shonnect.fragment.BaseFragment;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 */
public abstract class BaseListingFragment extends BaseFragment {
    protected static class AdjustAlphaVerticalScrollListener extends RecyclerView.OnScrollListener {
        private final View view;
        int totalY = 0;

        public AdjustAlphaVerticalScrollListener(View view) {
            this.view = view;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            totalY += dy;

            if (view != null) {
                int viewHeight = view.getHeight();
                float alpha = Math.max(viewHeight - totalY, 0) * 1.0f / viewHeight;
                view.setAlpha(alpha);
            }
        }
    }

    // TODO: (Dong) the similar listener for horizontal

    public static class ListingSimpleDividerDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public ListingSimpleDividerDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
        }
    }
}
