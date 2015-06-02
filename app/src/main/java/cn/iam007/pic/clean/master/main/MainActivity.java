package cn.iam007.pic.clean.master.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseActivity;
import cn.iam007.pic.clean.master.duplicate.DuplicateScanFragment;
import cn.iam007.pic.clean.master.recycler.RecyclerFragment;
import cn.iam007.pic.clean.master.utils.PlatformUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

import com.tonicartos.superslim.LayoutManager;

public class MainActivity extends BaseActivity {

    private Toolbar mToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    DrawerLayout mDrawerLayout = null;
    LayoutManager mLayoutManager = null;

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


//        long count = SharedPreferenceUtil.getLong(SharedPreferenceUtil.HANDLED_DUPLICATE_IMAGES_COUNT, 0L);
//        mCountTextView = (TextView) findViewById(R.id.total_delete_file_count);
//        String content = getString(R.string.slide_menu_already_handled_pic_count, count);
//
//        SpannableStringBuilder style = new SpannableStringBuilder(content);
//        int startIndex = content.length() - 1;
//        style.setSpan(new AbsoluteSizeSpan(16, true),
//                startIndex,
//                startIndex + 1,
//                Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置textview的背景颜色
//        mCountTextView.setText(style);

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
                break;

            default:
                break;
            }

            setFragment(pos);
            mDrawerLayout.closeDrawers();
        }
    };

    private int mCurrentFragment = -1;

    private final static int DUPLICATE_SCAN_FRAGMENT = 0x00;
    private final static int RECYCLER_FRAGMENT = 0x01;
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

}
