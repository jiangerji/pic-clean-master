package cn.iam007.pic.clean.master.base;

import android.support.v4.app.Fragment;

import com.baidu.mobstat.StatService;

/**
 * Created by Administrator on 2015/6/2.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        StatService.onPause(this);
    }
}
