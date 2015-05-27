package cn.iam007.pic.clean.master.duplicate;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import cn.iam007.pic.clean.master.utils.ImageUtils;
import cn.iam007.pic.clean.master.utils.LogUtil;

public class DuplicateImageFindTask extends AsyncTask<String, Integer, Long> {

    private DuplicateFindCallback mCallback = null;

    public DuplicateImageFindTask(DuplicateFindCallback callback) {
        mCallback = callback;
    }

    @Override
    protected Long doInBackground(String... folders) {
        if (folders.length > 0) {
            String rootDir = folders[0];
            File root = new File(rootDir);
            parseDirectory(root);
        }

        // 扫描结束
        if (mCallback != null) {
            mCallback.onDuplicateFindFinished(mTotalFileCount, mTotalFileSize);
        }

        return 0L;
    }

    private int mTotalFileCount = 0; // 总共文件数量
    private long mTotalFileSize = 0; // 总共文件大小

    private void parseDirectory(File root) {
        ArrayList<ImageHolder> holders = new ArrayList<>();
        if (root.isDirectory()) {
            File files[] = root.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    // 暂时什么都不做
                    parseDirectory(f);
                } else {
                    if (ImageUtils.isImage(f.getName())) {
                        String dateTime = getDateTime(f);
                        long fileSize = f.length();

                        mTotalFileSize += fileSize;
                        mTotalFileCount++;

//                        LogUtil.d("file:" + f + ", " + dateTime);
                        if (dateTime != null) {
                            ImageHolder holder = new ImageHolder(f.getAbsolutePath(),
                                    dateTime,
                                    fileSize);
                            holders.add(holder);
                        }
                    }
                }
            }
        }

        // 开始进行查找回调
        if (mCallback != null) {
            mCallback.onDuplicateFindStart(root.getAbsolutePath(), holders.size());
        }

        // 按时间排序
        Object[] objs = holders.toArray();
        java.util.Arrays.sort(objs, new MyComparator());
        LogUtil.d("Finish sort:" + objs.length);

        // 查找可能相同的图片
        if (objs.length > 1) {
            ImageHolder preImage = (ImageHolder) objs[0];
            ArrayList<ImageHolder> duplicateImagesSection = new ArrayList<>();
            duplicateImagesSection.add(preImage);
            ImageHolder imageHolder;
            for (int i = 1; i < objs.length; i++) {
                imageHolder = (ImageHolder) objs[i];

                // 扫描图片回调
                if (mCallback != null) {
                    mCallback.onDuplicateFindExecute(imageHolder.imagePath,
                            imageHolder.imageSize);
                    mCallback.onDuplicateFindProgressUpdate(((double) i)
                            / objs.length);
                }

                if (imageHolder.imageTS == 0) {
                    continue;
                }

                if (imageHolder.imageTS - preImage.imageTS < 5100) {
                    duplicateImagesSection.add(imageHolder);
                } else {
                    // 回调，找到duplicate section
                    if (mCallback != null && duplicateImagesSection.size() > 1) {
                        SectionItem sectionItem = new SectionItem(preImage.imageDate,
                                duplicateImagesSection);
                        mCallback.onDuplicateSectionFind(sectionItem);
                    }

                    duplicateImagesSection = new ArrayList<>();
                    duplicateImagesSection.add(imageHolder);
                }

                preImage = imageHolder;
            }

            // 处理还没有处理的，找到的duplicate section
            if (mCallback != null && duplicateImagesSection.size() > 1) {
                imageHolder = duplicateImagesSection.get(0);
                SectionItem sectionItem = new SectionItem(imageHolder.getImageDate(),
                        duplicateImagesSection);
                mCallback.onDuplicateSectionFind(sectionItem);
            }
        }
    }

    /**
     * 计算当前进度
     *
     * @param i
     * @return
     */
    private double calcProgress(int i) {
        double progress = 0.0f;
        if (i <= 100) {
            progress = Math.atan(-0.0001 * i * i + 0.02 * i) * 2
                    / Math.PI;
        } else {
            progress = Math.atan(i * 0.01) * 2 / Math.PI;
        }
        LogUtil.d("" + i + ":"
                + progress);
        return progress;
    }

    public static class SectionItem {
        public SectionItem(String header, ArrayList<ImageHolder> images) {
            this.header = header;
            this.images = images;
        }

        private String header;
        private ArrayList<ImageHolder> images;

        /**
         * @return the header
         */
        public String getHeader() {
            return header;
        }

        public ArrayList<ImageHolder> getImages() {
            return images;
        }
    }

    /**
     * 查找相似图片的回调接口
     *
     * @author Administrator
     */
    public interface DuplicateFindCallback {
        /**
         * 查找过程开始
         *
         * @param folder 查找的目录
         * @param count  目录下的文件数量
         */
        void onDuplicateFindStart(String folder, int count);

        /**
         * 正在查找比较图片文件
         *
         * @param file 文件绝对路径
         * @param size 文件大小
         */
        void onDuplicateFindExecute(String file, long size);

        /**
         * 当前查找进程的进度
         *
         * @param progress 当前进度，[0, 1)
         */
        void onDuplicateFindProgressUpdate(double progress);

        /**
         * 该次查找过程结束
         *
         * @param fileCount 总共扫描多少文件
         * @param fileSize  总共扫描文件的大小，单位为字节
         */
        void onDuplicateFindFinished(int fileCount, long fileSize);

        /**
         * 找到一组相似图片的信息
         *
         * @param sectionItem
         */
        void onDuplicateSectionFind(SectionItem sectionItem);
    }

    /**
     * 用于ImageHolder进行排序
     *
     * @author Administrator
     */
    class MyComparator implements Comparator<Object> {

        public int compare(Object obj1, Object obj2) {
            return ((ImageHolder) obj1).imageTS.compareTo(((ImageHolder) obj2).imageTS);
        }
    }

    /**
     * 获取图片文件的拍摄时间
     *
     * @param file 图片文件
     * @return
     */
    private String getDateTime(File file) {
        String dateTime = null;
        ExifInterface ei;
        try {
            ei = new ExifInterface(file.getAbsolutePath());
            dateTime = ei.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dateTime;
    }

    /**
     * 用于保存扫描的图片信息
     *
     * @author Administrator
     */
    public static class ImageHolder {
        public ImageHolder(String imagePath, String dateTime, long fileSize) {
            this.imageDate = dateTime;
            this.imagePath = imagePath;
            this.imageSize = fileSize;

            String[] formats = {"yyyy:MM:dd HH:mm:ss", "yyyy:MM:dd HH:mm::ss"};

            for (String _format : formats) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat(_format);
                    Date date = format.parse(dateTime);
                    imageTS = date.getTime();
                    LogUtil.d("Format To times:" + dateTime + " " + imageTS);
                    break;
                } catch (Exception e) {
                    LogUtil.d("" + e);
                    // 没有找到日期，暂时不处理
                }
            }

        }

        /**
         * 获取图片文件大小
         *
         * @return the imageSize
         */
        public long getImageSize() {
            return imageSize;
        }

        /**
         * 获取图片的绝对路径
         *
         * @return the imagePath
         */
        public String getImagePath() {
            return imagePath;
        }

        /**
         * 获取图片的拍摄时间
         *
         * @return the imageDate
         */
        public String getImageDate() {
            return imageDate;
        }

        /**
         * 获取图片的拍摄时间戳
         *
         * @return the imageTS
         */
        public Long getImageTS() {
            return imageTS;
        }

        /**
         * 图片文件大小
         */
        long imageSize = 0;

        /**
         * 图片地址
         */
        String imagePath;

        /**
         * 图片生成时间
         */
        String imageDate;

        /**
         * 图片生成时间戳
         */
        Long imageTS = (long) 0;
    }
}
