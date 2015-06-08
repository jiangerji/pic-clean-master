package cn.iam007.pic.clean.master.base;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.feedback.Comment;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.avos.avoscloud.feedback.FeedbackThread;

import java.util.List;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.widget.SystemBarTintManager;
import cn.iam007.pic.clean.master.feedback.FeedbackActivity;
import cn.iam007.pic.clean.master.main.MainActivity;
import cn.iam007.pic.clean.master.utils.DialogBuilder;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;
import cn.jpush.android.api.JPushInterface;

public class BaseActivity extends AppCompatActivity {
    protected final static String TAG = "BaseActivity";

    /**
     * 是否是debug模式，如果不是，所有debug开头的函数不会执行
     */
    protected static boolean DEBUG_MODE = true;

    static {
        DEBUG_MODE = false;
    }

    private FrameLayout mContainer = null;
    private Toolbar mToolbar;

    private SystemBarTintManager mTintManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.primary);
        //        mTintManager.setNavigationBarTintEnabled(true);

        mContainer = (FrameLayout) findViewById(R.id.container);

        initView();

        if (!(BaseActivity.this instanceof FeedbackActivity)) {
            final FeedbackAgent agent = new FeedbackAgent(this);
            final int originalCount = agent.getDefaultThread().getCommentsList().size();
            agent.getDefaultThread().sync(new FeedbackThread.SyncCallback() {
                @Override
                public void onCommentsSend(List<Comment> list, AVException e) {

                }

                @Override
                public void onCommentsFetch(List<Comment> list, AVException e) {
                    LogUtil.d(
                            "count=" + list.size() + " " + originalCount + " " + BaseActivity.this);
                    if (originalCount < list.size()) {
                        openFeedbackDialog();
                    }
                }
            });
        }
    }

    private void openFeedbackDialog() {
        MaterialDialog.ButtonCallback callback = new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                openFeedback();
            }
        };

        DialogBuilder builder = new DialogBuilder(this);
        builder.title(R.string.open_feedback_dialog_title).content(
                R.string.open_feedback_dialog_content).positiveText(
                R.string.open_feedback_dialog_positive).negativeText(
                R.string.cancel).callback(callback).build().show();
    }

    /**
     * 打开反馈页面
     */
    protected final void openFeedback() {
        Intent intent = new Intent(this, FeedbackActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            PlatformUtils.applyFonts(mToolbar);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 获取activity的toolbar实例
     *
     * @return
     */
    public final Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * 设置状态栏的背景颜色
     *
     * @param color
     */
    public final void setStatusBarTintColor(int color) {
        mTintManager.setStatusBarTintColor(color);
    }

    /**
     * 设置工具栏的背景颜色
     *
     * @param color
     */
    public final void setToolbarBackgroundColor(int color) {
        mToolbar.setBackgroundColor(color);
    }

    /**
     * 设置导航栏背景颜色
     *
     * @param color
     */
    public final void setNavigationBarTintColor(int color) {
        mTintManager.setNavigationBarTintColor(color);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        AVAnalytics.onPause(this);
        JPushInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setContentView(int layoutResID) {
        View.inflate(this, layoutResID, mContainer);
        PlatformUtils.applyFonts(mContainer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(0, R.anim.slide_out_right);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        boolean launch = needLaunchMainActivity();
        if (launch){
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
        }
        super.finish();
    }

    private boolean needLaunchMainActivity() {
        boolean launcher = false;
        try{
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1000);

            LogUtil.d(TAG, "================running app===============");
            String packageName;
            String className = null;
            ActivityManager.RunningTaskInfo _info = null;
            for (ActivityManager.RunningTaskInfo info : list) {
                packageName = info.topActivity.getPackageName();
                className = info.topActivity.getClassName();
                if (packageName.equalsIgnoreCase(getPackageName())){
                    _info = info;
                    break;
                }
            }

            if (_info.numActivities == 1){
                if (!className.equalsIgnoreCase(MainActivity.class.getName())){
                    launcher = true;
                }

            }
        } catch (Exception e){

        }

        return launcher;
    }
}
