package cn.iam007.pic.clean.master.duplicate;

import java.util.ArrayList;
import java.util.HashMap;

import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class DuplicateItemHeader extends DuplicateItem {

    public DuplicateItemHeader(int sectionFirstPosition) {
        super(sectionFirstPosition);

        setHeader(true);
    }

    private String mHeaderText = null;

    /**
     * 设置头部件的文字描述
     * 
     * @param header
     */
    public void setHeaderText(String header) {
        mHeaderText = header;
    }

    /**
     * 获取头部件文字描述
     * 
     * @return the mHeaderText
     */
    public String getHeaderText() {
        return mHeaderText;
    }

    private ArrayList<DuplicateItemImage> mItems = new ArrayList<DuplicateItemImage>();
    private ArrayList<Boolean> mItemsSelected = new ArrayList<Boolean>();
    // 用于表示某个image item的位置
    private HashMap<String, Integer> mItemHashMap = new HashMap<String, Integer>();

    /**
     * 添加属于该组的图片
     * 
     * @param item
     */
    public void addItem(DuplicateItemImage item) {
        item.setItemHeader(this);
        mItemHashMap.put(item.getImageUrl(), mItems.size());
        mItems.add(item);
        mItemsSelected.add(false);
    }

    public int getItemCount() {
        return mItems.size();
    }

    // 当前该组中选中图片的数量
    private int selectedCount = 0;

    public void setItemImageSelected(
            DuplicateItemImage item, boolean isSelected,
            boolean setSharedPreference) {
        if (item != null) {
            // 查找item的位置
            Integer pos = mItemHashMap.get(item.getImageUrl());
            if (pos != null && pos >= 0) {
                if (isSelected != mItemsSelected.get(pos)) {
                    mItemsSelected.set(pos, isSelected);
                    String key = SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_SIZE;
                    if (isSelected) {
                        selectedCount++;
                        if (setSharedPreference) {
                            SharedPreferenceUtil.addSharedPreference(key,
                                    item.getFileSize());
                        }
                    } else {
                        selectedCount--;
                        if (setSharedPreference) {
                            SharedPreferenceUtil.subSharedPreference(key,
                                    item.getFileSize());
                        }
                    }
                }
            }
        }

    }

    /**
     * @return the selectedCount
     */
    public int getSelectedCount() {
        return selectedCount;
    }

    /**
     * 返回标注为删除的相似图片对象
     * 
     * @return
     */
    public ArrayList<DuplicateItemImage> getSelectedItem() {
        ArrayList<DuplicateItemImage> items = new ArrayList<>();

        for (int i = 0; i < mItemsSelected.size(); i++) {
            if (mItemsSelected.get(i)) {
                items.add(mItems.get(i));
            }
        }

        return items;
    }

}
