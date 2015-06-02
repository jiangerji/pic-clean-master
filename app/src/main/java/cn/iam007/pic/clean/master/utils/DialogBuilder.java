package cn.iam007.pic.clean.master.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import cn.iam007.pic.clean.master.R;

/**
 * Created by Administrator on 2015/6/1.
 */
public class DialogBuilder extends MaterialDialog.Builder {

    public DialogBuilder(Context context) {
        super(context);

        theme(Theme.LIGHT);

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

        // 设置字体颜色
        titleColorRes(R.color.title);
        dividerColorRes(R.color.divider);
        positiveColorRes(R.color.red_light_EB5347);
        negativeColorRes(R.color.black_light_333333);
        backgroundColorRes(R.color.white_light_FAFAFA);
    }

    @Override
    public MaterialDialog build() {
        MaterialDialog dialog = super.build();
        PlatformUtils.applyFonts(dialog.getView());
        return dialog;
    }
}
