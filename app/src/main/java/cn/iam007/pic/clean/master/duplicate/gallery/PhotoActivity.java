package cn.iam007.pic.clean.master.duplicate.gallery;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.base.BaseActivity;
import cn.iam007.pic.clean.master.duplicate.DuplicateHoldAdapter;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageAdapter;

public class PhotoActivity extends BaseActivity {

    private Toolbar mToolbar = null;
    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private DuplicateImageAdapter mDuplicateImageAdapter;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_photo);

        initView();
    }

    private void initView() {

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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return true;
    }
}
