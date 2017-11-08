package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Adapter.ReceiptRecyclerSetter;
import kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType;
import kr.hs.emirim.uuuuri.haegbook.Model.Receipt;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-03.
 */

public class ReceiptFragment extends Fragment implements ReceiptType{

    private ArrayList<Receipt> testList;
    private RecyclerView recyclerView;
    private ReceiptRecyclerSetter receiptRecyclerSetter;
    public ReceiptFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        receiptRecyclerSetter = new ReceiptRecyclerSetter(getContext(), getActivity().getParent());

        test();

        Button receiptButton = rootView.findViewById(R.id.add_receipt_btn);
        receiptButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "영수증 추가 다이얼로그", Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }

    private void test() {
        testList = new ArrayList<>();

        /*
            0 - 식비
         */
        //(String date, String title, int amount, int type, String memo)
        testList.add(new Receipt("2017.11.08", "엉터리 생고기", 9800, FOOD, "생각보다 질이 떨어진듯"));
        testList.add(new Receipt("2017.11.08", "쇼핑", 12800, SHOPPING, "아디다스 져지"));
        testList.add(new Receipt("2017.11.08", "밍밍", 9800, CULTURE, "뮤지컬"));


        receiptRecyclerSetter.setRecyclerCardView(recyclerView, testList);
    }


}
