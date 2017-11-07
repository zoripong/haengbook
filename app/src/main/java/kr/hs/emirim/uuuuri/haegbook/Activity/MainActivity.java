package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Adapter.CardPagerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Adapter.ShadowTransformer;
import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.R;

public class MainActivity extends BaseActivity {
    private final String TAG = "MainActivity";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRefer;
    private ValueEventListener mUserListener;

    private ArrayList<CardBook> testSet;

    private ArrayList<String> mCardBookAddress;

    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDatabaseData();
        test();
        initialize();
    }

    private void initialize() {


        findViewById(R.id.add_schedule_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,
                        AddScheduleActivity.class);

//                Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                startActivity(intent);
            }
        });


        mViewPager = findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter(this);
        // TODO: 2017-11-07 debug
        mCardAdapter.addCardItems(testSet);

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance();

    }

    @Override
    public void onStop() {
        super.onStop();

        if(mUserListener != null)
            mUserRefer.removeEventListener(mUserListener);
    }

    public void getDatabaseData() {
        String uid = "testuid";
//        mUserRefer = mDatabase.getReference("UserInfo/"+uid);

//        mUserListener = mUserRefer.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        mUserRefer.addValueEventListener(mUserListener);
//
    }
//
    private void test() {
        testSet = new ArrayList<CardBook>();

        //param : String period, String location, String title, String bookCode
        testSet.add(new CardBook("2017.11.06 - 2017.11.07", "후쿠오카", "나의 후쿠오카 이야기",
                "abcdefghijk", "https://lh3.googleusercontent.com/puH2yZeBoBKSjBLIrXNdn9ps8xC2g3Nc20bQOMDWzaXVJ_lLfeTErBfUh1eqD337h8I=h900-rw"));
        testSet.add(new CardBook("2017.11.05 - 2017.11.09", "도쿄", "나랑 도쿄갈래?", "afghwzs2ijk",
                "https://lh3.googleusercontent.com/puH2yZeBoBKSjBLIrXNdn9ps8xC2g3Nc20bQOMDWzaXVJ_lLfeTErBfUh1eqD337h8I=h900-rw"));
        testSet.add(new CardBook("2017.10.01 - 2017.10.09", "샌프란시스코", "샌프란시스코에서 살고 싶다.",
                "abcdefghijk", "https://lh3.googleusercontent.com/puH2yZeBoBKSjBLIrXNdn9ps8xC2g3Nc20bQOMDWzaXVJ_lLfeTErBfUh1eqD337h8I=h900-rw"));
        testSet.add(new CardBook("2017.01.08 - 2017.06.10", "유럽", "유럽 배낭 여행기", "abcdefghijk",
                "https://lh3.googleusercontent.com/puH2yZeBoBKSjBLIrXNdn9ps8xC2g3Nc20bQOMDWzaXVJ_lLfeTErBfUh1eqD337h8I=h900-rw"));

    }

}
