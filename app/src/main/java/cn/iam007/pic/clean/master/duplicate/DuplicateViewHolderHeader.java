package cn.iam007.pic.clean.master.duplicate;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.delete.DeleteConfirmDialog;
import cn.iam007.pic.clean.master.delete.DeleteConfirmDialog.OnDeleteStatusListener;
import cn.iam007.pic.clean.master.utils.LogUtil;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

import java.util.ArrayList;

public class DuplicateViewHolderHeader extends DuplicateViewHolder {

    private TextView mSectionTitle;
    private ImageView mDelBtn;

    public DuplicateViewHolderHeader(View itemView) {
        super(itemView, DuplicateViewHolder.VIEW_TYPE_HEADER);
    }

    @Override
    public void onInit(View itemView) {
        mSectionTitle = (TextView) findViewById(R.id.text);
        mDelBtn = (ImageView) findViewById(R.id.del_current_section);
        mDelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtil.d("click on " + v);
                showDeleteDialog(v.getContext());
            }
        });
    }

    @Override
    public void onBindView(DuplicateItem item, boolean itemHasChanged) {
        if (item instanceof DuplicateItemHeader) {
            DuplicateItemHeader header = (DuplicateItemHeader) item;
            mSectionTitle.setText(header.getHeaderText());
            if (header.getSelectedCount() > 0) {
                mDelBtn.setEnabled(true);
            } else {
                mDelBtn.setEnabled(false);
            }
        }
    }

    private void showDeleteDialog(final Context context) {
        final Handler handler = new Handler();

        DuplicateItemHeader item = (DuplicateItemHeader) getDuplicateImageItem();
        if (item != null) {
            DeleteConfirmDialog dialog = DeleteConfirmDialog.builder(context);

            ArrayList<DuplicateItemImage> items = item.getSelectedItem();
            long tSize = 0;
            for (DuplicateItemImage image : items) {
                tSize += image.getFileSize();
            }
            final long size = tSize;

            dialog.addDeleteItems(items);
            dialog.show();

            dialog.setOnDeleteStatusListener(new OnDeleteStatusListener() {

                @Override
                public void onDeleteFinish() {
                    handler.post(new Runnable() {
                        public void run() {
                            getAdapter().deleteSection(getDuplicateImageItem());
                            SharedPreferenceUtil.subSharedPreference(SharedPreferenceUtil.SELECTED_DELETE_IMAGE_TOTAL_SIZE,
                                    size);
                        }
                    });
                }
            });
        }
    }
}
