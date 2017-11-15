package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import kr.hs.emirim.uuuuri.haegbook.Manager.CameraManager;
import kr.hs.emirim.uuuuri.haegbook.R;

public class TakePicturesForServiceActivity extends Activity {
    private final String FILE_PATH_EXTRA = "FILE PATH EXTRA";

    private CameraManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_pictures_for_service);

        Intent intent = getIntent();
        String directory = intent.getStringExtra(FILE_PATH_EXTRA);

        cm = new CameraManager(this);
        cm.intentCamera(directory);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cm.activityResult(requestCode, resultCode, data);
    }
}
