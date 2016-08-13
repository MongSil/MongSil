package kr.co.tacademy.mongsil.mongsil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Han on 2016-08-13.
 * 네트워크 상태 체크 브로드캐스트 - 아직 사용하지 않음
 */
public class CheckNetwork extends BroadcastReceiver {
    private Activity activity;

    public CheckNetwork() {
        super();
    }
    public CheckNetwork(Activity activity) {
        this.activity = activity;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action= intent.getAction();

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            try {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                NetworkInfo _wifi_network =
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if(_wifi_network != null) {
                    // wifi, 3g 둘 중 하나라도 있을 경우
                    if(_wifi_network != null && activeNetInfo != null){
                    }
                    // wifi, 3g 둘 다 없을 경우
                    else{
                    }
                }
            } catch (Exception e) {
                Log.i("ULNetworkReceiver", e.getMessage());
            }
        }
    }

/*  사용예 - 액티비티에 사용하면 됨
    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    appNetwork receiver = new appNetwork(this);
    registerReceiver(receiver, filter);*/
}
