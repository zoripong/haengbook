package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.hs.emirim.uuuuri.haegbook.Interface.CurrencyTag;
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

    private String mCurrencySymbol;
    private String mCurrencyName;

    private boolean mIsKorea;

    //시크바


    private int FoodVal = 20, BusVal = 20, ShopVal = 20, GiftVal = 20, ExpVal = 10, EtcVal=10;
    private int max = 100,  rand = 0, turn, mmax = 0, sum;
    private int pgmax, pgmmax;

    private int k = 0, cnt = 0, s, m;
    private int mul = 0, mod = 0;

    SeekBar sbar[] = new SeekBar[6];
    EditText etext[] = new EditText[6];

    //알림바
    private View rootView;

    String beforeSavedDate;
    private String date;
    private NotificationManager nNM;
    private static final int NOTIFICATION_ID = 1;

    private int resultNumber = 0;
    static final int DATE_DIALOG_ID = 0;

    String[] startday = new String[3]; //여행 시작날
    String[] today = new String[3]; //오늘 날짜
    String[] dstartday= new String[3]; //저장된 여행 시작날

    private int tYear, tMonth, tDay;         //오늘 연월일 변수
    private int dYear, dMonth, dDay;   //디데이 연월일 변수
    private long d, t, r;

    private int mMoneyRate[]={20,10,10,20,32,8};

    private Float mCardBookKorMoney = 0.0f;

    public FifthInputFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fifth_input, container, false);

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

        mCurrencySymbol=spm.retrieveString(CurrencyTag.CURRENCY_SYMBOL_TAG);
        mCurrencyName=spm.retrieveString(CurrencyTag.CURRENCY_COUNTRY_TAG);

        mIsKorea =spm.retrieveBoolean(SharedPreferenceTag.IS_KOR_TAG);
        firedate();
        initialize();
        notifyalarm();
    }

    private void notifyalarm() {
        Button btnGen = (Button) rootView.findViewById(R.id.notify);
        btnGen.setOnClickListener(myGenOnClickListener);

        startday =mCardBookStartDate.split("\\."); //여행시작날

        if(beforeSavedDate != null) { //이미 저장한 여행이 있음
            dstartday = beforeSavedDate.split("\\."); //저장된 여행시작날

            if ((Integer.valueOf(dstartday[0]) < Integer.valueOf(startday[0])) && (Integer.valueOf(dstartday[1]) < Integer.valueOf(startday[1])) && (Integer.valueOf(dstartday[2]) < Integer.valueOf(startday[2]))) { //지금 설정하는 여행이 이미 있는 여행보다 나중일
                dYear = Integer.parseInt(dstartday[0]); //저장된 여행날짜로 계산
                dMonth = Integer.parseInt(dstartday[1]);
                dDay = Integer.parseInt(dstartday[2]);
            } else { //전에 저장한 여행보다 가까운 일
                dYear = Integer.parseInt(startday[0]); //지금 입력하는 여행날짜로 계산
                dMonth = Integer.parseInt(startday[1]);
                dDay = Integer.parseInt(startday[2]);
                date = mCardBookStartDate; //d-day값을 바꿔줌
            }
        }
        else { //처음 저장할 때
            dYear = Integer.parseInt(startday[0]);
            dMonth = Integer.parseInt(startday[1]);
            dDay = Integer.parseInt(startday[2]);
            date = mCardBookStartDate; //d-day값을 바꿔줌

        }

        Calendar cal = Calendar.getInstance(); //오늘 날짜 저장
        tYear = cal.get(Calendar.YEAR);
        tMonth = cal.get(Calendar.MONTH) +1;
        tDay = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(tYear, tMonth, tDay);

        Calendar dcal = Calendar.getInstance();
        dcal.set(dYear, dMonth, dDay);

        t = cal.getTimeInMillis();            //오늘 날짜를 밀리타임으로 바꿈
        d = dcal.getTimeInMillis();           //디데이날짜를 밀리타임으로 바꿈
        r = (d - t) / (24 * 60 * 60 * 1000);              //디데이 날짜에서 오늘 날짜를 뺀 값을 '일'단위로 바꿈

        resultNumber = (int) r;
    }


    private void generatNotification() {
        nNM = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, FifthInputFragment.class), PendingIntent.FLAG_UPDATE_CURRENT)

        //Context context = MainActivity.this;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext()) ;
        if (resultNumber >= 0) { //날짜 안 지남
            builder.setContentTitle("여행 D-" + resultNumber);
        } else { //날짜 지남
            int absR = Math.abs(resultNumber);
            builder.setContentTitle("여행 D+" + absR);
        }
        // builder.setContentIntent(pendingIntent);
        builder.setContentText("빨리 여행가고파");
        builder.setSmallIcon(android.R.drawable.stat_notify_more);
        builder.setWhen(System.currentTimeMillis());

        nNM.notify(NOTIFICATION_ID, builder.build()); //알람 띄우기
    }

    Button.OnClickListener myGenOnClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            generatNotification();
        }
    };

    private void initialize() {
        // editText, seekbar implements listener
        sbar[0] = (SeekBar) rootView.findViewById(R.id.sbFood);
        etext[0] = (EditText) rootView.findViewById(R.id.etFood);
        etext[0].setText(String.valueOf(FoodVal));
        sbar[1] = (SeekBar) rootView.findViewById(R.id.sbBus);
        etext[1] = (EditText) rootView.findViewById(R.id.etBus);
        etext[1].setText(String.valueOf(BusVal));
        sbar[2] = (SeekBar) rootView.findViewById(R.id.sbShop);
        etext[2] = (EditText) rootView.findViewById(R.id.etShop);
        etext[2].setText(String.valueOf(ShopVal));
        sbar[3] = (SeekBar) rootView.findViewById(R.id.sbGift);
        etext[3] = (EditText) rootView.findViewById(R.id.etGift);
        etext[3].setText(String.valueOf(GiftVal));
        sbar[4] = (SeekBar) rootView.findViewById(R.id.sbExp);
        etext[4] = (EditText) rootView.findViewById(R.id.etExp);
        etext[4].setText(String.valueOf(ExpVal));
        sbar[5] = (SeekBar) rootView.findViewById(R.id.sbEtc);
        etext[5] = (EditText) rootView.findViewById(R.id.etEtc);
        etext[5].setText(String.valueOf(EtcVal));

        for(int i = 0; i<etext.length; i++){
            final int finalI = i;

            etext[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        //Update Seekbar value after entering a number
                        sbar[finalI].setProgress(Integer.parseInt(s.toString()));
                        turn = finalI;
                        // pgbar();
                    } catch (Exception ex) {
                        //Log.e(String.valueOf(val[0]), " : 0" );
                    }
                }
            }); //etext Listener

            sbar[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    pgbar();
                    for (m = 0; m < 6; m++)
                        etext[m].setText(String.valueOf(sbar[m].getProgress()));
                    turn = finalI;
                }
            }); //sbar Listener
        }
    }

    public void pgbar() { //progress bar 계산
        int high=0, low=0;

        for (k = 0; k < 4; k++) { //최대값 구하기
            if (sbar[k].getProgress() <= sbar[k + 1].getProgress()) {
                high = k + 1;
                low = k;
            }
            else {
                high = k;
                low = k+1;
            }
        }

        sum = sbar[0].getProgress() + sbar[1].getProgress() + sbar[2].getProgress() + sbar[3].getProgress() + sbar[4].getProgress() + sbar[5].getProgress();

        if (sum > max) { //감소
            cnt = sum - max;
            mul = cnt / 5; //몫
            mod = cnt % 5; //나머지

            for (s = 0; s < 6; s++) {
                Log.e("ssss : ", String.valueOf(s));
                Log.e("sbar : ", String.valueOf(sbar[s].getProgress() - mul));
                Log.e("turn : ", String.valueOf(turn));

                if (turn != s && sbar[s].getProgress()-mul > 0) { //움직인 시크바가 아니고, 값을 빼도 음수값이 안 나올 때
                    sbar[s].setProgress(sbar[s].getProgress() - mul);

                }
                else if(sbar[s].getProgress()-mul < 0) {
                    if (sbar[high].getProgress() < 1)
                        sbar[high].setProgress(sbar[high].getProgress() - mul);
                    else
                        sbar[low].setProgress(sbar[low].getProgress() - mul);
                }

                else ;
            }
            rand = (int) (Math.random() * 5);
            sbar[rand].setProgress(sbar[rand].getProgress() - mod);

        } else if (sum < max) { //증가
            cnt = max - sum;
            mul = cnt / 5; //몫
            mod = cnt % 5; //나머지

            for (s = 0; s < 6; s++) {
                if (turn != s)
                    sbar[s].setProgress(sbar[s].getProgress() + mul);
                else ;
            }

            rand = (int) (Math.random() * 5);
            sbar[rand].setProgress(sbar[rand].getProgress() + mod);

        }
    } //pgbar()

    public void firedate() {
        class dday {
            public String Date;

            public dday(String Date) {

            }
        }

        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference dayref= mDatabase.getReference("DDay");
        final DatabaseReference tokenRef = mDatabase.getReference("DDay/"+mUserToken);

        dayref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    date="2017.11.28"; //초기값 아무렇게나 주기
                    Map<String, Object> map = new HashMap<String, Object>();
                    initialize();
                    notifyalarm(); //date값 바뀜
                    beforeSavedDate = date;

                    map.put("date", date); //넣기
                    tokenRef.updateChildren(map);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    } //firedate()



    public boolean saveData(){

        //카드 업데이트
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference("BookInfo");

        final String postKey=ref.push().getKey();
        DatabaseReference cardRef = ref.child(postKey);

        Map<String, CardBook> cardBooks = new HashMap<String, CardBook>();
        Map<String, Object> showing = new HashMap<String, Object>();

        String period=mCardBookStartDate+"-"+mCardBookEndDate;
        cardBooks.put("Registration", new CardBook(period,mCardBookLocation,mCardBookTitle,null));
        showing.put("isShowing",new Boolean(checkIsShowing()));
        cardRef.setValue(cardBooks);
        cardRef.updateChildren(showing);

        DatabaseReference moneyRef =  mDatabase.getReference("TravelMoney/"+postKey);

        Map<String, Object> rateMap = new HashMap<String, Object>();
        for(int i=0;i<mMoneyRate.length;i++) {
            rateMap.put(String.valueOf(i+1),new Float(String.valueOf(etext[i].getText())));
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
        totalMoneyMap.put("restKorea",new Float(mCardBookKorMoney));
        moneyRef.child("Total").updateChildren(totalMoneyMap);

        if(mIsKorea){
            mCurrencySymbol="";
            mCurrencyName="";
        }

        Map<String, Object> currencyMap = new HashMap<String, Object>();
        currencyMap.put("symbol",mCurrencySymbol);
        currencyMap.put("name",mCurrencyName);
        moneyRef.child("Currency").updateChildren(currencyMap);


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
