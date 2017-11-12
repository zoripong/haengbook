package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.R;


// // TODO: 2017-11-12 sharedpreference에서 지정한 디렉토리명 가져오고 수정할 수 있게 한 다음 갱신까지..!!

public class TakePicturesActivity extends Activity {
    private final int CAMERA_PERMISSION_CODE = 10;
    private final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 20;

    private final int TAKE_CAMERA = 100;		// 카메라 인텐트 사용 시 onActivityResult에서 사용할 requestCode
    private final String TAG = "Arcanelux_CameraIntent";

    private Button btnCameraIntent;

    private EditText directoryNameEt;
    private TextView fileNameTv;

    private String path;
    private String folderName = "TEST5";
    private String folderPath ;
    private String fileName ="test";
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_pictures);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 권한 없음
            Log.e(TAG, "카메라 권한 없음");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this, "촬영을 하는데 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 권한 요청
                Log.e(TAG, "카메라 권한 요청");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_CODE);
            }
        }else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "외부저장소 쓰기권한 없음");

            // 권한 없음
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "파일 경로를 지정하는데 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 권한 요청
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        }else{

            directoryNameEt = (EditText) findViewById(R.id.directory_et);
            fileNameTv = (TextView) findViewById(R.id.file_name_tv);
            fileNameTv.setText(fileName);

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
                    folderName = directoryNameEt.getText().toString();
                    if(folderName.equals("")){
                        return;
                    }

                    path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    folderPath = path + "/DCIM/" + folderName;
                    filePath  = folderPath + "/" + fileName + ".jpg";


                            Intent intent = new Intent();
                            Camera mCamera = Camera.open();

                            Camera.Parameters parameters = mCamera.getParameters();
                            List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
                            // 카메라 SupportedPictureSize목록 출력 로그
                            // for(int i=0; i<sizeList.size(); i++){
                            // Size size = sizeList.get(i);
                            //	Log.e(TAG, "Width : " + size.width + ", Height : " + size.height);
                            // }
                            // 원하는 최적화 사이즈를 1280x720 으로 설정

                            Camera.Size size = getOptimalPictureSize(parameters.getSupportedPictureSizes(), 1280, 720);
                            Log.e(TAG, "Selected Optimal Size : (" + size.width + ", " + size.height + ")");
                            parameters.setPreviewSize(size.width, size.height);
                            parameters.setPictureSize(size.width, size.height);
                            mCamera.setParameters(parameters);
                            mCamera.release();


                            // 저장할 파일 설정
                            // 외부저장소 경로

                            // 파일 이름 지정
                            File file = new File(filePath);
                            Uri outputFileUri = Uri.fromFile(file);

                            // 카메라 작동시키는 Action으로 인텐트 설정, OutputFileURI 추가
                            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                            // requestCode지정해서 인텐트 실행
                            startActivityForResult(intent, TAKE_CAMERA);
                    }


            });
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == TAKE_CAMERA) {
                Log.e(TAG, "파일 저장 전");
                deleteLatestFromDCIM();

                // 카메라 찍기 액션 후, 지정된 파일을 비트맵으로 꺼내 이미지뷰에 삽입
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bm = BitmapFactory.decodeFile(filePath, options);

                try {

//					// 저장 폴더 지정 및 폴더 생성
                    File fileFolderPath = new File(folderPath);
                    if(!fileFolderPath.exists())
                        fileFolderPath.mkdir();

//					// 파일 이름 지정
                    File file = new File(filePath);
                    FileOutputStream fos = new FileOutputStream(file);

//					// 비트맵을 PNG방식으로 압축하여 저장
                    if (fos != null) {
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                    }

//					// 로그 및 토스트
                    String logMessage = "File Save Success, File : " + filePath;
                    Toast.makeText(getApplicationContext(), "파일 저장 성공 : "+filePath, Toast.LENGTH_LONG).show();
                    Log.e(TAG, logMessage);

                    MediaScannerConnection.scanFile(getApplicationContext(),
                            new String[]{file.getAbsolutePath()},
                            null,
                            new MediaScannerConnection.MediaScannerConnectionClient() {
                                @Override
                                public void onMediaScannerConnected() {

                                }

                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.e("File scan", "file:" + path + "was scanned successfully");
                                }
                            });


                    Log.e(TAG, "파일 저장 후");
                    deleteLatestFromDCIM();
                    TakePicturesActivity.this.finish();

                } catch (FileNotFoundException e)	{
                    e.printStackTrace();
                    Log.e(TAG, "File Save Failed");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "파일 저장 실패");
                }
            }
        }

    }


    // 지정한 해상도에 가장 최적화 된 카메라 캡쳐 사이즈 구해주는 함수
    private Camera.Size getOptimalPictureSize(List<Camera.Size> sizeList, int width, int height){
        Log.e(TAG, "getOptimalPictureSize, 기준 width,height : (" + width + ", " + height + ")");
        Camera.Size prevSize = sizeList.get(0);
        Camera.Size optSize = sizeList.get(1);
        for(Camera.Size size : sizeList){
            // 현재 사이즈와 원하는 사이즈의 차이
            int diffWidth = Math.abs((size.width - width));
            int diffHeight = Math.abs((size.height - height));

            // 이전 사이즈와 원하는 사이즈의 차이
            int diffWidthPrev = Math.abs((prevSize.width - width));
            int diffHeightPrev = Math.abs((prevSize.height - height));

            // 현재까지 최적화 사이즈와 원하는 사이즈의 차이
            int diffWidthOpt = Math.abs((optSize.width - width));
            int diffHeightOpt = Math.abs((optSize.height - height));

            // 이전 사이즈보다 현재 사이즈의 가로사이즈 차이가   적을 경우 && 현재까지 최적화 된 세로높이 차이보다 현재 세로높이 차이가 적거나 같을 경우에만 적용
            if(diffWidth < diffWidthPrev && diffHeight <= diffHeightOpt){
                optSize = size;
                Log.e(TAG, "가로사이즈 변경 / 기존 가로사이즈 : " + prevSize.width + ", 새 가로사이즈 : " + optSize.width);
            }
            // 이전 사이즈보다 현재 사이즈의 세로사이즈 차이가 적을 경우 && 현재까지 최적화 된 가로길이 차이보다 현재 가로길이 차이가 적거나 같을 경우에만 적용
            if(diffHeight < diffHeightPrev && diffWidth <= diffWidthOpt){
                optSize = size;
                Log.e(TAG, "세로사이즈 변경 / 기존 세로사이즈 : " + prevSize.height + ", 새 세로사이즈 : " + optSize.height);
            }

            // 현재까지 사용한 사이즈를 이전 사이즈로 지정
            prevSize = size;
        }
        Log.e(TAG, "결과 OptimalPictureSize : " + optSize.width + ", " + optSize.height);
        return optSize;
    }


    private void deleteLatestFromDCIM() {
        // // TODO: 2017-11-12 : 어떤파일들은 저장되고 어떤 파일들은 저장이 안된다.
        Log.e(TAG, "deleteLatestFromDCIM");
        File f = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera"); // 기본 카메라의 경우

        File [] files = f.listFiles();
        for(int i = 0; i<files.length; i++){
            Log.e(TAG, files[i].getPath()+"/ last Modified : "+files[i].lastModified());
        }

        Arrays.sort( files, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {

                if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                    return -1;
                } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        for(int i = 0; i<files.length; i++)
            Log.e(TAG, "sort 후 : "+files[i]);


//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        // uri = Video.Media.EXTERNAL_CONTENT_URI;
//        // 영상일 경우
//
//        String selection = MediaStore.Images.Media.DATA + " = ?";
//        String[] selectionArgs = {
//                files[0].getPath()
//        }; // 실제 파일의 경로
//        ContentResolver resolver = getContentResolver();
//        int count = resolver.delete(uri, selection, selectionArgs);
//
//        Log.e(TAG, "table 삭제 행 : "+count);
//
//        if (files[0].exists()){
//            Log.e(TAG, "삭제합니다. 뿅 : "+files[0].getPath());
//            files[0].delete();
//        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                }
                return;
            }
            case CAMERA_PERMISSION_CODE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_CODE);
                }
                return;

            }
        }
    }



}
