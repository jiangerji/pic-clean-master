package cn.iam007.pic.clean.master.recycler;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

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

    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        if (isSelected != this.isSelected) {
            this.isSelected = isSelected;

//            if (this.isSelected) {
//                SharedPreferenceUtil.addSharedPreference(SharedPreferenceUtil.SELECTED_RECYCLER_IMAGE_TOTAL_SIZE, 1L);
//            } else {
//                SharedPreferenceUtil.subSharedPreference(SharedPreferenceUtil.SELECTED_RECYCLER_IMAGE_TOTAL_SIZE, 1L);
//            }
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
