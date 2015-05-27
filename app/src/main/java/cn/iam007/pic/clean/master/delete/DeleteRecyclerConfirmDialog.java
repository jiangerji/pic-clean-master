package cn.iam007.pic.clean.master.delete;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import cn.iam007.pic.clean.master.R;

/**
 * Created by Administrator on 2015/5/26.
 */
public class DeleteRecyclerConfirmDialog extends MaterialDialog {

    /**
     * 构造确认删除的对话框
     *
     * @param context 上下文
     * @param count   需要删除的文件的数量
     * @return
     */
    public static DeleteRecyclerConfirmDialog builder(Context context, long count) {
        Builder builder = new MaterialDialog.Builder(context);

        builder.title(R.string.recycle)
                .theme(Theme.LIGHT)
                .positiveText(R.string.delete_confirm)
                .negativeText(R.string.cancel)
                .content(context.getString(R.string.recycler_delete_message, count));

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

        return new DeleteRecyclerConfirmDialog(builder);
    }

    protected DeleteRecyclerConfirmDialog(Builder builder) {
        super(builder);
    }
}
