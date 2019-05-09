package freaktemplate.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import freaktemplate.getset.Storegetset;
import freaktemplate.store3.R;

public class CustomHomeAdapter extends RecyclerView.Adapter<CustomHomeViewHolder> {
    private ArrayList<Storegetset> data;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Typeface tf;
    private Context context;
    private onClickItem onClickItem;


    public CustomHomeAdapter(ArrayList<Storegetset> data, RecyclerView recyclerView) {
        this.data = data;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleThreshold = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                final int lastItem = lastVisibleItem + visibleThreshold;
                Log.e("Check", isLoading + "  " + "Total Item Count " + totalItemCount + "lastVisibleItem " + lastVisibleItem + "visible threshold " + visibleThreshold);
                if (!isLoading && totalItemCount == (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }

            }
        });
    }

public Storegetset getItem(int position)
{
    return data.get(position);
}

    public void setOnClickListnerR(onClickItem clickListnerR) {
        this.onClickItem = clickListnerR;
    }

    public void addItem(ArrayList<Storegetset> item, int position) {
        if (item.size() != 0) {
            data.addAll(item);
            notifyItemInserted(position);
        }
    }

    @NonNull
    @Override
    public CustomHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        final View itemView = LayoutInflater.from(context).inflate(R.layout.home_cell, parent, false);
        final CustomHomeViewHolder viewHolder = new CustomHomeViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem.onItemClicked(viewHolder.getAdapterPosition(), itemView);
            }
        });
        tf = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/Roboto-Medium.ttf");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHomeViewHolder holder, int position) {

        //setting typefaces
        holder.txt_rname.setTypeface(tf);
        holder.txt_add.setTypeface(tf);

        //setting data
        holder.txt_rname.setText(data.get(position).getName());
        holder.txt_add.setText(data.get(position).getAddress());
        holder.txt_distance.setText(String.format("%s Km", data.get(position).getDistance()));
        holder.txt_rname.setText(data.get(position).getName());

        String featured = data.get(position).getFeatured();
        if (featured.equals("1")) {
            holder.img_featured.setImageResource(R.drawable.feature_text_bg);
        }

        String image = data.get(position).getThumbnail().replace(" ", "%20");
        Picasso.get().load(String.format("%suploads/store/full/%s", context.getString(R.string.link), image)).into(holder.programImage);

        holder.ratb.setFocusable(false);
        holder.ratb.setRating(Float.parseFloat(data.get(position).getRatting()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface onClickItem {
        void onItemClicked(int position, View view);

    }
}
