package cn.iam007.pic.clean.master.recycler;

public class RecyclerHoldAdapter {
    private static final RecyclerHoldAdapter holder = new RecyclerHoldAdapter();
    private RecyclerImageAdapter adapter;

    public static RecyclerHoldAdapter getInstance() {
        return holder;
    }

    public RecyclerImageAdapter getHoldAdapter() {
        return adapter;
    }

    public void setHoldAdapter(RecyclerImageAdapter adapter) {
        this.adapter = adapter;
    }
}
