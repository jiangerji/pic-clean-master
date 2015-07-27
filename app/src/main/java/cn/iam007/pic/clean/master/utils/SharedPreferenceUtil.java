package cn.iam007.pic.clean.master.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import cn.iam007.pic.clean.master.Iam007Application;

public class SharedPreferenceUtil {

    /**
     * 当前选中需要删除的图片中的文件大小
     */
    public final static String SELECTED_DELETE_IMAGE_TOTAL_SIZE =
            "SELECTED_DELETE_IMAGE_TOTAL_SIZE";
    public final static String SELECTED_RECYCLER_IMAGE_TOTAL_SIZE =
            "SELECTED_RECYCLER_IMAGE_TOTAL_SIZE";
    public final static String SELECTED_SCREENSHOT_IMAGE_TOTAL_SIZE =
            "SELECTED_SCREENSHOT_IMAGE_TOTAL_SIZE";
    public final static String SELECTED_DELETE_IMAGE_TOTAL_NUM =
            "SELECTED_DELETE_IMAGE_TOTAL_NUM";

    // 用于表示是否有清理相似图片, boolean
    public final static String HAS_DELETE_SOME_DUPLICATE_IMAGE = "HAS_DELETE_SOME_DUPLICATE_IMAGE";

    // 已经清理相似图片的总数
    public final static String HANDLED_DUPLICATE_IMAGES_COUNT = "HANDLED_DUPLICATE_IMAGES_COUNT";

    private final static String PREDERENCE_NAME = "pic.clean.master";

    public static void init(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREDERENCE_NAME, 0);
        Editor editor = sp.edit();
        editor.putLong(SELECTED_DELETE_IMAGE_TOTAL_SIZE, 0);
        editor.putLong(SELECTED_RECYCLER_IMAGE_TOTAL_SIZE, 0);
        editor.putBoolean(HAS_DELETE_SOME_DUPLICATE_IMAGE, false);
        editor.commit();
    }

    public static void setBoolean(String key, Boolean value) {
        SharedPreferences sp = Iam007Application.getApplication()
                .getSharedPreferences(PREDERENCE_NAME, 0);

        if (value != sp.getBoolean(key, false)) {
            Editor editor = sp.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public static boolean getBoolean(String key, Boolean defaultValue) {
        SharedPreferences sp = Iam007Application.getApplication()
                .getSharedPreferences(PREDERENCE_NAME, 0);
        return sp.getBoolean(key, defaultValue);
    }

    public static void setLong(String key, Long value) {
        SharedPreferences sp = Iam007Application.getApplication()
                .getSharedPreferences(PREDERENCE_NAME, 0);

        if (value != sp.getLong(key, 0)) {
            Editor editor = sp.edit();
            editor.putLong(key, value);
            editor.commit();
        }
    }

    public static long getLong(String key, Long defaultValue) {
        SharedPreferences sp = Iam007Application.getApplication()
                .getSharedPreferences(PREDERENCE_NAME, 0);
        return sp.getLong(key, defaultValue);
    }

    /**
     * 在当前保存的值上增加value值
     *
     * @param key
     * @param value
     */
    public static void addLong(String key, Long value) {
        SharedPreferences sp = Iam007Application.getApplication()
                .getSharedPreferences(PREDERENCE_NAME, 0);
        Editor editor = sp.edit();
        editor.putLong(key, sp.getLong(key, 0) + value);
        editor.commit();
    }

    /**
     * 在当前保存的值上减少value值
     *
     * @param key
     * @param value
     */
    public static void subLong(String key, Long value) {
        SharedPreferences sp = Iam007Application.getApplication()
                .getSharedPreferences(PREDERENCE_NAME, 0);
        Editor editor = sp.edit();
        editor.putLong(key, sp.getLong(key, 0) - value);
        editor.commit();
    }

    public static void setOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = Iam007Application.getApplication()
                .getSharedPreferences(PREDERENCE_NAME, 0);

        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void clearOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = Iam007Application.getApplication()
                .getSharedPreferences(PREDERENCE_NAME, 0);

        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
