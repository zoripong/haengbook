package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;

import java.util.ArrayList;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.R;

public class BookCardListFragment extends BottomSheetFragment {
    private final String TAG ="BookCardListFragment";
    private final String BUNDLE_TAG ="BUNDLE_TAG";

    ListView mListview;
    List<String> mList = new ArrayList<>();
    ArrayAdapter<String> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList.clear();
        for (int i = 0; i < 50; i++) {
            mList.add("Nanda - " + i);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<CardBook> cardBooks = getArguments().getParcelableArrayList(BUNDLE_TAG);

        return inflater.inflate(R.layout.fragment_book_card_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListview = (ListView) view.findViewById(R.id.listview);
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, mList);
        mListview.setAdapter(mAdapter);

        // 리스트 띄우기



    }
}
