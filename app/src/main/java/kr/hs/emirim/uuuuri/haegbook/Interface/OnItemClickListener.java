package kr.hs.emirim.uuuuri.haegbook.Interface;

import kr.hs.emirim.uuuuri.haegbook.Adapter.GalleryAdapter;


public interface OnItemClickListener {
    void OnItemClick(GalleryAdapter.PhotoViewHolder photoViewHolder, int position);
}
