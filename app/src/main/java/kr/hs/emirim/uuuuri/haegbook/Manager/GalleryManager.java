package kr.hs.emirim.uuuuri.haegbook.Manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Model.GalleryImage;

public class GalleryManager {
    private final String TAG = "GalleryManager";

    private Context mContext;

    public GalleryManager(Context context) {
        mContext = context;
    }


    /**
     * 갤러리 이미지 반환
     *
     * @return
     */
    public List<GalleryImage> getAllPhotoPathList() {

        ArrayList<GalleryImage> photoList = new ArrayList<>();

        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {

            GalleryImage galleryImage = new GalleryImage(cursor.getString(columnIndexData),false);
            photoList.add(galleryImage);
        }

        cursor.close();

        return photoList;
    }


    /**
     * 날짜별 갤러리 이미지 반환
     *
     * @return
     */
    public List<GalleryImage> getDatePhotoPathList(int year, int month, int day) {

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(year, month-1, day, 0, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(year, month-1, day, 24, 0);

        String startTime = String.valueOf(startCalendar.getTimeInMillis()).substring(0, 10);
        String endTime = String.valueOf(endCalendar.getTimeInMillis()).substring(0, 10);

        Log.e(TAG, startTime);
        Log.e(TAG, endTime);

        ArrayList<GalleryImage> photoList = new ArrayList<>();

        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        };

        String selection = MediaStore.Images.Media.DATE_ADDED + " >= " + startTime + " AND "
                         + MediaStore.Images.Media.DATE_ADDED + " <= " + endTime;

        Cursor cursor = mContext.getContentResolver().query(uri, projection, selection, null, null);

        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            GalleryImage galleryImage = new GalleryImage(cursor.getString(columnIndexData),false);
            Log.e(TAG, galleryImage.toString());
            photoList.add(galleryImage);
        }
        cursor.close();
        return photoList;
    }


}
