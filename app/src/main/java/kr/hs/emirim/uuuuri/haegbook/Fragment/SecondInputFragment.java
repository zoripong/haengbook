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
// TODO: 2017-11-04 여행 시작일 : 두리 
public class SecondInputFragment extends Fragment{

    public SecondInputFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second_input, container, false);

        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        Toast.makeText(getContext(), spm.retrieveString(ScheduleTag.LOCATION_TAG), Toast.LENGTH_SHORT).show();

        return rootView;
    }

}
