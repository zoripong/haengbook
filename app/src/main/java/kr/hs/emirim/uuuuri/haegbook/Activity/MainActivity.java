package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashSet;

import kr.hs.emirim.uuuuri.haegbook.Adapter.CardPagerAdapter;
import kr.hs.emirim.uuuuri.haegbook.Adapter.ShadowTransformer;
import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.Model.CardItem;
import kr.hs.emirim.uuuuri.haegbook.R;

public class MainActivity extends AppCompatActivity {

    HashSet<CardBook> testSet;

    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test();
        initialize();
    }

    private void test() {
        testSet = new HashSet<CardBook>();

        //param : String period, String location, String title, String bookCode
        testSet.add(new CardBook("2017.11.06 - 2017.11.07", "후쿠오카", "나의 후쿠오카 이야기", "abcdefghijk"));
        testSet.add(new CardBook("2017.11.05 - 2017.11.09", "도쿄", "나랑 도쿄갈래?", "afghwzs2ijk"));
        testSet.add(new CardBook("2017.10.01 - 2017.10.09", "샌프란시스코", "샌프란시스코에서 살고 싶다.", "abcdefghijk"));
        testSet.add(new CardBook("2017.01.08 - 2017.06.10", "유럽", "유럽 배낭 여행기", "abcdefghijk"));

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

        findViewById(R.id.detail_travel_btn).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TravelDetailActivity.class);
                startActivity(intent);
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.string.text_1));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);



    }

    public float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

}
