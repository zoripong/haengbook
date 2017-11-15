package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Manager.CameraManager;
import kr.hs.emirim.uuuuri.haegbook.R;

// TODO: 2017-11-15 타임스탬프


// // TODO: 2017-11-12 sharedpreference에서 지정한 디렉토리명 가져오고 수정할 수 있게 한 다음 갱신까지..!!

public class TakePicturesActivity extends Activity {
    private final String SERVICE_TAG = "service tag";
    private final String TAG = "CameraManager";

    private Button btnCameraIntent;
    private TextView fileNameTv;
    private CameraManager cm;

    PermissionListener permissionlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_pictures);

        cm = new CameraManager(TakePicturesActivity.this);
        fileNameTv = (TextView) findViewById(R.id.file_name_tv);
        fileNameTv.setText(cm.getFullPath());

        // 뷰 변수 할당
        btnCameraIntent = (Button) findViewById(R.id.btnCameraIntent);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePicturesActivity.this.finish();
            }
        });

        // 버튼 클릭리스터 설정
        btnCameraIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TedPermission.with(TakePicturesActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("권한이 없을 경우, 카메라 기능을 사용 할 수 없습니다.\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .check();

            }
        });


        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                cm.intentCamera();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(TakePicturesActivity.this, "권한이 없을 경우, 카메라 기능을 사용 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }


        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cm.activityResult(requestCode, resultCode, data);
    }


}
