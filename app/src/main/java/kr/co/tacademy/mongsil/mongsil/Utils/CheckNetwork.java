package kr.co.tacademy.mongsil.mongsil.Utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetwork {
    public static final int NET_TYPE_NONE = 0;
    public static final int NET_TYPE_WIFI = 1;
    public static final int NET_TYPE_3G = 2;
    private static CheckNetwork current = null;
    public boolean isChecked = false;

    public static CheckNetwork getInstance() {
        if (current == null) {
            current = new CheckNetwork();
        }
        return current;
    }
/*
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
                NetworkInfo _mobile_network =
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if(_mobile_network != null) {
                    // 3g일 경우
                }
                if(activeNetInfo != null){
                    // 네트워크 연결이 없을 경우
                }
            } catch (Exception e) {
                Log.i("ULNetworkReceiver", e.getMessage());
            }
        }
    }
*/

    private boolean getWifiState(Context p_oContext) {
            ConnectivityManager cm = (ConnectivityManager) p_oContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isConn = ni.isConnected();
            return isConn;
    }

    private boolean get3GState(Context p_oContext) {
            ConnectivityManager cm = (ConnectivityManager) p_oContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            boolean isConn = ni.isConnected();
            return isConn;
    }

    public int getNetType(Context p_oContext) {
        int nNetType = CheckNetwork.NET_TYPE_NONE;

        if (getWifiState(p_oContext)) {
            nNetType = CheckNetwork.NET_TYPE_WIFI;
        } else if (get3GState(p_oContext)) {
            nNetType = CheckNetwork.NET_TYPE_3G;
        }

        return nNetType;
    }
}