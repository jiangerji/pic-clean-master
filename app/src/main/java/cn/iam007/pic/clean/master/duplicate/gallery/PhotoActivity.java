package cn.iam007.pic.clean.master.duplicate.gallery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseActivity;
import cn.iam007.pic.clean.master.base.BaseItemInterface;
import cn.iam007.pic.clean.master.base.ImageAdapterInterface;
import cn.iam007.pic.clean.master.duplicate.DuplicateHoldAdapter;
import cn.iam007.pic.clean.master.main.MainActivity;
import cn.iam007.pic.clean.master.recycler.RecyclerHoldAdapter;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class PhotoActivity extends BaseActivity {

    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private ImageAdapterInterface mImageAdapterInterface;
    private TextView mTextView;
    private int mPosition;
    private int mFrom;
    private String SELECTED_DELETE_IMAGE_TOTAL_NUM =
            SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_NUM;
    private String SELECTED_RECYCLER_IMAGE_TOTAL_SIZE =
            SharedPreferenceUtil.SELECTED_RECYCLER_IMAGE_TOTAL_SIZE;
    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    if (key.equalsIgnoreCase(SELECTED_DELETE_IMAGE_TOTAL_NUM)) {
                        if (mTextView != null) {
                            long count = sharedPreferences.getLong(key, 0);
                            if (count <= 0) {
                                mTextView.setText("0");
                            } else {
                                mTextView.setText(String.valueOf(count));
                            }
                        }
                    } else if (key.equalsIgnoreCase(SELECTED_RECYCLER_IMAGE_TOTAL_SIZE)) {
                        if (mTextView != null) {
                            long count = sharedPreferences.getLong(key, 0);
                            if (count <= 0) {
                                mTextView.setText("0");
                            } else {
                                mTextView.setText(String.valueOf(count));
                            }
                        }
                    }

                }
            };
    private MenuItem mSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_photo);

        initView();
    }

    private void initView() {

        mPosition = getIntent().getIntExtra("position", 1);
        mFrom = getIntent().getIntExtra("fromFragment", MainActivity.DUPLICATE_SCAN_FRAGMENT);
        if (mFrom == MainActivity.DUPLICATE_SCAN_FRAGMENT) {
            mImageAdapterInterface = DuplicateHoldAdapter.getInstance()
                    .getHoldAdapter();
        } else {
            mImageAdapterInterface = RecyclerHoldAdapter.getInstance().getHoldAdapter();
        }

        /* Initialize recycler view */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mTextView = (TextView) findViewById(R.id.text_num);
        mAdapter = new PhotoAdapter(this, mImageAdapterInterface);
//        mAdapter.setOnItemSelectedListener(new PhotoAdapter.OnItemSelectedListener() {
//
//            @Override
//            public void onSelected(boolean isChecked) {
//                mTextView.setText(String.valueOf(mDuplicateImageAdapter.getSelectedImageCount()));
//            }
//        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new CenterLockListener(PhotoActivity.this, new CenterLockListener.CenterItemListener() {
            @Override
            public void onCenterItem(int position) {
                mPosition = position;
                invalidateOptionsMenu();
            }
        }));
        mRecyclerView.scrollToPosition(mPosition);
    }

    @Override
    public void onResume() {
        super.onResume();

        long count = 0L;
        if (mFrom == MainActivity.DUPLICATE_SCAN_FRAGMENT) {
            count = SharedPreferenceUtil.getLong(SELECTED_DELETE_IMAGE_TOTAL_NUM, 0L);
        } else {
            count = SharedPreferenceUtil.getLong(SELECTED_RECYCLER_IMAGE_TOTAL_SIZE, 0L);
        }

        if (count <= 0) {
            mTextView.setText("0");
        } else {
            mTextView.setText(String.valueOf(count));
        }
        SharedPreferenceUtil.setOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferenceUtil.clearOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        mSelected = menu.findItem(R.id.action_item_select);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        BaseItemInterface item = mImageAdapterInterface.getItem(mPosition);
        if (item.isSelected()) {
            mSelected.setChecked(true);
            mSelected.setIcon(R.drawable.ic_checkbox_checked);
        } else {
            mSelected.setChecked(false);
            mSelected.setIcon(R.drawable.ic_checkbox_unchecked);
        }
        return true; // Return false if nothing is done
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_item_select:
                BaseItemInterface duplicateItem = mImageAdapterInterface.getItem(mPosition);
                if (duplicateItem.isSelected()) {
                    mSelected.setChecked(false);
                    mSelected.setIcon(R.drawable.ic_checkbox_unchecked);
                    duplicateItem.setSelected(false, true);
                } else {
                    mSelected.setChecked(true);
                    mSelected.setIcon(R.drawable.ic_checkbox_checked);
                    duplicateItem.setSelected(true, true);
                }
                duplicateItem.refresh();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
