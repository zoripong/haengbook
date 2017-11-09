package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private String mPeriod;

    private RecyclerView recyclerView;
    private ReceiptRecyclerSetter receiptRecyclerSetter;

    private Spinner mDateSp;
    private Spinner mTypeSp;
    private EditText mTitleEt;
    private EditText mAmountEt;
    private Spinner currencySymbolSp;
    private EditText mMemoEt;

    int typeIndex;
    boolean isUpdateNull=true;


    private FirebaseDatabase mDatabase;
    private DatabaseReference mReceiptRefer;
    private ValueEventListener mReceiptListener;

    ArrayList<Receipt> mAllReceipts;
    ArrayList<Receipt> mReceipts;

    private Date beginDate;
    private Date endDate;
    private String stringDate[];

    final ArrayList<String> dialogDateList = new ArrayList<>();
    public ReceiptFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);
        mReceipts = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerview);
        receiptRecyclerSetter = new ReceiptRecyclerSetter(getContext(), getActivity().getParent());

        getDatabase();

        Log.e(TAG, "기간 : "+ mPeriod);

        stringDate = mPeriod.split("-");
        SimpleDateFormat dt = new SimpleDateFormat("yyyy.mm.dd");
        Log.e(TAG, "기간 : "+stringDate[0]+"  "+stringDate[1]);
        try {

            beginDate = dt.parse(stringDate[0]);
            endDate = dt.parse(stringDate[1]);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = endDate.getTime() - beginDate.getTime();
        int diffDays = (int)(diff / (24 * 60 * 60 * 1000));
        final ArrayList<String> stringList = new ArrayList<>();

        Spinner spinner = rootView.findViewById(R.id.spinner);
        stringList.add("전체보기");

        for(int i = 0; i<=diffDays; i++){
            Log.e(TAG, i+" : "+dt.format(beginDate));
            stringList.add(dt.format(beginDate));
            dialogDateList.add(dt.format(beginDate));//영수증 추가 다이얼로그에 띄울 날짜 리스트
            beginDate.setTime(beginDate.getTime()+(24 * 60 * 60 * 1000));

        }

        String stringArray[] = new String[stringList.size()];
        stringArray = stringList.toArray(stringArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, stringArray);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "선택 된 날짜 : "+stringList.get(i));

                // todo DEBUG
                mReceipts.clear();

                if(i == 0){
                    for(int j = 0; j<mAllReceipts.size(); j++){
                        mReceipts.add(mAllReceipts.get(j));
                    }
                }else{
                    for(int j = 0; j<mAllReceipts.size(); j++){
                        if(mAllReceipts.get(j).getDate().equals(stringList.get(i))){
                            Toast.makeText(getContext(), stringList.get(i), Toast.LENGTH_SHORT).show();
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



        Button receiptButton = rootView.findViewById(R.id.add_receipt_btn);
        receiptButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final Dialog mDialog = new Dialog(view.getContext(), R.style.MyDialog);
                mDialog.setContentView(R.layout.dialog_regist_receipt);


                mDateSp=  mDialog.findViewById(R.id.date_sp);
                mTypeSp= mDialog.findViewById(R.id.type_sp);
                mTitleEt= mDialog.findViewById(R.id.title_et);
                mAmountEt = mDialog.findViewById(R.id.amount_et);
                currencySymbolSp = mDialog.findViewById(R.id.currency_symbol_sp);
                mMemoEt = mDialog.findViewById(R.id.memo_et);

                String dateArray[] = new String[dialogDateList.size()];
                dateArray = dialogDateList.toArray(dateArray);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, dateArray);

                mDateSp.setAdapter(adapter);

                mTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        typeIndex=position;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });



                mDialog.findViewById(R.id.add_receipt_btn).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {

                        Log.e("파베"+String.valueOf(mDateSp.getSelectedItem().toString()),"");
                        updateFB(String.valueOf(mDateSp.getSelectedItem().toString()),typeIndex,String.valueOf(mTitleEt.getText()),
                                String.valueOf(mAmountEt.getText())+currencySymbolSp.getSelectedItem().toString(),String.valueOf(mMemoEt.getText()));
                        mDialog.dismiss();


                    }
                });
                mDialog.show();

            }
        });


        return rootView;
    }

    public void updateFB(final String date, final int type, final String title, final String amount, final String memo){
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference receiptRef = mDatabase.getReference("BookInfo/"+mBookCode+"/Content/Receipt");

        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            long keyIndex;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("입력값",date+"        "+title+amount+memo);
                if(date.equals("") || title.equals("") || amount.equals("") || memo.equals("")){
                    Toast.makeText(getContext(), "입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    keyIndex = dataSnapshot.getChildrenCount();
                    Map<String, Object> receiptUpdates = new HashMap<String, Object>();
                    receiptUpdates.put(String.valueOf(keyIndex + 1), new Receipt(date, title, amount, type, memo));
                    receiptRef.updateChildren(receiptUpdates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        Log.e("파베", String.valueOf(isUpdateNull));

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
