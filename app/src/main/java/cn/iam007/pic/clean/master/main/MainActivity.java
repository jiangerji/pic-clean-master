package cn.iam007.pic.clean.master.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseActivity;
import cn.iam007.pic.clean.master.duplicate.DuplicateScanFragment;
import cn.iam007.pic.clean.master.push.PushManager;
import cn.iam007.pic.clean.master.recycler.RecyclerFragment;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;

public class MainActivity extends BaseActivity {

    private Toolbar mToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    DrawerLayout mDrawerLayout = null;

    private void initView() {
        mToolbar = getToolbar();

        _initSlideMenu();

        showDuplicateScanFragment();
    }

    private void _initSlideMenu() {
        // Handle DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        PlatformUtils.applyFonts(mDrawerLayout);

        // Handle ActionBarDrawerToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new CustomDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        actionBarDrawerToggle.syncState();

        // Handle different Drawer States :D
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        View scan = findViewById(R.id.duplicate_scan);
        scan.setOnClickListener(mDrawLayoutOnClickListener);

        View recycler = findViewById(R.id.recycler);
        recycler.setOnClickListener(mDrawLayoutOnClickListener);

        View feedback = findViewById(R.id.feedback);
        feedback.setOnClickListener(mDrawLayoutOnClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mExitHintToast = null;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mExitHintToast != null) {
            mExitHintToast.cancel();
        }
    }

    private OnClickListener mDrawLayoutOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int pos = -1;
            switch (v.getId()) {
                case R.id.duplicate_scan:
                    pos = DUPLICATE_SCAN_FRAGMENT;
                    break;

                case R.id.recycler:
                    pos = RECYCLER_FRAGMENT;
                    break;

                case R.id.feedback:
                    openFeedback();
                    break;

                default:
                    break;
            }

            setFragment(pos);
            mDrawerLayout.closeDrawers();
        }
    };

    private int mCurrentFragment = -1;

    public final static int DUPLICATE_SCAN_FRAGMENT = 0x00;
    public final static int RECYCLER_FRAGMENT = 0x01;
    private DuplicateScanFragment mDuplicateScanFragment = null;
    private RecyclerFragment mRecyclerFragment = null;

    private void setFragment(int pos) {
        if (pos != mCurrentFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = null;
            switch (pos) {
                case DUPLICATE_SCAN_FRAGMENT:
                    if (mDuplicateScanFragment == null) {
                        mDuplicateScanFragment = new DuplicateScanFragment();
                    }
                    fragment = mDuplicateScanFragment;
                    break;

                case RECYCLER_FRAGMENT:
                    if (mRecyclerFragment == null) {
                        mRecyclerFragment = new RecyclerFragment();
                    }
                    fragment = mRecyclerFragment;
                    break;

                default:
                    break;
            }
            if (fragment != null) {
                mCurrentFragment = pos;
                ft.replace(R.id.fragment_layout, fragment);
            }
            ft.commit();
        }
    }

    private void showDuplicateScanFragment() {
        setFragment(DUPLICATE_SCAN_FRAGMENT);
    }

    // 上次按下返回键的时间
    private long mPreBackPressedTS = 0;
    private Toast mExitHintToast = null;

    private Handler mToastHandler = new Handler();

    @Override
    public void onBackPressed() {
        LogUtil.d("onBackPressed!");
        long currentTS = System.currentTimeMillis();
        if (currentTS - mPreBackPressedTS < 3000) {
            super.onBackPressed();
        }

        if (mExitHintToast != null) {
            mExitHintToast.cancel();
        }
        mExitHintToast = Toast.makeText(this, R.string.exit_hint, Toast.LENGTH_SHORT);
        mExitHintToast.show();
        mPreBackPressedTS = currentTS;

        mToastHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mExitHintToast != null) {
                    mExitHintToast.cancel();
                    mExitHintToast = null;
                }
            }
        }, 3000);
    }
}
