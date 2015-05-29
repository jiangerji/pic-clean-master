package cn.iam007.pic.clean.master.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * Created by Administrator on 2015/5/25.
 */
public class FileUtil {

    /**
     * 获取文件的后缀
     *
     * @param filePath 文件的路径
     * @return 返回该文件的后缀
     */
    public static String getFileExt(String filePath) {
        String ext = "";

        if (filePath != null) {
            int lastDotIndex = filePath.lastIndexOf(".");
            int lastSplashIndex = filePath.lastIndexOf(File.separatorChar);
            if (lastDotIndex > lastSplashIndex) {
                ext = filePath.substring(lastDotIndex + 1);
            }
        }

        return ext;
    }

    /**
     * 移动文件
     *
     * @param file       需要移动的文件
     * @param targetFile 移动文件的目标地址
     * @return 返回是否移动文件成功
     */
    public static boolean moveTo(File file, File targetFile) {
        File toFile = targetFile;
        boolean succ = false;

        try {
            if (file != null && file.isFile()) {
                // 复制到回收站
                copyfile(file, toFile, true);

                // 删除原文件
                succ = file.delete();
                LogUtil.d(
                        "Move " + file.getAbsolutePath() + " to " + toFile.getAbsolutePath() + " " + succ);
            }
        } catch (Exception e) {
            LogUtil.d(
                    "Move " + file.getAbsolutePath() + " to " + toFile.getAbsolutePath() + " exception:" + e.getMessage());
        }

        return succ;
    }

    /**
     * 复制文件
     *
     * @param fromFile 需要复制的文件
     * @param toFile   复制文件的目标地址
     * @param rewrite  如果目标文件存在，是否覆盖原文件
     */
    public static void copyfile(File fromFile, File toFile, boolean rewrite) {
        if (!fromFile.exists()) {
            return;
        }

        if (!fromFile.isFile()) {
            return;
        }

        if (!fromFile.canRead()) {
            return;
        }

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }

        if (toFile.exists() && rewrite) {
            toFile.delete();
        }

        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);

            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception ex) {
            LogUtil.e("Copy file exception:", ex.getMessage());
        }
    }
}
