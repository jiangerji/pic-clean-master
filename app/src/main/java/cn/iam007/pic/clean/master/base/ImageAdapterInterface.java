package cn.iam007.pic.clean.master.base;

import java.util.ArrayList;

public interface ImageAdapterInterface {
    ArrayList<? extends BaseItemInterface> getItems();

    BaseItemInterface getItem(int pos);

    long getSelectedCount();
}


