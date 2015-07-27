package cn.iam007.pic.clean.master.screenshot;

import cn.iam007.pic.clean.master.delete.DeleteItem;

public class ScreenshotImageItem extends DeleteItem {
    private String sourcePath;

    private boolean isSelected;

    private ScreenshotViewHolder viewHolder;
    private OnCheckedChangeListener mListener;


    /**
     * @param sourcePath 截屏文件原始文件路径
     */
    public ScreenshotImageItem(String sourcePath) {
        this.sourcePath = sourcePath;
        this.isSelected = false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Recycler Image Item:\n");
        builder.append("  source   = " + getSourcePath() + "\n");

        return super.toString();
    }

    @Override
    public String getImageUrl() {
        return "file://" + this.sourcePath;
    }

    @Override
    public String getImageRealPath() {
        return this.sourcePath;
    }

    @Override
    public void setSelected(boolean isSelected, boolean updateSelectedCount) {
        if (this.isSelected != isSelected) {
            this.isSelected = isSelected;
            if (mListener != null) {
                mListener.onCheckedChanged(isSelected);
            }
        }

        if (updateSelectedCount) {
            this.viewHolder.setChecked(isSelected);
        }
    }

    /**
     * 该item是否被选择
     *
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public boolean isHeader() {
        return false;
    }

    @Override
    public void refresh() {

    }

    /**
     * 获取截屏图片原始路径
     *
     * @return the sourcePath
     */
    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setViewHolder(ScreenshotViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

//    public void setChecked(boolean checked, boolean updateView) {
//        if (isSelected != checked) {
//            isSelected = checked;
//            if (mListener != null) {
//                mListener.onCheckedChanged(isSelected);
//            }
//        }
//
//        if (updateView) {
//            this.viewHolder.setChecked(checked);
//        }
//    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mListener = listener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean isChecked);
    }


}
