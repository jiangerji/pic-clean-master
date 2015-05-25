package cn.iam007.pic.clean.master.recycler;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.utils.ImageUtils;

public class RecyclerViewHolder extends ViewHolder {

    private ImageView mImageView = null;
    private CheckBox mCheckBox = null;

    public RecyclerViewHolder(View itemView) {
        super(itemView);

        mImageView = (ImageView) itemView.findViewById(R.id.image);
        mCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);
    }

    public void bindView(RecyclerImageItem item) {
        String imageUrl = item.getSourcePath();
        if (imageUrl != null) {
            ImageUtils.showImageByUrl(imageUrl, mImageView);
            mImageView.setVisibility(View.VISIBLE);
            mCheckBox.setVisibility(View.VISIBLE);
            mCheckBox.setChecked(item.isSelected());
        }

        item.setViewHolder(this);
    }

    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }
}
