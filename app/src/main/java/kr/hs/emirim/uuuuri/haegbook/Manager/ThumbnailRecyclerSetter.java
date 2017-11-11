package kr.hs.emirim.uuuuri.haegbook.Manager;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Adapter.ThumbnailRecyclerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Model.GalleryImage;


public class ThumbnailRecyclerSetter {
    List<GalleryImage> items;
    ThumbnailRecyclerAdapter adapter;

    Context context;
    Activity nowActivity;

    public ThumbnailRecyclerSetter(Context context, Activity nowActivity) {
        this.context = context;
        this.nowActivity = nowActivity;
    }

    public boolean setRecyclerCardView(RecyclerView recyclerView, List<GalleryImage> imageArrayList){
        items = imageArrayList;
        adapter = new ThumbnailRecyclerAdapter(context, nowActivity, items);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(nowActivity, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        return true;
    }

}
