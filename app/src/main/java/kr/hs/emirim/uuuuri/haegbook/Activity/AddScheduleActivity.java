package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import kr.hs.emirim.uuuuri.haegbook.Fragment.FifthInputFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.FirstInputFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.FourthInputFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.SecondInputFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.ThirdInputFragment;
import kr.hs.emirim.uuuuri.haegbook.Layout.CustomViewPager;
import kr.hs.emirim.uuuuri.haegbook.R;

public class AddScheduleActivity extends AppCompatActivity {
    private final int PAGE_COUNT = 5;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;

    private boolean isClick = false;

    private FirstInputFragment firstInputFragment;
    private SecondInputFragment secondInputFragment;
    private ThirdInputFragment thirdInputFragment;
    private FourthInputFragment fourthInputFragment;
    private FifthInputFragment fifthInputFragment;

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

        final TextView nextBtn = (TextView) findViewById(R.id.next_btn);
        final TextView previousBtn = (TextView) findViewById(R.id.previous_btn);
        previousBtn.setVisibility(View.INVISIBLE);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                switch (mViewPager.getCurrentItem()){
                    case 0:
                        if(firstInputFragment.saveData()){
                            // // TODO: 2017-11-05 DEBUG
                            secondInputFragment.getData();
                        }else{
                            Toast.makeText(getApplicationContext(), "입력바람", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case 1:
                        if(secondInputFragment.saveData()){
                            thirdInputFragment.getData();
                        }else{
                            Toast.makeText(getApplicationContext(), "입력바람", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case 2:
                        if(thirdInputFragment.saveData()){
                            // // TODO: 2017-11-05 DEBUG
                            fourthInputFragment.getData();
                        }else{
                            Toast.makeText(getApplicationContext(), "입력바람", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case 3:
                        if(fourthInputFragment.saveData()){
                            fifthInputFragment.getData();
                        }else{
                            Toast.makeText(getApplicationContext(), "입력바람", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;

                }
                if(mViewPager.getCurrentItem() >= 0 && mViewPager.getCurrentItem() < PAGE_COUNT )
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);

                if(mViewPager.getCurrentItem() == PAGE_COUNT - 1 && isClick){

                    fifthInputFragment.saveData();

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
                    firstInputFragment = new FirstInputFragment();
                    return firstInputFragment;
                case 1:
                    secondInputFragment = new SecondInputFragment();
                    return secondInputFragment;
                case 2:
                    thirdInputFragment = new ThirdInputFragment();
                    return thirdInputFragment;
                case 3:
                    fourthInputFragment = new FourthInputFragment();
                    return fourthInputFragment;
                case 4:
                    fifthInputFragment = new FifthInputFragment();
                    return fifthInputFragment;
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
