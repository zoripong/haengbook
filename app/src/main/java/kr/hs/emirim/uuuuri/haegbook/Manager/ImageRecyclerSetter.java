package kr.hs.emirim.uuuuri.haegbook.Manager;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Adapter.ImageRecyclerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Interface.OnItemClickListener;
import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;

/**
 * Created by doori on 2017-11-10.
 */

public class ImageRecyclerSetter{
    private ArrayList<FirebaseImage> items;
    private ImageRecyclerAdapter adapter;

    private Context context;
    private Activity nowActivity;
    private boolean isPhotoFragment;

    public ImageRecyclerSetter(Activity nowActivity, boolean isPhotoFragment) {
        this.nowActivity = nowActivity;
        this.isPhotoFragment = isPhotoFragment;
    }

    public boolean setRecyclerCardView(RecyclerView recyclerView, ArrayList<FirebaseImage> imageArrayList, OnItemClickListener listener ){

        items = imageArrayList;

        adapter = new ImageRecyclerAdapter(nowActivity,items, isPhotoFragment);
        adapter.setOnItemClickListener(listener);
        recyclerView.setAdapter(adapter);

        return true;

    }

    public List<FirebaseImage> getPhotoList(){
        return adapter.getPhotoList();
    }

}