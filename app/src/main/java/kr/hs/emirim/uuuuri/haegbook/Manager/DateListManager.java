package kr.hs.emirim.uuuuri.haegbook.Manager;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 유리 on 2017-11-11.
 */


public class DateListManager {
    private final String TAG = "DateListManager";
    ArrayList<String> dateList;

    public DateListManager(){
        dateList = new ArrayList<>();
    }

    public Date[] convertDates(String period){
        String[] stringDate = period.split("-");

        Date[] dates = new Date[2];

        dates[0] = convertDate(stringDate[0]);
        dates[1] = convertDate(stringDate[1]);

        return dates;
    }

    public Date convertDate(String period){
        String dateInfo[] = period.split("\\.");
        return new Date(Integer.parseInt(dateInfo[0])-1900, Integer.parseInt(dateInfo[1])-1, Integer.parseInt(dateInfo[2]), 0, 0, 0);
    }

    public ArrayList<String> makeDateList(Date beginDate, Date endDate){

        Date today = new Date();

        long diff;
        diff = endDate.getTime() - beginDate.getTime();
        Log.e(TAG, "원래 diff는 이렇게 나와야 한다 : " + diff +"/ diffDays : " +((int)diff/(24*60*60*1000)));
        if(today.before(endDate)){
            diff = today.getTime() - beginDate.getTime();
            Log.e(TAG, "아직 여행 끝나기 전이랍니다. diff : "+diff);
        }else{
            diff = endDate.getTime() - beginDate.getTime();
            Log.e(TAG, "여행이 끝났답니다. diff : "+diff


            );
        }

        int diffDays = (int)(diff / (24 * 60 * 60 * 1000));
        Log.e(TAG, "diffDays : "+diffDays);

        final ArrayList<String> stringList = new ArrayList<>();

        SimpleDateFormat dt = new SimpleDateFormat("yyyy.MM.dd");
        for(int i = 0; i<=diffDays; i++){
            Log.e(TAG, i+" : "+dt.format(beginDate));
            stringList.add(dt.format(beginDate));
            dateList.add(dt.format(beginDate));//영수증 추가 다이얼로그에 띄울 날짜 리스트
            beginDate.setTime(beginDate.getTime()+(24 * 60 * 60 * 1000));

        }

        return stringList;
    }

}
