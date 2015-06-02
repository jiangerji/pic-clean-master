package cn.iam007.pic.clean.master.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.widget.SystemBarTintManager;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;

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
        StatService.onResume(this);

        mExitHintToast = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);

        if (mExitHintToast != null){
            mExitHintToast.cancel();
        }
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

    // 上次按下返回键的时间
    private long mPreBackPressedTS = 0;
    private Toast mExitHintToast = null;

    private Handler mToastHandler = new Handler();

    @Override
    public void onBackPressed() {
        LogUtil.d("onBackPressed!");
        long currentTS = System.currentTimeMillis();
        if (currentTS - mPreBackPressedTS < 3000){
            super.onBackPressed();
        }

        if (mExitHintToast != null){
            mExitHintToast.cancel();
        }
        mExitHintToast = Toast.makeText(this, R.string.exit_hint, Toast.LENGTH_SHORT);
        mExitHintToast.show();
        mPreBackPressedTS = currentTS;

        mToastHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mExitHintToast != null){
                    mExitHintToast.cancel();
                    mExitHintToast = null;
                }
            }
        }, 3000);
    }
}
