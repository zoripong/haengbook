package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.hs.emirim.uuuuri.haegbook.Adapter.CardPagerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Interface.ScheduleTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.ShadowTransformer;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.R;
import kr.hs.emirim.uuuuri.haegbook.fcm.FirebaseInstanceIDService;

// TODO: 2017-11-12 : MainActivity => 파이어베이스 로딩중 클릭시 취소 방지
// TODO: 2017-11-12 : 이미지 로딩 화면 제공

public class MainActivity extends BaseActivity {
    private final String TAG = "MainActivity";


    private EditText bookCodeEt;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRefer;
    private ValueEventListener mUserListener;
    private DatabaseReference mCardBookRefer;
    private ValueEventListener mCardBookListener;

    private ArrayList<String> mCardBookAddress;
    private ArrayList<CardBook> mCardBooks;
    private ArrayList<CardBook> mReserveBooks;

    private ViewPager mViewPager;
    private UnderlinePageIndicator indicatorUnderline;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    LinearLayout card_item;
    int setLRPadding;
    int setTBPadding;
    int setMargin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initialize();
        getDatabase();
        getUserToken();



        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int w = dm.widthPixels; //디바이스 해상도 구하기

        DisplayMetrics displayMetrics = new DisplayMetrics(); //디바이스 1px당 mm 길이 구하기
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        double deviceDPI = displayMetrics.xdpi;
        double mm = 25.4f / deviceDPI;

        Log.e(TAG, "해상도 가로의 값은 무엇일까요요용요ㅛ요요요요 : "+(mm*w)+" mm : "+mm); //1px에 따른 mm*화면 px개수

        setLRPadding = (int) (mm*600);
        setTBPadding=(int)(mm*200);
        mViewPager.setPadding(setLRPadding, setTBPadding, setLRPadding, setTBPadding);
        Log.e(TAG, "디바이스 양쪽 패딩의 값은...? : "+setLRPadding); //1px에 따른 mm*화면 px개수

    }



    private void getUserToken(){
        FirebaseInstanceIDService tokenService = new FirebaseInstanceIDService();
        SharedPreferenceManager spm = new SharedPreferenceManager(this);
        spm.save(ScheduleTag.USER_TOKEN_TAG, tokenService.sendRegistrationToServer());
    }

    private void initialize() {
        mDatabase = FirebaseDatabase.getInstance();

        mCardBookAddress = new ArrayList<>();
        mCardBooks = new ArrayList<>();
        mReserveBooks = new ArrayList<>();


        findViewById(R.id.add_schedule_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog selectDialog = new Dialog(MainActivity.this, R.style.MyDialog);
                selectDialog.setContentView(R.layout.dialog_select);
                selectDialog.show();
                selectDialog.findViewById(R.id.add_code_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog inputDialog = new Dialog(MainActivity.this, R.style.MyDialog);
                        inputDialog.setContentView(R.layout.dialog_code_input);
                        selectDialog.hide();
                        inputDialog.show();
                        selectDialog.dismiss();
                        final ClipboardManager clipboardManager =  (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        bookCodeEt=inputDialog.findViewById(R.id.book_code_et);
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
                                //TODO
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
                        Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        mViewPager = findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter(this);
        // TODO: 2017-11-07 debug
        mCardAdapter.addCardItem(new CardBook("test", "test", "test", "test", "test"));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);

        // page
        indicatorUnderline = findViewById(R.id.activity_view_pager_indicator_underline);
        ViewPager.SimpleOnPageChangeListener pagerSyncronizer = getPagerSynchronizer();
        indicatorUnderline.setViewPager(mViewPager);
        indicatorUnderline.setOnPageChangeListener(pagerSyncronizer);

    }

    private ViewPager.SimpleOnPageChangeListener getPagerSynchronizer() {
        return new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position, true);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mUserListener != null)
            mUserRefer.removeEventListener(mUserListener);

        if(mCardBookListener != null)
            mCardBookRefer.removeEventListener(mCardBookListener);

    }

    public void getDatabase() {

        SharedPreferenceManager spm = new SharedPreferenceManager(this);

        String uid = spm.retrieveString(ScheduleTag.USER_TOKEN_TAG);
        showProgressDialog();
        mUserRefer = mDatabase.getReference("UserInfo/"+uid);

        mUserListener = mUserRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCardBookAddress.clear();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    mCardBookAddress.add(noteDataSnapshot.getValue(String.class));
                }
                getCardBook();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        mUserRefer.addValueEventListener(mUserListener);
    }


    public void updateFB(final String inputCode){
        SharedPreferenceManager spm = new SharedPreferenceManager(this);
        final String uid = spm.retrieveString(ScheduleTag.USER_TOKEN_TAG);

        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference receiptRef = mDatabase.getReference("BookInfo/"+inputCode);
        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            long keyIndex;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()<1){
                    Toast.makeText(MainActivity.this, "유효하지않은 코드입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //uid에 카드 업데이트

                final DatabaseReference userInfoRef = mDatabase.getReference("UserInfo/"+uid);

                userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    long keyIndex;

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(inputCode) != null){
                            Toast.makeText(MainActivity.this, "이미 등록된 코드입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        keyIndex = dataSnapshot.getChildrenCount();
                        Map<String, Object> haveCardBookUpdates = new HashMap<String, Object>();
                        haveCardBookUpdates.put(String.valueOf(keyIndex+1), inputCode);
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

    private void getCardBook(){
        mCardBookRefer = mDatabase.getReference("BookInfo");
        Log.e(TAG, "contain ? "+mCardBookAddress.toString());
        mCardBookListener = mCardBookRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCardBooks.clear();
                mReserveBooks.clear();

                mCardAdapter.addCardItems(mCardBooks);
                mViewPager.setAdapter(mCardAdapter);

                Iterator<DataSnapshot> bookDataSnapshot = dataSnapshot.getChildren().iterator();
                while(bookDataSnapshot.hasNext()){
                    DataSnapshot bookCodeSnapShot = bookDataSnapshot.next();
                    String bookCode = bookCodeSnapShot.getKey().toString(); // key
                    if(mCardBookAddress.contains(bookCode)){

                        String location = bookCodeSnapShot.child("Registration").child("location").getValue(String.class);
                        String period = bookCodeSnapShot.child("Registration").child("period").getValue(String.class);
                        String title = bookCodeSnapShot.child("Registration").child("title").getValue(String.class);

                        if(Boolean.parseBoolean(bookCodeSnapShot.child("isShowing").getValue().toString())){
                            // isShowing == true
                            String image = bookCodeSnapShot.child("Registration").child("image").getValue(String.class);
                            //(String period, String location, String title, String bookCode, String url)
                            // param
                            mCardBooks.add(new CardBook(period, location, title, bookCode, image));
                        }else{
                            mReserveBooks.add(new CardBook(period, location, title, bookCode, null));
                        }
                    }

                }

                Log.e(TAG, "카드북 : "+mCardBooks.toString());
                Log.e(TAG, "예약들 : "+mReserveBooks.toString());
                mCardAdapter.addCardItems(mCardBooks);
                if(mCardBooks.size() == 0)
                    mCardAdapter.addCardItem(new CardBook("test", "test", "test", "test", "test"));
                mViewPager.setAdapter(mCardAdapter);

                hideProgressDialog();
              }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCardBookRefer.addValueEventListener(mCardBookListener);


    }

}
