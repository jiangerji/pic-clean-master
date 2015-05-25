package cn.iam007.pic.clean.master.recycler;

import java.io.File;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.utils.ImageUtils;

public class RecyclerFragment extends Fragment {
    private View mRootView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.recycle);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_recycler, null);
            initView(mRootView);
        }


        return mRootView;
    }

    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerImageContainer;
    private RecyclerImageAdapter mRecyclerImageAdapter;

    private void initView(View rootView) {
        // 初始化recyclerView
        mLayoutManager = new GridLayoutManager(getActivity(), 3);

        mRecyclerImageContainer = (RecyclerView) rootView.findViewById(R.id.recycler_images);
        mRecyclerImageContainer.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerImageContainer.setLayoutManager(mLayoutManager);

        mRecyclerImageAdapter = new RecyclerImageAdapter();
        mRecyclerImageContainer.setAdapter(mRecyclerImageAdapter);

        startScanRecycler();
    }

    private String mRecyclerPath = "/sdcard/DCIM/nexus";

    private void startScanRecycler() {
        File root = new File(mRecyclerPath);

        RecyclerImageItem item = null;
        if (root.isDirectory()) {
            File files[] = root.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    // 暂时什么都不做
                } else {
                    if (ImageUtils.isImage(f.getName())) {
                        item = new RecyclerImageItem(f.getAbsolutePath(),
                                f.getAbsolutePath());
                        mRecyclerImageAdapter.addItem(item);
                    }
                }
            }
        }
    }

    private boolean mSelectAll = false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recycler_menu, menu);

        MenuItem item = menu.findItem(R.id.action_select_all);

        if (mSelectAll) {
            item.setTitle(R.string.cancel_select_all);
        } else {
            item.setTitle(R.string.select_all);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all:
                mSelectAll = !mSelectAll;
                if (mSelectAll) {
                    item.setTitle(R.string.cancel_select_all);
                } else {
                    item.setTitle(R.string.select_all);
                }
                mRecyclerImageAdapter.selectAll(mSelectAll, mLayoutManager);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
