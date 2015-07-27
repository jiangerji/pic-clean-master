package cn.iam007.pic.clean.master.delete;

import cn.iam007.pic.clean.master.base.BaseItemInterface;
import cn.iam007.pic.clean.master.recycler.RecyclerManager;

/**
 * Created by Administrator on 2015/7/27.
 */
public abstract class DeleteItem implements BaseItemInterface {

    public String getImageRealPath() {
        return null;
    }

    public void delete() {
        RecyclerManager.getInstance().deleteToRecycler(getImageRealPath());
    }
}
