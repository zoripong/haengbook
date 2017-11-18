package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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
        // Reference to an image file in Firebase Storage
        StorageReference storageReference = null;
        if (firebaseImage.getImageURI() != null) {

            storageReference = FirebaseStorage.getInstance().getReference(firebaseImage.getImageURI());
        }

        if (storageReference != null) {
            Glide.with(mActivity)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .centerCrop()
                    .into(holder.imageView);

        }



        if(!isPhotoFragment) {
            if(firebaseImage.isSelected()){
                holder.layoutSelect.setVisibility(View.VISIBLE);
            }else{
                holder.layoutSelect.setVisibility(View.INVISIBLE);
            }
        }

        final StorageReference finalStorageReference = storageReference;
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



                    if(finalStorageReference != null) {
                        Glide.with(mActivity)
                                .using(new FirebaseImageLoader())
                                .load(finalStorageReference)
                                .centerCrop()
                                .into(new SimpleTarget<GlideDrawable>() {
                                    @Override
                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                        preView.setImageDrawable(resource);
                                        dialog.show();
                                    }
                                });
                    }

                    ((TextView)dialog.findViewById(R.id.detail_tv)).setText(firebaseImage.getImageComment());
                    dialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.delete_iv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 2017-11-18 이미지 삭제하기
                        }
                    });
                    dialog.findViewById(R.id.download_iv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(mActivity, firebaseImage.getImageURI(),Toast.LENGTH_SHORT).show();
                            // TODO: 2017-11-18 : down load
                            // 퍼미션 체크 후 다운로드

                            File localFile = null;
                            try {
                                localFile = File.createTempFile("images", "jpg");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference(firebaseImage.getImageURI());

                            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Local temp file has been created
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

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