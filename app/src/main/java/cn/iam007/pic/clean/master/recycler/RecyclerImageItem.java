package cn.iam007.pic.clean.master.recycler;

public class RecyclerImageItem {

    private String sourcePath;
    private String recyclerPath;
    private boolean isSelected;

    private RecyclerViewHolder viewHolder;

    public RecyclerImageItem(String recyclerPath, String sourcePath) {
        this.sourcePath = "file://" + sourcePath;
        this.recyclerPath = recyclerPath;
        this.isSelected = false;
    }

    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected
     *            the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
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
     * @param mViewHolder
     *            the mViewHolder to set
     */
    public void setViewHolder(RecyclerViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

}
