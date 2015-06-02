package cn.iam007.pic.clean.master.recycler;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Transient;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import cn.iam007.pic.clean.master.utils.CryptoUtil;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class RecyclerImageItem {
    @Id
    private String id; // 用于保存到数据库中的index

    @Column(column = "sourcePath")
    private String sourcePath;

    @Column(column = "recyclerPath")
    private String recyclerPath;

    @Transient
    private boolean isSelected;

    private RecyclerViewHolder viewHolder;

    public RecyclerImageItem() {
    }

    /**
     * @param recyclerPath 回收站文件路径
     * @param sourcePath   回收站文件原始文件路径
     * @param id           回收站id, 用于在数据库中查看
     */
    public RecyclerImageItem(String recyclerPath, String sourcePath, String id) {
        this.sourcePath = sourcePath;
        this.recyclerPath = recyclerPath;
        this.isSelected = false;
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Recycler Image Item:\n");
        builder.append("  id       = " + getId() + "\n");
        builder.append("  source   = " + getSourcePath() + "\n");
        builder.append("  recycler = " + getRealRecyclerPath() + "\n");

        return super.toString();
    }

    /**
     * 该item是否被选择
     *
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    private WeakReference<RecyclerImageAdapter> mAdapter = null;

    public void setAdapter(RecyclerImageAdapter adapter) {
        mAdapter = new WeakReference<>(adapter);
    }

    /**
     * @param isSelected the isSelected to set
     * @param updateView 是否需要新fragment上的view
     */
    public void setSelected(boolean isSelected, boolean updateView) {
        if (isSelected != this.isSelected) {
            this.isSelected = isSelected;

            if (updateView) {
                if (mAdapter != null && mAdapter.get() != null) {
                    mAdapter.get().updateItem(this);
                }
            }
        }
    }

    /**
     * 获取回收站图片原始路径
     *
     * @return the sourcePath
     */
    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath){
        this.sourcePath = sourcePath;
    }

    /**
     * 获取图片在回收站的路径，加入file:///
     *
     * @return the recyclerPath
     */
    public String getImageRecyclerPath() {
        return "file://" + recyclerPath;
    }

    /**
     * 获取图片在回收站的路径
     *
     * @return the recyclerPath
     */
    public String getRealRecyclerPath(){
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

    public void setId(String id) {
        this.id = id;
    }
}
