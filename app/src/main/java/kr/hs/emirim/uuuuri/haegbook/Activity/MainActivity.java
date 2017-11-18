package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.hs.emirim.uuuuri.haegbook.Adapter.CardPagerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Adapter.OnSwipeTouchListener;
import kr.hs.emirim.uuuuri.haegbook.Fragment.BookCardListFragment;
import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.DateListManager;
import kr.hs.emirim.uuuuri.haegbook.Manager.ShadowTransformer;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.R;

// TODO: 2017-11-12 : 이미지 로딩 화면 제공
public class MainActivity extends BaseActivity {
    private final String TAG = "MainActivity";
    private final String BUNDLE_TAG ="BUNDLE_TAG";

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

    private SharedPreferenceManager spm;
    private LinearLayout card_item;
    private int setLRPadding;
    private int setTBPadding;
    private int setMargin;

    private BottomSheetLayout bottomSheetLayout;

    PermissionListener permissionlistener;

    private String PhoneNum;

    private ArrayList<CardBook> mNotPublishBook;
    private boolean isNotify;

    private CardBook travelingCard;
    private boolean isTraveling;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        getDatabase();
        getUserPhoneNumber();
        Log.e(TAG, "이친구는 언제 실행될까ㅏㅏ요?");
//        if(mNotPublishBook.size()!=0)

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int w = dm.widthPixels; //디바이스 해상도 구하기

        DisplayMetrics displayMetrics = new DisplayMetrics(); //디바이스 1px당 mm 길이 구하기
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        double deviceDPI = displayMetrics.xdpi;
        double mm = 25.4f / deviceDPI;

        Log.e(TAG, "해상도 가로의 값은 무엇일까요요용요ㅛ요요요요 : "+(mm*w)+" mm : "+mm); //1px에 따른 mm*화면 px개수

        setLRPadding = (int) (mm*1800);
        setTBPadding=(int)(mm*600);
        mViewPager.setPadding(setLRPadding, setTBPadding, setLRPadding, setTBPadding);
        Log.e(TAG, "디바이스 양쪽 패딩의 값은...? : "+setLRPadding); //1px에 따른 mm*화면 px개수

    }

    private void notificationPublish() {
        if(mNotPublishBook.size()==0)
            return;
        final Dialog notifyDialog = new Dialog(MainActivity.this, R.style.MyDialog);
        notifyDialog.setContentView(R.layout.dialog_notification);
        StringBuffer message = new StringBuffer();
        for(int i = 0; i<mNotPublishBook.size(); i++){
            message.append("- " + mNotPublishBook.get(i).getTitle()+"\n");
        }
        message.append("\n여러분들의 추억이 완성되지 않고 있습니다. :(\n빨리 등록해주세요!");

        notifyDialog.show();

        ((TextView)notifyDialog.findViewById(R.id.message_tv)).setText(message.toString());
        notifyDialog.findViewById(R.id.done_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, TravelDetailActivity.class);
                intent.putExtra("BOOK_CODE", mNotPublishBook.get(0).getBookCode());
                intent.putExtra("DATE", mNotPublishBook.get(0).getPeriod());
                intent.putExtra("Image", mNotPublishBook.get(0).getImage());
                startActivity(intent);
            }
        });

        notifyDialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDialog.dismiss();
            }
        });

    }

    public void getUserPhoneNumber(){
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                TelephonyManager telManger = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                PhoneNum = telManger.getLine1Number();

                if(PhoneNum == null) {//테스트 시 공기계일 경우를 위해서...
                    PhoneNum = "01086352026";
                }
                if(PhoneNum.startsWith("+82")){
                    PhoneNum = PhoneNum.replace("+82","0");
                }


                Log.e("내 핸드폰 번호",PhoneNum);

                SharedPreferenceManager spm = new SharedPreferenceManager(MainActivity.this);
                spm.save(SharedPreferenceTag.USER_TOKEN_TAG, PhoneNum);
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한이 없을 경우, 행북을 사용하실 수 없습니다.", Toast.LENGTH_SHORT).show();
            }

        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("권한이 없을 경우, 행북을 사용 할 수 없습니다.\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_PHONE_STATE)
                .check();
    }

    private void initialize() {
        Intent intent = getIntent();
        isNotify = intent.getBooleanExtra("SHOW NOTIFICATION", false);

        spm = new SharedPreferenceManager(MainActivity.this);

        mDatabase = FirebaseDatabase.getInstance();

        mCardBookAddress = new ArrayList<>();
        mCardBooks = new ArrayList<>();
        mReserveBooks = new ArrayList<>();


        findViewById(R.id.add_schedule_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTraveling && travelingCard != null) {
                    Intent intent = new Intent(MainActivity.this, TravelDetailActivity.class);
                    intent.putExtra("BOOK_CODE", travelingCard.getBookCode());
                    intent.putExtra("DATE", travelingCard.getPeriod());
                    intent.putExtra("Image", travelingCard.getImage());
                    startActivity(intent);

                } else {
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
                            final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            bookCodeEt = inputDialog.findViewById(R.id.book_code_et);
                            inputDialog.findViewById(R.id.paste_btn).setOnClickListener(new View.OnClickListener() {
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
                            Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                            startActivity(intent);
                            finish();
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
            }
        });


        findViewById(R.id.setting_btn).setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View view) {

                                                                  Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                                                                  startActivity(intent);
                                                              }
                                                          }
        );
        mViewPager = findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter(this);

        /*
        param : String period, String location, String title, String image
        * */
        mCardAdapter.addCardItem(new CardBook("자세히 보기 >", "", "혹시 행북이 처음이신가요?", "tutorial"));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);

        // page
        indicatorUnderline = findViewById(R.id.activity_view_pager_indicator_underline);
        ViewPager.SimpleOnPageChangeListener pagerSyncronizer = getPagerSynchronizer();
        indicatorUnderline.setViewPager(mViewPager);
        indicatorUnderline.setOnPageChangeListener(pagerSyncronizer);

        /*bottom Sheet*/
        bottomSheetLayout = findViewById(R.id.design_bottom_sheet);
        bottomSheetLayout.setInterceptContentTouch(true);

        findViewById(R.id.handle).setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeRight() {}

            @Override
            public void onSwipeLeft() {}

            @Override
            public void onSwipeTop() {
/*
 Bundle bundle = new Bundle();
        String myMessage = "Stackoverflow is cool!";
        bundle.putString("message", myMessage );
        FragmentClass fragInfo = new FragmentClass();
        fragInfo.setArguments(bundle);
        transaction.replace(R.id.fragment_single, fragInfo);
        transaction.commit();
 */
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(BUNDLE_TAG, mCardBooks);
                BookCardListFragment fragment = new BookCardListFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), R.id.design_bottom_sheet);
//                new BookCardListFragment().show(getSupportFragmentManager(), R.id.design_bottom_sheet);
            }

            @Override
            public void onSwipeBottom() {}


        });

        mNotPublishBook = new ArrayList<>();
        isTraveling = false;
        travelingCard = null;

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

        String uid = spm.retrieveString(SharedPreferenceTag.USER_TOKEN_TAG);
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


    private void updateFB(final String inputCode){
        SharedPreferenceManager spm = new SharedPreferenceManager(this);
        final String uid = spm.retrieveString(SharedPreferenceTag.USER_TOKEN_TAG);

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
//        Log.e(TAG, "contain ? "+mCardBookAddress.toString());
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
                        Boolean isShowing = Boolean.parseBoolean(bookCodeSnapShot.child("isShowing").getValue().toString());
                        String image = bookCodeSnapShot.child("Registration").child("image").getValue(String.class);

                        CardBook cardBook = new CardBook(period, location, title, bookCode, image, isShowing);
                        mCardBooks.add(cardBook);

                        DateListManager dateListManager = new DateListManager();
                        Date dates[] = dateListManager.convertDates(period);

                        Date now = new Date();
                        now = new Date(now.getYear(), now.getMonth(), now.getDate(), 0, 0, 0);

                        Log.e(TAG, dates[0].getTime()+"/"+dates[1].getTime()+"/"+now.getTime());
                        Log.e(TAG, dates[0].toString()+"/"+dates[1].toString()+"/"+now.toString());
                        if(dates[0].getTime() <= now.getTime() && dates[1].getTime() >= now.getTime()){
                            isTraveling = true;
                            travelingCard = cardBook;
                            spm.save(SharedPreferenceTag.DEFAULT_DIRECTORY, title);
                            Log.e(TAG, "여행 중");
                            Log.e(TAG, "default directory name : "+spm.retrieveString(SharedPreferenceTag.DEFAULT_DIRECTORY));
                        }else if(dates[0].getTime() < now.getTime() && dates[1].getTime() < now.getTime() && image==null){
                            mNotPublishBook.add(cardBook);
                        }
                    }

                }

//                Log.e(TAG, "sort 전 카드북 : "+mCardBooks.toString());
                DescendingObj descending = new DescendingObj();
                Collections.sort(mCardBooks, descending);
//                Log.e(TAG, "sort 후 카드북 : "+mCardBooks.toString());

                mCardAdapter.addCardItems(mCardBooks);

                if(mCardBooks.size() == 0)
                    mCardAdapter.addCardItem(new CardBook("test", "test", "test", "test", "test", false));
                mViewPager.setAdapter(mCardAdapter);

                spm.save(SharedPreferenceTag.IS_TRAVELING_TAG, isTraveling);
                Log.e(TAG, "그래서 여행중이라고?"+String.valueOf(spm.retrieveBoolean(SharedPreferenceTag.IS_TRAVELING_TAG)));

                hideProgressDialog();

                if(isNotify) {
                    isNotify = false;
                    notificationPublish();
                }

                if(isTraveling && travelingCard!=null){
                    ((TextView)findViewById(R.id.add_schedule_btn)).setText("현재 여행 바로가기 >");

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCardBookRefer.addValueEventListener(mCardBookListener);


    }

    // String 내림차순
    class DescendingObj implements Comparator<CardBook> {

        @Override
        public int compare(CardBook cardBook, CardBook t1) {

            DateListManager dateListManager = new DateListManager();
            Date fBookDates[] = dateListManager.convertDates(cardBook.getPeriod());
            Date sBookDates[] = dateListManager.convertDates(t1.getPeriod());

            Long fTime = fBookDates[0].getTime();
            Long sTime = sBookDates[0].getTime();

            return sTime.compareTo(fTime);
        }
    }

}
