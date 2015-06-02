package cn.iam007.pic.clean.master.duplicate;

public class DuplicateHoldAdapter {
    private DuplicateImageAdapter adapter;

    public DuplicateImageAdapter getHoldAdapter() {
        return adapter;
    }

    public void setHoldAdapter(DuplicateImageAdapter adapter) {
        this.adapter = adapter;
    }

    private static final DuplicateHoldAdapter holder = new DuplicateHoldAdapter();

    public static DuplicateHoldAdapter getInstance() {
        return holder;
    }
}
