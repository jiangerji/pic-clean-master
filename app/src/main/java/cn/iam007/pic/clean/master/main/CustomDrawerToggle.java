package cn.iam007.pic.clean.master.main;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

/**
 * Created by Administrator on 2015/6/1.
 */
public class CustomDrawerToggle extends ActionBarDrawerToggle {

    private Activity mActivity;
    private boolean mDrawerLayoutEnable = true;
    private DrawerLayout mDrawerLayout;

    public CustomDrawerToggle(Activity activity,
                              DrawerLayout drawerLayout,
                              Toolbar toolbar,
                              int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        mActivity = activity;
        mDrawerLayout = drawerLayout;

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayoutEnable) {
                    toggle();
                }
            }
        });
    }

    private void toggle() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void enableNavigation() {
        mDrawerLayoutEnable = true;
    }

    public void disableNavigation() {
        mDrawerLayoutEnable = false;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);

        TextView countTextView = (TextView) drawerView.findViewById(R.id.total_delete_file_count);
        long count = SharedPreferenceUtil.getLong(
                SharedPreferenceUtil.HANDLED_DUPLICATE_IMAGES_COUNT, 0L);

        String content = mActivity.getString(R.string.slide_menu_already_handled_pic_count, count);

        SpannableStringBuilder style = new SpannableStringBuilder(content);
        int startIndex = content.length() - 1;
        style.setSpan(new AbsoluteSizeSpan(16, true),
                startIndex,
                startIndex + 1,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置textview的背景颜色
        countTextView.setText(style);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);
        LogUtil.d("onDrawerStateChanged:" + newState);
    }

}
