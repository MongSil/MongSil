package kr.co.tacademy.mongsil.mongsil;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by Han on 2016-08-01.
 */
public class BaseActivity extends AppCompatActivity {

    LocationManager locationManager;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    private String gpsProvider = LocationManager.GPS_PROVIDER;
    private String networkProvider = LocationManager.NETWORK_PROVIDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // GPS 프로바이더
        isGPSEnabled = locationManager.isProviderEnabled(gpsProvider);
        // 네트워크 프로바이더
        isNetworkEnabled = locationManager.isProviderEnabled(networkProvider);

        if (!isGPSEnabled) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        if (!isNetworkEnabled) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }*/
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(TypekitContextWrapper.wrap(base));
    }

    private void updateLocation() {
        if (!locationManager.isProviderEnabled(gpsProvider)) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("위치 정보 경고");
                builder.setMessage("자신의 위치정보를 보기 위해서는 위치 권한을 허용하는 것을 권장해요!");
                builder.setCancelable(false);
                builder.setPositiveButton("넹", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission();
                    }
                });
                builder.create().show();
                return;
            }
            requestPermission();
            return;
        }
        Location location = locationManager.getLastKnownLocation(gpsProvider);

        /*if (location != null) {
            getLocation(location);
        }*/

        locationManager.requestLocationUpdates(gpsProvider, 5000, 5, gpsListener);
    }

    LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //getLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    /*private String getLocation(Location location) {
        // TODO : 두 정보를 받아올 방법이 필요함
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        return
    }*/

    private static final int RC_FINE_LOCATION = 100;

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_FINE_LOCATION) {
            if (permissions != null && permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                }
            }
        }
    }
}
