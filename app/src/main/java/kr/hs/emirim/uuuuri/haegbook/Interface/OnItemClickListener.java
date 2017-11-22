package kr.hs.emirim.uuuuri.haegbook.Interface;

import kr.hs.emirim.uuuuri.haegbook.Adapter.GalleryRecyclerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Adapter.ImageListRecyclerAdapter;


public interface OnItemClickListener {
    void OnItemClick(GalleryRecyclerAdapter.PhotoViewHolder photoViewHolder, int position);
    void OnItemClick(ImageListRecyclerAdapter.ImageViewHolder imageViewHolder, int position);
}
