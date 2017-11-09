package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Model.ImageUploadInfo;

/**
 * Created by doori on 2017-11-10.
 */

public class ImageRecyclerSetter{
    ArrayList<ImageUploadInfo> items;
    ImageRecyclerAdapter adapter;

    Context context;
    Activity nowActivity;

    public ImageRecyclerSetter(Context context, Activity nowActivity) {
        this.context = context;
        this.nowActivity = nowActivity;
    }

    public boolean setRecyclerCardView(RecyclerView recyclerView, ArrayList<ImageUploadInfo> imageArrayList){

//        items = new ArrayList<Receipt>();
        items = imageArrayList;

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        adapter = new ImageRecyclerAdapter(context, nowActivity, items);
        recyclerView.setAdapter(adapter);
        return true;

    }

}
