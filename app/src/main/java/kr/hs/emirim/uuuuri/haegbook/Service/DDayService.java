package kr.hs.emirim.uuuuri.haegbook.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import kr.hs.emirim.uuuuri.haegbook.Activity.MainActivity;
import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-22.
 */

public class DDayService extends Service {
    private final String TAG = "DDayService";
    private int mShowingDate;
    private NotificationManager nm;
    private NotificationCompat.Builder mCompatBuilder;
    private SharedPreferenceManager spm;
    private Timer timer;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate()");
        init();
        showCustomLayoutNotification();

        TimerTask task = new TimerTask()
        {
            private final Handler mHandler = new Handler(Looper.getMainLooper());

            @Override
            public void run()
            {
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Date now = new Date();
                        Log.e(TAG, "timer [ Hours : "+now.getHours()+" / Minutes : "+now.getMinutes()+" / Seconds : "+now.getSeconds() +"]");

                        if(now.getHours() == 0 && now.getMinutes() == 0 && now.getSeconds() == 0){
                            Log.e("TAG", "d-day 갱신 : "+mShowingDate+"->"+(mShowingDate-1));
                            updateUI(makeString(mShowingDate--));
                            spm.save(SharedPreferenceTag.D_Day, mShowingDate);
                        }

                        if(spm.retrieveInt(SharedPreferenceTag.D_Day) != mShowingDate){
                            mShowingDate = spm.retrieveInt(SharedPreferenceTag.D_Day);
                            updateUI(makeString(mShowingDate--));
                        }
                    }
                });
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000);

    }

    private void init() {
        spm = new SharedPreferenceManager(getApplicationContext());
        mShowingDate = spm.retrieveInt(SharedPreferenceTag.D_Day);

    }

    private NotificationCompat.Builder createNotification(){
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_dday);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_dday)
                .setLargeIcon(icon)
                .setContentTitle("StatusBar Title")
                .setContentText("StatusBar subTitle")
                .setSmallIcon(R.drawable.ic_notification_dday)
                .setAutoCancel(false)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_ALL);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder;
    }


    private void showCustomLayoutNotification(){
        mCompatBuilder = createNotification();

        //커스텀 화면 만들기
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.img, R.drawable.ic_notification_dday);
        remoteViews.setTextViewText(R.id.message, makeString(spm.retrieveInt(SharedPreferenceTag.D_Day)));
        remoteViews.setTextViewText(R.id.travel_name_tv, spm.retrieveString(SharedPreferenceTag.DDAY_TRAVEL_NAME));
        //노티피케이션에 커스텀 뷰 장착
        mCompatBuilder.setContent(remoteViews);
        mCompatBuilder.setContentIntent(createPendingIntent());

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(222, mCompatBuilder.build());

    }

    private PendingIntent createPendingIntent(){
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void updateUI(String text){
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.img, R.drawable.ic_notification_dday);
        remoteViews.setTextViewText(R.id.message, text);
        remoteViews.setTextViewText(R.id.travel_name_tv, spm.retrieveString(SharedPreferenceTag.DDAY_TRAVEL_NAME));
        //노티피케이션에 커스텀 뷰 장착
        mCompatBuilder.setContent(remoteViews);
        mCompatBuilder.setContentIntent(createPendingIntent());

    }

    private String makeString(int date) {
        String message = null;
        if(date == 0)
            message = "즐거운 여행 중이에요 :)";
        else if(date > 0)
            message = "여행 안간지 벌써 " + date +"일 째!";
        else if(date < 0)
            message = "여행까지 " +(-1*date) +"일!";

        return message;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (nm != null) {
            nm.cancel(222);
        }
        timer.cancel();
        this.stopSelf();


    }

}
