package cn.iam007.pic.clean.master;

import android.content.Context;

import java.io.File;

/**
 * Created by Administrator on 2015/5/25.
 */
public class Constants {

    /**
     * 回收站目录名字
     */
    private static File recyclerPath = null;

    /**
     * 获取回收站目录路径
     *
     * @return
     */
    public static File getRecyclerPath() {
        return recyclerPath;
    }

    public static void init(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }

        recyclerPath = new File(cacheDir, ".recycler");
        if (!recyclerPath.isDirectory()) {
            recyclerPath.mkdirs();
        }
    }
}
