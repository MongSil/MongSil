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

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private PropertyManager() {
        Context context = MongSilApplication.getMongSilContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public static final String KEY_DEVICE_ID = "deviceid";
    public static final String KEY_USERID = "userid";
    public static final String KEY_USER_PROFILE_IMG = "userprofileimg";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LAT_LOCATION = "latlocation";
    public static final String KEY_LON_LOCATION = "lonlocation";

    public static final String KEY_ALARM = "alarm";
    public static final String KEY_SAVE_GALLERY = "savegallery";
    public static final String KEY_USE_GPS = "usegps";

    public String getDeviceId() {
        return preferences.getString(KEY_DEVICE_ID, "");
    }

    public void setDeviceId(String UUID) {
        editor.putString(KEY_DEVICE_ID, UUID);
        editor.commit();
    }

    public String getUserId() {
        return preferences.getString(KEY_USERID, "11"); // 임시 7
    }

    public void setUserId(String userId) {
        editor.putString(KEY_USERID, userId);
        editor.commit();
    }

    public String getUserProfileImg() {
        return preferences.getString(KEY_USER_PROFILE_IMG,
                "https://s3.ap-northeast-2.amazonaws.com/mytproject2016/profile/img_1470648176191.jpg");
        // 임시 부산녀2
    }

    public void setUserProfileImg(String userProfileImg) {
        editor.putString(KEY_USER_PROFILE_IMG, userProfileImg);
        editor.commit();
    }

    public String getNickname() {
        return preferences.getString(KEY_NICKNAME, "부산녀2"); // 임시 부산녀2
    }

    public void setNickname(String nickname) {
        editor.putString(KEY_NICKNAME, nickname);
        editor.commit();
    }

    public String getLocation() {
        return preferences.getString(KEY_LOCATION, "부산"); // 임시 부산
    }

    public void setLocation(String location) {
        editor.putString(KEY_LOCATION, location);
        editor.commit();
    }

    public String getLatLocation() {
        return preferences.getString(KEY_LAT_LOCATION, "35.1788483"); // 임시 부산의 위도 경도
    }

    public void setLatLocation(String latLocation) {
        editor.putString(KEY_LAT_LOCATION, latLocation);
        editor.commit();
    }

    public String getLonLocation() {
        return preferences.getString(KEY_LAT_LOCATION, "129.0758175"); // 임시 부산의 위도 경도
    }

    public void setLonLocation(String lonLocation) {
        editor.putString(KEY_LON_LOCATION, lonLocation);
        editor.commit();
    }

    public Boolean getAlarm() {
        return preferences.getBoolean(KEY_ALARM, true);
    }

    public void setAlarm(Boolean alarm) {
        editor.putBoolean(KEY_ALARM, alarm);
        editor.commit();
    }

    public Boolean getUseGPS() {
        return preferences.getBoolean(KEY_USE_GPS, true);
    }

    public void setUseGps(Boolean usegps) {
        editor.putBoolean(KEY_USE_GPS, usegps);
        editor.commit();
    }

    public Boolean getSaveGallery() {
        return preferences.getBoolean(KEY_SAVE_GALLERY, true);
    }

    public void setSaveGallery(Boolean saveGallery) {
        editor.putBoolean(KEY_USE_GPS, saveGallery);
        editor.commit();
    }


}
