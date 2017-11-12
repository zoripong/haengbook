package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.hs.emirim.uuuuri.haegbook.Fragment.PhotoFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.ReceiptFragment;
import kr.hs.emirim.uuuuri.haegbook.Interface.SelectedFragment;
import kr.hs.emirim.uuuuri.haegbook.Manager.DateListManager;
import kr.hs.emirim.uuuuri.haegbook.Model.Receipt;
import kr.hs.emirim.uuuuri.haegbook.R;

import static kr.hs.emirim.uuuuri.haegbook.R.id.date_spinner;

public class TravelDetailActivity extends AppCompatActivity implements SelectedFragment{

    private final String LOG = "TRAVEL_DETAIL_ACTIVITY";
    private final int TAB_COUNT = 2;
    private SectionsPagerAdapter mSectionsPagerAdapter;



    private ViewPager mViewPager;
    private int mPosition = PHOTO; // DEFAULT PAGE

    private String mBookCode;
    private String mPeriod;

    private FloatingActionButton fab;

    private FirebaseDatabase mDatabase;
    private int typeIndex;
    private boolean isUpdateNull=true;

    PhotoFragment mPhotoFragment;
    ReceiptFragment mReceiptFragment;

    ArrayList<String> dateList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);


        Intent intent = getIntent();
        mBookCode = intent.getStringExtra("BOOK_CODE");
        mPeriod = intent.getStringExtra("DATE");
        Toast.makeText(getApplicationContext(), mBookCode, Toast.LENGTH_SHORT).show();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        if(tabLayout.getSelectedTabPosition() == PHOTO)
            fab.show();
        else
            fab.hide();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                mPosition = tab.getPosition();
                switch (mPosition){
                    case PHOTO:
                        break;
                    case RECEIPT:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mPosition = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mPosition = tab.getPosition();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG, String.valueOf(mPosition));
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
                        Spinner mTypeSp= mDialog.findViewById(R.id.type_sp);
                        final EditText mTitleEt= mDialog.findViewById(R.id.title_et);
                        final EditText mAmountEt = mDialog.findViewById(R.id.amount_et);
                        final Spinner currencySymbolSp = mDialog.findViewById(R.id.currency_symbol_sp);
                        final EditText mMemoEt = mDialog.findViewById(R.id.memo_et);

                        DateListManager dateListManager = new DateListManager();

                        Date [] dates = dateListManager.convertString(mPeriod);
                        ArrayList<String> dateList = dateListManager.makeDateList(dates[0], dates[1]);

                        String []stringArray = new String[dateList.size()];
                        stringArray = dateList.toArray(stringArray);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_spinner_item, stringArray);

                        mDateSp.setAdapter(adapter);

                        mDateSp.setSelection(stringArray.length-1);


                        mDialog.findViewById(R.id.add_receipt_btn).setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {

                                Log.e("파베"+String.valueOf(mDateSp.getSelectedItem().toString()),"");
                                updateFB(String.valueOf(mDateSp.getSelectedItem().toString()) ,typeIndex,String.valueOf(mTitleEt.getText()),
                                        String.valueOf(mAmountEt.getText()),currencySymbolSp.getSelectedItem().toString(),String.valueOf(mMemoEt.getText()));
                                mDialog.dismiss();


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
        getMenuInflater().inflate(R.menu.menu_travel_detail, menu);

        MenuItem item = menu.findItem(date_spinner);
        Spinner dateSpinner = (Spinner) MenuItemCompat.getActionView(item);

        DateListManager dateListManager = new DateListManager();
        Date [] dates = dateListManager.convertString(mPeriod);
        dateList =  dateListManager.makeDateList(dates[0], dates[1]);
        dateList.add(0, "전체보기");

        String stringArray[] = new String[dateList.size()];
        stringArray = dateList.toArray(stringArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stringArray);

        dateSpinner.setAdapter(adapter);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 출판 버튼
        if (id == R.id.publish_btn) {
            return true;
        }


        return super.onOptionsItemSelected(item);
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


    public void updateFB(final String date, final int type, final String title, final String amount, final String symbol, final String memo){
        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference receiptRef = mDatabase.getReference("BookInfo/"+mBookCode+"/Content/Receipt");

        receiptRef.addListenerForSingleValueEvent(new ValueEventListener() {
            long keyIndex;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("입력값",date+"        "+title+amount+memo);
                if(date.replaceAll(" ","").equals("") || title.equals("") || amount.equals("") || memo.equals("")){
                    Toast.makeText(getApplicationContext(), "입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    keyIndex = dataSnapshot.getChildrenCount();
                    Map<String, Object> receiptUpdates = new HashMap<String, Object>();
                    receiptUpdates.put(String.valueOf(keyIndex + 1), new Receipt(date, title, amount+symbol, type, memo));
                    receiptRef.updateChildren(receiptUpdates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        Log.e("파베", String.valueOf(isUpdateNull));

    }
}
