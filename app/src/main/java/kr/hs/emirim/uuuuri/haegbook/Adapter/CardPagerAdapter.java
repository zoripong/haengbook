package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Activity.TravelDetailActivity;
import kr.hs.emirim.uuuuri.haegbook.Interface.CardAdapter;
import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.R;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {
    private final String LOG = "CardPagerAdapter";
    private List<CardView> mViews;
    private List<CardBook> mData;
    private float mBaseElevation;
    private Activity mNowActivity;

    public CardPagerAdapter(Activity activity) {
        mNowActivity = activity;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardBook item) {
        mViews.add(null);
        mData.add(item);
    }

    public void addCardItems(HashSet<CardBook> items){
        Iterator<CardBook> iterator = items.iterator();
        while(iterator.hasNext()){
            CardBook cardBook= iterator.next();
            Log.e(LOG,cardBook.toString());
            mViews.add(null);
            mData.add(cardBook);
        }
    }

    public void addCardItems(ArrayList<CardBook> items){
        for(int i = 0; i<items.size(); i++){
            mViews.add(null);
            mData.add(items.get(i));
        }
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.viewpager_card, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.card_view);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(final CardBook item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.title_tv);
        TextView periodTextView = (TextView) view.findViewById(R.id.period_tv);
        TextView locationTextView = (TextView) view.findViewById(R.id.location_tv);
        CardView cardView = view.findViewById(R.id.card_view);
        ImageView sharedImageView = view.findViewById(R.id.shared_iv);

        titleTextView.setText(item.getTitle());
        periodTextView.setText(item.getPeriod());
        locationTextView.setText(item.getLocation());

        // TODO: 2017-11-07 set the background image

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mNowActivity, TravelDetailActivity.class);
                intent.putExtra("BOOK_CODE", item.getBookCode());
                mNowActivity.startActivity(intent);
            }
        });

        sharedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), item.getBookCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
