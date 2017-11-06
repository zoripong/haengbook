package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import kr.hs.emirim.uuuuri.haegbook.Fragment.PhotoFragment;
import kr.hs.emirim.uuuuri.haegbook.Fragment.ReceiptFragment;
import kr.hs.emirim.uuuuri.haegbook.Interface.SelectedFragment;
import kr.hs.emirim.uuuuri.haegbook.R;

public class TravelDetailActivity extends AppCompatActivity implements SelectedFragment {
    private final String LOG = "TRAVEL_DETAIL_ACTIVITY";
    private final int TAB_COUNT = 2;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private int mPosition = PHOTO; // DEFAULT PAGE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);

        Intent intent = getIntent();
        String code = intent.getStringExtra("BOOK_CODE");
        Toast.makeText(getApplicationContext(), code, Toast.LENGTH_SHORT).show();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                mPosition = tab.getPosition();
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG, String.valueOf(mPosition));
                Intent intent;
                switch (mPosition){
                    case PHOTO:
                        intent = new Intent(TravelDetailActivity.this, AddPhotoActivity.class);
                        break;
                    case RECEIPT:
                        intent = new Intent(TravelDetailActivity.this, AddReceiptActivity.class);
                        break;
                    default:
                        intent = null;
                        break;
                }

                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_travel_detail, menu);
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
                    return new PhotoFragment();
                case RECEIPT:
                    return new ReceiptFragment();
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
}
