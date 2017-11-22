package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.hs.emirim.uuuuri.haegbook.Activity.AddScheduleActivity;
import kr.hs.emirim.uuuuri.haegbook.Activity.TravelDetailActivity;
import kr.hs.emirim.uuuuri.haegbook.Activity.TutorialActivity;
import kr.hs.emirim.uuuuri.haegbook.Interface.CardAdapter;
import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.DateListManager;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {
    private final String TAG = "CardPagerAdapter";
    private List<CardView> mViews;
    private List<CardBook> mData;
    private float mBaseElevation;
    private Activity mNowActivity;

    private FirebaseDatabase mDatabase;

    SharedPreferenceManager spm;

    public CardPagerAdapter(Activity activity) {
        mNowActivity = activity;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardBook item) {
        mViews.add(null);
        mData.add(item);
    }

    public void addCardItems(ArrayList<CardBook> items){
//        Log.e(TAG, String.valueOf(items.size()));

        mData.clear();
        mViews.clear();
        DateListManager dateListManager = new DateListManager();
        Date now = new Date();
        now = new Date(now.getYear(), now.getMonth(), now.getDate(), 0, 0, 0);

        for(int i = 0; i<items.size(); i++){
//            Log.e(TAG, "킬킬"+items.get(i).toString());
            Date dates[] = dateListManager.convertDates(items.get(i).getPeriod());

            if(!(dates[0].getTime() > now.getTime() && dates[1].getTime()>now.getTime())){
                mViews.add(null);
                mData.add(items.get(i));

            }
        }
        if(items.size()==0)
            addCardItem(new CardBook("tutorial", "tutorial", "tutorial", "tutorial"));
        mViews.add(null);
        mData.add(new CardBook(null, null, null, null));

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
                .inflate(R.layout.item_card, container, false);
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

    private void bind(final CardBook item, final View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.title_tv);
        TextView periodTextView = (TextView) view.findViewById(R.id.period_tv);
        TextView locationTextView = (TextView) view.findViewById(R.id.location_tv);
        CardView cardView = view.findViewById(R.id.card_view);
        ImageView sharedImageView = view.findViewById(R.id.shared_iv);
        ImageView plusImageView = view.findViewById(R.id.plus_iv);
        ImageView gpsIconImageView = view.findViewById(R.id.gps_iv);
        final LinearLayout linearLayout = view.findViewById(R.id.gray_linear);

        mDatabase = FirebaseDatabase.getInstance();
        spm=new SharedPreferenceManager(mNowActivity);

        if(item.getTitle()!=null)
            titleTextView.setText(item.getTitle());
        if(item.getPeriod()!=null)
            periodTextView.setText(item.getPeriod());
        if(item.getLocation()!=null)
            locationTextView.setText(item.getLocation());

        if(item.getLocation() == null){
            view.findViewById(R.id.gps_iv).setVisibility(View.GONE);
        }else{
            if(item.getLocation().equals("")) {
                view.findViewById(R.id.gps_iv).setVisibility(View.GONE);
            }
        }



        cardView.setPreventCornerOverlap(false);
        StorageReference storageReference = null;
        if(item.getImage() != null)
            storageReference = FirebaseStorage.getInstance().getReference(item.getImage());

        if(storageReference != null) {
            Glide.with(mNowActivity.getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .fitCenter()
                    .placeholder(R.drawable.loading)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .into(new ViewTarget<CardView, GlideDrawable>(cardView) {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                            ImageView imageV = view.findViewById(R.id.linear); ////수정
                            imageV.setBackground(resource); //수정
                        }
                    });
            if(item.getTitle().equals("tutorial")){
                // 튜토리얼
                Glide.with(mNowActivity.getApplicationContext())
                        .fromResource()
                        .asBitmap()
                        .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG,100))
                        .load(R.drawable.tutorial_cover)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                ImageView imageV = view.findViewById(R.id.linear);

                                Drawable drawable = new BitmapDrawable(mNowActivity.getResources(), bitmap);
                                imageV.setBackgroundDrawable(drawable);

                                linearLayout.setBackgroundColor(Color.parseColor("#00000000"));

                            }
                        });

                titleTextView.setVisibility(View.GONE);
                periodTextView.setVisibility(View.GONE);
                locationTextView.setVisibility(View.GONE);
                gpsIconImageView.setVisibility(View.GONE);
                sharedImageView.setVisibility(View.GONE);
            }

        }
        if(item.getTitle() == null){
            sharedImageView.setVisibility(View.GONE);
            plusImageView.setVisibility(View.VISIBLE);
        }

        //todo
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                final Dialog deleteDialog = new Dialog(mNowActivity, R.style.MyDialog);
                deleteDialog.setContentView(R.layout.dialog_delete_card);
                deleteDialog.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String userToken = spm.retrieveString(SharedPreferenceTag.USER_TOKEN_TAG);

                        final DatabaseReference receiptRef = mDatabase.getReference("UserInfo/"+userToken);
                        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterator<DataSnapshot> childIterator = dataSnapshot.getChildren().iterator();
                                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
                                while (childIterator.hasNext()) {
                                    DataSnapshot snapshotIterator = childIterator.next();
                                    String cardCode = snapshotIterator.getValue(String.class);
                                    if (cardCode.equals(item.getBookCode())) {
                                        receiptRef.child(snapshotIterator.getKey()).removeValue();

                                        return;
                                    }

                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });


                        deleteDialog.dismiss();

                    }
                });
                deleteDialog.show();


                Toast.makeText(mNowActivity, item.toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        cardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getTitle() == null) {
                    selectDialog();
                } else if(item.getTitle().equals("tutorial")){
                    Intent intent = new Intent(mNowActivity, TutorialActivity.class);
                    mNowActivity.startActivity(intent);

                } else{
                    Intent intent = new Intent(mNowActivity, TravelDetailActivity.class);
                    intent.putExtra("BOOK_CODE", item.getBookCode());
                    intent.putExtra("DATE", item.getPeriod());
                    intent.putExtra("Image", item.getImage());
                    mNowActivity.startActivity(intent);
                }
            }
        });

        sharedImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cardBookCode=item.getBookCode();
                final Dialog mDialog = new Dialog(view.getContext(), R.style.MyDialog);
                mDialog.setContentView(R.layout.dialog_share);
                final TextView codeTv=(TextView)mDialog.findViewById(R.id.card_book_code_tv);
                codeTv.setText(cardBookCode);
                codeTv.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View view) {
                        final ClipboardManager clipboardManager =  (ClipboardManager) view.getContext().getSystemService(CLIPBOARD_SERVICE);
                        clipboardManager.setText(codeTv.getText());
                        Toast.makeText(view.getContext(), "북코드가 복사되었습니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                });
                mDialog.findViewById(R.id.dialog_button_share).setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        //TODO SHARE MESSAGE
                        String message = "당신의 행복한 여행을 책임질 여행 관리 서비스, 행북! \n제 여행 이야기, 함께 보실래요? 친구가 여행 이야기를 공유했습니다.\n앱을 설치 후 아래 코드를 추가해보세요.\n"+cardBookCode;
                        intent.putExtra(Intent.EXTRA_TEXT, message);
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

    private void selectDialog(){
        final Dialog selectDialog = new Dialog(mNowActivity, R.style.MyDialog);
        selectDialog.setContentView(R.layout.dialog_select);
        selectDialog.show();
        selectDialog.findViewById(R.id.add_code_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog inputDialog = new Dialog(mNowActivity, R.style.MyDialog);
                inputDialog.setContentView(R.layout.dialog_code_input);
                selectDialog.hide();
                inputDialog.show();
                selectDialog.dismiss();
                final ClipboardManager clipboardManager =  (ClipboardManager) mNowActivity.getSystemService(CLIPBOARD_SERVICE);
                final EditText bookCodeEt=inputDialog.findViewById(R.id.book_code_et);
                inputDialog.findViewById(R.id.paste_btn).setOnClickListener(new View.OnClickListener () {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    bookCodeEt.setText(clipboardManager.getText());
                                                                                }
                                                                            }
                );

                inputDialog.findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateFB(String.valueOf(bookCodeEt.getText()));
                        inputDialog.hide();
                        inputDialog.dismiss();
                    }
                });

            }
        });

        selectDialog.findViewById(R.id.add_basic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mNowActivity, AddScheduleActivity.class);
                mNowActivity.startActivity(intent);
                mNowActivity.finish();
                selectDialog.dismiss();
            }
        });
        selectDialog.findViewById(R.id.dimiss_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDialog.dismiss();
            }
        });
    }

    public void updateFB(final String inputCode){
        SharedPreferenceManager spm = new SharedPreferenceManager(mNowActivity);
        final String uid = spm.retrieveString(SharedPreferenceTag.USER_TOKEN_TAG);

        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference receiptRef = mDatabase.getReference("BookInfo/"+inputCode);
        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            long keyIndex;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()<1){
                    Toast.makeText(mNowActivity.getApplicationContext(), "유효하지않은 코드입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(inputCode.replaceAll(" ","").equals("")){
                    Toast.makeText(mNowActivity.getApplicationContext(), "코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;

                }
                //uid에 카드 업데이트

                final DatabaseReference userInfoRef = mDatabase.getReference("UserInfo/"+uid);
                final String postKey=userInfoRef.push().getKey();

                userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> childIterator = dataSnapshot.getChildren().iterator();
                        //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
                        while(childIterator.hasNext()) {
                            DataSnapshot snapshotIterator = childIterator.next();
                            String cardCode=snapshotIterator.getValue(String.class);
                            if(cardCode.equals(inputCode)){
                                Toast.makeText(mNowActivity.getApplicationContext(), "이미 등록된 코드입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }

                        Map<String, Object> haveCardBookUpdates = new HashMap<String, Object>();
                        haveCardBookUpdates.put(postKey, inputCode);
                        userInfoRef.updateChildren(haveCardBookUpdates);
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }


}
