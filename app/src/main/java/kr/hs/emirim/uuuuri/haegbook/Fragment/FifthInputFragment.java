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

import static android.R.id.input;

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


    private int[] typeInitRate = {20,20,20,20,10,10};
    private boolean[] isSbFocus = {false,false,false,false,false,false};
    private boolean[] isEtFocus = {false,false,false,false,false,false};


    private int max = 100,  rand = 0, turn, mmax = 0, sum;
    private int pgmax, pgmmax;

    private int k = 0, cnt = 0, s, m;
    private int mul = 0, mod = 0;

    SeekBar rateSeekBar[] = new SeekBar[6];
    EditText rateEt[] = new EditText[6];

    private int[] beforeRate = {20,20,20,20,10,10};

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


    private void initialize() {
        final int[] seekBarId ={R.id.sbFood, R.id.sbBus,R.id.sbShop,R.id.sbGift,R.id.sbExp,R.id.sbEtc };
        for(int i=0;i<seekBarId.length;i++){
            final int index;
            index = i;
            rateSeekBar[index] =  (SeekBar) rootView.findViewById(seekBarId[index]);
            rateSeekBar[index].setProgress(typeInitRate[index]);

            rateSeekBar[index].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    rateEt[index].setText(rateSeekBar[index].getProgress()+"");

                    if (isSbFocus[index]) {
                        Log.e("시크바",index +"번 : "+rateSeekBar[index].getProgress());
                        int gap= beforeRate[index] - rateSeekBar[index].getProgress();
                        int rand = 0;

                        Log.e("차이", String.valueOf(gap));
                        randomChange(gap,index);

                        beforeRate[index]= rateSeekBar[index].getProgress();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isSbFocus[index] = true;
                    beforeRate[index]= rateSeekBar[index].getProgress();
                    for(int i=0;i<6;i++){
                        Log.e("시크바 값", String.valueOf(rateSeekBar[i].getProgress()));

                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isSbFocus[index] = false;

                }
            });

        }

        final int[] editTextId ={R.id.etFood, R.id.etBus,R.id.etShop,R.id.etGift,R.id.etExp,R.id.etEtc };
        for(int i=0;i<editTextId.length;i++){
            final int index;
            index = i;
            rateEt[index] =  (EditText) rootView.findViewById(editTextId[index]);
            rateEt[index].setText(String.valueOf(typeInitRate[i]));

            rateEt[index].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.e("준비?","wnslq");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable inputRate) {
                    if (Integer.parseInt(String.valueOf(inputRate)) >= 0 && Integer.parseInt(String.valueOf(inputRate)) <= 100) {
                        boolean isEtInput = true;
                        for (int i = 0; i < editTextId.length; i++) {
                            if (isSbFocus[index])
                                isEtInput = false;
                        }
                        if (isEtFocus[index] && isEtInput) {
                            rateSeekBar[index].setProgress(Integer.parseInt(inputRate.toString()));

                            int gap = beforeRate[index] - input;
                            Log.e("갭", String.valueOf(gap));
                            randomChange(gap, index);


                        }
                    }
                }
            });
            rateEt[index].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean gainFocus) {
                    if (gainFocus)
                        isEtFocus[index]=true;
                    else
                        isEtFocus[index] = false;

                }
            });

        }





    }

    private void randomChange(int gap,int index){
        while(gap!=0) {
            rand = (int) (Math.random() * 6);//0부터 5?
            Log.e("랜덤", String.valueOf(rand));

            if (rand == index)//만약 자기 자신이면
                continue;
            Log.e("남은 갭", String.valueOf(gap));
            Log.e("랜덤으로 뽑은 시크바 값", String.valueOf(rateSeekBar[rand].getProgress()));



            Log.e("갭 절대값", String.valueOf(Math.abs(gap)));
            int randomValue =Integer.parseInt(String.valueOf(rateEt[rand].getText()));

            if(gap < 0){                            //랜덤을 줄여야한다면
                if(rateSeekBar[rand].getProgress() == 0 )//바꾼 값이 더 크면 랜덤을 줄여야함.
                    continue;
                if(Math.abs(gap) <= randomValue) {//차이가 랜덤뽑은 값보다 작다면 빼도 상관없음
                    rateEt[rand].setText(String.valueOf(randomValue + gap));
                    rateSeekBar[rand].setProgress(Integer.parseInt(String.valueOf(rateEt[rand].getText())));
                    gap=0;
                    beforeRate[rand]= rateSeekBar[rand].getProgress();
                    break;
                }else {
                    gap= gap + randomValue;
                    rateEt[rand].setText(String.valueOf(randomValue - randomValue));
                    rateSeekBar[rand].setProgress(Integer.parseInt(String.valueOf(rateEt[rand].getText())));
                    beforeRate[rand]= rateSeekBar[rand].getProgress();

                }

            }else if(gap >0){
                if(rateSeekBar[rand].getProgress() == 100)//바꾼 값이 더 크면 랜덤을 늘려야함.
                    continue;
                if(Math.abs(gap) <= 100 -randomValue) {//차이가 100 - 랜덤뽑은 값보다 작다면 더해도 상관없음
                    rateEt[rand].setText(String.valueOf(randomValue + gap));
                    rateSeekBar[rand].setProgress(Integer.parseInt(String.valueOf(rateEt[rand].getText())));
                    gap=0;
                    beforeRate[rand]= rateSeekBar[rand].getProgress();
                    break;
                }
                else {
                    gap = gap - (100 - randomValue);
                    rateEt[rand].setText(String.valueOf(randomValue + (100 - randomValue)));
                    rateSeekBar[rand].setProgress(Integer.parseInt(String.valueOf(rateEt[rand].getText())));
                    beforeRate[rand]= rateSeekBar[rand].getProgress();

                }
            }


            Log.e("바꼈당", String.valueOf(rateEt[index].getText()));
        }
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
        cardBooks.put("Registration", new CardBook(period,mCardBookLocation,mCardBookTitle,null));
        showing.put("isShowing",new Boolean(checkIsShowing()));
        cardRef.setValue(cardBooks);
        cardRef.updateChildren(showing);

        DatabaseReference moneyRef =  mDatabase.getReference("TravelMoney/"+postKey);

        Map<String, Object> rateMap = new HashMap<String, Object>();
        for(int i=0;i<mMoneyRate.length;i++) {
            rateMap.put(String.valueOf(i+1),new Float(String.valueOf(rateEt[i].getText())));
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
