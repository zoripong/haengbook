package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Switch;

import com.gun0912.tedpermission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Adapter.ExpandableListAdapter;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.R;

// settingsActivity -> floatingviewservice -> takepicturesForService
public class SettingActivity extends AppCompatActivity {
    private final String FILE_PATH_EXTRA = "FILE PATH EXTRA";
    private final String TAG = "SettingActivity";

    private Switch floatingWidgetSwitch;
    private SharedPreferenceManager spm;

    private Intent mServiceIntent;

    PermissionListener permissionlistener;

    private RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<ExpandableListAdapter.Item> data = new ArrayList<>();

        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "D-Day"));
        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Notification"));
        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "시작 시간 설정"));
        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "종료 시간 설정"));
        ExpandableListAdapter.Item places = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "카메라 플로팅 위젯");
        data.add(places);




        recyclerview.setAdapter(new ExpandableListAdapter(data));



//        floatingWidgetSwitch = (Switch) findViewById(R.id.floating_widget_switch);
//        floatingWidgetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if(isChecked) {
//                    if(spm.retrieveBoolean(SharedPreferenceTag.IS_TRAVELING_TAG)) {
//
//                        TedPermission.with(SettingActivity.this)
//                                .setPermissionListener(permissionlistener)
//                                .setDeniedMessage("권한이 없을 경우, 플로팅 위젯 기능을 사용 할 수 없습니다.\n\nPlease turn on permissions at [Setting] > [Permission]")
//                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.SYSTEM_ALERT_WINDOW)
//                                .check();
//                    }else{
//                        Toast.makeText(SettingActivity.this, "여행중에만 이용가능한 서비스 입니다.", Toast.LENGTH_SHORT).show();
//                        floatingWidgetSwitch.setChecked(false);
//                    }
//                }else{
//                    if(mServiceIntent!=null) {
//                        stopService(mServiceIntent);
//                        mServiceIntent = null;
//                    }
//                }
//            }
//        });


    }
}
