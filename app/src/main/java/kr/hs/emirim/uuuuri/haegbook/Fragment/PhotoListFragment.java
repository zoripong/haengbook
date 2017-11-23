package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Manager.GridDividerDecoration;
import kr.hs.emirim.uuuuri.haegbook.Manager.ImageListRecyclerSetter;
import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;
import kr.hs.emirim.uuuuri.haegbook.R;

public class PhotoListFragment extends BottomSheetFragment {
    private final String TAG = "PhotoFragment";
    private final String BUNDLE_TAG ="BUNDLE_TAG";
    private final String BUNDLE_TAG_2 ="BUNDLE_TAG_2";

    private FirebaseDatabase mDatabase;

    private View rootView;

    private RecyclerView recyclerView;
    private ImageListRecyclerSetter imageRecyclerSetter;

    private ArrayList<FirebaseImage> mAllImages;
    private ArrayList<Bitmap> mAllBitmaps;

    public PhotoListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAllBitmaps = getArguments().getParcelableArrayList(BUNDLE_TAG);
        mAllImages = getArguments().getParcelableArrayList(BUNDLE_TAG_2);

        rootView = inflater.inflate(R.layout.fragment_photo_list, container, false);

        imageRecyclerSetter = new ImageListRecyclerSetter(getActivity(), true);


        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));

        // TODO: 2017-11-23 change bitmap
        imageRecyclerSetter.setRecyclerCardView(recyclerView, mAllImages, mAllBitmaps, null);

        return rootView;
    }



}