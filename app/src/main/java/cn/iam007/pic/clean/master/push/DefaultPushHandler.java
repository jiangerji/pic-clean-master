package cn.iam007.pic.clean.master.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.webview.WebViewActivity;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/6/5.
 */
public class DefaultPushHandler extends PushHandler {

    private final static int ACTION_UNKNOWN = -1;
    private final static int ACTION_OPEN_URL = 0x01;

    @Override
    public boolean onNotificationOpened(Context context, Bundle bundle) {
        LogUtil.d("onNotificationReceived:");
        LogUtil.d("  " + printBundle(bundle));

        String command = bundle.getString(JPushInterface.EXTRA_EXTRA);
        int action = ACTION_UNKNOWN;
        String url = null;
        String title = null;
        try {
            JSONObject commandJson = new JSONObject(command);
            action = commandJson.optInt("action");
            url = commandJson.optString("url");
            title = commandJson.optString("title");
        } catch (Exception e) {

        }

        if (action == ACTION_OPEN_URL) {
            //打开自定义的Activity
            Intent i = new Intent(context, WebViewActivity.class);
            i.putExtra(WebViewActivity.DATA_URL, url);
            i.putExtra(WebViewActivity.DATA_TITLE, title);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

        return false;
    }
}
