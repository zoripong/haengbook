package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Interface.OnItemClickListener;
import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by doori on 2017-11-10.
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder> {
    private final String TAG = "ImageRecyclerAdapter";

    private Activity mActivity;
    private ArrayList<FirebaseImage> photoList;
    private boolean isPhotoFragment;

    private OnItemClickListener onItemClickListener;

    public ImageRecyclerAdapter(Activity activity,ArrayList<FirebaseImage> items, boolean isPhotoFragment){
        this.mActivity = activity;
        this.photoList = items;
        this.isPhotoFragment = isPhotoFragment;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final FirebaseImage firebaseImage = photoList.get(position);
        Glide.with(mActivity)
                .load(firebaseImage.getImageURI())
                .centerCrop()
                .crossFade()
                .into(holder.imageView);



        if(!isPhotoFragment) {
            if(firebaseImage.isSelected()){
                holder.layoutSelect.setVisibility(View.VISIBLE);
            }else{
                holder.layoutSelect.setVisibility(View.INVISIBLE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isPhotoFragment) {
                    final Dialog dialog = new Dialog(mActivity, R.style.MyDialog);
                    dialog.setContentView(R.layout.dialog_image_preview);

                    final ImageView preView = dialog.findViewById(R.id.preview_iv);
                    Log.e(TAG, "Dialog : " + firebaseImage.getImageURI());

                    dialog.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    Glide.with(mActivity)
                            .load(firebaseImage.getImageURI())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                    // Do something with bitmap here.
                                    preView.setImageBitmap(bitmap);
                                    dialog.show();
                                }
                            });
                }else{

                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(holder, position);
                    }

                }
            }
        });
    }

    public List<FirebaseImage> getPhotoList() {
        return photoList;
    }


    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public RelativeLayout layoutSelect;


        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo_iv);
            layoutSelect = (RelativeLayout) itemView.findViewById(R.id.select_check_layout);

        }
    }
}