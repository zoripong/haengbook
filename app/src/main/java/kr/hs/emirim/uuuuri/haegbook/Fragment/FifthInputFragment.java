package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Model.CardBook;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-04.
 */

// TODO: 2017-11-04 seekbar : 민주
public class FifthInputFragment extends Fragment{

    private FirebaseDatabase mDatabase;


    private String mCardBookTitle;
    private String mCardBookLocation;
    private String mCardBookStartDate;
    private String mCardBookEndDate;
    private String mUserToken;




    private int mMoneyRate[]={20,10,10,20,32,8};

    private Float mCardBookKorMoney = 0.0f;
    private Float mCardBookForeignMoney = 0.0f;

    public FifthInputFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fifth_input, container, false);

        return rootView;
    }

    public void getData(){
        if(getActivity() == null)
            return;
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());

        mCardBookTitle=spm.retrieveString(SharedPreferenceTag.TITLE_TAG);
        mCardBookLocation = spm.retrieveString(SharedPreferenceTag.LOCATION_TAG);
        mCardBookStartDate = spm.retrieveString(SharedPreferenceTag.START_DATE_TAG);
        mCardBookEndDate = spm.retrieveString(SharedPreferenceTag.END_DATE_TAG);
        mUserToken=spm.retrieveString(SharedPreferenceTag.USER_TOKEN_TAG);

        mCardBookKorMoney = spm.retrieveFloat(SharedPreferenceTag.KOR_MONEY_TAG);
        mCardBookForeignMoney = spm.retrieveFloat(SharedPreferenceTag.FOREIGN_MONEY_TAG);
    }

    public boolean saveData(){
        //카드 업데이트
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference("BookInfo");

        final String postKey=ref.push().getKey();
        DatabaseReference cardRef = ref.child(postKey);

        Map<String, CardBook> cardBooks = new HashMap<String, CardBook>();
        Map<String, Object> showing = new HashMap<String, Object>();

        String period=mCardBookStartDate+"-"+mCardBookEndDate;
        cardBooks.put("Registration", new CardBook(period,mCardBookLocation,mCardBookTitle,"url"));
        showing.put("isShowing",new Boolean(checkIsShowing()));
        cardRef.setValue(cardBooks);
        cardRef.updateChildren(showing);

        DatabaseReference moneyRef =  mDatabase.getReference("TravelMoney/"+postKey);

        Map<String, Object> rateMap = new HashMap<String, Object>();
        for(int i=0;i<mMoneyRate.length;i++) {
            rateMap.put(String.valueOf(i+1),new Float(mMoneyRate[i]));
        }
        moneyRef.child("Rate").updateChildren(rateMap);

        Map<String, Object> moneyMap = new HashMap<String, Object>();
        Log.e("ㄴㅇㄹ","찍혀라");

        for(int i=0;i<mMoneyRate.length;i++) {
            Log.e("d?", String.valueOf(new Float((float) mMoneyRate[i]/100 * mCardBookKorMoney)));
            moneyMap.put(String.valueOf(i+1),new Float(0));
        }
        moneyRef.child("Money").updateChildren(moneyMap);

        Map<String, Object> totalMoneyMap = new HashMap<String, Object>();
        totalMoneyMap.put("korea",new Float(mCardBookKorMoney));
        totalMoneyMap.put("foreign",new Float(mCardBookForeignMoney));
        totalMoneyMap.put("restKorea",new Float(mCardBookKorMoney));
        moneyRef.child("Total").updateChildren(totalMoneyMap);

        //uid에 카드 업데이트

        final DatabaseReference tokenRef = mDatabase.getReference("UserInfo/"+mUserToken);

        tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            long keyIndex;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                keyIndex = dataSnapshot.getChildrenCount();
                Log.e("아이들 갯수", String.valueOf(keyIndex));
                Map<String, Object> haveCardBookUpdates = new HashMap<String, Object>();
                Log.e("카드 인덱스", String.valueOf(keyIndex+1));
                haveCardBookUpdates.put(String.valueOf(keyIndex+1), postKey);
                tokenRef.updateChildren(haveCardBookUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        spm.resetData();
        return true;
    }

    public boolean checkIsShowing() {

        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            Date today = new Date();
            Date startDate = format.parse(mCardBookStartDate);

            long calDate = today.getTime() - startDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / ( 24*60*60*1000);
            Log.e("날짜빼기", String.valueOf(calDateDays));
            if(calDateDays < 0) return false;

            Log.e("dfs","찍힘");

        } catch (ParseException e) {
            Log.e("날짜 파싱에러","서로 타입이 맞지 않음.");
        }
        return true;

    }


}
