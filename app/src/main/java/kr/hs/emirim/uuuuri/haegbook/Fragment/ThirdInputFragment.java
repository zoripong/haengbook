package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.R;

import static kr.hs.emirim.uuuuri.haegbook.R.id.end_date_choose_btn;

/**
 * Created by 유리 on 2017-11-04.
 */

public class ThirdInputFragment extends Fragment{
    private String mStartDate;
    private ImageView mEndDateChooseBtn;
    private TextView mEndDateTv;

    public int mEndYear=0, mEndMonth, mEndDay;
    private int mYear, mMonth, mDay;

    private String mEndDate;

    SharedPreferenceManager spm;

    View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_third_input, container, false);
        spm = new SharedPreferenceManager((Activity) rootView.getContext());

        String[] defaultDate = spm.retrieveString(SharedPreferenceTag.START_DATE_TAG).split("\\.");

        mEndDateChooseBtn = (ImageView) rootView.findViewById(end_date_choose_btn);
        mEndDateChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), mDateSetListener,mYear,mMonth,mDay).show();
            }
        });

        return rootView;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yearSelected, int monthOfYear, int dayOfMonth) {
            mEndYear = yearSelected;
            mEndMonth = monthOfYear+1;
            mEndDay = dayOfMonth;
            mEndDate=mEndYear +" . "+ mEndMonth +" . "+ mEndDay;
            mEndDateTv.setText(mEndDate);
            mEndDate=mEndDate.replaceAll(" ","");


        }
    };
    public void getData(){
        if(getActivity() == null){
            return;
        }
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        mStartDate= spm.retrieveString(SharedPreferenceTag.START_DATE_TAG);
        String[] defaultDate = spm.retrieveString(SharedPreferenceTag.START_DATE_TAG).split("\\.");

        mYear = Integer.parseInt(defaultDate[0]);
        mMonth = Integer.parseInt(defaultDate[1])-1;
        mDay = Integer.parseInt(defaultDate[2]);
        mEndDate = mYear +"."+ String.valueOf(mMonth+1) +"."+ mDay;

        mEndDateTv = (TextView) rootView.findViewById(R.id.end_date_tv);
        mEndDateTv.setText(mYear+" . "+String.valueOf(mMonth+1)+" . "+mDay);

    }


    public boolean saveData(){
        if(checkDate()) {
            Toast.makeText(getContext(), "여행 끝은 시작보다 뒤로 신청하세요 :)", Toast.LENGTH_SHORT).show();
            return false;
        }

        spm.save(SharedPreferenceTag.END_DATE_TAG, mEndDate);
        return true;
    }

    public boolean checkDate() {

        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            Date startDate = format.parse(mStartDate);
            Date endDate = format.parse(mEndDate);

            long calDate = startDate.getTime() - endDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / ( 24*60*60*1000);
            if(calDateDays <= 0) return false;//날짜 적당

        } catch (ParseException e) {
            Log.e("날짜 파싱에러","서로 타입이 맞지 않음.");
        }
        return true;//시작날짜가 끝나는 날짜보다 뒤에 있음

    }


}
