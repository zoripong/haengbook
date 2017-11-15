package kr.hs.emirim.uuuuri.haegbook.Manager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 유리 on 2017-11-15.
 */
// TODO: 2017-11-15 이미지 로테이션
public class CameraManager {
    private Activity nowActivity;

    private final int CAMERA_PERMISSION_CODE = 10;
    private final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 20;
    private final int TAKE_CAMERA = 100;
    private final String TAG = "CameraManager";

    private String folderPath;
    private String filePath;
    private SharedPreferenceManager spm;

    public CameraManager(){
        makeFullPath();
    }

    public CameraManager(Activity activity){
        nowActivity = activity;
        spm = new SharedPreferenceManager(activity);
        makeFullPath();
    }

    public String getFullPath(){
        return filePath;
    }

    private void makeFullPath(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd__HHmmss").format(new Date());
        String fileName = "JPEG_"+timeStamp;
        String mBasicPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        folderPath = mBasicPath + "/DCIM/HaengBook/" + spm.retrieveString(SharedPreferenceTag.DEFAULT_DIRECTORY);
        filePath  = folderPath + "/" + fileName + ".jpg";
    }

    private void makeFullPath(String folderPath){
        String timeStamp = new SimpleDateFormat("yyyyMMdd__HHmmss").format(new Date());
        String fileName = "JPEG_"+timeStamp;
        String mBasicPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        folderPath = mBasicPath + "/DCIM/HaengBook/" + folderPath;
        filePath  = folderPath + "/" + fileName + ".jpg";
    }


    public void intentCamera(String folderPath){
        makeFullPath(folderPath);
        intentCamera();
    }

    public void intentCamera(){
        if(nowActivity == null)
            return;

        Intent intent = new Intent();
        Camera mCamera = Camera.open();

        Camera.Parameters parameters = mCamera.getParameters();
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
        nowActivity.startActivityForResult(intent, TAKE_CAMERA);
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

    private int deleteOverlap(){
        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
        Cursor imageCursor = nowActivity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        if(imageCursor.moveToFirst()){
            int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d(TAG, "getLastImageId::id " + id);
            Log.d(TAG, "getLastImageId::path " + fullPath);
            imageCursor.close();

            if(!fullPath.contains("/DCIM/HaengBook/")) {
                Log.e(TAG, "중복된 파일 삭제 됨 [file path : "+fullPath+" ]");
                ContentResolver cr = nowActivity.getContentResolver();
                cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?", new String[]{Long.toString(id)});
                return 1;
            }

            return 0;

        }else{
            return 0;
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data){
        if(nowActivity == null)
            return;

        if(resultCode == RESULT_OK){
            if(requestCode == TAKE_CAMERA) {
                Log.e(TAG, "파일 저장 전");

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

                    if(deleteOverlap() == 0){
                        Toast.makeText(nowActivity, "중복 삭제 없음", Toast.LENGTH_SHORT).show();
                    }

//					// 로그 및 토스트
                    String logMessage = "File Save Success, File : " + filePath;
                    Toast.makeText(nowActivity, "파일 저장 성공 : "+filePath, Toast.LENGTH_LONG).show();
                    Log.e(TAG, logMessage);

                    MediaScannerConnection.scanFile(nowActivity,
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
                    nowActivity.finish();


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



}
