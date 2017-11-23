package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
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

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by doori on 2017-11-10.
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder> {
    private final String TAG = "ImageRecyclerAdapter";

    private FirebaseDatabase mDatabase;


    private Activity mActivity;
    private ArrayList<FirebaseImage> photoList;
    private ArrayList<Bitmap> images;

    private OnItemClickListener onItemClickListener;

    SharedPreferenceManager spm;

    public ImageRecyclerAdapter(Activity activity,ArrayList<FirebaseImage> items, ArrayList<Bitmap> images){
        this.mActivity = activity;
        this.photoList = items;
        this.images = images;
        spm = new SharedPreferenceManager(activity);
        Log.e(TAG, "실행실행 사이즈 : "+photoList.size());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo_card, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final FirebaseImage firebaseImage = photoList.get(position);
        final Bitmap bitmap = images.get(position);
        // TODO: 2017-11-23 Bitmap으로 넘기기
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(firebaseImage.getImageURI());

        Point pt = new Point();
        mActivity.getWindowManager().getDefaultDisplay().getSize(pt);
        ((WindowManager) mActivity.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(pt);
        int width = pt.x;


        Log.e(TAG, "실행실행");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);


        Glide.with(mActivity)
                .load(stream.toByteArray())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)// for local images
                .centerCrop()
                .override(width, width)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        holder.imageView.setImageBitmap(bitmap);
                    }
                });

        holder.commentTv.setText(firebaseImage.getImageComment());
        holder.dateTv.setText(firebaseImage.getDate());

        holder.menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mActivity, R.style.MyDialog);
                dialog.setContentView(R.layout.dialog_menu);

                dialog.findViewById(R.id.download_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/HaengBook/Download";
                        String filePath = folderPath + "/" + System.currentTimeMillis() + ".jpg";

                        File fileFolderPath = new File(folderPath);
                        if (!fileFolderPath.exists())
                            fileFolderPath.mkdir();

                        File file = new File(filePath);
                        try {
                            FileOutputStream fos = new FileOutputStream(file);

                            if (fos != null) {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.close();
                            } else {
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
                        Toast.makeText(mActivity, "다운로드가 완료 되었습니다. :)", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                if(spm.retrieveString(TravelDetailTag.IS_PUBLISHING_TAG)!=null)
                    dialog.findViewById(R.id.delete_tv).setVisibility(View.GONE);

                dialog.findViewById(R.id.delete_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    // TODO: 2017-11-23 애니메이션
                        final Dialog deleteDialog = new Dialog(mActivity, R.style.MyDialog);
                        deleteDialog.setContentView(R.layout.dialog_delete);
                        deleteDialog.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteFirebaseImage(firebaseImage.getKey());
                                deleteDialog.dismiss();

                            }
                        });
                        deleteDialog.show();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });



        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mActivity, R.style.MyDialog);
                dialog.setContentView(R.layout.dialog_image_detail);


                final ImageView preView = dialog.findViewById(R.id.preview_iv);
                Log.e(TAG, "Dialog : " + firebaseImage.getImageURI());

                dialog.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                Glide.with(mActivity)
                        .load(stream.toByteArray())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                preView.setImageBitmap(bitmap);
                            }
                        });


                dialog.show();
            }



        });
    }

    private void deleteFirebaseImage(String key){
        mDatabase = FirebaseDatabase.getInstance();
        String bookCode =  spm.retrieveString(TravelDetailTag.CARD_BOOK_CODE_TAG);
//        Log.e(TAG, "spm -- >")
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
        public ImageView menuIv;
        public TextView commentTv;
        public TextView dateTv;


        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.preview_iv);
            menuIv = itemView.findViewById(R.id.menu_iv);
            commentTv = itemView.findViewById(R.id.detail_tv);
            dateTv = itemView.findViewById(R.id.date_tv);
        }
    }
}