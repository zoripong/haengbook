package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import kr.hs.emirim.uuuuri.haegbook.Adapter.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Interface.ScheduleTag;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-04.
 */

// TODO: 2017-11-04 seekbar : 민주
public class FifthInputFragment extends Fragment{
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

        Toast.makeText(getContext(), spm.retrieveString(ScheduleTag.TITLE_TAG), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), spm.retrieveString(ScheduleTag.LOCATION_TAG), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), spm.retrieveString(ScheduleTag.START_DATE_TAG), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), spm.retrieveString(ScheduleTag.END_DATE_TAG), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), String.valueOf(spm.retrieveInt(ScheduleTag.KOR_MONEY_TAG)), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), String.valueOf(spm.retrieveInt(ScheduleTag.FOREIGN_MONEY_TAG)), Toast.LENGTH_SHORT).show();

    }

    public boolean saveData(){
        // TODO: 2017-11-05 firebase save

        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        spm.resetData();
        return true;
    }



}
