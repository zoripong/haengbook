package kr.hs.emirim.uuuuri.haegbook.Interface;

import kr.hs.emirim.uuuuri.haegbook.Adapter.GalleryRecyclerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Adapter.ImageRecyclerAdapter;


public interface OnItemClickListener {
    void OnItemClick(GalleryRecyclerAdapter.PhotoViewHolder photoViewHolder, int position);
    void OnItemClick(ImageRecyclerAdapter.ImageViewHolder imageViewHolder, int position);
}
