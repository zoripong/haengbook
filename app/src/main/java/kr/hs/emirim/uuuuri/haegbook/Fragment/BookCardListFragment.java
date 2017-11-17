package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Adapter.WholeCardBookListAdapter;
import kr.hs.emirim.uuuuri.haegbook.Manager.DateListManager;
import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.R;


public class BookCardListFragment extends BottomSheetFragment {
    private final String TAG ="BookCardListFragment";
    private final String BUNDLE_TAG ="BUNDLE_TAG";

    private RecyclerView recyclerview;

    ArrayList<CardBook> mPublishTravels;
    ArrayList<CardBook> mPastTravels;
    ArrayList<CardBook> mCurrentTravels;
    ArrayList<CardBook> mFutureTravels;

    private View mRootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPublishTravels = new ArrayList<>();
        mPastTravels = new ArrayList<>();
        mCurrentTravels = new ArrayList<>();
        mFutureTravels = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<CardBook> cardBooks = getArguments().getParcelableArrayList(BUNDLE_TAG);

        DateListManager dateListManager = new DateListManager();
        Date now = new Date();
        now = new Date(now.getYear(), now.getMonth(), now.getDate(), 0, 0, 0);

        for(int i = 0; i<cardBooks.size(); i++){

            Date dates[] = dateListManager.convertDates(cardBooks.get(i).getPeriod());


            if(dates[0].getTime() <= now.getTime() && dates[1].getTime() >= now.getTime()){
                Log.e(TAG, "여행 중 :"+cardBooks.get(i).getTitle()+ " ["+cardBooks.get(i).getPeriod()+"]");
                mCurrentTravels.add(cardBooks.get(i));
            }else if(dates[0].getTime() < now.getTime() && dates[1].getTime() < now.getTime()){
                if(cardBooks.get(i).getImage() == null)
                    mPastTravels.add(cardBooks.get(i));
                else mPublishTravels.add(cardBooks.get(i));
                Log.e(TAG, "여행 끝 : "+cardBooks.get(i).getTitle()+ " ["+cardBooks.get(i).getPeriod()+"]");
            }else if(dates[0].getTime() > now.getTime() && dates[1].getTime() > now.getTime()){
                Log.e(TAG, "여행 전 : "+ cardBooks.get(i).getTitle() + " ["+cardBooks.get(i).getPeriod()+"]");
                mFutureTravels.add(cardBooks.get(i));
            }


        }



        mRootView = inflater.inflate(R.layout.fragment_book_card_list, container, false);
        recyclerview = mRootView.findViewById(R.id.recyclerview);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<WholeCardBookListAdapter.Item> data = new ArrayList<>();

        data.add(new WholeCardBookListAdapter.Item(WholeCardBookListAdapter.HEADER, "즐거운 추억이에요."));
        for(int i = 0; i<mPublishTravels.size(); i++){
            data.add(new WholeCardBookListAdapter.Item(WholeCardBookListAdapter.CHILD, mPublishTravels.get(i)));
        }


        data.add(new WholeCardBookListAdapter.Item(WholeCardBookListAdapter.HEADER, "마무리 짓고 있어요."));
        for(int i = 0; i < mPastTravels.size(); i++){
            data.add(new WholeCardBookListAdapter.Item(WholeCardBookListAdapter.CHILD, mPastTravels.get(i)));
        }

        data.add(new WholeCardBookListAdapter.Item(WholeCardBookListAdapter.HEADER,  "지금 즐기고 있어요."));
        for(int i = 0; i < mCurrentTravels.size(); i++){
            data.add(new WholeCardBookListAdapter.Item(WholeCardBookListAdapter.CHILD,mCurrentTravels.get(i)));
        }

        data.add(new WholeCardBookListAdapter.Item(WholeCardBookListAdapter.HEADER, "두근두근 기대돼요."));

        for(int i = 0; i<mFutureTravels.size(); i++){
            data.add(new WholeCardBookListAdapter.Item(WholeCardBookListAdapter.CHILD, mFutureTravels.get(i)));
        }
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerview.setAdapter(new WholeCardBookListAdapter(getActivity(), data));
    }

}
