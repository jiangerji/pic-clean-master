package cn.iam007.pic.clean.master.recycler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.List;

import cn.iam007.pic.clean.master.Constants;
import cn.iam007.pic.clean.master.utils.CryptoUtil;
import cn.iam007.pic.clean.master.utils.FileUtil;
import cn.iam007.pic.clean.master.utils.LogUtil;

/**
 * Created by Administrator on 2015/5/26.
 */
public class RecyclerManager {

    private final static String RECYCLER_DB_NAME = ".recycler";

    private static RecyclerManager _instance = new RecyclerManager();
    private Context mContext;
    private DbUtils mDbUtils;

    private RecyclerManager() {

    }

    public static RecyclerManager getInstance() {
        return _instance;
    }

    public void init(Context context) {
        mContext = context;
        mDbUtils = DbUtils.create(mContext, Constants.getRecyclerPath().getAbsolutePath(),
                RECYCLER_DB_NAME);
        LogUtil.d("Init Finished!");

        validate();
    }

    private void validate() {
        try {
            List<RecyclerImageItem> items = mDbUtils.findAll(RecyclerImageItem.class);
            if (items != null) {
                for (RecyclerImageItem item : items) {
                    if (item != null) {
                        LogUtil.d(item.toString());
                    }
                }
            }
        } catch (DbException e) {

        }
    }

    /**
     * 将filePath指定的文件移除回收站中
     *
     * @param filePath 需要移除到回收站的文件的绝对路径
     */
    public void deleteToRecycler(String filePath) {
        if (filePath != null) {
            // 删除到回收站中
            File file = new File(filePath);
            String ext = FileUtil.getFileExt(filePath);
            String id = CryptoUtil.getMD5String(filePath);
            String recyclerFileName = id + "." + ext;
            File recyclerFile = new File(Constants.getRecyclerPath(), recyclerFileName);

            RecyclerImageItem item =
                    new RecyclerImageItem(recyclerFile.getAbsolutePath(), filePath,
                            recyclerFileName);
            try {
                mDbUtils.save(item);
            } catch (Exception e) {
                LogUtil.d("Save Recycler Exception:" + e.toString());
            }

            FileUtil.moveTo(file, recyclerFile);

            MediaScannerConnection.scanFile(mContext, new String[]{filePath}, null, null);
        }

    }

    /**
     * 永久删除回收站的文件
     *
     * @param item
     */
    public void delete(RecyclerImageItem item) {
        if (item != null) {
            try {
                mDbUtils.delete(item);
                LogUtil.d("Delete Recycler Item:" + item.getId());
            } catch (DbException e) {
                LogUtil.d("Delete Recycler Exception:" + e.toString());
            }

            File file = new File(item.getRealRecyclerPath());
            file.delete();
        }
    }

    /**
     * 将item的回收站图片恢复到原始路径上
     *
     * @param item
     */
    public void restore(RecyclerImageItem item) {
        if (item != null) {
            String sourcePath = item.getSourcePath();
            String recyclerPath = item.getRealRecyclerPath();
            FileUtil.moveTo(new File(recyclerPath), new File(sourcePath));

            MediaScannerConnection.scanFile(mContext, new String[]{sourcePath}, null, null);

            try {
                mDbUtils.delete(item);
                LogUtil.d("Delete Recycler Item:" + item.getId());
            } catch (DbException e) {
                LogUtil.d("Delete Recycler Exception:" + e.toString());
            }
        }
    }

    public RecyclerImageItem findSourcePath(String id) {
        String sourcePath = "";
        RecyclerImageItem item = null;

        try {
            item = mDbUtils.findById(RecyclerImageItem.class, id);
            if (item != null){
                sourcePath = item.getSourcePath();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return item;
    }
}
