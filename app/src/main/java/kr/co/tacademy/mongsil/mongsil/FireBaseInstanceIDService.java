package kr.co.tacademy.mongsil.mongsil;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FireBaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMInstanceIDService";

    //발급받은 FCM토근을 업데이트 하는 FCM전용 콜백메소드
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //필요하다면 다시 FCM토큰을 업데이트 함
        sendRegistrationToServer(refreshedToken);
    }
    /**
     *
     */
    private void sendRegistrationToServer(String token) {
    }
}
