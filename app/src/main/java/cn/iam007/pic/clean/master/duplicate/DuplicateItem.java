package cn.iam007.pic.clean.master.duplicate;

public abstract class DuplicateItem {

    /**
     * 当前item绑定的view对象
     */
    private DuplicateViewHolder viewHolder = null;

    /**
     * 是否是头部件
     */
    private boolean isHeader = false;

    // 该item的头部件的位置
    private int sectionFirstPosition = 0;

    public DuplicateItem(int sectionFirstPosition) {
        this.sectionFirstPosition = sectionFirstPosition;
    }

    /**
     * 返回绑定view holder
     * 
     * @return the viewHolder
     */
    public DuplicateViewHolder getViewHolder() {
        return viewHolder;
    }

    /**
     * 设置绑定view holder
     * 
     * @param viewHolder
     *            the viewHolder to set
     */
    public void setViewHolder(DuplicateViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    /**
     * 是否是头部件
     * 
     * @return the isHeader
     */
    public boolean isHeader() {
        return isHeader;
    }

    /**
     * 设置是否是头部件
     * 
     * @param isHeader
     *            the isHeader to set
     */
    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    /**
     * 获取该部件所属头部件的位置
     * 
     * @return
     */
    public int getSectionFirstPosition() {
        return sectionFirstPosition;
    }

    /**
     * @param sectionFirstPosition
     *            the sectionFirstPosition to set
     */
    public void setSectionFirstPosition(int sectionFirstPosition) {
        this.sectionFirstPosition = sectionFirstPosition;
    }

}
