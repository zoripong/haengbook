package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import kr.hs.emirim.uuuuri.haegbook.Adapter.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Interface.ScheduleTag;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-04.
 */

// TODO: 2017-11-04 환율 : 두리
public class FourthInputFragment extends Fragment{
    public FourthInputFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fourth_input, container, false);
        return rootView;
    }



    public void getData(){
        if(getActivity() == null)
            return;
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        Toast.makeText(getContext(), spm.retrieveString(ScheduleTag.ADDRESS_TAG), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), String.valueOf(spm.retrieveBoolean(ScheduleTag.IS_KOR_TAG)), Toast.LENGTH_SHORT).show();

    }

    public boolean saveData(){
        // TODO: 2017-11-05 돈 지정 안했을 경우 return false!!!!!!!!
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        spm.save(ScheduleTag.KOR_MONEY_TAG, 1000);
        // TODO: 2017-11-05 외국일 경우 if 해주셈
        spm.save(ScheduleTag.FOREIGN_MONEY_TAG, 500);
        return true;
    }


}
