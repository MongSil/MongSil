package kr.co.tacademy.mongsil.mongsil;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by ccei on 2016-08-18.
 */
public class GPSManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_RESOLVE_ERROR = 18;

    private GoogleApiClient googleApiClient;
    boolean resolvingError = false;

    private BaseActivity activity;
    private String latitude, longitude;

    GPSManager(BaseActivity activity) {
        this.activity = activity;
        if(!resolvingError) {
            googleApiClient.connect();
        }
    }

    public void googleApiStart() {
        googleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void GPSstop() {
        googleApiClient.disconnect();
    }

    public void getLastKnownLocation(){
        int permissionCheck = ContextCompat.checkSelfPermission(
                activity.getApplicationContext(), Manifest.permission.WRITE_CALENDAR);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            // 권한 없음
        }else{
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());

            Log.d("Location : Latitude", latitude);
            Log.d("Location : Longitude", longitude);
            Log.d("Location : Accuracy", String.valueOf(location.getAccuracy()));

        }
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(resolvingError) {
            return;
       } else if (connectionResult.hasResolution()){
            try {
                resolvingError = true;
                connectionResult.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                googleApiClient.connect();
            }
        } else {
            Log.e("구글 API 연결 실패", ": " + connectionResult.getErrorCode());
            showErrorDialog();
            resolvingError = true;
        }
    }

    private void showErrorDialog() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(MiddleAloneDialogFragment.newInstance(80),
                        "google_api_connection_fail").commit();
    }

}
