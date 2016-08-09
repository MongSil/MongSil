package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PropertyManager {
    private static PropertyManager instance;
    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private PropertyManager() {
        Context context = MongSilApplication.getMongSilContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
    }

    public static final String KEY_DEVICE_ID = "deviceid";
    public static final String KEY_USERID = "userid";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_USE_GPS = "usegps";

    public String getDeviceId() {
        return mPrefs.getString(KEY_DEVICE_ID, "");
    }

    public void setDeviceId(String UUID) {
        mEditor.putString(KEY_DEVICE_ID, UUID);
        mEditor.commit();
    }

    public String getUserId() {
        return mPrefs.getString(KEY_USERID, "");
    }

    public void setUserId(String userId) {
        mEditor.putString(KEY_USERID, userId);
        mEditor.commit();
    }

    public String getNickname() {
        return mPrefs.getString(KEY_NICKNAME, "");
    }

    public void setNickname(String nickname) {
        mEditor.putString(KEY_NICKNAME, nickname);
        mEditor.commit();
    }

    public String getLocation() {
        return mPrefs.getString(KEY_LOCATION, "");
    }

    public void setLocation(String location) {
        mEditor.putString(KEY_LOCATION, location);
        mEditor.commit();
    }
    public String getUseGPS() {
        return mPrefs.getString(KEY_USE_GPS, "");
    }

    public void setUseGps(String usegps) {
        mEditor.putString(KEY_USE_GPS, usegps);
        mEditor.commit();
    }


}
