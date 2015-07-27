package cn.iam007.pic.clean.master.screenshot;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.recycler.RecyclerImageAdapter;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.PlatformUtils;

public class ScreenshotViewHolder extends ViewHolder {

    private ImageView mImageView = null;
    private CheckBox mCheckBox = null;

    private ScreenshotImageItem mItem;

    public ScreenshotViewHolder(final View itemView) {
        super(itemView);

        mImageView = (ImageView) itemView.findViewById(R.id.image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mItem.setSelected(isChecked, false);
            }
        });

        PlatformUtils.applyFonts(itemView);
    }

    public void bindView(ScreenshotImageItem item) {
        mItem = item;
        String imageUrl = item.getImageUrl();
        if (imageUrl != null) {
            ImageUtils.showImageByUrl(imageUrl, mImageView);
            mImageView.setVisibility(View.VISIBLE);
            mCheckBox.setVisibility(View.VISIBLE);
            setChecked(item.isSelected());
        }
        mItem.setViewHolder(this);
    }

    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }

    /**
     * 刷新绑定view
     */
    public void refresh() {
        bindView(mItem);
    }
}
