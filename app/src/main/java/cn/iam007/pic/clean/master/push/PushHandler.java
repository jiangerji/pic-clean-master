package cn.iam007.pic.clean.master.push;

import android.content.Context;
import android.os.Bundle;

import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/6/5.
 */
public abstract class PushHandler {

    // JPushInterface.ACTION_MESSAGE_RECEIVED

    /**
     * 处理接收到推送的自定义消息
     *
     * @return 返回是否已经处理该消息
     */
    public boolean onMessageReceived(Context context, Bundle bundle) {
        LogUtil.d("onMessageReceived:");
        LogUtil.d("  " + printBundle(bundle));
        return false;
    }

    //JPushInterface.ACTION_NOTIFICATION_RECEIVED

    /**
     * 接收到推送的标准消息
     *
     * @return 返回是否已经处理
     */
    public boolean onNotificationReceived(Context context, Bundle bundle) {
        LogUtil.d("onNotificationReceived:");
        LogUtil.d("  " + printBundle(bundle));
        return false;
    }

    // JPushInterface.ACTION_NOTIFICATION_OPENED

    /**
     * 用户点击notification
     *
     * @return
     */
    public abstract boolean onNotificationOpened(Context context, Bundle bundle);


    public boolean onRegistrationId(Context context, Bundle bundle) {
        LogUtil.d("onRegistrationId:");
        LogUtil.d("  " + printBundle(bundle));
        return false;
    }

    public boolean onConnectionChanged(Context context, boolean booleanExtra, Bundle bundle) {
        LogUtil.d("onConnectionChanged:");
        LogUtil.d("  " + printBundle(bundle));
        return false;
    }

    public boolean onRichPushCallback(Context context, Bundle bundle) {
        LogUtil.d("onRichPushCallback:");
        LogUtil.d("  " + printBundle(bundle));
        return false;
    }

    // 打印所有的 intent extra 数据
    protected static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
}
