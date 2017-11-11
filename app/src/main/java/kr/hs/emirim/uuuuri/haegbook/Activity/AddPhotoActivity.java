package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Adapter.GalleryAdapter;
import kr.hs.emirim.uuuuri.haegbook.Interface.OnItemClickListener;
import kr.hs.emirim.uuuuri.haegbook.Manager.DateListAdapter;
import kr.hs.emirim.uuuuri.haegbook.Manager.GalleryManager;
import kr.hs.emirim.uuuuri.haegbook.Manager.GridDividerDecoration;
import kr.hs.emirim.uuuuri.haegbook.Model.GalleryImage;
import kr.hs.emirim.uuuuri.haegbook.R;

// TODO: 2017-11-11 long click -> 자세히
// TODO: 2017-11-11 click -> thumbnail list
// TODO: 2017-11-11 menu click -> add photodetailActivity -> upload
// TODO: 2017-11-11 spinner -> 날짜별로 view

// TODO: 2017-11-11 취소버튼 생성

public class AddPhotoActivity extends AppCompatActivity {
    private final String TAG = "AddPhotoActivity";
    private final int READ_EXTERNAL_STORAGE_CODE = 5;
    private GalleryManager mGalleryManager;

    private RecyclerView recyclerGallery;
    private Spinner mDateSpinner;
    private GalleryAdapter galleryAdapter;

    private String mBookCode;
    private String mPeriod;

    private ArrayList<String> dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);


        Intent intent = getIntent();
        mBookCode = intent.getStringExtra("BOOK_CODE");
        mPeriod = intent.getStringExtra("DATE");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한 없음
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "이미지를 불러오는데 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 권한 요청
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_CODE);
            }
        }else{
            // 권한이 있음
            initLayout();
            init();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLayout();
                    init();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_CODE);
                }
                return;
            }
        }
    }


    /**
     * 레이아웃 초기화
     */
    private void initLayout() {
        recyclerGallery = (RecyclerView) findViewById(R.id.recyclerview);
        findViewById(R.id.canceL_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDone();
            }
        });

        mDateSpinner = findViewById(R.id.date_spinner);

        DateListAdapter dateListAdapter = new DateListAdapter();
        Date[] dates = dateListAdapter.convertString(mPeriod);

        dateList = dateListAdapter.makeDateList(dates[0], dates[1]);

        String stringArray[] = new String[dateList.size()];
        stringArray = dateList.toArray(stringArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, stringArray);

        mDateSpinner.setAdapter(adapter);

    }

    /**
     * 데이터 초기화
     */
    private void init() {
        //갤러리 리사이클러뷰 초기화
        initRecyclerGallery();
    }


    /**
     * 갤러리 아미지 데이터 초기화
     */
    private List<GalleryImage> initGalleryPathList() {
        mGalleryManager = new GalleryManager(getApplicationContext());
        return mGalleryManager.getDatePhotoPathList(2017, 11, 11);
//        return mGalleryManager.getAllPhotoPathList();
    }

    /**
     * 확인 버튼 선택 시
     */
    private void selectDone() {
        // TODO: 2017-11-11 intent & putExtra
        List<GalleryImage> selectedPhotoList = galleryAdapter.getSelectedPhotoList();
        for (int i = 0; i < selectedPhotoList.size(); i++) {
            Log.e(TAG, ">>> selectedPhotoList   :  " + selectedPhotoList.get(i).getImgPath());
        }
    }


    /**
     * 갤러리 리사이클러뷰 초기화
     */
    private void initRecyclerGallery() {

        galleryAdapter = new GalleryAdapter(AddPhotoActivity.this, initGalleryPathList(), R.layout.item_photo);
        galleryAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerGallery.setAdapter(galleryAdapter);
        recyclerGallery.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerGallery.setItemAnimator(new DefaultItemAnimator());
        recyclerGallery.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.divider_recycler_gallery));
    }


    /**
     * 리사이클러뷰 아이템 선택시 호출 되는 리스너
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void OnItemClick(GalleryAdapter.PhotoViewHolder photoViewHolder, int position) {

            GalleryImage galleryImage = galleryAdapter.getmPhotoList().get(position);

            if(galleryImage.isSelected()){
                galleryImage.setSelected(false);
            }else{
                galleryImage.setSelected(true);
            }

            galleryAdapter.getmPhotoList().set(position, galleryImage);
            galleryAdapter.notifyDataSetChanged();

        }
    };

}
