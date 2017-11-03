package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import kr.hs.emirim.uuuuri.haegbook.CustomViewPager;
import kr.hs.emirim.uuuuri.haegbook.Fragment.FifthInputFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.FirstInputFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.FourthInputFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.SecondInputFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.ThirdInputFragment;
import kr.hs.emirim.uuuuri.haegbook.R;

public class AddScheduleActivity extends AppCompatActivity {
    private final int PAGE_COUNT = 5;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;

    private boolean isClick = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        initialize();
    }

    private void initialize(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);

        final Button nextBtn = (Button) findViewById(R.id.next_btn);
        final Button previousBtn = (Button) findViewById(R.id.previous_btn);
        previousBtn.setVisibility(View.INVISIBLE);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewPager.getCurrentItem() >= 0 && mViewPager.getCurrentItem() < PAGE_COUNT )
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);

                if(mViewPager.getCurrentItem() == PAGE_COUNT - 1 && isClick){
                    Intent intent = new Intent(AddScheduleActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                if(mViewPager.getCurrentItem() == PAGE_COUNT - 1){
                    nextBtn.setText("FINISH");
                    isClick = true;
                }else if(mViewPager.getCurrentItem() >= 0 && mViewPager.getCurrentItem() < PAGE_COUNT - 1){
                    nextBtn.setText("NEXT");
                    isClick = false;
                }

                if(mViewPager.getCurrentItem() > 0 && mViewPager.getCurrentItem() < PAGE_COUNT){
                    previousBtn.setVisibility(View.VISIBLE);
                }

            }
        });



        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isClick = false;
                if(mViewPager.getCurrentItem() > 0 && mViewPager.getCurrentItem() < PAGE_COUNT)
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);

                if(mViewPager.getCurrentItem() == 0){
                    previousBtn.setVisibility(View.INVISIBLE);
                }else if(mViewPager.getCurrentItem() > 0 && mViewPager.getCurrentItem() < PAGE_COUNT){
                    previousBtn.setVisibility(View.VISIBLE);
                }

                if(mViewPager.getCurrentItem() >= 0 && mViewPager.getCurrentItem() < PAGE_COUNT - 1){
                    nextBtn.setText("NEXT");
                }
            }
        });

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new FirstInputFragment();
                case 1:
                    return new SecondInputFragment();
                case 2:
                    return new ThirdInputFragment();
                case 3:
                    return new FourthInputFragment();
                case 4:
                    return new FifthInputFragment();
                default:
            return null;
            }
        }

        @Override
        public int getCount() {
//            TODO
            return 5;
        }

    }
}
