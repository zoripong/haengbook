package kr.hs.emirim.uuuuri.haegbook.Fragment;

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
import java.util.Calendar;
import java.util.Date;

import kr.hs.emirim.uuuuri.haegbook.Adapter.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Interface.ScheduleTag;
import kr.hs.emirim.uuuuri.haegbook.R;

import static kr.hs.emirim.uuuuri.haegbook.R.id.end_date_choose_btn;

/**
 * Created by 유리 on 2017-11-04.
 */

// TODO: 2017-11-04 여행 마지막일 : 두리
public class ThirdInputFragment extends Fragment{
    private String mStartDate;
    private ImageView mEndDateChooseBtn;
    private TextView mEndDateTv;

    public int mEndYear, mEndMonth, mEndDay;
    private int mYear, mMonth, mDay;

    private boolean isChecked;

    private String mEndDate;


    public ThirdInputFragment() {
        final Calendar cc = Calendar.getInstance();
        mYear = cc.get(Calendar.YEAR);
        mMonth = cc.get(Calendar.MONTH);
        mDay = cc.get(Calendar.DAY_OF_MONTH);
        isChecked=false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_third_input, container, false);
        mEndDateTv = (TextView) rootView.findViewById(R.id.end_date_tv);
        mEndDateTv.setText(mYear+" . "+String.valueOf(mMonth+1)+" . "+mDay);

        mEndDateChooseBtn = (ImageView) rootView.findViewById(end_date_choose_btn);
        mEndDateChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChecked=true;
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
        mStartDate= spm.retrieveString(ScheduleTag.START_DATE_TAG);

    }


    public boolean saveData(){
        if(!isChecked)
            return false;
        if(checkDate()) {
            Toast.makeText(getContext(), "여행 끝은 시작날보다 뒤로 신청하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        spm.save(ScheduleTag.END_DATE_TAG, mEndDate);
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
