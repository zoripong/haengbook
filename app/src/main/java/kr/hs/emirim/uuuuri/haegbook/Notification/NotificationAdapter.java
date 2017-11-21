package kr.hs.emirim.uuuuri.haegbook.Notification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import kr.hs.emirim.uuuuri.haegbook.Interface.NotificationTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;

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
    SharedPreferenceManager spm;

    private boolean isNotify;

    String time;

    public NotificationAdapter(Activity mActivity, Context mContext) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        mNotiCount = 0;
        alarmManager = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);
        spm = new SharedPreferenceManager(mActivity);
    }

    public void setNotification(int notifyId, String title, String message) {
        Intent intent = new Intent(mActivity, AlarmReceiver.class);
        isNotify = spm.retrieveBoolean(NotificationTag.NOTIFICATION_SET_TAG);

        if (isNotify) {
            switch (notifyId){
                case 0://시작
                    time = spm.retrieveString(NotificationTag.START_TIME_TAG);
                    break;
                case 1://끝
                    time = spm.retrieveString(NotificationTag.FINISH_TIME_TAG);
                    break;
            }
            String[] notifyTime=time.split(":");

            intent.putExtra(EXTRA_TITLE, title);
            intent.putExtra(EXTRA_MESSAGE, message);
            intent.putExtra(EXTRA_COUNT, mNotiCount++);
            PendingIntent servicePending = PendingIntent.getBroadcast(mActivity, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.e("TAG", servicePending.toString());
            Calendar calendar = Calendar.getInstance();

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), Integer.parseInt(notifyTime[0]), Integer.parseInt(notifyTime[1]), 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), servicePending);
        }
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

