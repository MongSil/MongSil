package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

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

    private static final String KEY_DEVICE_ID = "deviceid";
    private static final String KEY_USERID = "userid";
    private static final String KEY_USER_PROFILE_IMG = "userprofileimg";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_LAT_LOCATION = "latlocation";
    private static final String KEY_LON_LOCATION = "lonlocation";
    private static final String KEY_FCM_TOKEN = "fcmtoken";

    private static final String KEY_ALARM = "alarm";
    private static final String KEY_SAVE_GALLERY = "savegallery";
    private static final String KEY_USE_GPS = "usegps";

    public static final String KEY_MARK_COUNT = "markcount";
    public static final String KEY_WARNING = "warning";

    // UUID(장치 아이디)
    public String getDeviceId() {
        return preferences.getString(KEY_DEVICE_ID, "");
    }

    public void setDeviceId(String UUID) {
        editor.putString(KEY_DEVICE_ID, UUID);
        editor.commit();
    }

    // UserId(유저 아이디)
    public String getUserId() {
        return preferences.getString(KEY_USERID, "");
    }

    public void setUserId(String userId) {
        editor.putString(KEY_USERID, userId);
        editor.commit();
    }

    // 프로필 이미지
    public String getUserProfileImg() {
        return preferences.getString(KEY_USER_PROFILE_IMG, "");
    }

    public void setUserProfileImg(String userProfileImg) {
        editor.putString(KEY_USER_PROFILE_IMG, userProfileImg);
        editor.commit();
    }

    // 닉네임(이름)
    public String getNickname() {
        return preferences.getString(KEY_NICKNAME, "");
    }

    public void setNickname(String nickname) {
        editor.putString(KEY_NICKNAME, nickname);
        editor.commit();
    }

    // 지역
    public String getLocation() {
        return preferences.getString(KEY_LOCATION, "");
    }

    public void setLocation(String location) {
        editor.putString(KEY_LOCATION, location);
        editor.commit();
    }

    // 지역 - 위도
    public String getLatLocation() {
        return preferences.getString(KEY_LAT_LOCATION, LocationData.ChangeToLatLon(getLocation())[0]);
    }

    public void setLatLocation(String latLocation) {
        editor.putString(KEY_LAT_LOCATION, latLocation);
        editor.commit();
    }

    // 지역 - 경도
    public String getLonLocation() {
        return preferences.getString(KEY_LAT_LOCATION, LocationData.ChangeToLatLon(getLocation())[1]);
    }

    public void setLonLocation(String lonLocation) {
        editor.putString(KEY_LON_LOCATION, lonLocation);
        editor.commit();
    }

    // fcm token
    public String getFCMToken() {
        return preferences.getString(KEY_FCM_TOKEN, "");
    }

    public void setFCMToken(String fcmToken) {
        editor.putString(KEY_FCM_TOKEN, fcmToken);
        editor.commit();
    }

    // 알람설정
    public Boolean getAlarm() {
        return preferences.getBoolean(KEY_ALARM, true);
    }

    public void setAlarm(Boolean alarm) {
        editor.putBoolean(KEY_ALARM, alarm);
        editor.commit();
    }

    // 이미지 저장설정
    public Boolean getSaveGallery() {
        return preferences.getBoolean(KEY_SAVE_GALLERY, true);
    }

    public void setSaveGallery(Boolean saveGallery) {
        editor.putBoolean(KEY_SAVE_GALLERY, saveGallery);
        editor.commit();
    }

    // GPS 사용설정
    public Boolean getUseGPS() {
        return preferences.getBoolean(KEY_USE_GPS, true);
    }

    public void setUseGps(Boolean useGPS) {
        editor.putBoolean(KEY_USE_GPS, useGPS);
        editor.commit();
    }

    // 현재 즐겨찾기 수
    public int getMarkCount() {
        return preferences.getInt(KEY_MARK_COUNT, 0);
    }

    public void setMarkCount(int markCount) {
        editor.putInt(KEY_MARK_COUNT, markCount);
        editor.commit();
    }

    // 글 쓸 때 경고 메세지
    public boolean getWarning() {
        return preferences.getBoolean(KEY_WARNING, false);
    }

    public void setWarning(boolean warning) {
        editor.putBoolean(KEY_WARNING, warning);
        editor.commit();
    }
}
