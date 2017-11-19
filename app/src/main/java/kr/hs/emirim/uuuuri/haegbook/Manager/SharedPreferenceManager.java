package kr.hs.emirim.uuuuri.haegbook.Manager;

import android.app.Activity;
import android.content.SharedPreferences;

import kr.hs.emirim.uuuuri.haegbook.Interface.CurrencyTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.NotificationTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.TravelDetailTag;

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

    public void save(String tag, float value){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putFloat(tag, value);
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

    public float retrieveFloat(String tag){
        return sharedpreferences.getFloat(tag, 0.0f);
    }

    public boolean retrieveBoolean(String tag){
        return sharedpreferences.getBoolean(tag, false);
    }

    public void resetData(){
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(SharedPreferenceTag.TITLE_TAG, "");
        editor.putString(SharedPreferenceTag.LOCATION_TAG, "");
        editor.putString(SharedPreferenceTag.START_DATE_TAG, "");
        editor.putString(SharedPreferenceTag.END_DATE_TAG, "");
        editor.putFloat(SharedPreferenceTag.FOREIGN_MONEY_TAG, 0.0f);
        editor.putFloat(SharedPreferenceTag.KOR_MONEY_TAG, 0.0f);

        //환율
        editor.putFloat(CurrencyTag.CHOOSE_CURRENCY_TAG, 0.0f);
        editor.putString(CurrencyTag.CURRENCY_SYMBOL_TAG, "");
        editor.putString(CurrencyTag.CURRENCY_COUNTRY_TAG, "");


        //알람 시작시간, 종료 시간
        editor.putBoolean(NotificationTag.NOTIFICATION_SET_TAG, true);
        editor.putString(NotificationTag.START_TIME_TAG, "06:00");
        editor.putString(NotificationTag.FINISH_TIME_TAG, "22:00");


        editor.putString(TravelDetailTag.CARD_BOOK_CODE_TAG, "");
        editor.putString(TravelDetailTag.IS_PUBLISHING_TAG, "");

        //잡아놓은 한국돈
        editor.putFloat(TravelDetailTag.TOTAL_KOREA_MONEY_TAG, 0.0f);
        //남은돈
        editor.putFloat(TravelDetailTag.REST_MONEY_TAG, 0.0f);

        editor.putFloat(TravelDetailTag.FOOD_MONEY_TAG, 0.0f);
        editor.putFloat(TravelDetailTag.TRAFFIC_MONEY_TAG, 0.0f);
        editor.putFloat(TravelDetailTag.SHOPPING_MONEY_TAG, 0.0f);
        editor.putFloat(TravelDetailTag.GIFT_MONEY_TAG, 0.0f);
        editor.putFloat(TravelDetailTag.CULTURE_MONEY_TAG, 0.0f);
        editor.putFloat(TravelDetailTag.ETC_MONEY_TAG, 0.0f);

        editor.putInt(TravelDetailTag.FOOD_RATE_TAG, -1);
        editor.putInt(TravelDetailTag.TRAFFIC_RATE_TAG, -1);
        editor.putInt(TravelDetailTag.SHOPPING_RATE_TAG, -1);
        editor.putInt(TravelDetailTag.GIFT_RATE_TAG, -1);
        editor.putInt(TravelDetailTag.CULTURE_RATE_TAG, -1);
        editor.putInt(TravelDetailTag.ETC_RATE_TAG, -1);

        editor.commit();
    }

}

