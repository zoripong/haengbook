package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.haegbook.Adapter.ReceiptRecyclerSetter;
import kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType;
import kr.hs.emirim.uuuuri.haegbook.Model.Receipt;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-03.
 */

public class ReceiptFragment extends Fragment implements ReceiptType{
    private final String TAG = "ReceiptFragment";
    private String mBookCode;

    private RecyclerView recyclerView;
    private ReceiptRecyclerSetter receiptRecyclerSetter;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReceiptRefer;
    private ValueEventListener mReceiptListener;

    ArrayList<Receipt> receipts;

    public ReceiptFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        receiptRecyclerSetter = new ReceiptRecyclerSetter(getContext(), getActivity().getParent());

        getDatabase();
        
        Button receiptButton = rootView.findViewById(R.id.add_receipt_btn);
        receiptButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "영수증 추가 다이얼로그", Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }

    public void getDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        mReceiptRefer = mDatabase.getReference("BookInfo/"+mBookCode+"/Content/Receipt");
        Log.e(TAG, "GetDatabase : "+mBookCode);

        receipts = new ArrayList<Receipt>();

        mReceiptListener = mReceiptRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                receipts.clear();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    receipts.add(iterator.next().getValue(Receipt.class));
                }

                Log.e(TAG, receipts.toString());
                receiptRecyclerSetter.setRecyclerCardView(recyclerView, receipts);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setBookCode(String bookCode){
        mBookCode = bookCode;
    }


    @Override
    public void onStop() {
        super.onStop();
        if(mReceiptListener != null)
            mReceiptRefer.removeEventListener(mReceiptListener);
    }
}
