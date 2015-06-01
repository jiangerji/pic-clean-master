package cn.iam007.pic.clean.master.duplicate.gallery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseActivity;
import cn.iam007.pic.clean.master.duplicate.DuplicateHoldAdapter;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageAdapter;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;
import cn.iam007.pic.clean.master.utils.StringUtils;

public class PhotoActivity extends BaseActivity {

    private Toolbar mToolbar = null;
    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private DuplicateImageAdapter mDuplicateImageAdapter;
    private TextView mTextView;
    private Button mDeleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_photo);

        initView();
    }

    private void initView() {

        mToolbar = getToolbar();

        int position = getIntent().getIntExtra("position", 1);
        mDuplicateImageAdapter = DuplicateHoldAdapter.getInstance()
                .getHoldAdapter();

        /* Initialize recycler view */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mTextView = (TextView) findViewById(R.id.text_num);
        mTextView.setText(String.valueOf(mDuplicateImageAdapter.getSelectedImageCount()));
        mDeleteBtn = (Button) findViewById(R.id.delbutton);

        mAdapter = new PhotoAdapter(this, mDuplicateImageAdapter);
        mAdapter.setOnItemSelectedListener(new PhotoAdapter.OnItemSelectedListener() {

            @Override
            public void onSelected(boolean isChecked) {
                mTextView.setText(String.valueOf(mDuplicateImageAdapter.getSelectedImageCount()));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new CenterLockListener(PhotoActivity.this));
        mRecyclerView.scrollToPosition(position);
    }

    private String SELECTED_DELETE_IMAGE_TOTAL_SIZE =
            SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_SIZE;

    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

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
    public void onResume() {
        super.onResume();

        long count = SharedPreferenceUtil.getSharedPreference(SELECTED_DELETE_IMAGE_TOTAL_SIZE, 0L);
        if (count <= 0) {
            mDeleteBtn.setText(R.string.delete);
        } else {
            mDeleteBtn.setText(getString(R.string.delete_with_size,
                    StringUtils.convertFileSize(count)));
        }
        SharedPreferenceUtil.setOnSharedPreferenceChangeListener(
                SELECTED_DELETE_IMAGE_TOTAL_SIZE,
                mSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferenceUtil.clearOnSharedPreferenceChangeListener(
                SELECTED_DELETE_IMAGE_TOTAL_SIZE,
                mSharedPreferenceChangeListener);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            }
            return true;
        }
    };
}
