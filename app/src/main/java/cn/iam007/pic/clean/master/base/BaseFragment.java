package cn.iam007.pic.clean.master.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.avos.avoscloud.AVAnalytics;

import cn.iam007.pic.clean.master.utils.PlatformUtils;

/**
 * Created by Administrator on 2015/6/2.
 */
public class BaseFragment extends Fragment {

    /**
     * 是否有menu
     *
     * @return 返回该fragm是否有menu
     */
    protected boolean hasOptionMenu() {
        return true;
    }

    /**
     * 获取fragment的title
     *
     * @return
     */
    public String getFragmentTitle() {
        return null;
    }

    public Toolbar getToolbar() {
        Toolbar toolbar = null;
        try {
            toolbar = ((BaseActivity) getActivity()).getToolbar();
        } catch (Exception e) {

        }
        return toolbar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(hasOptionMenu());
        String title = getFragmentTitle();
        if (!TextUtils.isEmpty(title)) {
            getActivity().setTitle(title);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            PlatformUtils.applyFonts(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String name = this.getClass().getSimpleName();
        AVAnalytics.onFragmentStart(name);
    }

    @Override
    public void onPause() {
        super.onPause();
        String name = this.getClass().getSimpleName();
        AVAnalytics.onFragmentEnd(name);
    }
}
