package kr.hs.emirim.uuuuri.haegbook.Manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Adapter.ImageListRecyclerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Interface.OnItemClickListener;
import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;

/**
 * Created by doori on 2017-11-10.
 */

public class ImageListRecyclerSetter {
    private ArrayList<FirebaseImage> items;
    private ImageListRecyclerAdapter adapter;

    private Context context;
    private Activity nowActivity;
    private boolean isPhotoFragment;

    public ImageListRecyclerSetter(Activity nowActivity, boolean isPhotoFragment) {
        this.nowActivity = nowActivity;
        this.isPhotoFragment = isPhotoFragment;
    }

    public boolean setRecyclerCardView(RecyclerView recyclerView, ArrayList<FirebaseImage> imageArrayList, ArrayList<Bitmap> bitmaps, OnItemClickListener listener ){

        items = imageArrayList;

        adapter = new ImageListRecyclerAdapter(nowActivity,items, bitmaps, isPhotoFragment);
        if(listener != null)
            adapter.setOnItemClickListener(listener);
        recyclerView.setAdapter(adapter);

        return true;
    }

    public ArrayList<FirebaseImage> getPhotoList(){
        return adapter.getPhotoList();
    }

}