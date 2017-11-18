package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.hs.emirim.uuuuri.haegbook.Adapter.GalleryRecyclerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Adapter.ImageRecyclerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Fragment.PhotoFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.ReceiptFragment;
import kr.hs.emirim.uuuuri.haegbook.Interface.CurrencyTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.OnItemClickListener;
import kr.hs.emirim.uuuuri.haegbook.Interface.SelectedFragment;
import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.TravelDetailTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.DateListManager;
import kr.hs.emirim.uuuuri.haegbook.Manager.GridDividerDecoration;
import kr.hs.emirim.uuuuri.haegbook.Manager.ImageRecyclerSetter;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Manager.ViewFindUtils;
import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;
import kr.hs.emirim.uuuuri.haegbook.Model.Receipt;
import kr.hs.emirim.uuuuri.haegbook.Model.TabEntity;
import kr.hs.emirim.uuuuri.haegbook.R;


// TODO: 2017-11-18 커스텀 액션바로 바꾸면 publishButton을 여행이 지난 날짜부터 볼 수 있게 set
public class TravelDetailActivity extends BaseActivity implements SelectedFragment{

    private final String TAG = "TRAVEL_DETAIL_ACTIVITY";
    private final int TAB_COUNT = 2;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mRegistrationRef;

    private Spinner mDateSpinner;
    private ViewPager mViewPager;
    private int mPosition = PHOTO; // DEFAULT PAGE

    private String mBookCode;
    private String mPeriod;

    private String mCurrencyName;
    private String mCurrencySymbol;

    private boolean mIsKorea;


    private FloatingActionButton fab;

    private FirebaseDatabase mDatabase;
    private int typeIndex=0;
    private boolean isUpdateNull=true;

    PhotoFragment mPhotoFragment;
    ReceiptFragment mReceiptFragment;

    ArrayList<String> dateList;

    private RecyclerView recyclerView;
    private ArrayList<FirebaseImage> mImages;
    private ImageRecyclerSetter imageRecyclerSetter;

    private Float[] mTravelMoney=new Float[6];
    private Float[] mTravelRate=new Float[6];
    private Float mKoreaMoney=0.0f;
    private Float mRestMoney =0.0f;

    private boolean isNotSelected;
    private FirebaseImage mSelectImage;

    private int selectCurrencySymbol=0;

    SharedPreferenceManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);

        spm = new SharedPreferenceManager(this);

        Intent intent = getIntent();
        mBookCode = intent.getStringExtra("BOOK_CODE");
        mPeriod = intent.getStringExtra("DATE");
        String image = intent.getStringExtra("Image");

        isNotSelected = true;

        //TODO : 돈 가져와서 SPM에
        getDetailInfo();
        Toast.makeText(getApplicationContext(), mBookCode, Toast.LENGTH_SHORT).show();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        if(image != null)
            fab.hide();
        else
            fab.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // START CUSTOM TAB
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        mTabEntities.add(new TabEntity("PHOTO"));
        mTabEntities.add(new TabEntity("RECEIPT"));

        View mDecorView = getWindow().getDecorView();
        mViewPager = ViewFindUtils.find(mDecorView, R.id.container);

        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(sectionsPagerAdapter);

        final CommonTabLayout tabLayout = ViewFindUtils.find(mDecorView, R.id.tabs);
        tabLayout.setTabData(mTabEntities);

        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mPosition = position;
                mViewPager.setCurrentItem(mPosition);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        tabLayout.setCurrentTab(0);
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
                mPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // END OF TAB


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, String.valueOf(mPosition));
                Intent intent;
                switch (mPosition){
                    case PHOTO:
                        intent = new Intent(TravelDetailActivity.this, AddPhotoActivity.class);
                        intent.putExtra("BOOK_CODE", mBookCode);
                        intent.putExtra("DATE", mPeriod);
                        startActivity(intent);
                        break;
                    case RECEIPT:

                        final Dialog mDialog = new Dialog(view.getContext(), R.style.MyDialog);
                        mDialog.setContentView(R.layout.dialog_regist_receipt);

                        final Spinner mDateSp=  mDialog.findViewById(R.id.date_sp);
                        final Spinner mTypeSp= mDialog.findViewById(R.id.type_sp);
                        final EditText mTitleEt= mDialog.findViewById(R.id.title_et);
                        final EditText mAmountEt = mDialog.findViewById(R.id.amount_et);
                        final Spinner currencySymbolSp = mDialog.findViewById(R.id.currency_symbol_sp);
                        final EditText mMemoEt = mDialog.findViewById(R.id.memo_et);

                        DateListManager dateListManager = new DateListManager();

                        Date [] dates = dateListManager.convertDates(mPeriod);
                        ArrayList<String> dateList = dateListManager.makeDateList(dates[0], dates[1]);

                        String []stringArray = new String[dateList.size()];
                        stringArray = dateList.toArray(stringArray);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, stringArray);

                        mDateSp.setAdapter(adapter);
                        mDateSp.setSelection(stringArray.length-1);

                        mTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                typeIndex=position;
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });


                        //TODO
                        String[] currencyArray;

                        if(mIsKorea){
                            int currencySize=1;
                            currencyArray = new String[currencySize];
                            currencyArray[0]="\uFFE6";

                        }else{
                            int currencySize=2;
                            currencyArray = new String[currencySize];
                            currencyArray[0]= "\uFFE6";
                            currencyArray[1]=mCurrencySymbol;
                        }

                        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, currencyArray);

                        currencySymbolSp.setAdapter(currencyAdapter);

                        currencySymbolSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                //todo
                                selectCurrencySymbol=position;
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });


                        mDialog.findViewById(R.id.exit_btn).setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                mDialog.dismiss();
                            }
                        });


                        mDialog.findViewById(R.id.add_receipt_btn).setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {

                                Log.e("파베"+String.valueOf(mDateSp.getSelectedItem().toString()),"");
                                updateFB(mDialog,String.valueOf(mDateSp.getSelectedItem().toString()) ,typeIndex,String.valueOf(mTitleEt.getText()),
                                        String.valueOf(mAmountEt.getText()).replaceAll(" ",""),currencySymbolSp.getSelectedItem().toString(),String.valueOf(mMemoEt.getText()));


                            }
                        });
                        mDialog.show();

                        break;
                    default:
                        break;
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.actionbar_travel_detail, null);

        mDateSpinner = actionbar.findViewById(R.id.date_spinner);

        DateListManager dateListManager = new DateListManager();
        Date [] dates = dateListManager.convertDates(mPeriod);
        dateList =  dateListManager.makeDateList(dates[0], dates[1]);
        dateList.add(0, "전체보기");

        String stringArray[] = new String[dateList.size()];
        stringArray = dateList.toArray(stringArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stringArray);

        mDateSpinner.setAdapter(adapter);

        mDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (mPosition){
                    case PHOTO:
                        Log.e("탭", "포토");
                        mPhotoFragment.setDateList(dateList);
                        mPhotoFragment.spinnerItemSelected(i);

                        break;
                    case RECEIPT:
                        Log.e("탭", "영수증");
                        mReceiptFragment.setDateList(dateList);
                        mReceiptFragment.spinnerItemSelected(i);
                        break;
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);


        getMenuInflater().inflate(R.menu.menu_travel_detail, menu);
        MenuItem publishItem = menu.findItem(R.id.publish_btn);
//
//
//        Spinner dateSpinner = (Spinner) MenuItemCompat.getActionView(spinnerItem);
//
//

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.publish_btn:
                Toast.makeText(TravelDetailActivity.this, "publish", Toast.LENGTH_SHORT).show();

                publishTravel();

                break;

        }


        return super.onOptionsItemSelected(item);
    }

    private void publishTravel() {
        final Dialog publishDialog = new Dialog(TravelDetailActivity.this, R.style.MyDialog);
        publishDialog.setContentView(R.layout.dialog_publish_travel);
        publishDialog.show();

        mImages = new ArrayList<FirebaseImage>();
        recyclerView = publishDialog.findViewById(R.id.recyclerview);
        imageRecyclerSetter = new ImageRecyclerSetter(TravelDetailActivity.this, false);

        // // TODO: 2017-11-18 기기 마다 패딩 다르게
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));

        getFBImage();

        publishDialog.findViewById(R.id.publish_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 파베에 업데이트 이미지
                showProgressDialog();
                mRegistrationRef = FirebaseDatabase.getInstance().getReference("BookInfo/"+mBookCode+"/Registration/image");
                mRegistrationRef.setValue(mSelectImage.getImageURI());
                hideProgressDialog();
                publishDialog.dismiss();
                Intent intent = new Intent(TravelDetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public void getFBImage(){
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference imageRef = mDatabase.getReference("BookInfo/"+mBookCode+"/Content/Images");

        showProgressDialog();
        ValueEventListener imageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot FBimage) {
                mImages.clear();

                Iterator<DataSnapshot> childIterator = FBimage.getChildren().iterator();
                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
                while(childIterator.hasNext()) {
                    DataSnapshot imageSnapshot = childIterator.next();
                    String imageComment=imageSnapshot.child("imageComment").getValue(String.class);
                    String imageURI=imageSnapshot.child("imageURI").getValue(String.class);
                    String date=imageSnapshot.child("date").getValue(String.class);

                    mImages.add(new FirebaseImage(imageComment,imageURI,date));

                }

                imageRecyclerSetter.setRecyclerCardView(recyclerView, mImages, mOnItemClickListener);

                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        imageRef.addValueEventListener(imageListener);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case PHOTO:
                    mPhotoFragment = new PhotoFragment();
                    mPhotoFragment.setBookCode(mBookCode);
                    mPhotoFragment.setPeriod(mPeriod);
                    return mPhotoFragment;
                case RECEIPT:
                    mReceiptFragment = new ReceiptFragment();
                    mReceiptFragment.setBookCode(mBookCode);
                    mReceiptFragment.setPeriod(mPeriod);
                    return mReceiptFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case PHOTO:
                    return "PHOTO";
                case RECEIPT:
                    return "RECEIPT";
            }
            return null;
        }
    }


    public void updateFB(final Dialog mDialog, final String date, final int type, final String title, final String amount, final String symbol, final String memo){
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference receiptRef = mDatabase.getReference("BookInfo/"+mBookCode+"/Content/Receipt");

        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            long keyIndex;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("입력값",date+"        "+title+amount+memo);
                if(date.replaceAll(" ","").equals("") || title.replaceAll(" ","").equals("") || amount.replaceAll(" ","").equals("") || memo.replaceAll(" ","").equals("")){
                    Toast.makeText(getApplicationContext(), "입력해주세요.", Toast.LENGTH_SHORT).show();

                }else {
                    Float restMoney = spm.retrieveFloat(TravelDetailTag.REST_MONEY_TAG);

                    if (restMoney < Float.parseFloat(amount)) {
                        Toast.makeText(getApplicationContext(), "예정한 금액보다 더 많은 금액을 소비하셨습니다.", Toast.LENGTH_SHORT).show();
                        //// TODO: 2017-11-15  다이얼로그 , 금액 늘리게

                    } else {
                        updateTypeMoney(type,amount);
                        keyIndex = dataSnapshot.getChildrenCount();
                        Map<String, Object> receiptUpdates = new HashMap<String, Object>();
                        receiptUpdates.put(String.valueOf(keyIndex + 1), new Receipt(date, title, amount + symbol, type, memo));
                        receiptRef.updateChildren(receiptUpdates);
                        mDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        Log.e("파베", String.valueOf(isUpdateNull));

    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void OnItemClick(GalleryRecyclerAdapter.PhotoViewHolder photoViewHolder, int position) {}

        @Override
        public void OnItemClick(ImageRecyclerAdapter.ImageViewHolder imageViewHolder, int position) {
            List<FirebaseImage> firebaseImages = imageRecyclerSetter.getPhotoList();
            FirebaseImage firebaseImage = firebaseImages.get(position);
            isNotSelected = false;
            for(int i = 0; i<firebaseImages.size(); i++){
                if(i == position)
                    continue;
                firebaseImages.get(i).setSelected(false);
            }

            if(firebaseImage.isSelected){
                isNotSelected = true;
                mSelectImage = null;
                firebaseImage.setSelected(false);
            }else{
                mSelectImage = firebaseImage;
                firebaseImage.setSelected(true);
            }

            Log.e(TAG, "선택되었나?" + isNotSelected);

            imageRecyclerSetter.setRecyclerCardView(recyclerView, mImages, mOnItemClickListener);
        }

    };


    public void updateTypeMoney(final int type, String amount){
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference receiptRef = mDatabase.getReference("TravelMoney/"+mBookCode);



        Float restMoney = spm.retrieveFloat(TravelDetailTag.REST_MONEY_TAG);
        Float typeMoney = 0.0f;
        switch (type){
            case 0://음식
                typeMoney = spm.retrieveFloat(TravelDetailTag.FOOD_MONEY_TAG);
                break;
            case 1:
                typeMoney = spm.retrieveFloat(TravelDetailTag.TRAFFIC_MONEY_TAG);
                break;
            case 2:
                typeMoney = spm.retrieveFloat(TravelDetailTag.SHOPPING_MONEY_TAG);
                break;
            case 3:
                typeMoney = spm.retrieveFloat(TravelDetailTag.GIFT_MONEY_TAG);
                break;
            case 4:
                typeMoney = spm.retrieveFloat(TravelDetailTag.CULTURE_MONEY_TAG);
                break;
            case 5:
                typeMoney = spm.retrieveFloat(TravelDetailTag.ETC_MONEY_TAG);
                break;
        }

        //restmonet -- 타입머니 ++ 만약 예정한 타입머니보다 많이 썻다면 토스트
        //TODO 만약 ㅁ외국돈으로 선택됫다면 (float) (Math.round(afterMoney / rate[currencyIndex] * 1000) / 1000.0)

        if(selectCurrencySymbol == 1){
            Log.e("영수증 돈 변환 전 ",amount);
            float rate = spm.retrieveFloat(CurrencyTag.CHOOSE_CURRENCY_TAG);
            amount= String.valueOf((float) (Math.round( Float.parseFloat(amount) / rate * 1000) / 1000.0));
            Log.e("영수증 돈 변환 ","환율은 : "+rate+"변환:"+amount);
        }

        Map<String, Object> restMoneyUpdates = new HashMap<String, Object>();
        restMoneyUpdates.put("restKorea", new Float(restMoney - Float.parseFloat(amount)));
        receiptRef.child("Total").updateChildren(restMoneyUpdates);

        Map<String, Object> typeMoneyUpdates = new HashMap<String, Object>();
        typeMoneyUpdates.put(String.valueOf(type+1), new Float(Float.parseFloat(amount)+typeMoney));
        receiptRef.child("Money").updateChildren(typeMoneyUpdates);


    }

    public void getDetailInfo(){

//// TODO: 2017-11-15 photo 도 가져오기

        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference travleDeatilRef = mDatabase.getReference("TravelMoney/"+mBookCode);

        showProgressDialog();
        travleDeatilRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(int i=1;i<7;i++){
                    mTravelMoney[i-1]=dataSnapshot.child("Money").child(String.valueOf(i)).getValue(Float.class);
                    mTravelRate[i-1]=dataSnapshot.child("Rate").child(String.valueOf(i)).getValue(Float.class);
                    mKoreaMoney = dataSnapshot.child("Total").child("korea").getValue(Float.class);
                    mRestMoney = dataSnapshot.child("Total").child("restKorea").getValue(Float.class);
                    mCurrencyName = dataSnapshot.child("Currency").child("name").getValue(String.class);
                    mCurrencySymbol = dataSnapshot.child("Currency").child("symbol").getValue(String.class);
                    if(mCurrencyName.equals(""))
                        mIsKorea=true;

                }
                saveData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private class getRate extends AsyncTask<Void,Void,Void> {
        private Elements titleElement;//나라이름
        private Elements saleElement;//환율

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("http://info.finance.naver.com/marketindex/exchangeList.nhn").get();
                titleElement = doc.select(".tit");
                saleElement = doc.select(".sale");
                for(int i=0;i<titleElement.size();i++) {
                    String parsingCountry = titleElement.get(i).text().replaceAll("[^\uAC00-\uD7AF\u1100-\u11FF\u3130-\u318F]", "");
                    parsingCountry = parsingCountry.replaceAll("엔", "");
                    double sale = Double.parseDouble(saleElement.get(i).text().replaceAll(",", ""));
                    if(mCurrencyName.equals(parsingCountry)){
                        Log.e("선택한 국가", parsingCountry);
                        Log.e("환율", String.valueOf(sale));
                        double rate= Math.round(1 / sale * 100000000) / 100000000.0;
                        if (parsingCountry.contains("일본")) {
                            rate = Math.round(1 / (sale / 100) * 100000000) / 100000000.0;
                        }
                        spm.save(CurrencyTag.CHOOSE_CURRENCY_TAG, (float) rate);

                        hideProgressDialog();
                        return null;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void saveData(){
        spm.save(TravelDetailTag.FOOD_MONEY_TAG,mTravelMoney[0]);
        spm.save(TravelDetailTag.TRAFFIC_MONEY_TAG,mTravelMoney[1]);
        spm.save(TravelDetailTag.SHOPPING_MONEY_TAG,mTravelMoney[2]);
        spm.save(TravelDetailTag.GIFT_MONEY_TAG,mTravelMoney[3]);
        spm.save(TravelDetailTag.CULTURE_MONEY_TAG,mTravelMoney[4]);
        spm.save(TravelDetailTag.ETC_MONEY_TAG,mTravelMoney[5]);

        spm.save(TravelDetailTag.FOOD_RATE_TAG,mTravelRate[0]);
        spm.save(TravelDetailTag.TRAFFIC_RATE_TAG,mTravelRate[1]);
        spm.save(TravelDetailTag.SHOPPING_RATE_TAG,mTravelRate[2]);
        spm.save(TravelDetailTag.GIFT_RATE_TAG,mTravelRate[3]);
        spm.save(TravelDetailTag.CULTURE_RATE_TAG,mTravelRate[4]);
        spm.save(TravelDetailTag.ETC_RATE_TAG,mTravelRate[5]);

        spm.save(TravelDetailTag.TOTAL_KOREA_MONEY_TAG,mKoreaMoney);
        spm.save(TravelDetailTag.REST_MONEY_TAG, mRestMoney);

        spm.save(CurrencyTag.CURRENCY_COUNTRY_TAG,mCurrencyName);
        spm.save(CurrencyTag.CURRENCY_SYMBOL_TAG,mCurrencySymbol);
        spm.save(SharedPreferenceTag.IS_KOR_TAG,mIsKorea);



        for(int i=0;i<6;i++) {
            Log.e("트레블 디테일", String.valueOf(mTravelMoney[i]));
        }
        Log.e("트레블 코리아", String.valueOf(mKoreaMoney));
        Log.e("트레블 포린", String.valueOf(mRestMoney));

        if(mIsKorea)
            hideProgressDialog();
        else
            new getRate().execute();



    }
}

