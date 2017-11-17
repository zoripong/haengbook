package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.haegbook.Manager.GridDividerDecoration;
import kr.hs.emirim.uuuuri.haegbook.Manager.ImageRecyclerSetter;
import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;
import kr.hs.emirim.uuuuri.haegbook.R;

// todo hide animation when recyclerview scroll down
public class PhotoFragment extends Fragment {
    private final String TAG = "PhotoFragment";


    private FirebaseDatabase mDatabase;

    private View rootView;

    private RecyclerView recyclerView;
    private ImageRecyclerSetter imageRecyclerSetter;

    private String mBookCode;
    private String mPeriod;


    private int spinnerIndex=0;
    private ImageView imageView;

    private ArrayList<FirebaseImage> mAllImages;


    ArrayList<FirebaseImage> mImages;


    private ArrayList<String> dateList;


    public PhotoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        mAllImages = new ArrayList<>();
        mImages = new ArrayList<>();

        imageView= rootView.findViewById(R.id.image);
        imageRecyclerSetter = new ImageRecyclerSetter(getActivity(), true);


        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));

        getFBImage();


        return rootView;
    }

    public void getFBImage(){
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference imageRef = mDatabase.getReference("BookInfo/"+mBookCode+"/Content/Images");
        final ProgressDialog gettingImageDialog = new ProgressDialog(getActivity());
        gettingImageDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        gettingImageDialog.setMessage("로딩중입니다..");
        gettingImageDialog.show();
        ValueEventListener imageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot FBimage) {
                mAllImages.clear();

                Iterator<DataSnapshot> childIterator = FBimage.getChildren().iterator();
                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
                while(childIterator.hasNext()) {
                    DataSnapshot imageSnapshot = childIterator.next();
                    String imageComment=imageSnapshot.child("imageComment").getValue(String.class);
                    String imageURI=imageSnapshot.child("imageURI").getValue(String.class);
                    String date=imageSnapshot.child("date").getValue(String.class);

                    mAllImages.add(new FirebaseImage(imageComment,imageURI,date));

                }
                if(dateList == null){

                    if(mAllImages.size() == 0)
                        ((TextView)rootView.findViewById(R.id.message_tv)).setText("등록된 이미지가 없습니다 :(");
                    else
                        ((TextView)rootView.findViewById(R.id.message_tv)).setText("");

                    imageRecyclerSetter.setRecyclerCardView(recyclerView, mAllImages, null);
                }
                else{
                    spinnerItemSelected(spinnerIndex);
                }

                gettingImageDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        imageRef.addValueEventListener(imageListener);
    }

    public void spinnerItemSelected(int i){
        Log.e(TAG, "선택 된 날짜 : "+ dateList.get(i));
        Log.e(TAG, "DIALOG LIST : "+ dateList.toString());
        Log.e(TAG, "선택 index : "+i);

        spinnerIndex=i;
        mImages.clear();

        if(i == 0){
            for(int j = 0; j<mAllImages.size(); j++){
                mImages.add(mAllImages.get(j));
            }
            Log.e("TAG", "보여지는 사진 : "+ mImages.toString());
        }else{
            for(int j = 0; j<mAllImages.size(); j++){
                if(mAllImages.get(j).getDate().equals(dateList.get(i))){
                    Toast.makeText(getContext(), dateList.get(i), Toast.LENGTH_SHORT).show();
                    mImages.add(mAllImages.get(j));
                }
            }
        }

        if(mImages.size() == 0)
            ((TextView)rootView.findViewById(R.id.message_tv)).setText("등록된 이미지가 없습니다 :(");
        else
            ((TextView)rootView.findViewById(R.id.message_tv)).setText("");

        imageRecyclerSetter.setRecyclerCardView(recyclerView, mImages, null);
    }

    public void setDateList(ArrayList<String> dateList) {
        this.dateList = dateList;
    }

    public void setBookCode(String bookCode){
        mBookCode = bookCode;
    }

    public void setPeriod(String period){
        mPeriod = period;
    }

}