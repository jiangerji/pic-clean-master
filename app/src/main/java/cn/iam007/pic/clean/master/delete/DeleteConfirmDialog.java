package cn.iam007.pic.clean.master.delete;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.duplicate.DuplicateItemImage;
import cn.iam007.pic.clean.master.utils.DialogBuilder;
import cn.iam007.pic.clean.master.utils.FileUtil;
import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.PlatformUtils;
import cn.iam007.pic.clean.master.utils.SharedPreferenceUtil;

public class DeleteConfirmDialog {

    public static DeleteConfirmDialog builder(Context context) {
        DialogBuilder builder = new DialogBuilder(context);

        builder.title(R.string.delete)
                .positiveText(R.string.delete_confirm)
                .negativeText(R.string.cancel)
                .customView(R.layout.fragment_duplicate_delete_dialog, true);

        DeleteConfirmDialog dialog = new DeleteConfirmDialog(builder);
        return dialog;
    }

    private ArrayList<DuplicateItemImage> mDuplicateItemImages;

    public void addDeleteItems(ArrayList<DuplicateItemImage> items) {
        if (mDuplicateItemImages == null) {
            mDuplicateItemImages = new ArrayList<>();
        }

        mDuplicateItemImages.addAll(items);
    }

    public void show() {
        loadDisplayDeleteItems();
        mDialog.show();
    }

    private void loadDisplayDeleteItems() {
        // 总共删除的文件数量
        mDeleteItemCount = mDuplicateItemImages.size();
        if (mDeleteItemCount == 0) {
            return;
        }

        // 设置dialog自定义内容
        View view = mDialog.getCustomView();
        if (view != null) {
            TextView textView = (TextView) view.findViewById(R.id.text);
            String recycle = mDialog.getContext().getString(R.string.recycle);

            String content = mDialog.getContext().getString(R.string.delete_message,
                    mDeleteItemCount, recycle);
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            int startIndex = content.indexOf(recycle);
            style.setSpan(new ForegroundColorSpan(Color.RED),
                    startIndex,
                    startIndex + recycle.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置textview的背景颜色  
            textView.setText(style);
        }

        ImageUtils.showImageByUrl(mDuplicateItemImages.get(0).getImageUrl(),
                first);
        first.setVisibility(View.VISIBLE);

        first.getViewTreeObserver()
                .addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        int width = first.getWidth();
                        if (width > 0) {
                            ViewGroup.LayoutParams layoutParams = first.getLayoutParams();
                            layoutParams.height = width;

                            first.setLayoutParams(layoutParams);
                            second.setLayoutParams(layoutParams);
                            third.setLayoutParams(layoutParams);
                            forth.setLayoutParams(layoutParams);

                            first.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                    }
                });

        if (mDeleteItemCount > 1) {
            ImageUtils.showImageByUrl(mDuplicateItemImages.get(1).getImageUrl(),
                    second);
            second.setVisibility(View.VISIBLE);
        }

        if (mDeleteItemCount > 2) {
            ImageUtils.showImageByUrl(mDuplicateItemImages.get(2).getImageUrl(),
                    third);
            third.setVisibility(View.VISIBLE);
        }

        if (mDeleteItemCount > 3) {
            ImageUtils.showImageByUrl(mDuplicateItemImages.get(3).getImageUrl(),
                    forth);
            forth.setVisibility(View.VISIBLE);
        }

    }

    private ImageView first;
    private ImageView second;
    private ImageView third;
    private ImageView forth;
    private int mDeleteItemCount = 0;

    private MaterialDialog mDialog = null;

    private DeleteConfirmDialog(DialogBuilder builder) {
        mDialog = builder.build();

        first = (ImageView) mDialog.findViewById(R.id.first);
        second = (ImageView) mDialog.findViewById(R.id.second);
        third = (ImageView) mDialog.findViewById(R.id.third);
        forth = (ImageView) mDialog.findViewById(R.id.forth);

//        View deleteConfirm = getActionButton(DialogAction.POSITIVE);
//        deleteConfirm.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startToDelete();
//                dismiss();
//            }
//        });

        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                startToDelete();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
            }
        });
    }

    private MaterialDialog mDeleteProgressDialog = null;

    public void startToDelete() {
        String content = mDialog.getContext().getString(R.string.deleting_progress,
                0,
                mDeleteItemCount);
        DialogBuilder builder = new DialogBuilder(mDialog.getContext());
        builder.title(R.string.delete)
                .content(content)
                .progress(true, 0);

        mDeleteProgressDialog = builder.build();
        mDeleteProgressDialog.setCancelable(false);
        mDeleteProgressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startDeleteTask();
            }
        }, 500);

    }

    private Handler mUpdateDeleteProgress = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String info = (String) msg.obj;
            mDeleteProgressDialog.setContent(info);
            return true;
        }
    });

    private void startDeleteTask() {
        new Thread(new Runnable() {
            public void run() {
                if (mOnDeleteStatusListener != null){
                    mOnDeleteStatusListener.onDeleteStart();
                }

                if (mDuplicateItemImages != null) {
                    int count = 0;
                    int size = mDuplicateItemImages.size();
                    for (DuplicateItemImage image : mDuplicateItemImages) {
                        try {
                            image.delete();
                        } catch (Exception e) {
                        }

                        if (mOnDeleteStatusListener != null){
                            mOnDeleteStatusListener.onDeleteImage(image.getImageRealPath());
                        }

                        String content = mDialog.getContext().getString(R.string.deleting_progress_format, ++count, size);
                        Message msg = new Message();
                        msg.obj = content;
                        mUpdateDeleteProgress.sendMessage(msg);
                    }

                    SharedPreferenceUtil.addLong(
                            SharedPreferenceUtil.HANDLED_DUPLICATE_IMAGES_COUNT,
                            (long) mDuplicateItemImages.size());
                }


                mDeleteProgressDialog.dismiss();

                if (mOnDeleteStatusListener != null) {
                    mOnDeleteStatusListener.onDeleteFinish();
                }

                SharedPreferenceUtil.setBoolean(
                        SharedPreferenceUtil.HAS_DELETE_SOME_DUPLICATE_IMAGE, true);
            }
        }).start();
    }

    public void startDeleteAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);
        alphaAnimation.setDuration(150);

        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                1,
                Animation.RELATIVE_TO_PARENT,
                0,
                Animation.RELATIVE_TO_SELF,
                1,
                Animation.RELATIVE_TO_SELF,
                1);
        translate.setDuration(150);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translate);

        first.startAnimation(AnimationUtils.loadAnimation(mDialog.getContext(),
                R.anim.slide_out_left));

        if (mDeleteItemCount > 1) {
            second.startAnimation(animationSet);
        }
    }

    public interface OnDeleteStatusListener {
        void onDeleteStart();
        void onDeleteImage(String filePath);
        void onDeleteFinish();
    }

    private OnDeleteStatusListener mOnDeleteStatusListener = null;

    /**
     * @param onDeleteStatusListener the onDeleteStatusListener to set
     */
    public void setOnDeleteStatusListener(
            OnDeleteStatusListener onDeleteStatusListener) {
        this.mOnDeleteStatusListener = onDeleteStatusListener;
    }

}
