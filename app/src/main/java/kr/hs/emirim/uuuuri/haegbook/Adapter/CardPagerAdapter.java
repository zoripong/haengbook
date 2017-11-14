package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Activity.TravelDetailActivity;
import kr.hs.emirim.uuuuri.haegbook.Interface.CardAdapter;
import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {
    private final String TAG = "CardPagerAdapter";
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
//            Log.e(TAG,cardBook.toString());
            mViews.add(null);
            mData.add(cardBook);
        }
    }

    public void addCardItems(ArrayList<CardBook> items){
//        Log.e(TAG, String.valueOf(items.size()));

        mData.clear();
        mViews.clear();

        for(int i = 0; i<items.size(); i++){
//            Log.e(TAG, "킬킬"+items.get(i).toString());

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
//        mViews.set(position, null);
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

//        Log.e(TAG, "<Before> width : "+ cardView.getWidth() +" / height : " + cardView.getHeight());
//        Log.e(TAG, "url : " + item.getImage());

        cardView.setPreventCornerOverlap(false);


        Glide.with(mNowActivity.getApplicationContext())
                .load(item.getImage())
                .fitCenter()
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new ViewTarget<CardView, GlideDrawable>(cardView) {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                        CardView myView = this.view;
                        // Set your resource on myView and/or start your animation here.
                        myView.setBackground(resource);
                    }
                });
//        Log.e(TAG, "<After> width : "+ cardView.getWidth() +" / height : " + cardView.getHeight());



        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mNowActivity, TravelDetailActivity.class);
                intent.putExtra("BOOK_CODE", item.getBookCode());
                intent.putExtra("DATE", item.getPeriod());
                mNowActivity.startActivity(intent);
            }
        });

        sharedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cardBookCode=item.getBookCode();
                final Dialog mDialog = new Dialog(view.getContext(), R.style.MyDialog);
                mDialog.setContentView(R.layout.dialog_share);
                final TextView codeTv=(TextView)mDialog.findViewById(R.id.card_book_code_tv);
                codeTv.setText(cardBookCode);
                mDialog.findViewById(R.id.dialog_button_copy).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        final ClipboardManager clipboardManager =  (ClipboardManager) view.getContext().getSystemService(CLIPBOARD_SERVICE);
                        clipboardManager.setText(codeTv.getText());
                        Toast.makeText(view.getContext(), "북코드가 복사되었습니다.", Toast.LENGTH_SHORT).show();

                        mDialog.dismiss();


                    }
                });
                mDialog.findViewById(R.id.dialog_button_share).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, cardBookCode);
                        intent.setType("text/plain");
                        Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
                        view.getContext().startActivity(chooser);


                        mDialog.dismiss();

                    }
                });

                mDialog.show();
            }
        });

    }

}
