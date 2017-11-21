package kr.hs.emirim.uuuuri.haegbook.Notification;

/**
 * Created by 유리 on 2017-10-19.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.hs.emirim.uuuuri.haegbook.Activity.MainActivity;
import kr.hs.emirim.uuuuri.haegbook.Activity.TravelDetailActivity;
import kr.hs.emirim.uuuuri.haegbook.R;

public class AlarmReceiver extends BroadcastReceiver {
    private final String EXTRA_TITLE = "EXTRA_TITLE";
    private final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private final String NOTIFY_ID = "NOTIFY_ID";
    private final String NOTIFY_TIME ="NOTIFY_TIME";

    Context context;

    private int mCount;
    private String mTitle;
    private String mMessage;
    private int mNotifyId;
    private String mTime;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        mNotifyId =intent.getExtras().getInt(NOTIFY_ID);
        mTime =intent.getExtras().getString(NOTIFY_TIME);

        mTitle = intent.getExtras().getString(EXTRA_TITLE);
        mMessage = intent.getExtras().getString(EXTRA_MESSAGE);

        long nowTime = System.currentTimeMillis();
        Date date = new Date(nowTime);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String getNowTime = timeFormat.format(date);

        if(checkTime(mTime,getNowTime)) {
            wakeLock.release();
            notification();
        }
    }

    void notification(){
        Intent intent = new Intent();
        switch (mNotifyId){
            case 0:
                intent = new Intent(context,MainActivity.class);
                break;
            case 1:case 2:
                intent = new Intent(context,TravelDetailActivity.class);
                break;
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(context, mNotifyId, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(mTitle)
                .setContentText(mMessage)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mNotifyId, notifiBuilder.build());
    }
    public boolean checkTime(String time,String compareTime) {

        try{
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date Date = format.parse(time);
            Date compareDate = format.parse(compareTime);


            long calDate = Date.getTime() - compareDate.getTime();

            long minute = calDate / 60000;

            if(minute == (long)0) return true;

        } catch (ParseException e) {
            Log.e("날짜 파싱에러","서로 타입이 맞지 않음.");
        }
        return false;

    }

}