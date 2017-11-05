package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.content.SharedPreferences;

import kr.hs.emirim.uuuuri.haegbook.Interface.ScheduleTag;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 유리 on 2017-11-05.
 */

public class SharedPreferenceManager {
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    public SharedPreferenceManager(Activity activity){
        sharedpreferences = activity.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

    }

    public void save(String tag, String value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(tag, value);
        editor.commit();
    }

    public void save(String tag, int value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(tag, value);
        editor.commit();
    }

    public void save(String tag, boolean value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(tag, value);
        editor.commit();
    }

    public String retrieveString(String tag){
        return sharedpreferences.getString(tag, null);
    }

    public int retrieveInt(String tag){
        return sharedpreferences.getInt(tag, -1);
    }

    public boolean retrieveBoolean(String tag){
        return sharedpreferences.getBoolean(tag, false);
    }

    public void resetData(){
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(ScheduleTag.TITLE_TAG, "");
        editor.putString(ScheduleTag.LOCATION_TAG, "");
        editor.putString(ScheduleTag.START_DATE_TAG, "");
        editor.putString(ScheduleTag.END_DATE_TAG, "");
        editor.putInt(ScheduleTag.FOREIGN_MONEY_TAG, 0);
        editor.putInt(ScheduleTag.KOR_MONEY_TAG, 0);

        editor.commit();
    }

}

