package cn.iam007.pic.clean.master.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.utils.DialogBuilder;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;
import cn.iam007.pic.clean.master.webview.WebViewActivity;

/**
 * Created by Administrator on 2015/6/10.
 */
public class Iam007Service extends Service {

    public class Iam007Binder extends Binder {
        public Iam007Service getService() {
            return Iam007Service.this;
        }
    }

    private Iam007Binder mBinder = new Iam007Binder();

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public Iam007Binder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("Service onCreate!");
        checkVersion();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("Service onDestroy!");
    }

    /**
     * ************************************检查更新************************************************
     */
    private final static String KEY_UPDATE_KEY = "key";
    private final static String KEY_UPDATE_VALUE = "value";
    private final static String KEY_UPDATE_VERSION_CODE = "versionCode";
    private final static String KEY_UPDATE_VERSION_NAME = "versionName";
    private final static String KEY_UPDATE_FORCE = "force"; // 该版本是否需要强制更新
    private final static String KEY_UPDATE_LOG = "log"; // 新版本更新日志
    private final static String KEY_UPDATE_URL = "url"; // 最新版本apk地址
    private final static String KEY_UPDATE_DETAIL_URL = "detailUrl"; // 新版本详细更新日志网址

    private boolean mInCheckVersion = false;

    /**
     * 检查是否有新版本
     */
    public void checkVersion() {
        if (mInCheckVersion) {
            return;
        }

        mInCheckVersion = true;
        AVQuery<AVObject> query = new AVQuery<>("AppConfig");
        query.whereEqualTo("group", "update");
        query.whereEqualTo("platform", "android");

        // 设置返回的结果
        ArrayList<String> keys = new ArrayList<>();
        keys.add(KEY_UPDATE_KEY);
        keys.add(KEY_UPDATE_VALUE);
        query.selectKeys(keys);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                mInCheckVersion = false;
                if (e == null) {
                    HashMap<String, String> values = new HashMap<String, String>();
                    String key, value;
                    for (AVObject object : list) {
                        key = object.getString(KEY_UPDATE_KEY);
                        value = object.getString(KEY_UPDATE_VALUE);
                        values.put(key, value);
                    }

                    String versionCodeStr = values.get(KEY_UPDATE_VERSION_CODE);
                    if (versionCodeStr != null) {
                        Integer versionCode = PlatformUtils.getVersionCode(Iam007Service.this);
                        try {
                            if (versionCode < Integer.valueOf(versionCodeStr)) {
                                // 需要更新
                                showUpdateDialog(values);
                            }
                        } catch (Exception e2) {
                            LogUtil.d("Exception:" + e2.getMessage());
                        }
                    }
                } else {
                    LogUtil.d("检查更新出现异常：" + e.getMessage());
                }
            }
        });
    }

    private final static String IGNORE_CURRENT_VERSION = "IGNORE_CURRENT_VERSION";
    private final static String IGNORE_CURRENT_VERSION_CODE = "IGNORE_CURRENT_VERSION_CODE";

    /**
     * 显示需要更新的dialog
     *
     * @param values
     */
    private void showUpdateDialog(HashMap<String, String> values) {
        LogUtil.d("Show update Dialog!");
        final int versionCode = Integer.valueOf(values.get(KEY_UPDATE_VERSION_CODE));

        long ignoreVersionCode = SharedPreferenceUtil.getLong(IGNORE_CURRENT_VERSION_CODE, -1L);
        boolean ignore = SharedPreferenceUtil.getBoolean(IGNORE_CURRENT_VERSION, false);
        if (ignore && (ignoreVersionCode == versionCode)) {
            // 忽略该版本
//            Toast.makeText(this, "ignore", Toast.LENGTH_SHORT).show();
            return;
        } else {
            SharedPreferenceUtil.setBoolean(IGNORE_CURRENT_VERSION, false);
            SharedPreferenceUtil.setLong(IGNORE_CURRENT_VERSION_CODE, -1L);
        }

        // 是否强制更新
        final boolean forceUpdate = Boolean.valueOf(values.get(KEY_UPDATE_FORCE));

        String versionName = values.get(KEY_UPDATE_VERSION_NAME);
        String log = values.get(KEY_UPDATE_LOG);
        String detailUrl = values.get(KEY_UPDATE_DETAIL_URL);
        final String url = values.get(KEY_UPDATE_URL);

        DialogBuilder dialogBuilder = new DialogBuilder(this);
        dialogBuilder.content(getString(R.string.update_dialog_content, versionName, log));
        dialogBuilder.title(R.string.update_dialog_title);
        dialogBuilder.positiveText(R.string.update_dialog_confirm);
        if (!forceUpdate) {
            dialogBuilder.neutralText(R.string.update_dialog_ignore);
            dialogBuilder.negativeText(R.string.update_dialog_cancel);
        }
        dialogBuilder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onNeutral(MaterialDialog dialog) {
                super.onNeutral(dialog);
                SharedPreferenceUtil.setBoolean(IGNORE_CURRENT_VERSION, true);
                SharedPreferenceUtil.setLong(IGNORE_CURRENT_VERSION_CODE,
                        Long.valueOf(versionCode));
            }

            @Override
            public void onPositive(MaterialDialog dialog) {
                downloadLatestApk(url, versionCode, forceUpdate);
            }
        });

        MaterialDialog dialog = dialogBuilder.build();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
//
//        // 设置内容
////        dialogBuilder.content(getString(R.string.update_dialog_content, versionName, log));
//        TextView contentTV = dialog.getContentView();
//        String contentStr = getString(R.string.update_dialog_content, versionName, log);
//        if (!URLUtil.isNetworkUrl(detailUrl)) {
//            contentTV.setText(contentStr);
//        } else {
//            String contentDetail = getString(R.string.update_dialog_content_detail);
//            contentStr += "\n\n" + contentDetail;
//            SpannableString spannableString = new SpannableString(contentStr);
//            spannableString.setSpan(new NoLineClickSpan(detailUrl),
//                    contentStr.length() - contentDetail.length(), contentStr.length(),
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            contentTV.setText(spannableString);
//        }
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
     * 下载最新的apk文件
     */
    private void downloadLatestApk(String url, int versionCode, boolean forceUpdate) {
        // 显示一个progress dialog
        DialogBuilder dialogBuilder = new DialogBuilder(this);
        dialogBuilder.content(R.string.update_dialog_downloading);
        dialogBuilder.cancelable(false);
        dialogBuilder.progress(false, 100);
        if (!forceUpdate) {
            dialogBuilder.positiveText(R.string.update_dialog_download_in_background);
        }

        final MaterialDialog dialog = dialogBuilder.build();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();

        HttpUtils httpUtils = new HttpUtils();
        File target = new File(getExternalCacheDir(), "latest.apk");

        // 检查是否已经下载完毕
        if (checkIsLatestAPK(target.getAbsolutePath(), versionCode)) {
            installAPK(target);
        } else {
            target.delete();

            httpUtils.download(url, target.getAbsolutePath(), true, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    installAPK(responseInfo.result);
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    LogUtil.d("Download APK Failed:" + e.getMessage() + " " + s);
                    Toast.makeText(Iam007Service.this, R.string.update_dialog_download_apk_failed,
                            Toast.LENGTH_SHORT).show();
                    try {
                        dialog.dismiss();
                    } catch (Exception e1) {

                    }
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    LogUtil.d(
                            "Total:" + total + ", current=" + current + ", isUploading=" + isUploading);
                    dialog.setProgress((int) (current / ((float) total) * 100));
                }
            });
        }
    }

    private boolean checkIsLatestAPK(String path, int versionCode) {
        boolean isLatest = false;
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pkgInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            if (pkgInfo != null) {
                if (getPackageName().equalsIgnoreCase(pkgInfo.packageName)
                        && (versionCode == pkgInfo.versionCode)) {
                    isLatest = true;
                }
            }
        } catch (Exception e) {
            LogUtil.d("checkIsLatestAPK:" + e.getMessage());
        }
        return isLatest;
    }

    private void installAPK(File file) {
        if (file.isFile() && file.exists()) {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(installIntent);
        }
    }

    private class NoLineClickSpan extends ClickableSpan {
        String url;

        public NoLineClickSpan(String url) {
            super();
            this.url = url;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
        }

        @Override
        public void onClick(View widget) {
            try {
                Intent intent = new Intent();
                intent.setClass(Iam007Service.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.DATA_URL, url);
                intent.putExtra(WebViewActivity.DATA_TITLE,
                        getString(R.string.update_dialog_detail_title));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    }
}
