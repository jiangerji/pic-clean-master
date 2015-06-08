package cn.iam007.pic.clean.master.about;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import cn.iam007.pic.clean.master.R;
import cn.iam007.pic.clean.master.utils.PlatformUtils;

public class AboutRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<AboutLibrary> libs = new LinkedList<AboutLibrary>();

    private Integer aboutVersionCode;
    private String aboutVersionName;
    private Drawable aboutIcon;

    public AboutRecyclerViewAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.activity_about_header, viewGroup, false);
            PlatformUtils.applyFonts(v);
            return new HeaderViewHolder(v);
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.activity_about_library_item, viewGroup, false);
        PlatformUtils.applyFonts(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final Context ctx = viewHolder.itemView.getContext();
        if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            //Set the description or hide it
            holder.aboutAppName.setText(R.string.app_name);
            //set the Version
            holder.aboutVersion.setText(ctx.getString(R.string.about_version, aboutVersionName));
            //Set the description
            holder.aboutAppDescription.setText(Html.fromHtml(getDescription()));
        } else if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;

            final AboutLibrary library = getItem(position);

            RippleForegroundListener rippleForegroundListener = new RippleForegroundListener();
            rippleForegroundListener.setCardView(holder.card);

            //Set texts
            holder.libraryName.setText(library.getLibraryName());
            holder.libraryCreator.setText(library.getAuthor());
            holder.libraryDescription.setText(library.getLibraryDescription());

            holder.libraryBottomDivider.setVisibility(View.GONE);
            //Set License or Version Text
            holder.libraryBottomContainer.setVisibility(View.GONE);
            holder.libraryBottomContainer.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(library.getLibraryVersion())) {
                holder.libraryBottomDivider.setVisibility(View.VISIBLE);
                holder.libraryBottomContainer.setVisibility(View.VISIBLE);
                holder.libraryVersion.setText(library.getLibraryVersion());
            }

            //Define onClickListener
            if (!TextUtils.isEmpty(library.getAuthorWebsite())) {
                holder.libraryCreator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAuthorWebsite(ctx, library.getAuthorWebsite());
                    }
                });
            } else {
                holder.libraryCreator.setOnTouchListener(null);
                holder.libraryCreator.setOnClickListener(null);
            }

            if (!TextUtils.isEmpty(library.getLibraryWebsite()) && !TextUtils.isEmpty(
                    library.getRepositoryLink())) {
                holder.libraryDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLibraryWebsite(ctx,
                                library.getLibraryWebsite() != null ? library.getLibraryWebsite() : library.getRepositoryLink());
                    }
                });
            } else {
                holder.libraryDescription.setOnTouchListener(null);
                holder.libraryDescription.setOnClickListener(null);
            }

            holder.libraryBottomContainer.setOnTouchListener(null);
            holder.libraryBottomContainer.setOnClickListener(null);

//            }
        }
    }

    /**
     * helper method to open the author website
     *
     * @param ctx
     * @param authorWebsite
     */
    private void openAuthorWebsite(Context ctx, String authorWebsite) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorWebsite));
            ctx.startActivity(browserIntent);
        } catch (Exception ex) {
        }
    }

    /**
     * helper method to open the library website
     *
     * @param ctx
     * @param libraryWebsite
     */
    private void openLibraryWebsite(Context ctx, String libraryWebsite) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(libraryWebsite));
            ctx.startActivity(browserIntent);
        } catch (Exception ex) {
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return libs == null ? 0 : libs.size() + 1;
    }

    public AboutLibrary getItem(int pos) {
        return libs.get(pos - 1);
    }

    public long getItemId(int pos) {
        return pos;
    }

    public void setLibs(List<AboutLibrary> libs) {
        this.libs = libs;
        this.notifyDataSetChanged();
    }

    public void addLibs(List<AboutLibrary> libs) {
        this.libs.addAll(libs);
    }

    String description;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setHeader(String aboutVersionName, Integer aboutVersionCode, Drawable aboutIcon) {
        this.libs.add(0, null);
        this.aboutVersionName = aboutVersionName;
        this.aboutVersionCode = aboutVersionCode;
        this.aboutIcon = aboutIcon;
        this.notifyItemInserted(0);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView aboutAppName;
        TextView aboutVersion;
        View aboutDivider;
        TextView aboutAppDescription;

        public HeaderViewHolder(View headerView) {
            super(headerView);

            aboutAppName = (TextView) headerView.findViewById(R.id.aboutName);
            aboutVersion = (TextView) headerView.findViewById(R.id.aboutVersion);
            aboutDivider = headerView.findViewById(R.id.aboutDivider);
            aboutAppDescription = (TextView) headerView.findViewById(R.id.aboutDescription);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;

        TextView libraryName;
        TextView libraryCreator;
        View libraryDescriptionDivider;
        TextView libraryDescription;

        View libraryBottomDivider;
        View libraryBottomContainer;

        TextView libraryVersion;
        TextView libraryLicense;

        public ViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView;

            libraryName = (TextView) itemView.findViewById(R.id.libraryName);
            libraryCreator = (TextView) itemView.findViewById(R.id.libraryCreator);
            libraryDescriptionDivider = itemView.findViewById(R.id.libraryDescriptionDivider);
            libraryDescription = (TextView) itemView.findViewById(R.id.libraryDescription);

            libraryBottomDivider = itemView.findViewById(R.id.libraryBottomDivider);
            libraryBottomContainer = itemView.findViewById(R.id.libraryBottomContainer);

            libraryVersion = (TextView) itemView.findViewById(R.id.libraryVersion);
            libraryLicense = (TextView) itemView.findViewById(R.id.libraryLicense);
        }

    }
}
