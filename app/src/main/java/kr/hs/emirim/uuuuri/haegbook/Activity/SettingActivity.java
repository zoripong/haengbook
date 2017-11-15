package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.R;
import kr.hs.emirim.uuuuri.haegbook.Widget.FloatingViewService;

// settingsActivity -> floatingviewservice -> takepicturesForService
public class SettingActivity extends AppCompatActivity {
    private final String FILE_PATH_EXTRA = "FILE PATH EXTRA";
    private final String TAG = "SettingActivity";

    private Switch floatingWidgetSwitch;
    private SharedPreferenceManager spm;

    PermissionListener permissionlistener;
    private Intent mServiceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        spm = new SharedPreferenceManager(this);

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                mServiceIntent = new Intent(SettingActivity.this, FloatingViewService.class);
                String path = spm.retrieveString(SharedPreferenceTag.DEFAULT_DIRECTORY);
                Log.e(TAG, path);
                mServiceIntent.putExtra(FILE_PATH_EXTRA, path);
                startService(mServiceIntent);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(SettingActivity.this, "권한이 없을 경우, 플로팅 위젯 기능을 사용 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }


        };

        floatingWidgetSwitch = (Switch) findViewById(R.id.floating_widget_switch);
        floatingWidgetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    if(spm.retrieveBoolean(SharedPreferenceTag.IS_TRAVELING_TAG)) {

                        TedPermission.with(SettingActivity.this)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage("권한이 없을 경우, 플로팅 위젯 기능을 사용 할 수 없습니다.\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.SYSTEM_ALERT_WINDOW)
                                .check();
                    }else{
                        Toast.makeText(SettingActivity.this, "여행중에만 이용가능한 서비스 입니다.", Toast.LENGTH_SHORT).show();
                        floatingWidgetSwitch.setChecked(false);
                    }
                }else{
                    if(mServiceIntent!=null) {
                        stopService(mServiceIntent);
                        mServiceIntent = null;
                    }
                }
            }
        });


    }
}
