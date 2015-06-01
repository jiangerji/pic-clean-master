package cn.iam007.pic.clean.master.duplicate.gallery;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.iam007.find.image.R;
import com.iam007.find.image.base.BaseActivity;
import com.iam007.find.image.duplicate.DupliacteHoldAdapter;
import com.iam007.find.image.duplicate.DuplicateImageAdapter;
import com.iam007.find.image.duplicate.gallery.PhotoAdapter.OnItemSelectedListener;

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

        mToolbar = getToolbar();

        int position = getIntent().getIntExtra("position", 1);
        mDuplicateImageAdapter = DupliacteHoldAdapter.getInstance()
                .getHoldAdapter();

        /* Initialize recycler view */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mTextView = (TextView) findViewById(R.id.text_num);
        mTextView.setText(String.valueOf(mDuplicateImageAdapter.getSelectedImageCount()));

        mAdapter = new PhotoAdapter(this, mDuplicateImageAdapter);
        mAdapter.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onSelected(boolean isChecked) {
                mTextView.setText(String.valueOf(mDuplicateImageAdapter.getSelectedImageCount()));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new CenterLockListener(PhotoActivity.this));
        mRecyclerView.scrollToPosition(position);
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
