package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Interface.OnItemClickListener;
import kr.hs.emirim.uuuuri.haegbook.Interface.TravelDetailTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by doori on 2017-11-10.
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder> {
    private final String TAG = "ImageRecyclerAdapter";

    private FirebaseDatabase mDatabase;


    private Activity mActivity;
    private ArrayList<FirebaseImage> photoList;
    private boolean isPhotoFragment;

    private OnItemClickListener onItemClickListener;

    SharedPreferenceManager spm;

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

        spm = new SharedPreferenceManager(mActivity);

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
                    final ProgressDialog mProgressDialog =  new ProgressDialog(mActivity);
                    mProgressDialog.setMessage("이미지를 불러오고 있습니다.");
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();

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

                    final Bitmap[] previewBitmap = new Bitmap[1];

                    if(finalStorageReference != null) {
                        Glide.with(mActivity)
                                .using(new FirebaseImageLoader())
                                .load(finalStorageReference)
                                .asBitmap()
                                .centerCrop()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                        preView.setImageBitmap(bitmap);
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                        }

                                        dialog.show();
                                        previewBitmap[0] = bitmap;
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
                            deleteFirebaseImage(firebaseImage.getKey());
                            Log.e("이미지 키",firebaseImage.getKey());
                            Toast.makeText(mActivity, "사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    //TODO DEBUG PUBLISHING GONE
                    if(spm.retrieveString(TravelDetailTag.IS_PUBLISHING_TAG)!=null)
                        dialog.findViewById(R.id.delete_iv).setVisibility(View.GONE);

                    dialog.findViewById(R.id.download_iv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/HaengBook/Download";
                            String filePath  = folderPath + "/" + System.currentTimeMillis() + ".jpg";

                            File fileFolderPath = new File(folderPath);
                            if(!fileFolderPath.exists())
                                fileFolderPath.mkdir();

                            File file = new File(filePath);
                            try {
                                FileOutputStream fos = new FileOutputStream(file);

                                if (fos != null && preView!=null) {
                                    previewBitmap[0].compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    fos.close();
                                }else{
                                    Toast.makeText(mActivity, "다운로드 실패", Toast.LENGTH_SHORT).show();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            MediaScannerConnection.scanFile(mActivity,
                                    new String[]{file.getAbsolutePath()},
                                    null,
                                    new MediaScannerConnection.MediaScannerConnectionClient() {
                                        @Override
                                        public void onMediaScannerConnected() {
                                            Toast.makeText(mActivity, "다운로드 완료", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onScanCompleted(String path, Uri uri) {
                                            Log.e("File scan", "file:" + path + "was scanned successfully");
                                        }
                                    });

                            dialog.dismiss();

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
    private void deleteFirebaseImage(String key){
        mDatabase = FirebaseDatabase.getInstance();
        String bookCode =  spm.retrieveString(TravelDetailTag.CARD_BOOK_CODE_TAG);
        final DatabaseReference receiptRef = mDatabase.getReference("BookInfo/"+bookCode+"/Content/Images/"+key);
        receiptRef.removeValue();
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