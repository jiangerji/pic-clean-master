package cn.iam007.pic.clean.master.duplicate.gallery;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by root on 24/4/15.
 */
public class CenterLockListener extends RecyclerView.OnScrollListener {
    boolean autoSet = true;//To avoid recursive calls
    int SCREEN_CENTER_X;

    public CenterLockListener(Context context) {

        DisplayMetrics dm = new DisplayMetrics();
        // 取得窗口属性
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(dm);
        SCREEN_CENTER_X = dm.widthPixels / 2;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        Log.d("center", "Scrolle  newState: " + newState);
        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (!autoSet) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //ScrollStoppped
                View view = findCenterView(lm);//get the view nearest to center
                if (view != null) {
                    int scrollXNeeded = (int) (SCREEN_CENTER_X -
                            (view.getLeft() + view.getRight()) / 2);//compute scroll from center
                    Log.d("center", "Scrolle  scrollXNeeded: " + scrollXNeeded);
                    recyclerView.smoothScrollBy(scrollXNeeded
                            * (view.getRight() < SCREEN_CENTER_X ? 1 : -1), 0);
                    autoSet = true;
                }
            }
        }
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING
                || newState == RecyclerView.SCROLL_STATE_SETTLING) {
            autoSet = false;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

    }

    private View findCenterView(LinearLayoutManager lm) {
        int mindist = 0;
        View view = null, retview = null;
        boolean notfound = true;
        for (int i = lm.findFirstVisibleItemPosition(); i <= lm.findLastVisibleItemPosition()
                && notfound; i++) {
            view = lm.findViewByPosition(i);

            if (view.getVisibility() == View.VISIBLE) {
                int leastdiff = Math.abs(SCREEN_CENTER_X
                        - (view.getLeft() + view.getRight()) / 2);

                if (leastdiff <= mindist
                        || i == lm.findFirstVisibleItemPosition())
                {
                    mindist = leastdiff;
                    retview = view;
                }
                else
                {
                    notfound = false;

                }
            }
        }
        return retview != null ? retview : view;
    }
}
