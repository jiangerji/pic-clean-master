package cn.iam007.pic.clean.master.recycler;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.io.File;

import cn.iam007.pic.clean.master.Constants;
import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.delete.DeleteRecyclerConfirmDialog;
import cn.iam007.pic.clean.master.duplicate.DuplicateItemImage;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

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

        if (SharedPreferenceUtil.getBoolean(SharedPreferenceUtil.HAS_DELETE_SOME_DUPLICATE_IMAGE, false)){
            mRecyclerImageAdapter.clear();
            SharedPreferenceUtil.setBoolean(SharedPreferenceUtil.HAS_DELETE_SOME_DUPLICATE_IMAGE, false);
        }
        startScanRecycler();
        return mRootView;
    }

    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerImageContainer;
    private RecyclerImageAdapter mRecyclerImageAdapter;

    private Button mRestoreBtn;
    private Button mDeleteBtn;

    private void initView(View rootView) {
        mRestoreBtn = (Button) rootView.findViewById(R.id.restore_btn);
        mDeleteBtn = (Button) rootView.findViewById(R.id.delete_btn);
        mDeleteBtn.setOnClickListener(mDeleteBtnClickListener);

        // 初始化recyclerView
        mLayoutManager = new GridLayoutManager(getActivity(), 3);

        mRecyclerImageContainer = (RecyclerView) rootView.findViewById(R.id.recycler_images);
        mRecyclerImageContainer.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerImageContainer.setLayoutManager(mLayoutManager);

        mRecyclerImageAdapter = new RecyclerImageAdapter();
        mRecyclerImageContainer.setAdapter(mRecyclerImageAdapter);
    }

    private View.OnClickListener mDeleteBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final DeleteRecyclerConfirmDialog dialog = DeleteRecyclerConfirmDialog.builder(getActivity());
            dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startToDelete();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

    public void startToDelete() {
        String content = getActivity().getString(R.string.deleting_progress);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title(R.string.delete)
                .content(content)
                .theme(Theme.LIGHT)
                .progress(true, 0);

        builder.titleColorRes(R.color.title)
                .dividerColorRes(R.color.divider)
                .positiveColorRes(R.color.red_light_EB5347)
                .negativeColorRes(R.color.black_light_333333)
                .backgroundColorRes(R.color.white_light_FAFAFA);
        final MaterialDialog deleteProgressDialog = builder.build();
        deleteProgressDialog.setCancelable(false);
        deleteProgressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startDeleteTask(deleteProgressDialog);
            }
        }, 500);

    }

    private void startDeleteTask(final MaterialDialog progressDialog) {
        new Thread(new Runnable() {
            public void run() {
                mRecyclerImageAdapter.deleteItems();
                progressDialog.dismiss();
            }
        }).start();
    }

    private File mRecyclerPath = Constants.getRecyclerPath();

    private void startScanRecycler() {
        RecyclerImageItem item;
        if (mRecyclerPath.isDirectory()) {
            File files[] = mRecyclerPath.listFiles();
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
    private MenuItem mSelectAllMenuItem = null;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recycler_menu, menu);

        mSelectAllMenuItem = menu.findItem(R.id.action_select_all);

        applySelectState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all:
                mSelectAll = !mSelectAll;
                applySelectState();
                mRecyclerImageAdapter.selectAll(mSelectAll, mLayoutManager);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applySelectState() {
        if (mSelectAll) {
            mSelectAllMenuItem.setTitle(R.string.cancel_select_all);
            mRestoreBtn.setEnabled(true);
            mDeleteBtn.setEnabled(true);
        } else {
            mSelectAllMenuItem.setTitle(R.string.select_all);
            mRestoreBtn.setEnabled(false);
            mDeleteBtn.setEnabled(false);
        }
    }

}
