package cn.iam007.pic.clean.master.recycler;

import java.util.ArrayList;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.LayoutParams;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.utils.PlatformUtils;

public class RecyclerImageAdapter extends Adapter<RecyclerViewHolder> {

    private ArrayList<RecyclerImageItem> mItems = new ArrayList<>();

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItems(ArrayList<RecyclerImageItem> items) {
        mItems.addAll(items);
    }

    public void addItem(RecyclerImageItem item) {
        mItems.add(item);
    }

    @Override
    public RecyclerViewHolder
            onCreateViewHolder(ViewGroup parent, int viewType) {
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

    public void selectAll(boolean select, GridLayoutManager layoutManager) {
        for (RecyclerImageItem item : mItems) {
            item.setSelected(select);
        }

        if (layoutManager == null) {
            notifyDataSetChanged();
        } else {
            int startPos = layoutManager.findFirstVisibleItemPosition();
            int lastPos = layoutManager.findLastVisibleItemPosition();
            RecyclerImageItem item = null;
            while (startPos <= lastPos) {
                item = mItems.get(startPos);
                item.getViewHolder().setChecked(select);
                startPos++;
            }
        }
    }
}
