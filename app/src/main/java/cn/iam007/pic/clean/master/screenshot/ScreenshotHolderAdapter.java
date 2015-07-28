package cn.iam007.pic.clean.master.screenshot;

/**
 * Created by Administrator on 2015/7/27.
 */
public class ScreenshotHolderAdapter {
    private ScreenshotImageAdapter adapter;

    public ScreenshotImageAdapter getHoldAdapter() {
        return adapter;
    }

    public void setHoldAdapter(ScreenshotImageAdapter adapter) {
        this.adapter = adapter;
    }

    private static final ScreenshotHolderAdapter holder = new ScreenshotHolderAdapter();

    public static ScreenshotHolderAdapter getInstance() {
        return holder;
    }
}
