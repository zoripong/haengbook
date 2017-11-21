package kr.hs.emirim.uuuuri.haegbook.Notification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by 유리 on 2017-10-19.
 */

public class NotificationAdapter {
    private final String EXTRA_TITLE = "EXTRA_TITLE";
    private final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private final String EXTRA_COUNT = "EXTRA_COUNT";

    AlarmManager alarmManager;
    private int mNotiCount;
    Activity mActivity;
    Context mContext;

    public NotificationAdapter(Activity mActivity, Context mContext) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        mNotiCount = 0;
        alarmManager = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);

    }

    public void setNotification(int notifyId, String title, String message,int hour, int min){
        Intent intent = new Intent(mActivity, AlarmReceiver.class);

        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_COUNT, mNotiCount++);
        PendingIntent servicePending = PendingIntent.getBroadcast(mActivity, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e("TAG", servicePending.toString());
        Calendar calendar = Calendar.getInstance();

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour, min, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), servicePending);
        Log.e("TAG", "알람 설정"+hour+":"+min);
    }

    public void cancelNotification(int notifyId) {
        Intent intent = new Intent(mActivity, AlarmReceiver.class);

        PendingIntent checkIntent = PendingIntent.getBroadcast(mActivity, notifyId, intent, PendingIntent.FLAG_NO_CREATE);
        boolean result = (checkIntent == null);
        if(!result){
            PendingIntent cancelIntent = PendingIntent.getBroadcast(mActivity, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(cancelIntent);
            cancelIntent.cancel();
        }


    }
}

