package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.CameraManager;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.R;
import kr.hs.emirim.uuuuri.haegbook.Service.FloatingViewService;

public class TakePicturesForServiceActivity extends Activity {
    private final String TAG = "TakePicturesForServiceActivity";
    private final String FILE_PATH_EXTRA = "FILE PATH EXTRA";

    private CameraManager cm;
    SharedPreferenceManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_pictures_for_service);

        spm = new SharedPreferenceManager(TakePicturesForServiceActivity.this);

        Intent intent = getIntent();
        final String directory = intent.getStringExtra(FILE_PATH_EXTRA);
        if(directory != null) {
            cm = new CameraManager(this);
            cm.intentCamera(directory);
        }else{
            /*
            Dialog selectDialog = new Dialog(MainActivity.this, R.style.MyDialog);
                selectDialog.setContentView(R.layout.dialog_select);
                selectDialog.show();
             */

            final Dialog updateDialog = new Dialog(TakePicturesForServiceActivity.this, R.style.MyDialog);
            updateDialog.setContentView(R.layout.dialog_update_path);
            updateDialog.show();
            updateDialog.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateDialog.dismiss();
                    TakePicturesForServiceActivity.this.finish();
                }
            });
            updateDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        TakePicturesForServiceActivity.this.finish();
                        updateDialog.dismiss();

                        TakePicturesForServiceActivity.this.finish();
                        Intent mServiceIntent = new Intent(TakePicturesForServiceActivity.this, FloatingViewService.class);
                        String path = spm.retrieveString(SharedPreferenceTag.DEFAULT_DIRECTORY);
                        Log.e(TAG, path);
                        mServiceIntent.putExtra(FILE_PATH_EXTRA, path);
                        startService(mServiceIntent);

                    }
                    return true;
                }
            });

            updateDialog.findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newPath = ((EditText)updateDialog.findViewById(R.id.update_path_et)).getText().toString();

                    if(newPath.contains("DCIM/HaengBook")){
                        Toast.makeText(TakePicturesForServiceActivity.this, "사용불가한 경로입니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        spm.save(SharedPreferenceTag.DEFAULT_DIRECTORY, newPath);


                    }
                    updateDialog.dismiss();
                    TakePicturesForServiceActivity.this.finish();
                    Intent mServiceIntent = new Intent(TakePicturesForServiceActivity.this, FloatingViewService.class);
                    String path = spm.retrieveString(SharedPreferenceTag.DEFAULT_DIRECTORY);
                    Log.e(TAG, path);
                    mServiceIntent.putExtra(FILE_PATH_EXTRA, path);
                    startService(mServiceIntent);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cm.activityResult(requestCode, resultCode, data);
    }
}
