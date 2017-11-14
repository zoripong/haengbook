package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by 유리 on 2017-11-04.
 */

public class FirstInputFragment extends Fragment{
    private View mRootView;
    private EditText mTripTitleEt;
    private Place mPlace;

    private final String TAG = "FIRST_FRAGMENT";

    private final int REQUEST_CODE_AUTOCOMPLETE = 1;

    public FirstInputFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_first_input, container, false);

        initialize();
        return mRootView;
    }

    private void initialize() {
        mPlace = null;

        mRootView.findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            openAutocompleteActivity();
                Log.e(TAG, String.valueOf(mTripTitleEt.getText()));
            }
        });

        mTripTitleEt = mRootView.findViewById(R.id.trip_title_et);

    }

    private void openAutocompleteActivity() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                mPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.e(TAG, (String) mPlace.getName());
                Log.e(TAG, (String) mPlace.getAddress());

                if(String.valueOf(mTripTitleEt.getText()).equals(""))
                    mTripTitleEt.setText("나의 "+ mPlace.getName()+" 이야기");

                ((ImageView)mRootView.findViewById(R.id.search_btn)).setImageResource(R.drawable.seleceted_gps);

                CharSequence attributions = mPlace.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                    Log.e(TAG, String.valueOf(Html.fromHtml(attributions.toString())));
                } else {
                    Log.e(TAG, "");
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                Log.e(TAG, "실패");
            }
        }
    }

    public boolean saveData(){
        if(mPlace == null)
            return false;

        String address[] = mPlace.getAddress().toString().split(" ");
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());

        spm.save(SharedPreferenceTag.TITLE_TAG, mTripTitleEt.getText().toString());
        spm.save(SharedPreferenceTag.LOCATION_TAG, (String) mPlace.getName());
        spm.save(SharedPreferenceTag.ADDRESS_TAG, address[0]);

        if(address[0].equals("대한민국"))
            spm.save(SharedPreferenceTag.IS_KOR_TAG, true);
        else
            spm.save(SharedPreferenceTag.IS_KOR_TAG, false);


        return true;
    }

}
