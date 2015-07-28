package cn.iam007.pic.clean.master.screenshot;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseItemInterface;
import cn.iam007.pic.clean.master.base.ImageAdapterInterface;
import cn.iam007.pic.clean.master.delete.DeleteItem;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

/**
 * Created by Administrator on 2015/7/27.
 */
public class ScreenshotImageAdapter extends RecyclerView.Adapter<ScreenshotViewHolder> implements
        ImageAdapterInterface {

    private ArrayList<ScreenshotImageItem> mItems = new ArrayList<>();
    private MyItemClickListener mItemClickListener;

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItem(ScreenshotImageItem item) {
        if (item != null) {
            mItems.add(item);
        }
    }

    @Override
    public ArrayList<? extends BaseItemInterface> getItems() {
        return mItems;
    }

    public ScreenshotImageItem getItem(int pos) {
        if (pos < mItems.size()) {
            return mItems.get(pos);
        } else {
            return null;
        }
    }

    @Override
    public long getSelectedCount() {
        return mSelectedItem;
    }

    public void setSelectedCount(long count) {
        mSelectedItem = count;
    }

    @Override
    public ScreenshotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_screenshot_item_image, parent, false);

        int width = PlatformUtils.getScreenWidth(parent.getContext()) / 3;
        GridLayoutManager.LayoutParams
                layoutParams = new GridLayoutManager.LayoutParams(width, width);
        itemView.setLayoutParams(layoutParams);

        ScreenshotViewHolder holder = new ScreenshotViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ScreenshotViewHolder holder, final int position) {
        ScreenshotImageItem item = mItems.get(position);
        holder.bindView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    private long mSelectedItem = 0;

//    // 主要用于更新已选择的数量
//    public void updateItem(ScreenshotImageItem item) {
//        if (item != null) {
//            if (item.isSelected()) {
//                mSelectedItem++;
//            } else {
//                mSelectedItem--;
//            }
//        }
//
//        LogUtil.d("updateItem:" + mSelectedItem);
//        SharedPreferenceUtil.setLong(
//                SharedPreferenceUtil.SELECTED_SCREENSHOT_IMAGE_TOTAL_SIZE, mSelectedItem);
//    }

    public void selectAll(boolean select, GridLayoutManager layoutManager) {
        for (ScreenshotImageItem item : mItems) {
            item.setSelected(select, false);
        }

        SharedPreferenceUtil.setLong(
                SharedPreferenceUtil.SELECTED_SCREENSHOT_IMAGE_TOTAL_SIZE, mSelectedItem);

        if (layoutManager == null) {
            notifyDataSetChanged();
        } else {
            int startPos = layoutManager.findFirstVisibleItemPosition();
            int lastPos = layoutManager.findLastVisibleItemPosition();
            ScreenshotImageItem item = null;
            while (startPos <= lastPos) {
                if (startPos >= 0 && startPos < mItems.size()) {
                    item = mItems.get(startPos);
                    item.setSelected(select, true);
                }
                startPos++;
            }
        }
    }

    public ArrayList<? extends DeleteItem> getSelectedItems() {
        ArrayList<ScreenshotImageItem> deleteItems = new ArrayList<>();
        for (ScreenshotImageItem item : mItems) {
            if (item.isSelected()) {
                deleteItems.add(item);
            }
        }

        return deleteItems;
    }

    public void removeItems(ArrayList<DeleteItem> deleteItems) {
        if (deleteItems != null) {
            mItems.removeAll(deleteItems);
            mSelectedItem -= deleteItems.size();
        }
    }

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }
}
