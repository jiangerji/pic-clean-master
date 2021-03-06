package cn.iam007.pic.clean.master.duplicate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.tonicartos.superslim.LayoutManager;

import java.io.File;
import java.util.ArrayList;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseFragment;
import cn.iam007.pic.clean.master.base.widget.CustomRecyclerView;
import cn.iam007.pic.clean.master.delete.DeleteConfirmDialog;
import cn.iam007.pic.clean.master.delete.DeleteConfirmDialog.OnDeleteStatusListener;
import cn.iam007.pic.clean.master.delete.DeleteItem;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageAdapter.HeaderViewCallback;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageFindTask.DuplicateFindCallback;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageFindTask.ImageHolder;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageFindTask.SectionItem;
import cn.iam007.pic.clean.master.duplicate.gallery.PhotoActivity;
import cn.iam007.pic.clean.master.main.MainActivity;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.PlatformUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;
import cn.iam007.pic.clean.master.utils.StringUtils;
import cn.iam007.pic.clean.master.utils.StringUtils.Unit;

public class DuplicateScanFragment extends BaseFragment {

    private final static int SCAN_HINT_UPDATE = 0x01;
    private final static int SCAN_PROGRESS_UPDATE = 0x02;
    private final static int SCAN_DUPLICATE_SECTION_FIND = 0x03; // 找到一组相似图片
    private final static int SCAN_DUPLICATE_FIND_FINISHED = 0x04; // 查找相似图片结束
    private final static int DELETE_DUPLICATE_FINISHED = 0x05; // 删除完成
    protected View mScanHeaderView;
    protected TextView mScanCount;
    protected TextView mScanCountUnit;
    protected TextView mScanResultHint;
    protected TextView mScanHint;
    protected ProgressBar mScanProgressBar;
    boolean mIsStarted = false;
    private View mRootView;
    private View mScanHintHeaderContainer;
    private TextView mScanHintHeader;
    private ProgressBar mScanHintHeaderProgressBar;
    private LayoutManager mLayoutManager;
    private CustomRecyclerView mDuplicateImageContainer;
    private DuplicateImageAdapter mDuplicateImageAdapter;
    private FloatingActionButton mFloatingButton;
    private View mDeleteBtnContainer;
    private Button mDeleteBtn;
    private ProgressBar mProgressBar;
    private Toast mDeleteHint = null;
    // 保存需要被删除组
    private ArrayList<DuplicateItem> mDeleteDuplicateItemHeader = new ArrayList<>();
    private File mCameraDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    private long mDuplicateImageFilesSize = 0; // 所有已发现相似图片文件的总大小
    private int mDuplicateImageFileCount = 0; // 所有已发现相似图片文件的数量
    private boolean mDuplicateImagesSectionChanged = false;
    private boolean mDuplicateImageScanFinished = false;
    private String mScanHintText = "";
    @SuppressLint("RtlHardcoded")
    private int mScanHintGravity = Gravity.LEFT;
    private HeaderViewCallback mHeaderViewCallback = new HeaderViewCallback() {

        @Override
        public void onHeaderViewCreated(int layout, View view) {
            if (layout == R.layout.fragment_duplicate_scan_header) {
                mScanHeaderView = view;

                mScanCount = (TextView) view.findViewById(R.id.scanCount);
                mScanCountUnit = (TextView) view.findViewById(R.id.scanCountUnit);
                mScanResultHint = (TextView) view.findViewById(R.id.scanResultHint);

                Unit unit = StringUtils.convertFileSize(mDuplicateImageFilesSize);
                if (mScanCount != null) {
                    mScanCount.setText(unit.getCount());
                }

                if (mScanCountUnit != null) {
                    mScanCountUnit.setText(unit.getUnit());
                }
            } else if (layout == R.layout.fragment_duplicate_scan_progress) {
                mScanHint = (TextView) view.findViewById(R.id.scanHint);
                mScanHint.setText(mScanHintText);
                mScanHint.setGravity(mScanHintGravity);

                mScanProgressBar = (ProgressBar) view.findViewById(R.id.scanProgress);
            }
        }
    };
    private int mCurrentProgress = 0;
    private Handler mUpdateHandler = new Handler();
    private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScanHint != null) {
                mScanHint.setText(mScanHintText);
                mScanHint.setGravity(mScanHintGravity);
            }

            if (mScanHintHeader != null) {
                mScanHintHeader.setText(mScanHintText);
                mScanHintHeader.setGravity(mScanHintGravity);
            }

            if (mScanProgressBar != null) {
                mScanProgressBar.setProgress(mCurrentProgress);
            }

            if (mScanHintHeaderProgressBar != null) {
                mScanHintHeaderProgressBar.setProgress(mCurrentProgress);
            }

            if (!mDuplicateImageScanFinished) {
                mUpdateHandler.postDelayed(mUpdateRunnable, 33);
            }
        }
    };
    //智能选择删除哪些图片
    private MenuItem mAutoSelect = null;
    private boolean mAutoSelected = false;
    private String SELECTED_DELETE_IMAGE_TOTAL_SIZE =
            SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_SIZE;
    private String SELECTED_DELETE_IMAGE_TOTAL_NUM =
            SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_NUM;
    private Handler mHandler = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_HINT_UPDATE:
                    mScanHintText = (String) msg.obj;
                    mScanHintGravity = msg.arg1;
                    break;

                case SCAN_PROGRESS_UPDATE:
                    mCurrentProgress = msg.arg1;
                    break;

                case SCAN_DUPLICATE_SECTION_FIND:
                    SectionItem sectionItem = (SectionItem) msg.obj;
                    doDuplicateSectionFind(sectionItem);
                    break;

                case SCAN_DUPLICATE_FIND_FINISHED:
                    mDuplicateImageScanFinished = true;
                    mScanProgressBar.setVisibility(View.INVISIBLE);
                    mScanHintHeaderProgressBar.setVisibility(View.INVISIBLE);

                    startFinishAnimation();
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (mDuplicateImagesSectionChanged) {
                                mDuplicateImageAdapter.notifyDataSetChanged();
                                mDuplicateImagesSectionChanged = false;
                            }
                        }
                    }, 50);
                    break;

                case DELETE_DUPLICATE_FINISHED:
                    deleteDuplicateFinished();
                    break;

                default:
                    break;
            }
            return false;
        }
    });
    private OnDeleteStatusListener mOnDeleteStatusListener = new OnDeleteStatusListener() {
        @Override
        public void onDeleteStart() {

        }

        @Override
        public void onDeleteImage(DeleteItem filePath) {

        }

        @Override
        public void onDeleteFinish() {
            mHandler.sendEmptyMessage(DELETE_DUPLICATE_FINISHED);
        }
    };
    private OnClickListener mDeleteBtnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            AVAnalytics.onEvent(getActivity(), "delete.btn.click", AV_TAG);
            DeleteConfirmDialog dialog = DeleteConfirmDialog.builder(getActivity());

            int index = 0;
            DuplicateItem item = null;
            ArrayList<DuplicateItemImage> items = null;
            int count = 0;
            while (index < mDuplicateImageAdapter.getRealItemCount()) {
                item = mDuplicateImageAdapter.getItem(index);
                if (item.isHeader()) {
                    items = ((DuplicateItemHeader) item).getSelectedItem();
                    dialog.addDeleteItems(items);
                    count += items.size();

                    if (items.size() > 0) {
                        // 该header有选中的图片，表示已经处理
                        mDeleteDuplicateItemHeader.add(item);
                    }
                }
                index++;
            }

            if (count > 0) {
                dialog.show();
                dialog.setOnDeleteStatusListener(mOnDeleteStatusListener);
            } else {
                if (mDeleteHint != null) {
                    mDeleteHint.cancel();
                }

                mDeleteHint =
                        Toast.makeText(getActivity(), R.string.delete_hint, Toast.LENGTH_SHORT);
                mDeleteHint.show();
            }
        }
    };
    @SuppressLint("RtlHardcoded")
    private DuplicateFindCallback mDuplicateFindCallback = new DuplicateFindCallback() {

        @Override
        public void onDuplicateFindStart(final String folder, int count) {
            Message msg = mHandler.obtainMessage(SCAN_HINT_UPDATE);
            msg.obj = String.format(getString(R.string.scanning), folder, "");
            msg.arg1 = Gravity.LEFT;
            mHandler.sendMessage(msg);
            LogUtil.d("Find start:" + folder + ", " + count);
        }

        @Override
        public void onDuplicateFindExecute(final String file, long size) {
            Message msg = mHandler.obtainMessage(SCAN_HINT_UPDATE);
            File aFile = new File(file);
            msg.obj = String.format(getString(R.string.scanning), aFile.getParentFile().getName(),
                    aFile.getName());
            msg.arg1 = Gravity.LEFT;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onDuplicateFindProgressUpdate(final double progress) {
            Message msg = mHandler.obtainMessage(SCAN_PROGRESS_UPDATE);
            msg.arg1 = (int) (100 * progress);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onDuplicateFindFinished(
                final int fileCount, final long fileSize) {
            try {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).enableDrawerLayout();
                }
            } catch (Exception e) {

            }
            mIsStarted = false;
            LogUtil.d("Find finished:" + mDuplicateImageFileCount);
            Message msg = mHandler.obtainMessage(SCAN_HINT_UPDATE);
            msg.obj = String.format(getString(R.string.scan_result_progress_hint),
                    fileCount,
                    mDuplicateImageFileCount);
            msg.arg1 = Gravity.CENTER;
            mHandler.sendMessage(msg);

            mHandler.sendEmptyMessage(SCAN_DUPLICATE_FIND_FINISHED);
        }

        @Override
        public void onDuplicateSectionFind(SectionItem sectionItem) {
            //            LogUtil.d("Find onDuplicateSectionFind");
            Message msg = mHandler.obtainMessage(SCAN_DUPLICATE_SECTION_FIND);
            msg.obj = sectionItem;
            mHandler.sendMessage(msg);

            if (sectionItem != null && sectionItem.getImages() != null) {
                mDuplicateImageFileCount += sectionItem.getImages().size();
            }
        }

    };
    private OnClickListener startBtnOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!mIsStarted) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).disableDrawerLayout();
                }
                startDuplicateImageFindTask();
                mProgressBar.setVisibility(View.VISIBLE);
                mFloatingButton.setVisibility(View.INVISIBLE);
                mFloatingButton.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        R.anim.abc_slide_out_bottom));
//                mStartProgress.startAnimation(true, 750L);
                mIsStarted = true;
            }
        }
    };
    private OnSharedPreferenceChangeListener mSharedPreferenceChangeListener =
            new OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    if (key.equalsIgnoreCase(SELECTED_DELETE_IMAGE_TOTAL_SIZE)) {
                        if (mDeleteBtn != null) {
                            long count = sharedPreferences.getLong(key, 0);
                            if (count <= 0) {
                                mDeleteBtn.setText(R.string.delete);
                            } else {
                                mDeleteBtn.setText(getString(R.string.delete_with_size,
                                        StringUtils.convertFileSize(count)));
                            }
                        }
                    }

                }
            };

    @Override
    public String getFragmentTitle() {
        return getString(R.string.slide_menu_duplicate_scan);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_duplicate_scan,
                    null);
            initView(mRootView);
        }

        return mRootView;
    }

    private void initView(View rootView) {
        mScanHintHeaderContainer = rootView.findViewById(R.id.scanHintHeaderContainer);
        mScanHintHeader = (TextView) mScanHintHeaderContainer.findViewById(R.id.scanHint);
        mScanHintHeaderProgressBar =
                (ProgressBar) mScanHintHeaderContainer.findViewById(R.id.scanProgress);

        // 初始化recyclerView
        mLayoutManager = new LayoutManager(getActivity());

        mDuplicateImageContainer =
                (CustomRecyclerView) rootView.findViewById(R.id.duplicate_images);
        mDuplicateImageContainer.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mDuplicateImageContainer.setLayoutManager(mLayoutManager);
        mDuplicateImageContainer.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = mLayoutManager.findFirstVisibleItemPosition();
                if (i >= 1) {
                    mScanHintHeaderContainer.setVisibility(View.VISIBLE);

                    mScanHintHeader.setText(mScanHintText);
                    mScanHintHeader.setGravity(mScanHintGravity);
                } else if (i == 0) {
                    if (mScanHintHeaderContainer != null) {
                        mScanHintHeaderContainer.setVisibility(View.INVISIBLE);
                    }
                }

                if (mDuplicateImagesSectionChanged) {
                    mDuplicateImageAdapter.notifyDataSetChanged();
                    mDuplicateImagesSectionChanged = false;
                }
            }
        });

        mDuplicateImageAdapter = new DuplicateImageAdapter(getActivity());
        mDuplicateImageAdapter.setHeaderViewCallback(mHeaderViewCallback);
        mDuplicateImageAdapter.addCustomHeader(R.layout.fragment_duplicate_scan_header);
        mDuplicateImageAdapter.addCustomHeader(R.layout.fragment_duplicate_scan_progress);
        mDuplicateImageContainer.setAdapter(mDuplicateImageAdapter);
        mDuplicateImageAdapter.setOnItemClickListener(
                new DuplicateImageAdapter.MyItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("debug",
                                "click item postion: "
                                        + (position - mDuplicateImageAdapter.getCustomHeaderCount()));
                        if (mDuplicateImageScanFinished) {
                            DuplicateHoldAdapter holdAdapter = DuplicateHoldAdapter.getInstance();
                            holdAdapter.setHoldAdapter(mDuplicateImageAdapter);
                            Intent intent = new Intent(getActivity(),
                                    PhotoActivity.class);
                            intent.putExtra("position",
                                    (position - mDuplicateImageAdapter.getCustomHeaderCount()));
                            intent.putExtra("fromFragment", MainActivity.DUPLICATE_SCAN_FRAGMENT);
                            getActivity().startActivity(intent);
                        }
                    }
                });

        mFloatingButton =
                (FloatingActionButton) rootView.findViewById(R.id.startProgress);
        mFloatingButton.setOnClickListener(startBtnOnClickListener);
        mFloatingButton.setVisibility(View.VISIBLE);
        mFloatingButton.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.abc_slide_in_bottom));

        mDeleteBtnContainer = rootView.findViewById(R.id.delete_btn_container);
        mDeleteBtn = (Button) rootView.findViewById(R.id.delete_btn);
        mDeleteBtn.setOnClickListener(mDeleteBtnClickListener);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
    }

    @SuppressLint("SdCardPath")
    private void startDuplicateImageFindTask() {
        DuplicateImageFindTask mDuplicateImageFindTask =
                new DuplicateImageFindTask(mDuplicateFindCallback, getActivity());

        if (mCameraDir != null) {
            String rootDir = mCameraDir.getAbsolutePath();//"/sdcard/DCIM/nexus";
            LogUtil.d("Start scan " + rootDir);
            mDuplicateImageFindTask.execute(rootDir);
        }

        mUpdateHandler.post(mUpdateRunnable);
    }

    //
    private void deleteDuplicateFinished() {
        mDeleteBtn.setText(R.string.delete);
        mAutoSelect.setTitle(R.string.auto_select);

        for (DuplicateItem item : mDeleteDuplicateItemHeader) {
            mDuplicateImageAdapter.deleteSection(item);
        }
        mDeleteDuplicateItemHeader.clear();
        mDuplicateImageAdapter.notifyDataSetChanged();
    }

    /**
     * 处理找到相似图片
     *
     * @param sectionItem
     */
    private void doDuplicateSectionFind(SectionItem sectionItem) {
        if (sectionItem != null) {
            ArrayList<ImageHolder> images = sectionItem.getImages();
            for (ImageHolder image : images) {
                mDuplicateImageFilesSize += image.getImageSize();
            }

            Unit unit = StringUtils.convertFileSize(mDuplicateImageFilesSize);
            if (mScanCount != null) {
                mScanCount.setText(unit.getCount());
            }

            if (mScanCountUnit != null) {
                mScanCountUnit.setText(unit.getUnit());
            }

            mDuplicateImageAdapter.addSection(sectionItem);

            if (mLayoutManager.findLastVisibleItemPosition() < mDuplicateImageAdapter.getRealItemCount()) {
                mDuplicateImagesSectionChanged = true;
            } else {
                mDuplicateImageAdapter.notifyDataSetChanged();
                mDuplicateImagesSectionChanged = false;
            }
        }
    }

    private void startFinishAnimation() {
        // 使用sharedpreference来获取当前选中图片的大小
        SharedPreferenceUtil.setLong(SELECTED_DELETE_IMAGE_TOTAL_SIZE,
                0L);
        SharedPreferenceUtil.setLong(SELECTED_DELETE_IMAGE_TOTAL_NUM,
                0L);

        // 显示menu
        mAutoSelect.setVisible(true);

        mProgressBar.setVisibility(View.GONE);
        mProgressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.abc_fade_out));

        mDeleteBtnContainer.setVisibility(View.VISIBLE);
        mDeleteBtnContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                R.anim.abc_slide_in_bottom));

        final int scanHeaderViewHeight = mScanHeaderView.getHeight();
        final float scanCountTextSize = mScanCount.getTextSize();
        final float scanCountUnitTextSize = mScanCountUnit.getTextSize();
        final float scanResultHintTextSize = mScanResultHint.getTextSize();
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.375f)
                .setDuration(750);
        animator.start();
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float) valueAnimator.getAnimatedValue();

                // 修改header高度
                int newHeight = (int) (scanHeaderViewHeight * Math.sqrt(value));
                LayoutParams layoutParams = mScanHeaderView.getLayoutParams();
                if (layoutParams.height != newHeight) {
                    layoutParams.height = newHeight;
                    mScanHeaderView.setLayoutParams(layoutParams);
                }

                // 修改header中字体大小
                float nValue = (float) (0.2 + 0.8 * value);
                mScanCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, scanCountTextSize * nValue);

                nValue = (float) (0.6 + 0.4 * value);
                mScanCountUnit.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        scanCountUnitTextSize * nValue);

                nValue = (float) (0.2 + 0.8 * value);
                mScanResultHint.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        scanResultHintTextSize * nValue);
                mScanResultHint.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        mAutoSelect = menu.findItem(R.id.action_auto_select);

        if (mDuplicateImageScanFinished) {
            mAutoSelect.setVisible(true);
            if (mAutoSelected) {
                mAutoSelect.setTitle(R.string.cancel_auto_select);
            } else {
                mAutoSelect.setTitle(R.string.auto_select);
            }
        } else {
            mAutoSelect.setVisible(false);
        }
    }

    public final static String AV_TAG = "duplicate.scan.fragment";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_auto_select:
                LayoutManager layoutManager = mLayoutManager;
                if (mDuplicateImageContainer.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                    layoutManager = null;
                }

                if (!mAutoSelected) {
                    mDuplicateImageAdapter.autoSelect(layoutManager);
                    item.setTitle(R.string.cancel_auto_select);
                    AVAnalytics.onEvent(getActivity(), "auto.select", AV_TAG);
                } else {
                    mDuplicateImageAdapter.cancelAutoSelect(layoutManager);
                    item.setTitle(R.string.auto_select);
                    AVAnalytics.onEvent(getActivity(), "cancel.auto.select", AV_TAG);
                }

                mAutoSelected = !mAutoSelected;
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        long count = SharedPreferenceUtil.getLong(SELECTED_DELETE_IMAGE_TOTAL_SIZE, 0L);
        if (count <= 0) {
            mDeleteBtn.setText(R.string.delete);
        } else {
            mDeleteBtn.setText(getString(R.string.delete_with_size,
                    StringUtils.convertFileSize(count)));
        }
        SharedPreferenceUtil.setOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferenceUtil.clearOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }

}
