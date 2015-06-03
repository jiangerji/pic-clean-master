package cn.iam007.pic.clean.master;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;

import cn.iam007.pic.clean.master.recycler.RecyclerManager;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class Iam007Application extends Application {

    private static Iam007Application mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        AVOSCloud.initialize(this, "hjep2oqkdkg7g3nsq3u4vjnfrf078yez1bsx20mlkdrz5r8s",
                "49yph32t550wub0w2p3se0hz6o8qcp9gyqbsmf7oslbh0eh7");
        //AVAnalytics.start(this);    已经不再需要这行代码了
        AVAnalytics.enableCrashReport(this, true);

        ImageUtils.init(this);
        Constants.init(this);
        SharedPreferenceUtil.init(this);
        RecyclerManager.getInstance().init(this);
    }

    public static Iam007Application getApplication() {
        return mApplication;
    }

}
