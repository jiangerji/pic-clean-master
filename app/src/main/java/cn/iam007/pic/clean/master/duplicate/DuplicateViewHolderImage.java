package cn.iam007.pic.clean.master.duplicate;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.LogUtil;


public class DuplicateViewHolderImage extends DuplicateViewHolder {
    public enum ImagePos {
        LEFT, MIDDLE, RIGHT
    }

    private ImageView mDuplicateImage;
    private CheckBox mCheckBox;;

    public DuplicateViewHolderImage(View itemView, ImagePos pos) {
        super(itemView, DuplicateViewHolder.VIEW_TYPE_CONTENT_LEFT);

        int type;
        switch (pos) {
        case MIDDLE:
            type = DuplicateViewHolder.VIEW_TYPE_CONTENT_MIDDLE;
            break;

        case RIGHT:
            type = DuplicateViewHolder.VIEW_TYPE_CONTENT_RIGHT;
            break;

        case LEFT:
        default:
            type = DuplicateViewHolder.VIEW_TYPE_CONTENT_LEFT;
            break;
        }

        setViewType(type);
    }

    @Override
    public void onInit(View itemView) {
        mDuplicateImage = (ImageView) findViewById(R.id.image);
        mDuplicateImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtil.d("debug", "click on " + mDuplicateImage);
            }
        });

        mCheckBox = (CheckBox) findViewById(R.id.checkBox);

        OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(
                    CompoundButton buttonView, boolean isChecked) {
                DuplicateItem item = getDuplicateImageItem();

                if (item != null && (item instanceof DuplicateItemImage)) {
                    ((DuplicateItemImage) item).setSelected(isChecked, true);
                }
            }
        };

        mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    @Override
    public void onBindView(DuplicateItem item, boolean itemHasChanged) {
        if (item instanceof DuplicateItemImage) {
            String imageUrl = ((DuplicateItemImage) item).getImageUrl();
            if (imageUrl != null) {
                if (itemHasChanged) {
                    ImageUtils.showImageByUrl(imageUrl,
                            mDuplicateImage);
                    mDuplicateImage.setVisibility(View.VISIBLE);
                }
                mCheckBox.setVisibility(View.VISIBLE);
                mCheckBox.setChecked(((DuplicateItemImage) item).isSelected());
            } else {
                // 占位图片
                if (itemHasChanged) {
                    mDuplicateImage.setImageBitmap(null);
                    mDuplicateImage.setVisibility(View.INVISIBLE);
                }
                mCheckBox.setVisibility(View.INVISIBLE);
            }
        } else {
            // 出现错误，暂时不显示
            mDuplicateImage.setImageBitmap(null);
            mCheckBox.setVisibility(View.INVISIBLE);
        }
    }

}
