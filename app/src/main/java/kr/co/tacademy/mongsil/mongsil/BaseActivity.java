package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by Han on 2016-08-01.
 */
public class BaseActivity extends AppCompatActivity
        implements MiddleAloneDialogFragment.OnMiddleAloneDialogListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        CheckNetwork net_status = CheckNetwork.getInstance();

        if(!net_status.isChecked) {
            switch (net_status.getNetType(getApplicationContext())) {
                case CheckNetwork.NET_TYPE_WIFI:
                    // WIFI 연결상태
                    break;
                case CheckNetwork.NET_TYPE_3G:
                    // 3G 혹은 LTE연결 상태
                    getSupportFragmentManager().beginTransaction()
                            .add(MiddleAloneDialogFragment.newInstance(98), "connection_mobile")
                            .commit();
                    break;
                case CheckNetwork.NET_TYPE_NONE:
                    // 연결된 네트워크 없음
                    getSupportFragmentManager().beginTransaction()
                            .add(MiddleAloneDialogFragment.newInstance(99), "connection_fail")
                            .commit();
                    break;
            }
            net_status.isChecked = true;
        }
    }

    @Override
    public void onMiddleAlone(int select) {
        switch (select) {
            case 99 :
                finish();
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(TypekitContextWrapper.wrap(base));
    }
}