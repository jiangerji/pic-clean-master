package cn.iam007.pic.clean.master;

import android.app.Application;

import cn.iam007.pic.clean.master.recycler.RecyclerManager;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class Iam007Application extends Application {

    private static Iam007Application mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        ImageUtils.init(this);
        Constants.init(this);
        SharedPreferenceUtil.init(this);
        RecyclerManager.getInstance().init(this);
    }

    public static Iam007Application getApplication() {
        return mApplication;
    }

}
