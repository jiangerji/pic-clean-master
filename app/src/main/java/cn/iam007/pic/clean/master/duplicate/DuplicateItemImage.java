package cn.iam007.pic.clean.master.duplicate;

import java.io.File;

import cn.iam007.pic.clean.master.recycler.RecyclerManager;
import cn.iam007.pic.clean.master.utils.CryptoUtil;
import cn.iam007.pic.clean.master.utils.FileUtil;

public class DuplicateItemImage extends DuplicateItem {

    // 用于显示的图片
    private String imageUrl = null;

    // 该图片是否被选中需要删除
    private boolean isSelected = false;

    public DuplicateItemImage(String imageUrl, int sectionFirstPosition) {
        super(sectionFirstPosition);

        this.imageUrl = imageUrl;
    }

    /**
     * 返回图片的地址
     *
     * @return the imageUrl
     */
    public String getImageUrl() {
        if (imageUrl != null) {
            return "file://" + imageUrl;
        } else {
            return null;
        }
    }

    /**
     * 返回图片的绝对路径
     *
     * @return
     */
    public String getImageRealPath() {
        return imageUrl;
    }

    // 图片文件大小
    private long fileSize = 0;

    /**
     * @return the fileSize
     */
    public long getFileSize() {
        if (imageUrl != null && fileSize == 0) {
            File file = new File(imageUrl);
            fileSize = file.length();
        }
        return fileSize;
    }

    /**
     * 设置显示图片的地址
     *
     * @param imageUrl the imageUrl to set
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 是否被选中
     *
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * 设置图片是否被选中删除
     *
     * @param isSelected       the isSelected to set
     * @param updateTotalCount 是否将需要删除的图片文件总大小更新保存到SharedPreference中
     */
    public void setSelected(boolean isSelected, boolean updateTotalCount) {
        this.isSelected = isSelected;

        if (itemHeader != null) {
            itemHeader.setItemImageSelected(this, isSelected, updateTotalCount);
            itemHeader.refresh();
        }
    }

    private DuplicateItemHeader itemHeader = null;

    /**
     * 获取该部件所属头部件
     *
     * @return the item header
     */
    public DuplicateItemHeader getItemHeader() {
        return itemHeader;
    }

    /**
     * 设置该部件所属头部件
     *
     * @param itemHeader the header item to set
     */
    public void setItemHeader(DuplicateItemHeader itemHeader) {
        this.itemHeader = itemHeader;
    }


    public void delete() {
        RecyclerManager.getInstance().deleteToRecycler(imageUrl);
    }
}
