package freaktemplate.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import freaktemplate.store3.R;

public class CustomHomeViewHolder extends RecyclerView.ViewHolder {
    public TextView txt_rname, txt_add, txt_distance;
    public ImageView img_featured, programImage;
    public RatingBar ratb;

    public CustomHomeViewHolder(View itemView) {
        super(itemView);

        //initializing cell views
        txt_rname = itemView.findViewById(R.id.txt_name);
        txt_add = itemView.findViewById(R.id.txt_address);
        txt_distance = itemView.findViewById(R.id.txt_distance);
        img_featured = itemView.findViewById(R.id.img_featured);
        programImage = itemView.findViewById(R.id.img_storediff);
        ratb = itemView.findViewById(R.id.rate1);
    }
}
