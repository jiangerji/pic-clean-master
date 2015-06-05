package cn.iam007.pic.clean.master.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.iam007.pic.clean.master.main.MainActivity;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/6/5.
 */
public class PushReceiver extends BroadcastReceiver {

    private final static int ACTION_REGISTRATION_ID = 0;
    private final static int ACTION_MESSAGE_RECEIVED = 1;
    private final static int ACTION_NOTIFICATION_RECEIVED = 2;
    private final static int ACTION_NOTIFICATION_OPENED = 3;
    private final static int ACTION_RICHPUSH_CALLBACK = 4;
    private final static int ACTION_CONNECTION_CHANGE = 5;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        LogUtil.d("[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(
                bundle));

        int action = -1;

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtil.d("[MyReceiver] 接收Registration Id : " + regId);
            action = ACTION_REGISTRATION_ID;
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            action = ACTION_MESSAGE_RECEIVED;
            LogUtil.d("[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(
                    JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            action = ACTION_NOTIFICATION_RECEIVED;
            LogUtil.d("[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LogUtil.d("[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            action = ACTION_NOTIFICATION_OPENED;
            LogUtil.d("[MyReceiver] 用户点击打开了通知");
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            action = ACTION_RICHPUSH_CALLBACK;
            LogUtil.d("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(
                    JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            action = ACTION_CONNECTION_CHANGE;
            boolean connected =
                    intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            LogUtil.d(
                    "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            LogUtil.d("[MyReceiver] Unhandled intent - " + intent.getAction());
        }

        for (PushHandler handler : PushManager.getPushHandlers()) {
            boolean handled = false;
            try {
                switch (action) {
                    case ACTION_REGISTRATION_ID:
                        handled = handler.onRegistrationId(context, bundle);
                        break;

                    case ACTION_MESSAGE_RECEIVED:
                        handled = handler.onMessageReceived(context, bundle);
                        break;

                    case ACTION_CONNECTION_CHANGE:
                        handled = handler.onConnectionChanged(context,
                                intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE,
                                        false), bundle);
                        break;

                    case ACTION_NOTIFICATION_OPENED:
                        handled = handler.onNotificationOpened(context, bundle);
                        break;

                    case ACTION_NOTIFICATION_RECEIVED:
                        handled = handler.onNotificationReceived(context, bundle);
                        break;

                    case ACTION_RICHPUSH_CALLBACK:
                        handled = handler.onRichPushCallback(context, bundle);
                        break;
                }
            } catch (Exception e) {
                LogUtil.d("PushHandler Exception:" + e.getMessage());
            }

            if (handled) {
                break;
            }
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
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
