package kr.hs.emirim.uuuuri.haegbook.Notification;

import android.app.Activity;
import android.content.Context;

import kr.hs.emirim.uuuuri.haegbook.Interface.CurrencyTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.TravelDetailTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;

/**
 * Created by doori on 2017-11-21.
 */

public class NotificationMessage {

    SharedPreferenceManager spm;

    public NotificationMessage(Context context){
        spm= new SharedPreferenceManager((Activity) context);

    }

    public String getFinishNotificationMessage(){
        float restMoney = spm.retrieveFloat(TravelDetailTag.REST_MONEY_TAG);
        float rate = spm.retrieveFloat(CurrencyTag.CHOOSE_CURRENCY_TAG);
        String symbol = spm.retrieveString(CurrencyTag.CURRENCY_SYMBOL_TAG);
        boolean isKorea = spm.retrieveBoolean(SharedPreferenceTag.IS_KOR_TAG);
        if(isKorea){
            return "여행 경비는 "+restMoney+"\uFFE6 남았습니다.";

        }
        return "여행 경비는 "+restMoney+"\uFFE6 , "+restMoney*rate + symbol+" 남았습니다.";


    }
    public String getStartNotificationMessage(){

        float[] typeMoney = new float[5];

        typeMoney[0] = spm.retrieveFloat(TravelDetailTag.FOOD_MONEY_TAG);
        typeMoney[1] =  spm.retrieveFloat(TravelDetailTag.TRAFFIC_MONEY_TAG);
        typeMoney[2] = spm.retrieveFloat(TravelDetailTag.SHOPPING_MONEY_TAG);
        typeMoney[3] =  spm.retrieveFloat(TravelDetailTag.GIFT_MONEY_TAG);
        typeMoney[4] =  spm.retrieveFloat(TravelDetailTag.CULTURE_MONEY_TAG);

        float typeMin = typeMoney[0];
        int minIndex=0;

        for(int i=1; i < typeMoney.length ; i++) {

            if (typeMoney[i] < typeMin ) {
                typeMin = typeMoney[i];
                minIndex=i;
            }

        }

        String[] typeName={"음식","교통","쇼핑","기념품","문화"};
        return "오늘은 "+typeName[minIndex]+"에 돈을 써보시는 건 어떠세요?";

    }
}
