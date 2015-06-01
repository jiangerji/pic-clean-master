package cn.iam007.pic.clean.master.recycler;

import java.util.ArrayList;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.LayoutParams;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class RecyclerImageAdapter extends Adapter<RecyclerViewHolder> {

    private ArrayList<RecyclerImageItem> mItems = new ArrayList<>();

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItem(RecyclerImageItem item) {
        mItems.add(item);
        item.setAdapter(this);
    }

    public void clear() {
        mItems.clear();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recycler_item_image, parent, false);

        int width = PlatformUtils.getScreenWidth(parent.getContext()) / 3;
        LayoutParams layoutParams = new LayoutParams(width, width);
        itemView.setLayoutParams(layoutParams);

        RecyclerViewHolder holder = new RecyclerViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        RecyclerImageItem item = mItems.get(position);
        holder.bindView(item);
    }

    private long mSelectedItem = 0;

    // 主要用于更新已选择的数量
    public void updateItem(RecyclerImageItem item) {
        if (item != null) {
            if (item.isSelected()) {
                mSelectedItem++;
            } else {
                mSelectedItem--;
            }
        }

        LogUtil.d("updateItem:" + mSelectedItem);
        SharedPreferenceUtil.setLong(
                SharedPreferenceUtil.SELECTED_RECYCLER_IMAGE_TOTAL_SIZE, mSelectedItem);
    }

    public void selectAll(boolean select, GridLayoutManager layoutManager) {
        for (RecyclerImageItem item : mItems) {
            item.setSelected(select, false);
        }

        if (select) {
            mSelectedItem = mItems.size();
        } else {
            mSelectedItem = 0;
        }

        SharedPreferenceUtil.setLong(
                SharedPreferenceUtil.SELECTED_RECYCLER_IMAGE_TOTAL_SIZE, mSelectedItem);

        if (layoutManager == null) {
            notifyDataSetChanged();
        } else {
            int startPos = layoutManager.findFirstVisibleItemPosition();
            int lastPos = layoutManager.findLastVisibleItemPosition();
            RecyclerImageItem item = null;
            while (startPos <= lastPos) {
                if (startPos >= 0 && startPos < mItems.size()) {
                    item = mItems.get(startPos);
                    item.getViewHolder().setChecked(select);
                }
                startPos++;
            }
        }
    }

    /**
     * 删除选中的图片
     */
    public void deleteItems() {
        ArrayList<RecyclerImageItem> deleteItems = new ArrayList<>();
        for (RecyclerImageItem item : mItems) {
            if (item.isSelected()) {
                RecyclerManager.getInstance().delete(item);
                deleteItems.add(item);
            }
        }

        mSelectedItem = 0;
        SharedPreferenceUtil.setLong(
                SharedPreferenceUtil.SELECTED_RECYCLER_IMAGE_TOTAL_SIZE, mSelectedItem);

        mItems.removeAll(deleteItems);
    }

    /**
     * 将选择的回收站图片恢复到原始路径
     */
    public void restoreItems(){
        ArrayList<RecyclerImageItem> deleteItems = new ArrayList<>();
        for (RecyclerImageItem item : mItems) {
            if (item.isSelected()) {
                RecyclerManager.getInstance().restore(item);
                deleteItems.add(item);
            }
        }

        mSelectedItem = 0;
        SharedPreferenceUtil.setLong(
                SharedPreferenceUtil.SELECTED_RECYCLER_IMAGE_TOTAL_SIZE, mSelectedItem);

        mItems.removeAll(deleteItems);
    }

    /**
     * 获取当前选中的图片数量
     *
     * @return
     */
    public long getSelectedItem() {
        return mSelectedItem;
    }
}
