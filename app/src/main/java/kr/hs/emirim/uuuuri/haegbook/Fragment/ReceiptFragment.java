package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType;
import kr.hs.emirim.uuuuri.haegbook.Manager.DateListManager;
import kr.hs.emirim.uuuuri.haegbook.Manager.ReceiptRecyclerSetter;
import kr.hs.emirim.uuuuri.haegbook.Model.Receipt;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-03.
 */

// // TODO: 2017-11-11 여행 중이라면 오늘까지, 여행이 끝났으면 여행이 끝난날까지 --> spinner add

public class ReceiptFragment extends Fragment implements ReceiptType{
    private final String TAG = "ReceiptFragment";
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);
        mReceipts = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerview);
        receiptRecyclerSetter = new ReceiptRecyclerSetter(getContext(), getActivity().getParent());

        getDatabase();

        /*
        *         add the item into spinner
        * */
        Log.e(TAG, "기간 : "+ mPeriod);

        Spinner spinner = rootView.findViewById(R.id.spinner);

        DateListManager dateListManager = new DateListManager();
        Date[] dates = dateListManager.convertString(mPeriod);

        dateList = dateListManager.makeDateList(dates[0], dates[1]);
        dateList.add(0, "전체보기");

        String stringArray[] = new String[dateList.size()];
        stringArray = dateList.toArray(stringArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, stringArray);

        spinner.setAdapter(adapter);
         /*
        *   [end]      add the item into spinner
        * */

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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

                receiptRecyclerSetter.setRecyclerCardView(recyclerView, mReceipts);
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return rootView;
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

    @Override
    public void onStop() {
        super.onStop();
        if(mReceiptListener != null)
            mReceiptRefer.removeEventListener(mReceiptListener);
    }
}