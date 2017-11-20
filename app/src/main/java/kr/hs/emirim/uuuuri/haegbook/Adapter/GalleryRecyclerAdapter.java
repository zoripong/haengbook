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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Interface.OnItemClickListener;
import kr.hs.emirim.uuuuri.haegbook.Model.GalleryImage;
import kr.hs.emirim.uuuuri.haegbook.R;


public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.PhotoViewHolder> {
    private final String TAG = "GalleryRecyclerAdapter";

    private Activity mActivity;

    private int itemLayout;
    private List<GalleryImage> mPhotoList;
    private List<GalleryImage> mSelectedPhotoList;

    private OnItemClickListener onItemClickListener;

    /**
     * PhotoList 반환
     * @return
     */
    public List<GalleryImage> getmPhotoList() {
        return mPhotoList;
    }

    /**
     * 선택된 PhotoList 반환
     * @return
     */
    public List<GalleryImage> getSelectedPhotoList(){
        return mSelectedPhotoList;
    }

    public void addSelectedPhotoList(GalleryImage galleryImage){
        if(mSelectedPhotoList.contains(galleryImage))
            return;

        mSelectedPhotoList.add(galleryImage);
    }

    public void removeSelectedPhotoList(GalleryImage galleryImage){
        mSelectedPhotoList.remove(galleryImage);
    }

    /**
     * 아이템 선택시 호출되는 리스너
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    /**
     * 생성자
     * @param photoList
     * @param itemLayout
     */
    public GalleryRecyclerAdapter(Activity activity, List<GalleryImage> photoList, int itemLayout) {

        mActivity = activity;

        this.mPhotoList = photoList;
        this.itemLayout = itemLayout;

        mSelectedPhotoList = new ArrayList<>();

    }

    /**
     * 레이아웃을 만들어서 Holer에 저장
     *
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new PhotoViewHolder(view);
    }


    /**
     * listView getView 를 대체
     * 넘겨 받은 데이터를 화면에 출력하는 역할
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final PhotoViewHolder viewHolder, final int position) {

        final GalleryImage galleryImage = mPhotoList.get(position);

        Log.e(TAG, "Recycler : "+galleryImage.getImgPath());

        Glide.with(mActivity)
                .load(galleryImage.getImgPath())
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)// for local images
                .into(viewHolder.photoIv);

        //선택
        if(galleryImage.isSelected()){
            viewHolder.layoutSelect.setVisibility(View.VISIBLE);
        }else{
            viewHolder.layoutSelect.setVisibility(View.INVISIBLE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(viewHolder, position);
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(mActivity, R.style.MyDialog);
                dialog.setContentView(R.layout.dialog_image_preview);

                final ImageView preView = dialog.findViewById(R.id.preview_iv);
                Log.e(TAG, "Dialog : "+galleryImage.getImgPath());

                final LinearLayout root = dialog.findViewById(R.id.root);

                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                dialog.findViewById(R.id.delete_iv).setVisibility(View.GONE);
                dialog.findViewById(R.id.download_iv).setVisibility(View.GONE);

                dialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });



                //// TODO: 2017-11-18 다이얼로그 가로만큼..
                Glide.with(mActivity)
                        .load(galleryImage.getImgPath())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)// for local images
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                preView.setImageBitmap(bitmap);
                                dialog.show();
                                Log.e(TAG, "base : " + bitmap.getWidth()+" / resize : "+preView.getWidth());
                            }
                        });

                ((TextView)dialog.findViewById(R.id.detail_tv)).setText(galleryImage.getImgPath());


                 return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }


    /**
     * 뷰 재활용을 위한 viewHolder
     */
    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        public ImageView photoIv;
        public RelativeLayout layoutSelect;

        public PhotoViewHolder(View itemView) {
            super(itemView);

            photoIv = (ImageView) itemView.findViewById(R.id.photo_iv);
            layoutSelect = (RelativeLayout) itemView.findViewById(R.id.select_check_layout);
        }

    }
}

