package cn.iam007.pic.clean.master.recycler;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import cn.iam007.pic.clean.master.utils.CryptoUtil;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class RecyclerImageItem {
    @Id
    private String id; // 用于保存到数据库中的index

    @Column(column = "name")
    private String sourcePath;

    private String recyclerPath;
    private boolean isSelected;

    private RecyclerViewHolder viewHolder;

    public RecyclerImageItem(String recyclerPath, String sourcePath) {
        this.sourcePath = "file://" + sourcePath;
        this.recyclerPath = recyclerPath;
        this.isSelected = false;
        this.id = CryptoUtil.getMD5String(sourcePath);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Recycler Image Item:\n");
        builder.append("  id       = " + getId() + "\n");
        builder.append("  source   = " + getSourcePath() + "\n");
        builder.append("  recycler = " + getRecyclerPath() + "\n");

        return super.toString();
    }

    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    private WeakReference<RecyclerImageAdapter> mAdapter = null;

    public void setAdapter(RecyclerImageAdapter adapter){
        mAdapter = new WeakReference<>(adapter);
    }

    /**
     * @param isSelected the isSelected to set
     * @param updateView 是否需要新fragment上的view
     */
    public void setSelected(boolean isSelected, boolean updateView) {
        if (isSelected != this.isSelected) {
            this.isSelected = isSelected;

            if (updateView){
                if (mAdapter != null && mAdapter.get() != null){
                    mAdapter.get().updateItem(this);
                }
            }
        }
    }

    /**
     * @return the sourcePath
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * @return the recyclerPath
     */
    public String getRecyclerPath() {
        return recyclerPath;
    }

    /**
     * @return the mViewHolder
     */
    public RecyclerViewHolder getViewHolder() {
        return viewHolder;
    }

    /**
     * @param viewHolder the mViewHolder to set
     */
    public void setViewHolder(RecyclerViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public String getId() {
        return id;
    }

}
