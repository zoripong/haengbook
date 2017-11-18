package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.haegbook.Adapter.OnSwipeTouchListener;
import kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType;
import kr.hs.emirim.uuuuri.haegbook.Manager.ReceiptRecyclerSetter;
import kr.hs.emirim.uuuuri.haegbook.Model.Receipt;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-03.
 */

public class ReceiptFragment extends Fragment implements ReceiptType{
    private final String TAG = "ReceiptFragment";

    private View rootView;
    private String mBookCode;
    private String mPeriod;

    private RecyclerView recyclerView;
    private ReceiptRecyclerSetter receiptRecyclerSetter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReceiptRefer;
    private ValueEventListener mReceiptListener;

    ArrayList<Receipt> mAllReceipts;
    ArrayList<Receipt> mReceipts;

    private ArrayList<String> dateList;
    public ReceiptFragment() {}

    private BottomSheetLayout bottomSheetLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_receipt, container, false);

        bottomSheetLayout = rootView.findViewById(R.id.design_bottom_sheet);
        bottomSheetLayout.setInterceptContentTouch(true);

        bottomSheetLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {}

            @Override
            public void onSwipeLeft() {}

            @Override
            public void onSwipeTop() {
/*
 Bundle bundle = new Bundle();
        String myMessage = "Stackoverflow is cool!";
        bundle.putString("message", myMessage );
        FragmentClass fragInfo = new FragmentClass();
        fragInfo.setArguments(bundle);
        transaction.replace(R.id.fragment_single, fragInfo);
        transaction.commit();
 */
Log.e("헬로","슬라이딩");
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList(BUNDLE_TAG, mCardBooks);
//                BookCardListFragment fragment = new BookCardListFragment();
//                fragment.setArguments(bundle);
//                fragment.show(getSupportFragmentManager(), R.id.design_bottom_sheet);
////                new BookCardListFragment().show(getSupportFragmentManager(), R.id.design_bottom_sheet);
            }

            @Override
            public void onSwipeBottom() {}


        });



        mReceipts = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerview);
        receiptRecyclerSetter = new ReceiptRecyclerSetter(getContext(), getActivity().getParent());

        getDatabase();


        return rootView;
    }

    public void spinnerItemSelected(int i){
        Log.e(TAG, "선택 된 날짜 : "+ dateList.get(i));
        Log.e(TAG, "DIALOG LIST : "+ dateList.toString());
        Log.e(TAG, "전체 영수증 : "+mAllReceipts.toString()+"/ size : "+mAllReceipts.size());
        Log.e(TAG, "선택 index : "+i);

        mReceipts.clear();

        if(i == 0){
            for(int j = 0; j<mAllReceipts.size(); j++){
                mReceipts.add(mAllReceipts.get(j));
            }
            Log.e("TAG", "보여지는 영수증 : "+mReceipts.toString());
        }else{
            for(int j = 0; j<mAllReceipts.size(); j++){
                if(mAllReceipts.get(j).getDate().equals(dateList.get(i))){
                    Toast.makeText(getContext(), dateList.get(i), Toast.LENGTH_SHORT).show();
                    mReceipts.add(mAllReceipts.get(j));
                }
            }
        }


        if(mReceipts.size() == 0)
            ((TextView)rootView.findViewById(R.id.message_tv)).setText("등록된 영수증이 없습니다 :(");
        else
            ((TextView)rootView.findViewById(R.id.message_tv)).setText("");

        receiptRecyclerSetter.setRecyclerCardView(recyclerView, mReceipts);
    }

    public void getDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        mReceiptRefer = mDatabase.getReference("BookInfo/"+mBookCode+"/Content/Receipt");
        Log.e(TAG, "GetDatabase : "+mBookCode);

        mAllReceipts = new ArrayList<Receipt>();

        mReceiptListener = mReceiptRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAllReceipts.clear();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    mAllReceipts.add(iterator.next().getValue(Receipt.class));
                }

                Log.e(TAG, mAllReceipts.toString());

                if(mAllReceipts.size() == 0)
                    ((TextView)rootView.findViewById(R.id.message_tv)).setText("등록된 영수증이 없습니다 :(");
                else
                    ((TextView)rootView.findViewById(R.id.message_tv)).setText("");

                receiptRecyclerSetter.setRecyclerCardView(recyclerView, mAllReceipts);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setBookCode(String bookCode){
        mBookCode = bookCode;
    }

    public void setPeriod(String date){
        mPeriod = date;
    }


    public void setDateList(ArrayList<String> dateList) {
        this.dateList = dateList;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mReceiptListener != null)
            mReceiptRefer.removeEventListener(mReceiptListener);
    }
}