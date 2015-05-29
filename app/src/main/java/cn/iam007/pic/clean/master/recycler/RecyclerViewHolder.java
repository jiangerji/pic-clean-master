package cn.iam007.pic.clean.master.recycler;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class RecyclerViewHolder extends ViewHolder {

    private ImageView mImageView = null;
    private CheckBox mCheckBox = null;

    public RecyclerViewHolder(final View itemView) {
        super(itemView);

        mImageView = (ImageView) itemView.findViewById(R.id.image);
        mCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mItem != null) {
                    mItem.setSelected(isChecked, true);
                }
            }
        });
    }

    private RecyclerImageItem mItem;

    public void bindView(RecyclerImageItem item) {
        mItem = item;
        String imageUrl = item.getRecyclerPath();
        if (imageUrl != null) {
            ImageUtils.showImageByUrl(imageUrl, mImageView);
            mImageView.setVisibility(View.VISIBLE);
            mCheckBox.setVisibility(View.VISIBLE);
            setChecked(item.isSelected());
        }

        item.setViewHolder(this);
    }

    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }
}
