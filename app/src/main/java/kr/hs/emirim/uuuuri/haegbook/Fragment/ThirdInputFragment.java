package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.hs.emirim.uuuuri.haegbook.Adapter.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Interface.ScheduleTag;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-04.
 */

// TODO: 2017-11-04 여행 마지막일 : 두리 
public class ThirdInputFragment extends Fragment{
    public ThirdInputFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_third_input, container, false);
        return rootView;
    }

    public boolean saveData(){
        // TODO: 2017-11-05 끝날짜 선택 안했을 경우 return false!!!!!!!!
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        spm.save(ScheduleTag.END_DATE_TAG, "종료날짜");
        return true;
    }

}
