package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.haegbook.Manager.ImageRecyclerSetter;
import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;
import kr.hs.emirim.uuuuri.haegbook.R;

// todo hide animation when recyclerview scroll down
public class PhotoFragment extends Fragment {
    private final String TAG = "PhotoFragment";

    private boolean isLoading;
    private View rootView;
    private ImageRecyclerSetter imageRecyclerSetter;

    //
    private FirebaseDatabase mDatabase;



    private RecyclerView recyclerView;

    private String mBookCode;
    private String mPeriod;


    private int spinnerIndex=0;
    private ImageView imageView;

    private ArrayList<FirebaseImage> mAllImages;
    private ArrayList<Bitmap> mAllBitmaps;


    private ArrayList<FirebaseImage> mImages;
    private ArrayList<Bitmap> mBitmaps;


    private ArrayList<String> dateList;

    private ProgressDialog gettingImageDialog;

    public PhotoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        mAllImages = new ArrayList<>();
        mImages = new ArrayList<>();
        mAllBitmaps = new ArrayList<>();
        mBitmaps = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerview);
        imageRecyclerSetter = new ImageRecyclerSetter(getActivity());

        getFBImage();


        isLoading = true;

        return rootView;
    }

    public ArrayList<Bitmap> getBitmaps(){
        return mAllBitmaps;
    }

    public ArrayList<FirebaseImage> getImages(){
        return mAllImages;
    }
    public void getFBImage(){
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference imageRef = mDatabase.getReference("BookInfo/"+mBookCode+"/Content/Images");
        gettingImageDialog = new ProgressDialog(getActivity());
        gettingImageDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        gettingImageDialog.setMessage("이미지를 불러오는 중입니다..");
        gettingImageDialog.setCancelable(false);
        gettingImageDialog.show();
        ValueEventListener imageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot FBimage) {
                mAllImages.clear();
                Iterator<DataSnapshot> childIterator = FBimage.getChildren().iterator();
                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
                while(childIterator.hasNext()) {
                    DataSnapshot imageSnapshot = childIterator.next();
                    String key= imageSnapshot.getKey();
                    String imageComment=imageSnapshot.child("imageComment").getValue(String.class);
                    String imageURI=imageSnapshot.child("imageURI").getValue(String.class);
                    String date=imageSnapshot.child("date").getValue(String.class);

                    mAllImages.add(new FirebaseImage(key,imageComment,imageURI,date));

                }
                if(dateList == null){

                    if(mAllImages.size() == 0)
                        ((TextView)rootView.findViewById(R.id.message_tv)).setText("등록된 이미지가 없습니다 :(");
                    else
                        ((TextView)rootView.findViewById(R.id.message_tv)).setText("");


                }
                else{
                    spinnerItemSelected(spinnerIndex);
                }
                getBitmapImages();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        imageRef.addValueEventListener(imageListener);
    }

    public ArrayList<Bitmap> spinnerItemSelected(int i){
        if(isLoading){
            return new ArrayList<>();
        }

        Log.e(TAG, "선택 된 날짜 : "+ dateList.get(i));
        Log.e(TAG, "DIALOG LIST : "+ dateList.toString());
        Log.e(TAG, "선택 index : "+i);

        spinnerIndex=i;
        mImages.clear();
        mBitmaps.clear();

        if(i == 0){
            for(int j = 0; j<mAllImages.size(); j++){
                mImages.add(mAllImages.get(j));
                mBitmaps.add(mAllBitmaps.get(j));
            }
            Log.e("TAG", "보여지는 사진 : "+ mImages.toString());
        }else{
            for(int j = 0; j<mAllImages.size(); j++){
                if(mAllImages.get(j).getDate().equals(dateList.get(i))){
                    Toast.makeText(getContext(), dateList.get(i), Toast.LENGTH_SHORT).show();
                    mImages.add(mAllImages.get(j));
                    mBitmaps.add(mAllBitmaps.get(j));
                }
            }
        }

        if(mImages.size() == 0)
            ((TextView)rootView.findViewById(R.id.message_tv)).setText("등록된 이미지가 없습니다 :(");
        else
            ((TextView)rootView.findViewById(R.id.message_tv)).setText("");

        imageRecyclerSetter.setRecyclerCardView(recyclerView, mImages, mBitmaps);

        return mBitmaps;
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

    public void getBitmapImages() {

        mAllBitmaps.clear();

        if(mAllImages.size() == 0)
            gettingImageDialog.dismiss();
        for (int i = 0; i < mAllImages.size(); i++) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference(mAllImages.get(i).getImageURI());

            final int finalI = i;
            storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Use the bytes to display the image
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mAllBitmaps.add(bitmap);
                    Log.e(TAG, "이거 되고 있니.. ?");
                    if(finalI == mAllImages.size()-1){
                        isLoading = false;
                        if(mAllImages.size() == 0)
                            ((TextView)rootView.findViewById(R.id.message_tv)).setText("등록된 이미지가 없습니다 :(");
                        else {
                            ((TextView) rootView.findViewById(R.id.message_tv)).setText("");
                            imageRecyclerSetter.setRecyclerCardView(recyclerView, mAllImages, mAllBitmaps);
                        }
                        Log.e(TAG, "SIZE!!!!!!!!!!!" + mAllBitmaps.size() +"/"+mAllImages.size() );
                        gettingImageDialog.dismiss();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }

    }
}