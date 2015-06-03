package cn.iam007.pic.clean.master.base;

import android.support.v4.app.Fragment;

import com.avos.avoscloud.AVAnalytics;

/**
 * Created by Administrator on 2015/6/2.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd(this.getClass().getSimpleName());
    }
}
