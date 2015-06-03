package cn.iam007.pic.clean.master.duplicate.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.duplicate.DuplicateImageAdapter;
import cn.iam007.pic.clean.master.duplicate.DuplicateItem;
import cn.iam007.pic.clean.master.duplicate.DuplicateItemImage;
import cn.iam007.pic.clean.master.duplicate.DuplicateViewHolder;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.PlatformUtils;


public class PhotoAdapter extends Adapter<PhotoAdapter.MyViewHolder> {

    private Context mContext;
    private final ArrayList<DuplicateItem> mItems;
    private DuplicateImageAdapter mDuplicateImageAdapter;

    public PhotoAdapter(Context context, DuplicateImageAdapter adapter) {
        mContext = context;
        mDuplicateImageAdapter = adapter;
        mItems = adapter.getItems();
    }

    @Override
    public int getItemViewType(int position) {

        DuplicateItem item = mItems.get(position);
        int type = -1;
        if (item.isHeader()) {
            type = DuplicateViewHolder.VIEW_TYPE_HEADER;
        } else {
            // 判断位于第几个
            int index = (position - item.getSectionFirstPosition() - 1) % 3;
            if (index == 0) {
                type = DuplicateViewHolder.VIEW_TYPE_CONTENT_LEFT;
            } else if (index == 1) {
                type = DuplicateViewHolder.VIEW_TYPE_CONTENT_MIDDLE;
            } else {
                type = DuplicateViewHolder.VIEW_TYPE_CONTENT_RIGHT;
            }

            try {
                String imageUrl = ((DuplicateItemImage) item).getImageUrl();
                if (imageUrl == null) {
                    type = DuplicateViewHolder.VIEW_TYPE_HEADER;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

        return type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view;

        MyViewHolder holder = null;

        switch (viewType) {
        case DuplicateViewHolder.VIEW_TYPE_HEADER:
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_head_view, null);
            holder = new HeadHolder(view);
            break;

        case DuplicateViewHolder.VIEW_TYPE_CONTENT_LEFT:
        case DuplicateViewHolder.VIEW_TYPE_CONTENT_MIDDLE:
        case DuplicateViewHolder.VIEW_TYPE_CONTENT_RIGHT:
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_item_view, null);
            holder = new PhotoHolder(view);
            break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DuplicateItem item = mItems.get(position);
        final View itemView = holder.itemView;

        holder.bindView(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public abstract class MyViewHolder extends ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindView(DuplicateItem item);
    }

    public class PhotoHolder extends MyViewHolder {
        private View mItemView;
        private ImageView mImageView;
        private CheckBox mCheckBox;
        private TextView mTextView;
        private DuplicateItem mDuplicateImageItem = null;

        public PhotoHolder(View itemView) {
            super(itemView);
            onInit(itemView);
        }

        public void onInit(View itemView) {
            mItemView = itemView;
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            mTextView = (TextView) itemView.findViewById(R.id.text);
            OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(
                        CompoundButton buttonView, boolean isChecked) {
                    DuplicateItem item = mDuplicateImageItem;

                    if (mDuplicateImageAdapter != null) {
                        mDuplicateImageAdapter.onDuplicateItemImageSelected((DuplicateItemImage) item,
                                isChecked);
                    }

                    if (item != null && (item instanceof DuplicateItemImage)) {
                        ((DuplicateItemImage) item).setSelected(isChecked, true);
                        item.refresh();
                    }

                    if (mOnItemSelectedListener != null) {
                        mOnItemSelectedListener.onSelected(isChecked);
                    }
                }
            };

            mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
        }

        public void bindView(DuplicateItem item) {
            mDuplicateImageItem = item;

            if (item instanceof DuplicateItemImage) {
                String imageUrl = ((DuplicateItemImage) item).getImageUrl();
                if (imageUrl != null) {
                    ImageUtils.showImageByUrl(imageUrl,
                            mImageView);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(PlatformUtils.getScreenWidth(mContext)
                            - (int) (PlatformUtils.getDensity(mContext) * 10),
                            (PlatformUtils.getScreenWidth(mContext) * 4) / 3);
                    mImageView.setLayoutParams(params);
                    mItemView.setVisibility(View.VISIBLE);
                    mCheckBox.setVisibility(View.INVISIBLE);
                    mCheckBox.setChecked(((DuplicateItemImage) item).isSelected());
                    mTextView.setText(String.valueOf(getPosition()));
                } else {
                    // 占位图片
                    mItemView.setVisibility(View.GONE);
                    mImageView.setImageBitmap(null);
                }
            } else {
                // 出现错误，暂时不显示
                mItemView.setVisibility(View.GONE);
                mImageView.setImageBitmap(null);
            }
        }
    }

    public class HeadHolder extends MyViewHolder {
        public HeadHolder(View itemView) {
            super(itemView);
        }

        public void bindView(DuplicateItem item) {

        }
    }

    private OnItemSelectedListener mOnItemSelectedListener = null;

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public interface OnItemSelectedListener {
        void onSelected(boolean isChecked);
    }
}
