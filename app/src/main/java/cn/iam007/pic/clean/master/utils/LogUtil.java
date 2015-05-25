package cn.iam007.pic.clean.master.utils;

import android.util.Log;

public class LogUtil {

    private final static boolean LOGGER_ENABLE = true;

    //    public static Logger getLogger() {
    //        return Logger.getLogger("main");
    //    }
    //
    //    public static Logger getLogger(String name) {
    //        if (name == null || name.length() == 0) {
    //            name = "main";
    //        }
    //        return Logger.getLogger(name);
    //    }

    public static void d(String msg) {
        if (LOGGER_ENABLE) {
            StackTraceElement[] elements = Thread.currentThread()
                    .getStackTrace();
            String filename = elements[3].getFileName();
            int index = filename.indexOf(".");
            String tag = filename;
            if (index >= 0) {
                tag = filename.substring(0, index);
            }

            d(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LOGGER_ENABLE) {
            Log.d(tag, msg);
        }
    }

    public static void v(String msg) {
        if (LOGGER_ENABLE) {
            StackTraceElement[] elements = Thread.currentThread()
                    .getStackTrace();
            String tag = elements[1].getClass().getName();

            v(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (LOGGER_ENABLE) {
            Log.v(tag, msg);
        }
    }

    public static void e(String msg) {
        if (LOGGER_ENABLE) {
            StackTraceElement[] elements = Thread.currentThread()
                    .getStackTrace();
            String tag = elements[1].getClass().getName();

            e(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LOGGER_ENABLE) {
            Log.e(tag, msg);
        }
    }
}
