package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.haegbook.Adapter.CardPagerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Manager.ImageRecyclerSetter;
import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;
import kr.hs.emirim.uuuuri.haegbook.R;

// todo hide animation when recyclerview scroll down
public class PhotoFragment extends Fragment {

    private FirebaseDatabase mDatabase;

    private View rootView;

    private RecyclerView recyclerView;
    private ImageRecyclerSetter imageRecyclerSetter;

    private String mBookCode;
    private String mPeriod;

    private ImageView imageView;

    private ArrayList<FirebaseImage> mImages;

    private CardPagerAdapter mImagesAdapter;

    public PhotoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        mImages = new ArrayList<>();

        imageView= rootView.findViewById(R.id.image);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        imageRecyclerSetter = new ImageRecyclerSetter(getContext(), getActivity().getParent());

        getFBImage();


        return rootView;
    }

    public void getFBImage(){
        mDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference imageRef = mDatabase.getReference("BookInfo/"+mBookCode+"/Content/Images");

        ValueEventListener imageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot FBimage) {


                Iterator<DataSnapshot> childIterator = FBimage.getChildren().iterator();
                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
                while(childIterator.hasNext()) {
                    DataSnapshot imageSnapshot = childIterator.next();
                    String imageComment=imageSnapshot.child("imageComment").getValue(String.class);
                    String imageURL=imageSnapshot.child("imageURI").getValue(String.class);
                    String date=imageSnapshot.child("date").getValue(String.class);

                    mImages.add(new FirebaseImage(imageComment,imageURL,date));

                }
                imageRecyclerSetter.setRecyclerCardView(recyclerView, mImages);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        imageRef.addValueEventListener(imageListener);
    }
    public void setBookCode(String bookCode){
        mBookCode = bookCode;
    }

    public void setPeriod(String period){
        mPeriod = period;
    }

}
