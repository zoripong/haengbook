package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Adapter.GalleryAdapter;
import kr.hs.emirim.uuuuri.haegbook.Interface.OnItemClickListener;
import kr.hs.emirim.uuuuri.haegbook.Manager.GalleryManager;
import kr.hs.emirim.uuuuri.haegbook.Manager.GridDividerDecoration;
import kr.hs.emirim.uuuuri.haegbook.Model.GalleryImage;
import kr.hs.emirim.uuuuri.haegbook.R;

public class AddPhotoActivity extends AppCompatActivity {
    private final String TAG = "AddPhotoActivity";
    private final int READ_EXTERNAL_STORAGE_CODE = 5;
    private GalleryManager mGalleryManager;

    private RecyclerView recyclerGallery;
    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_done:
                selectDone();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 레이아웃 초기화
     */
    private void initLayout() {
        recyclerGallery = (RecyclerView) findViewById(R.id.recyclerview);
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
        List<GalleryImage> selectedPhotoList = galleryAdapter.getSelectedPhotoList();
        for (int i = 0; i < selectedPhotoList.size(); i++) {
            Log.i(TAG, ">>> selectedPhotoList   :  " + selectedPhotoList.get(i).getImgPath());
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
