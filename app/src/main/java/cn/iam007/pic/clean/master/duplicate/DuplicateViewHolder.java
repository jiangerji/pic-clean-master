package cn.iam007.pic.clean.master.duplicate;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

public class DuplicateViewHolder extends ViewHolder {

    public static final int VIEW_TYPE_HEADER = 0x01;
    public static final int VIEW_TYPE_CONTENT_LEFT = 0x02;
    public static final int VIEW_TYPE_CONTENT_LEFT_BOTTOM = 0x021;
    public static final int VIEW_TYPE_CONTENT_MIDDLE = 0x03;
    public static final int VIEW_TYPE_CONTENT_MIDDLE_BOTTOM = 0x031;
    public static final int VIEW_TYPE_CONTENT_RIGHT = 0x04;
    public static final int VIEW_TYPE_CONTENT_RIGHT_BOTTOM = 0x041;

    private int mViewType;
    private View mItemView;

    public DuplicateViewHolder(View itemView, int viewType) {
        super(itemView);

        mViewType = viewType;
        mItemView = itemView;

        onInit(mItemView);
    }

    private DuplicateItem mDuplicateImageItem = null;

    /**
     * 绑定view
     * 
     * @param imageItem
     */
    public void bindView(DuplicateItem imageItem) {
        boolean itemHasChanged = (imageItem != mDuplicateImageItem);

        mDuplicateImageItem = imageItem;
        mDuplicateImageItem.setViewHolder(this);

        onBindView(imageItem, itemHasChanged);
    }

    /**
     * @return the duplicateImageItem
     */
    public DuplicateItem getDuplicateImageItem() {
        return mDuplicateImageItem;
    }

    /**
     * 刷新绑定view
     */
    public void refresh() {
        bindView(mDuplicateImageItem);
    }
    /**
     * 找到某个UI item
     * 
     * @param id
     * @return
     */
    public final View findViewById(int id) {
        return mItemView.findViewById(id);
    }

    /**
     * @return the mViewType
     */
    public int getViewType() {
        return mViewType;
    }

    /**
     * @param viewType
     *            the mViewType to set
     */
    public void setViewType(int viewType) {
        this.mViewType = viewType;
    }

    DuplicateImageAdapter mAdapter;

    /**
     * @param adapter
     *            the adapter to set
     */
    public void setAdapter(DuplicateImageAdapter adapter) {
        this.mAdapter = adapter;
    }

    /**
     * @return the adapter
     */
    public DuplicateImageAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * view holder初始化，子类复写
     */
    public void onInit(View itemView) {

    }

    /**
     * 子类复写该方法，绑定view
     * 
     * @param item
     *            view绑定的item数据
     * @param itemHasChanged
     *            view绑定的item是否发生变化
     */
    public void onBindView(DuplicateItem item, boolean itemHasChanged) {

    }
}
