package cn.iam007.pic.clean.master.push;

import android.app.Notification;
import android.content.Context;

import java.util.ArrayList;

import cn.iam007.pic.clean.master.R;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/6/5.
 */
public class PushManager {

    private static ArrayList<PushHandler> mHandlers = new ArrayList<>();


    public static final void init(Context context) {
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(context);       // 初始化 JPush

        mHandlers.add(new DefaultPushHandler());
    }

    public static ArrayList<PushHandler> getPushHandlers() {
        return mHandlers;
    }

    /**
     * 设置通知提示方式 - 基础属性
     */
    public static void setStyleBasic(Context context) {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
        builder.statusBarDrawable = R.drawable.app_icon;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; //设置为点击后自动消失
        builder.notificationDefaults =
                Notification.DEFAULT_SOUND; //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(1, builder);
    }
}
