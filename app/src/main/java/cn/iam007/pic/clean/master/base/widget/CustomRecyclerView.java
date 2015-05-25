package cn.iam007.pic.clean.master.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.iam007.pic.clean.master.utils.LogUtil;

public class CustomRecyclerView extends android.support.v7.widget.RecyclerView {

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
    }

    // 是否需要禁用parent的滚动事件
    boolean mCanDisallowParentInterceptTouchEvent = false;

    public void canDisallowParentInterceptTouchEvent(boolean can) {
        mCanDisallowParentInterceptTouchEvent = can;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            //当手指触到listview的时候，让父ScrollView交出ontouch权限，也就是让父scrollview停住不能滚动
            getParent().requestDisallowInterceptTouchEvent(mCanDisallowParentInterceptTouchEvent);
            LogUtil.d("onInterceptTouchEvent down");
            break;

        case MotionEvent.ACTION_MOVE:
            LogUtil.d("onInterceptTouchEvent move");
            break;

        case MotionEvent.ACTION_UP:
            LogUtil.d("onInterceptTouchEvent up");
        case MotionEvent.ACTION_CANCEL:
            LogUtil.d("onInterceptTouchEvent cancel");
            //当手指松开时，让父ScrollView重新拿到onTouch权限
            getParent().requestDisallowInterceptTouchEvent(false);
            break;
        default:
            break;

        }
        return super.onInterceptTouchEvent(ev);
    }
}
