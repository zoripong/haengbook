package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by doori on 2017-11-10.
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder> {
    Activity mActivity;

    ArrayList<FirebaseImage> items;


    public ImageRecyclerAdapter(Activity activity,ArrayList<FirebaseImage> items){
        this.mActivity = activity;
        this.items = items;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        final FirebaseImage firebaseImage = items.get(position);
        Glide.with(mActivity)
                .load(firebaseImage.getImageURI())
                .centerCrop()
                .crossFade()
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public RelativeLayout layoutSelect;


        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo_iv);
            //  layoutSelect = (RelativeLayout) itemView.findViewById(R.id.select_check_layout);

        }
    }
}