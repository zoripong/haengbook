package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import kr.hs.emirim.uuuuri.haegbook.Adapter.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Interface.ScheduleTag;
import kr.hs.emirim.uuuuri.haegbook.R;

import static kr.hs.emirim.uuuuri.haegbook.R.id.start_date_choose_btn;

/**
 * Created by 유리 on 2017-11-04.
 */
// TODO: 2017-11-04 여행 시작일 : 두리
public class SecondInputFragment extends Fragment{

    private ImageView mStartDateChooseBtn;
    private TextView mStartDateTv;

    public int mStartYear, mStartMonth, mStartDay;
    private int mYear, mMonth, mDay;


    private String mStartDate;

    public SecondInputFragment() {
        final Calendar cc = Calendar.getInstance();
        mYear = cc.get(Calendar.YEAR);
        mMonth = cc.get(Calendar.MONTH);
        mDay = cc.get(Calendar.DAY_OF_MONTH);
        mStartDate = mYear +"."+ String.valueOf(mMonth+1) +"."+ mDay;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second_input, container, false);
        mStartDateTv = (TextView) rootView.findViewById(R.id.start_date_tv);
        mStartDateTv.setText(mYear+" . "+String.valueOf(mMonth+1)+" . "+mDay);
        mStartDateChooseBtn = (ImageView) rootView.findViewById(start_date_choose_btn);
        mStartDateChooseBtn.setOnClickListener(new View.OnClickListener() {
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
            mStartYear = yearSelected;
            mStartMonth = monthOfYear+1;
            mStartDay = dayOfMonth;
            mStartDate = mStartYear +" . "+ mStartMonth +" . "+ mStartDay;
            mStartDateTv.setText(mStartDate);
            mStartDate=mStartDate.replaceAll(" ","");



        }
    };



    // TODO: 2017-11-05 DEBUG
    public void getData(){
        if(getActivity() == null){
            return;
        }
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        Toast.makeText(getContext(), spm.retrieveString(ScheduleTag.LOCATION_TAG), Toast.LENGTH_SHORT).show();

    }


    public boolean saveData(){
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        spm.save(ScheduleTag.START_DATE_TAG, mStartDate);
        return true;
    }
}
