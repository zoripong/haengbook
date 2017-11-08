package kr.hs.emirim.uuuuri.haegbook.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    String token;
    @Override
    public void onTokenRefresh() {
        token= FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "사용자 토큰: " + token);

    }

    public String sendRegistrationToServer() {
        onTokenRefresh();
        return  token;
    }
}