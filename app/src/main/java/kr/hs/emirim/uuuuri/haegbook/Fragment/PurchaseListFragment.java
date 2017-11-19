package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;

import java.util.ArrayList;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Adapter.WholePurchaseListAdapter;
import kr.hs.emirim.uuuuri.haegbook.Model.PurchaseAmount;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by doori on 2017-11-19.
 */

public class PurchaseListFragment extends BottomSheetFragment {
    private final String TAG ="PurchaseListFragment";
    private final String BUNDLE_TAG ="BUNDLE_TAG";

    private RecyclerView recyclerview;

    ArrayList<PurchaseAmount> mPresentPurchases;
    ArrayList<PurchaseAmount> mRestPurchases;
    ArrayList<PurchaseAmount> mPlanPurchases;


    private View mRootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresentPurchases = new ArrayList<>();
        mRestPurchases = new ArrayList<>();
        mPlanPurchases = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<PurchaseAmount> purchases = getArguments().getParcelableArrayList(BUNDLE_TAG);


        for(int i = 0; i<purchases.size(); i++){
            switch (purchases.get(i).getPurchaseType()){
                case 1:
                    mPresentPurchases.add(purchases.get(i));
                    break;
                case 2:
                    mRestPurchases.add(purchases.get(i));
                    break;
                case 3:
                    mPlanPurchases.add(purchases.get(i));
                    break;
            }


        }

        mRootView = inflater.inflate(R.layout.fragment_purchase_list, container, false);
        recyclerview = mRootView.findViewById(R.id.recyclerview);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<WholePurchaseListAdapter.Item> data = new ArrayList<>();


        data.add(new WholePurchaseListAdapter.Item(WholePurchaseListAdapter.HEADER, "현재까지 사용한 금액이에요."));
        for(int i = 0; i < mPresentPurchases.size(); i++){
            data.add(new WholePurchaseListAdapter.Item(WholePurchaseListAdapter.CHILD, mPresentPurchases.get(i)));
        }
        data.add(new WholePurchaseListAdapter.Item(WholePurchaseListAdapter.HEADER,  "남은 금액이에요."));
        for(int i = 0; i < mRestPurchases.size(); i++){
            data.add(new WholePurchaseListAdapter.Item(WholePurchaseListAdapter.CHILD,mRestPurchases.get(i)));
        }

        data.add(new WholePurchaseListAdapter.Item(WholePurchaseListAdapter.HEADER,  "여행 전 예정했던 금액이에요."));
        for(int i = 0; i < mPlanPurchases.size(); i++){
            data.add(new WholePurchaseListAdapter.Item(WholePurchaseListAdapter.CHILD,mPlanPurchases.get(i)));
        }


        recyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerview.setAdapter(new WholePurchaseListAdapter(getActivity(), data));
    }

}
