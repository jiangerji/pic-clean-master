package cn.iam007.pic.clean.master.screenshot;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.avos.avoscloud.AVAnalytics;

import java.io.File;
import java.util.ArrayList;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseActivity;
import cn.iam007.pic.clean.master.base.BaseFragment;
import cn.iam007.pic.clean.master.delete.DeleteConfirmDialog;
import cn.iam007.pic.clean.master.delete.DeleteItem;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;

/**
 * Created by Administrator on 2015/7/24.
 */
public class ScreenshotFragment extends BaseFragment implements
        ScreenshotImageItem.OnCheckedChangeListener {

    private final static String AV_TAG = "screenshot.fragment";

    private FloatingActionButton mDeleteBtn;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mScreenshotImageContainer;
    private ScreenshotImageAdapter mScreenshotImageAdapter;

    private boolean mSelectAll = false;
    private MenuItem mSelectAllMenuItem = null;
    private View mRootView;

    @Override
    public String getFragmentTitle() {
        return getResources().getString(R.string.screenshot);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.screenshot_menu, menu);
        mSelectAllMenuItem = menu.findItem(R.id.action_select_all);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all:
                if (mSelectAll) {
                    AVAnalytics.onEvent(getActivity(), "cancel.select.all", AV_TAG);
                } else {
                    AVAnalytics.onEvent(getActivity(), "select.all", AV_TAG);
                }
                mSelectAll = !mSelectAll;
                applySelectState();
                mScreenshotImageAdapter.selectAll(mSelectAll, mLayoutManager);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applySelectState() {
        if (mSelectAll) {
            mSelectAllMenuItem.setTitle(R.string.cancel_select_all);
        } else {
            mSelectAllMenuItem.setTitle(R.string.select_all);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_screenshot, null);
            initView(mRootView);
            startScanScreenshot();
        }

        return mRootView;
    }

    private void initView(View rootView) {
        mDeleteBtn = (FloatingActionButton) rootView.findViewById(R.id.delete);
        mDeleteBtn.setOnClickListener(mDeleteBtnClickListener);

        // 初始化recyclerView
        mLayoutManager = new GridLayoutManager(getActivity(), 3);

        mScreenshotImageContainer = (RecyclerView) rootView.findViewById(R.id.screenshot_images);
        mScreenshotImageContainer.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mScreenshotImageContainer.setLayoutManager(mLayoutManager);

        mScreenshotImageAdapter = new ScreenshotImageAdapter();
        mScreenshotImageContainer.setAdapter(mScreenshotImageAdapter);
//        mRecyclerImageAdapter.setOnItemClickListener(
//                new RecyclerImageAdapter.MyItemClickListener() {
//
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        RecyclerHoldAdapter holdAdapter = RecyclerHoldAdapter.getInstance();
//                        holdAdapter.setHoldAdapter(mRecyclerImageAdapter);
//                        Intent intent = new Intent(getActivity(),
//                                PhotoActivity.class);
//                        intent.putExtra("position", position);
//                        intent.putExtra("fromFragment", MainActivity.RECYCLER_FRAGMENT);
//                        getActivity().startActivity(intent);
//                    }
//                });
    }

    private void scanFile(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                File files[] = file.listFiles();

                ArrayList<File> subDirs = new ArrayList<>();

                for (File f : files) {
                    if (f.isDirectory()) {
                        subDirs.add(f);
                    } else {
                        scanFile(f);
                    }
                }

                for (File f : subDirs) {
                    scanFile(f);
                }
            } else {
                if (ImageUtils.isImage(file.getName())) {
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), option);

                    if (option.outWidth == PlatformUtils.getScreenWidth(getActivity()) &&
                            option.outHeight == PlatformUtils.getDisplayScreenHeight(
                                    getActivity())) {
                        ScreenshotImageItem item = new ScreenshotImageItem(file.getAbsolutePath());
                        item.setOnCheckedChangeListener(this);
                        LogUtil.d("Screenshot:" + item);
                        mScreenshotImageAdapter.addItem(item);
                    }
                }
            }
        }
    }

    private void startScanScreenshot() {
        ((BaseActivity) getActivity()).showProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取截屏目录
                File file = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);

                if (file != null && file.isDirectory()) {
                    scanFile(file);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        mScreenshotImageAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private int mSelected = 0;

    @Override
    public void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            mSelected++;
        } else {
            mSelected--;
        }

        if (mSelected > 0) {
            if (mDeleteBtn.getVisibility() != View.VISIBLE) {
                mDeleteBtn.setVisibility(View.VISIBLE);
                mDeleteBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        R.anim.abc_slide_in_bottom));
            }
        } else {
            mDeleteBtn.setVisibility(View.INVISIBLE);
            mDeleteBtn.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                    R.anim.abc_slide_out_bottom));
        }
    }

    private ArrayList<DeleteItem> mDeleteItems;

    private View.OnClickListener mDeleteBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DeleteConfirmDialog dialog = DeleteConfirmDialog.builder(getActivity());
            dialog.addDeleteItems(mScreenshotImageAdapter.getSelectedItems());
            dialog.setOnDeleteStatusListener(new DeleteConfirmDialog.OnDeleteStatusListener() {
                @Override
                public void onDeleteStart() {
                    mDeleteItems = new ArrayList<>();
                }

                @Override
                public void onDeleteImage(DeleteItem item) {
                    mDeleteItems.add(item);
                }

                @Override
                public void onDeleteFinish() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mScreenshotImageAdapter.removeItems(mDeleteItems);
                            mScreenshotImageAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });
            dialog.show();
        }
    };
}
