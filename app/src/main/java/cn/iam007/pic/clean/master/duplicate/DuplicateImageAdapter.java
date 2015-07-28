package cn.iam007.pic.clean.master.duplicate;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LayoutManager;
import com.tonicartos.superslim.LayoutManager.LayoutParams;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.ImageAdapterInterface;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageFindTask.ImageHolder;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageFindTask.SectionItem;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class DuplicateImageAdapter extends Adapter<DuplicateViewHolder> implements
        ImageAdapterInterface {

    // 用于获取custom header对应position的mask
    private final static int CUSTOM_HEADER_MASK = 0x00FF;
    private final ArrayList<DuplicateItem> mItems;
    public ArrayList<Integer> mCustomHeaderType = new ArrayList<Integer>();
    public MyItemClickListener mItemClickListener;
    private Context mContext;
    private int mHeaderCount = 0;
    private int mDuplicateImageCount = 0;
    private int mCustomHeaderCount = 0;
    private ArrayList<Integer> mCustomHeaderLayout = new ArrayList<Integer>();
    private ArrayList<View> mCustomHeaderView = new ArrayList<View>();
    private HeaderViewCallback mHeaderViewCallback = null;

    public DuplicateImageAdapter(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
    }

    /**
     * 添加section
     *
     * @param header
     * @param images
     */
    public void addSection(String header, ArrayList<String> images) {
        int sectionFirstPosition = mCustomHeaderCount + mHeaderCount
                + mDuplicateImageCount;

        DuplicateItemHeader sectionHeader = new DuplicateItemHeader(sectionFirstPosition);
        sectionHeader.setHeaderText(header);

        mItems.add(sectionHeader);

        for (String imageUrl : images) {
            DuplicateItemImage imageItem = new DuplicateItemImage(imageUrl,
                    sectionFirstPosition);
            mItems.add(imageItem);
            sectionHeader.addItem(imageItem);
        }

        // 不足3个，使用空图片进行填位
        int imagesSize = images.size();
        if (imagesSize % 3 > 0) {
            int i = 0;
            while (i++ < (3 - imagesSize % 3)) {
                DuplicateItemImage imageItem = new DuplicateItemImage(null,
                        sectionFirstPosition);
                mItems.add(imageItem);
            }

            mDuplicateImageCount += 3 - imagesSize % 3;
        }

        mHeaderCount++;
        mDuplicateImageCount += images.size();
    }

    public void addSection(SectionItem sectionItem) {
        ArrayList<String> imagesPath = new ArrayList<>();
        for (ImageHolder image : sectionItem.getImages()) {
            imagesPath.add(image.getImagePath());
        }

        addSection(sectionItem.getHeader(), imagesPath);
    }

    public void clear() {
        mItems.clear();
    }

    /**
     * 添加可头部对象
     *
     * @param layout
     */
    public View addCustomHeader(int layout) {
        mCustomHeaderCount++;
        mCustomHeaderType.add(0xDEAD0000 | mCustomHeaderCount);
        mCustomHeaderLayout.add(layout);

        View view = LayoutInflater.from(mContext).inflate(layout, null, false);
        mCustomHeaderView.add(view);

        PlatformUtils.applyFonts(view);
        return view;
    }

    public ArrayList<DuplicateItem> getItems() {
        return mItems;
    }

    @Override
    public int getItemCount() {
        return mItems.size() + mCustomHeaderCount;
    }

    public int getRealItemCount() {
        return mItems.size();
    }

    public int getCustomHeaderCount() {
        return mCustomHeaderCount;
    }

    public DuplicateItem getItem(int pos) {
        if (pos < mItems.size()) {
            return mItems.get(pos);
        } else {
            return null;
        }
    }

    @Override
    public long getSelectedCount() {
        return SharedPreferenceUtil.getLong(
                SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_NUM,
                0L);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mCustomHeaderCount) {
            return mCustomHeaderType.get(position);
        }

        DuplicateItem item = mItems.get(position - mCustomHeaderCount);

        DuplicateItemHeader headerItem =
                (DuplicateItemHeader) mItems.get(item.getSectionFirstPosition()
                        - mCustomHeaderCount);
        int totalCount = headerItem.getItemCount() / 3 * 3;

        int type = -1;
        if (item.isHeader()) {
            type = DuplicateViewHolder.VIEW_TYPE_HEADER;
        } else {
            // 判断位于第几个
            int tPos = (position - item.getSectionFirstPosition() - 1);
            int index = tPos % 3;

            if (index == 0) {
                if (tPos >= totalCount) {
                    type = DuplicateViewHolder.VIEW_TYPE_CONTENT_LEFT_BOTTOM;
                } else {
                    type = DuplicateViewHolder.VIEW_TYPE_CONTENT_LEFT;
                }
            } else if (index == 1) {
                if (tPos >= totalCount) {
                    type = DuplicateViewHolder.VIEW_TYPE_CONTENT_MIDDLE_BOTTOM;
                } else {
                    type = DuplicateViewHolder.VIEW_TYPE_CONTENT_MIDDLE;
                }
            } else {
                if (tPos >= totalCount) {
                    type = DuplicateViewHolder.VIEW_TYPE_CONTENT_RIGHT_BOTTOM;
                } else {
                    type = DuplicateViewHolder.VIEW_TYPE_CONTENT_RIGHT;
                }
            }
        }

        return type;
    }

    private void onBindViewHeader(DuplicateViewHolder holder, int position) {
        View itemView = holder.itemView;

        LayoutParams lp = (LayoutManager.LayoutParams) itemView.getLayoutParams();
        // Overrides xml attrs, could use different layouts too.
        if (position == 0) {
            lp.headerDisplay = LayoutParams.HEADER_INLINE;
        }
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;

        lp.setSlm(LinearSLM.ID);
        lp.setFirstPosition(0);
        itemView.setLayoutParams(lp);
    }

    @Override
    public void onBindViewHolder(DuplicateViewHolder holder, int position) {
        if (position < mCustomHeaderCount) {
            onBindViewHeader(holder, position);
            return;
        }

        final DuplicateItem item = mItems.get(position - mCustomHeaderCount);
        final View itemView = holder.itemView;

        holder.bindView(item);

        GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
        // Overrides xml attrs, could use different layouts too.
        if (item.isHeader()) {
            lp.headerDisplay = LayoutParams.HEADER_INLINE;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            lp.width = PlatformUtils.getScreenWidth(mContext) / 3;
            lp.height = PlatformUtils.getScreenWidth(mContext) / 3;
        }
        lp.setSlm(GridSLM.ID);
        lp.setNumColumns(3);
        lp.setFirstPosition(item.getSectionFirstPosition());
        itemView.setLayoutParams(lp);
    }

    @Override
    public DuplicateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        DuplicateViewHolder holder = null;

        switch (viewType) {
            case DuplicateViewHolder.VIEW_TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.duplicate_item_header, parent, false);
                holder = new DuplicateViewHolderHeader(view);
                break;

            case DuplicateViewHolder.VIEW_TYPE_CONTENT_LEFT:
            case DuplicateViewHolder.VIEW_TYPE_CONTENT_LEFT_BOTTOM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.duplicate_item_image_left, parent, false);
                holder = new DuplicateViewHolderImage(view,
                        DuplicateViewHolderImage.ImagePos.LEFT, mItemClickListener);

                if (viewType == DuplicateViewHolder.VIEW_TYPE_CONTENT_LEFT_BOTTOM) {
                    view.findViewById(R.id.background)
                            .setBackgroundResource(R.drawable.duplicate_item_left_bottom_bg);
                }
                break;

            case DuplicateViewHolder.VIEW_TYPE_CONTENT_MIDDLE:
            case DuplicateViewHolder.VIEW_TYPE_CONTENT_MIDDLE_BOTTOM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.duplicate_item_image_middle,
                                parent,
                                false);
                holder = new DuplicateViewHolderImage(view,
                        DuplicateViewHolderImage.ImagePos.MIDDLE, mItemClickListener);
                if (viewType == DuplicateViewHolder.VIEW_TYPE_CONTENT_MIDDLE_BOTTOM) {
                    view.setBackgroundResource(R.drawable.duplicate_item_middle_bottom_bg);
                }
                break;
            case DuplicateViewHolder.VIEW_TYPE_CONTENT_RIGHT:
            case DuplicateViewHolder.VIEW_TYPE_CONTENT_RIGHT_BOTTOM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.duplicate_item_image_right, parent, false);
                holder = new DuplicateViewHolderImage(view,
                        DuplicateViewHolderImage.ImagePos.RIGHT, mItemClickListener);
                if (viewType == DuplicateViewHolder.VIEW_TYPE_CONTENT_RIGHT_BOTTOM) {
                    view.findViewById(R.id.background)
                            .setBackgroundResource(R.drawable.duplicate_item_right_bottom_bg);
                }
                break;

            default:
                int layout = mCustomHeaderLayout.get((viewType & CUSTOM_HEADER_MASK) - 1);
                view = LayoutInflater.from(parent.getContext())
                        .inflate(layout, parent, false);
                if (mHeaderViewCallback != null) {
                    mHeaderViewCallback.onHeaderViewCreated(layout, view);
                }

                holder = new DuplicateViewHolder(view, viewType);
                break;
        }

        holder.setAdapter(this);
        return holder;
    }

    /**
     * 删除某一组相似图片
     *
     * @param duplicateImageItem
     */
    public void deleteSection(DuplicateItem duplicateImageItem) {
        if (duplicateImageItem instanceof DuplicateItemHeader) {
            // 删除这一组
            int startPos = duplicateImageItem.getSectionFirstPosition()
                    - mCustomHeaderCount;
            int endPos = startPos;
            ArrayList<DuplicateItem> items = new ArrayList<>();
            items.add(mItems.get(endPos));
            endPos++;

            while (endPos < mItems.size()) {
                DuplicateItem item = mItems.get(endPos);
                if (item.isHeader()) {
                    endPos--;
                    break;
                } else {
                    items.add(item);
                }

                endPos++;
            }

            LogUtil.d("remove from " + startPos + " to " + endPos);
            int count = endPos - startPos + 1;
            endPos++;
            while (endPos < mItems.size()) {
                DuplicateItem item = mItems.get(endPos);
                item.setSectionFirstPosition(item.getSectionFirstPosition()
                        - count);
                endPos++;
            }

            mItems.removeAll(items);
            notifyItemRangeRemoved(startPos + mCustomHeaderCount, endPos
                    + mCustomHeaderCount);
        } else {

        }
    }

    /**
     * 自动选择
     */
    public void autoSelect(LayoutManager layoutManager) {
        int index = 0;
        DuplicateItem bestItem = null;
        DuplicateItem item = null;
        // 将所有的计算选择状态
        long totalCount = 0;
        long totalNum = 0;
        while (index < mItems.size()) {
            item = mItems.get(index);
            if (item.isHeader()) {
                bestItem = null;
            } else {
                if (bestItem == null) {
                    bestItem = item;
                    bestItem.setSelected(false, false);
                } else {
                    long preBestFileSize = ((DuplicateItemImage) bestItem).getFileSize();
                    long currentFileSize = ((DuplicateItemImage) item).getFileSize();

                    if (currentFileSize >= preBestFileSize) {
                        bestItem.setSelected(true, false);
                        totalCount += ((DuplicateItemImage) bestItem).getFileSize();
                        if (((DuplicateItemImage) bestItem).getFileSize() > 0) {
                            totalNum += 1;
                        }
                        bestItem = item;
                        bestItem.setSelected(false, false);
                    } else {
                        item.setSelected(true, false);
                        totalCount += ((DuplicateItemImage) item).getFileSize();
                        if (((DuplicateItemImage) item).getFileSize() > 0) {
                            totalNum += 1;
                        }
                    }

                }
            }

            index++;
        }

        SharedPreferenceUtil.setLong(
                SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_SIZE,
                totalCount);
        SharedPreferenceUtil.setLong(
                SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_NUM,
                totalNum);

        if (layoutManager == null) {
            notifyDataSetChanged();
        } else {
            int startVisiblePos = layoutManager.findFirstVisibleItemPosition();
            // 头部件不需要进行更新
            startVisiblePos = Math.max(mCustomHeaderCount, startVisiblePos);
            int lastVisiblePos = layoutManager.findLastVisibleItemPosition();

            notifyItemRangeChanged(startVisiblePos, lastVisiblePos);
        }
    }

    /**
     * 取消智能选择
     */
    public void cancelAutoSelect(LayoutManager layoutManager) {
        for (DuplicateItem item : mItems) {
            if (!item.isHeader()) {
                item.setSelected(false, false);
            }
        }

        SharedPreferenceUtil.setLong(
                SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_SIZE,
                0L);
        SharedPreferenceUtil.setLong(
                SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_NUM,
                0L);

        if (layoutManager == null) {
            notifyDataSetChanged();
        } else {
            int startVisiblePos = layoutManager.findFirstVisibleItemPosition();
            // 头部件不需要进行更新
            startVisiblePos = Math.max(mCustomHeaderCount, startVisiblePos);
            int lastVisiblePos = layoutManager.findLastVisibleItemPosition();

            notifyItemRangeChanged(startVisiblePos, lastVisiblePos);
        }
    }

    /**
     * @param headerViewCallback the mHeaderViewCallback to set
     */
    public void setHeaderViewCallback(HeaderViewCallback headerViewCallback) {
        this.mHeaderViewCallback = headerViewCallback;
    }

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface HeaderViewCallback {
        void onHeaderViewCreated(int layout, View view);
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }
}
