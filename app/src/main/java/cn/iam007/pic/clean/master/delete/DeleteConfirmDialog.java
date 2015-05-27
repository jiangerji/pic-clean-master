package cn.iam007.pic.clean.master.delete;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
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
import cn.iam007.pic.clean.master.utils.FileUtil;
import cn.iam007.pic.clean.master.utils.ImageUtils;

public class DeleteConfirmDialog extends MaterialDialog {

    public static DeleteConfirmDialog builder(Context context) {
        Builder builder = new MaterialDialog.Builder(context);

        builder.title(R.string.delete)
                .theme(Theme.LIGHT)
                .positiveText(R.string.delete_confirm)
                .negativeText(R.string.cancel)
                .customView(R.layout.fragment_duplicate_delete_dialog, true);

        // 设置字体颜色
        //        new MaterialDialog.Builder(this)
        //        .titleColorRes(R.color.material_red_500)
        //        .contentColor(Color.WHITE) // notice no 'res' postfix for literal color
        //        .dividerColorRes(R.color.material_pink_500)
        //        .backgroundColorRes(R.color.material_blue_grey_800)
        //        .positiveColorRes(R.color.material_red_500)
        //        .neutralColorRes(R.color.material_red_500)
        //        .negativeColorRes(R.color.material_red_500)
        //        .widgetColorRes(R.color.material_red_500)
        //        .show();
        builder.titleColorRes(R.color.title)
                .dividerColorRes(R.color.divider)
                .positiveColorRes(R.color.red_light_EB5347)
                .negativeColorRes(R.color.black_light_333333)
                .backgroundColorRes(R.color.white_light_FAFAFA);

        return new DeleteConfirmDialog(builder);
    }

    private ArrayList<DuplicateItemImage> mDuplicateItemImages;

    public void addDeleteItems(ArrayList<DuplicateItemImage> items) {
        if (mDuplicateItemImages == null) {
            mDuplicateItemImages = new ArrayList<>();
        }

        mDuplicateItemImages.addAll(items);
    }

    @Override
    public void show() {
        loadDisplayDeleteItems();
        super.show();
    }

    private void loadDisplayDeleteItems() {
        // 总共删除的文件数量
        mDeleteItemCount = mDuplicateItemImages.size();
        if (mDeleteItemCount == 0) {
            return;
        }

        // 设置dialog自定义内容
        View view = getCustomView();
        if (view != null) {
            TextView textView = (TextView) view.findViewById(R.id.text);
            String recycle = getContext().getString(R.string.recycle);

            String content = getContext().getString(R.string.delete_message,
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

    private DeleteConfirmDialog(Builder builder) {
        super(builder);

        first = (ImageView) findViewById(R.id.first);
        second = (ImageView) findViewById(R.id.second);
        third = (ImageView) findViewById(R.id.third);
        forth = (ImageView) findViewById(R.id.forth);

        View deleteConfirm = getActionButton(DialogAction.POSITIVE);
        deleteConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startToDelete();
                dismiss();
            }
        });

        builder.callback(new ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                Toast.makeText(getContext(), "on positive", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                Toast.makeText(getContext(), "on negative", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void startToDelete() {
        String content = getContext().getString(R.string.deleting_progress,
                0,
                mDeleteItemCount);
        Builder builder = new MaterialDialog.Builder(getContext())
                .title(R.string.delete)
                .content(content)
                .progress(true, 0);

        builder.titleColorRes(R.color.title)
                .dividerColorRes(R.color.divider)
                .positiveColorRes(R.color.red_light_EB5347)
                .negativeColorRes(R.color.black_light_333333)
                .backgroundColorRes(R.color.white_light_FAFAFA);
        //        deleteProgressDialog.setCancelable(false);
        final MaterialDialog deleteProgressDialog = builder.build();
        deleteProgressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startDeleteTask(deleteProgressDialog);
            }
        }, 500);

    }

    private void startDeleteTask(final MaterialDialog progressDialog) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (mDuplicateItemImages != null) {
                        for (DuplicateItemImage image : mDuplicateItemImages) {
                            image.delete();
                        }
                    }
                } catch (Exception e) {
                }

                progressDialog.dismiss();

                if (mOnDeleteStatusListener != null) {
                    mOnDeleteStatusListener.onDeleteFinish();
                }
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

        first.startAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_out_left));

        if (mDeleteItemCount > 1) {
            second.startAnimation(animationSet);
        }
    }

    public interface OnDeleteStatusListener {
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
