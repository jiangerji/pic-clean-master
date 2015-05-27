package cn.iam007.pic.clean.master;

import android.app.Application;

import cn.iam007.pic.clean.master.recycler.RecyclerManager;
import cn.iam007.pic.clean.master.utils.ImageUtils;

public class Iam007Application extends Application {

    private static Iam007Application mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();

        ImageUtils.init(this);
        Constants.init(this);
        RecyclerManager.getInstance().init(this);

        mApplication = this;
    }

    public static Iam007Application getApplication() {
        return mApplication;
    }

}
