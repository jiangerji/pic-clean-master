package cn.iam007.pic.clean.master.base;

public interface BaseItemInterface {
    String getImageUrl();

    void setSelected(boolean isSelected, boolean updateSelectedCount);

    boolean isSelected();

    boolean isHeader();

    void refresh();
}
