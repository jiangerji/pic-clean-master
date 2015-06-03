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
import cn.iam007.pic.clean.master.base.BaseItemInterface;
import cn.iam007.pic.clean.master.base.ImageAdapterInterface;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.PlatformUtils;


public class PhotoAdapter extends Adapter<PhotoAdapter.MyViewHolder> {

    public static final int VIEW_TYPE_IS_IMAGE = 0x01;
    public static final int VIEW_TYPE_IS_NOT_IMAGE = 0x02;
    private final ArrayList<BaseItemInterface> mItems;
    private Context mContext;
    private ImageAdapterInterface mImageAdapterInterface;
    private OnItemSelectedListener mOnItemSelectedListener = null;

    public PhotoAdapter(Context context, ImageAdapterInterface adapter) {
        mContext = context;
        mImageAdapterInterface = adapter;
        mItems = (ArrayList<BaseItemInterface>) adapter.getItems();
    }

    @Override
    public int getItemViewType(int position) {

        BaseItemInterface item = mItems.get(position);
        int type = -1;
        if (item.isHeader()) {
            type = VIEW_TYPE_IS_NOT_IMAGE;
        } else {
            type = VIEW_TYPE_IS_IMAGE;
            try {
                String imageUrl = item.getImageUrl();
                if (imageUrl == null) {
                    type = VIEW_TYPE_IS_NOT_IMAGE;
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
            case VIEW_TYPE_IS_NOT_IMAGE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photo_head_view, null);
                holder = new HeadHolder(view);
                break;

            case VIEW_TYPE_IS_IMAGE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photo_item_view, null);
                holder = new PhotoHolder(view);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final BaseItemInterface item = mItems.get(position);
//        final View itemView = holder.itemView;
        holder.bindView(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public interface OnItemSelectedListener {
        void onSelected(boolean isChecked);
    }

    public abstract class MyViewHolder extends ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindView(BaseItemInterface item);
    }

    public class PhotoHolder extends MyViewHolder {
        private View mItemView;
        private ImageView mImageView;
        private CheckBox mCheckBox;
        private TextView mTextView;
        private BaseItemInterface mBaseItemInterface = null;

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
                    BaseItemInterface item = mBaseItemInterface;

                    if (item != null) {
                        item.setSelected(isChecked, true);
                        item.refresh();
                    }

                    if (mOnItemSelectedListener != null) {
                        mOnItemSelectedListener.onSelected(isChecked);
                    }
                }
            };

            mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
        }

        public void bindView(BaseItemInterface item) {
            mBaseItemInterface = item;

            String imageUrl = item.getImageUrl();
            if (imageUrl != null) {
                ImageUtils.showImageByUrl(imageUrl,
                        mImageView);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(PlatformUtils.getScreenWidth(mContext)
                        - (int) (PlatformUtils.getDensity(mContext) * 10),
                        (PlatformUtils.getScreenWidth(mContext) * 4) / 3);
                mImageView.setLayoutParams(params);
                mItemView.setVisibility(View.VISIBLE);
                mCheckBox.setVisibility(View.INVISIBLE);
                mCheckBox.setChecked(item.isSelected());
                mTextView.setText(String.valueOf(getAdapterPosition()));
            } else {
                // 占位图片
                mItemView.setVisibility(View.GONE);
                mImageView.setImageBitmap(null);
            }
        }
    }

    public class HeadHolder extends MyViewHolder {
        public HeadHolder(View itemView) {
            super(itemView);
        }

        public void bindView(BaseItemInterface item) {

        }
    }
}
