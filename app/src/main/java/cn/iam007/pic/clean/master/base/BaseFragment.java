package cn.iam007.pic.clean.master.base;

import android.support.v4.app.Fragment;

import com.avos.avoscloud.AVAnalytics;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/6/2.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        String name = this.getClass().getSimpleName();
        AVAnalytics.onFragmentStart(name);
//        JPushInterface.onFragmentResume(getActivity(), name);
    }

    @Override
    public void onPause() {
        super.onPause();
        String name = this.getClass().getSimpleName();
        AVAnalytics.onFragmentEnd(name);
//        JPushInterface.onFragmentPause(getActivity(), name);
    }
}
