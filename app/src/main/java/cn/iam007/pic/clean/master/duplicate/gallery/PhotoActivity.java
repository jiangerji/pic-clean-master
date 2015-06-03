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
import cn.iam007.pic.clean.master.duplicate.DuplicateHoldAdapter;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageAdapter;
import cn.iam007.pic.clean.master.duplicate.DuplicateItem;
import cn.iam007.pic.clean.master.duplicate.DuplicateItemImage;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class PhotoActivity extends BaseActivity {

    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private DuplicateImageAdapter mDuplicateImageAdapter;
    private TextView mTextView;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_photo);

        initView();
    }

    private void initView() {

        mPosition = getIntent().getIntExtra("position", 1);
        mDuplicateImageAdapter = DuplicateHoldAdapter.getInstance()
                .getHoldAdapter();

        /* Initialize recycler view */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mTextView = (TextView) findViewById(R.id.text_num);
        mTextView.setText(String.valueOf(mDuplicateImageAdapter.getSelectedImageCount()));

        mAdapter = new PhotoAdapter(this, mDuplicateImageAdapter);
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

    private String SELECTED_DELETE_IMAGE_TOTAL_NUM =
            SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_NUM;

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
                    }

                }
            };

    @Override
    public void onResume() {
        super.onResume();

        long count = SharedPreferenceUtil.getLong(SELECTED_DELETE_IMAGE_TOTAL_NUM, 0L);
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

    private MenuItem mSelected = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        mSelected = menu.findItem(R.id.action_item_select);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        DuplicateItem item = mDuplicateImageAdapter.getItem(mPosition);
        if (item instanceof DuplicateItemImage) {
            if (((DuplicateItemImage) item).isSelected()){
                mSelected.setChecked(true);
                mSelected.setIcon(R.drawable.ic_checkbox_checked);
            } else {
                mSelected.setChecked(false);
                mSelected.setIcon(R.drawable.ic_checkbox_unchecked);
            }
            return true;
        }
        return false; // Return false if nothing is done
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_item_select:
                DuplicateItem duplicateItem = mDuplicateImageAdapter.getItem(mPosition);
                if (duplicateItem instanceof DuplicateItemImage) {
                    if (((DuplicateItemImage) duplicateItem).isSelected()){
                        mSelected.setChecked(false);
                        mSelected.setIcon(R.drawable.ic_checkbox_unchecked);
                        ((DuplicateItemImage) duplicateItem).setSelected(false, true);
                    } else {
                        mSelected.setChecked(true);
                        mSelected.setIcon(R.drawable.ic_checkbox_checked);
                        ((DuplicateItemImage) duplicateItem).setSelected(true, true);
                    }
                    duplicateItem.refresh();
                    return true;
                }
            default:
                break;
        }

        return true;
    }
}
