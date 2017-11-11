package kr.hs.emirim.uuuuri.haegbook.Interface;

import kr.hs.emirim.uuuuri.haegbook.Adapter.GalleryRecyclerAdapter;


public interface OnItemClickListener {
    void OnItemClick(GalleryRecyclerAdapter.PhotoViewHolder photoViewHolder, int position);
}
