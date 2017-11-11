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
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Interface.OnItemClickListener;
import kr.hs.emirim.uuuuri.haegbook.Model.GalleryImage;
import kr.hs.emirim.uuuuri.haegbook.R;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {


    private Activity mActivity;

    private int itemLayout;
    private List<GalleryImage> mPhotoList;

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

        List<GalleryImage> mSelectPhotoList = new ArrayList<>();

        for (int i = 0; i < mPhotoList.size(); i++) {

            GalleryImage galleryImage = mPhotoList.get(i);
            if(galleryImage.isSelected()){
                mSelectPhotoList.add(galleryImage);
            }
        }

        return mSelectPhotoList;
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
    public GalleryAdapter(Activity activity, List<GalleryImage> photoList, int itemLayout) {

        mActivity = activity;

        this.mPhotoList = photoList;
        this.itemLayout = itemLayout;

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

        Glide.with(mActivity)
                .load(galleryImage.getImgPath())
                .centerCrop()
                .crossFade()
                .into(viewHolder.imgPhoto);

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

                return false;
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

        public ImageView imgPhoto;
        public RelativeLayout layoutSelect;

        public PhotoViewHolder(View itemView) {
            super(itemView);

            imgPhoto = (ImageView) itemView.findViewById(R.id.photo_iv);
            layoutSelect = (RelativeLayout) itemView.findViewById(R.id.select_check_layout);
        }

    }
}

