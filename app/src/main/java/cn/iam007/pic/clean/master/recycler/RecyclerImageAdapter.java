package cn.iam007.pic.clean.master.recycler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.LayoutParams;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.ImageAdapterInterface;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class RecyclerImageAdapter extends Adapter<RecyclerViewHolder> implements ImageAdapterInterface {

    private ArrayList<RecyclerImageItem> mItems = new ArrayList<>();

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItem(RecyclerImageItem item) {
        mItems.add(item);
        item.setAdapter(this);
    }

    public ArrayList<RecyclerImageItem> getItems() {
        return mItems;
    }

    public RecyclerImageItem getItem(int pos) {
        if (pos < mItems.size()) {
            return mItems.get(pos);
        } else {
            return null;
        }
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

        RecyclerViewHolder holder = new RecyclerViewHolder(itemView, mItemClickListener);
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
    public void deleteItems(Context context, Handler handler) {
        ArrayList<RecyclerImageItem> deleteItems = new ArrayList<>();
        int count = 0;
        String content;
        for (RecyclerImageItem item : mItems) {
            if (item.isSelected()) {
                RecyclerManager.getInstance().delete(item);
                deleteItems.add(item);

                content = context.getString(R.string.deleting_progress_format, ++count, mSelectedItem);
                Message msg = new Message();
                msg.obj = content;
                handler.sendMessage(msg);
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
    public void restoreItems(Context context, Handler handler){
        ArrayList<RecyclerImageItem> deleteItems = new ArrayList<>();
        int count = 0;
        String content;
        for (RecyclerImageItem item : mItems) {
            if (item.isSelected()) {
                RecyclerManager.getInstance().restore(item);
                deleteItems.add(item);

                content = context.getString(R.string.restoring_progress_format, ++count, mSelectedItem);
                Message msg = new Message();
                msg.obj = content;
                handler.sendMessage(msg);
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

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    public MyItemClickListener mItemClickListener;

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }
}
