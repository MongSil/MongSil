package kr.co.tacademy.mongsil.mongsil;

import org.json.JSONObject;

/**
 * Created by ccei on 2016-08-01.
 */
public class UserData implements JSONParseHandler {
    int     userId;
    String  username;
    String  profileUrl;
    String  area;
    Boolean replyAlarm;
    Boolean gps;
    String  date;

    @Override
    public void setData(JSONObject jsonObject) {
        this.userId =     jsonObject.optInt("userId");
        this.username =   jsonObject.optString("username");
        this.profileUrl = jsonObject.optString("profileUrl");
        this.area =       jsonObject.optString("area");
        this.replyAlarm = jsonObject.optBoolean("replyAlarm");
        this.gps =        jsonObject.optBoolean("gps");
        this.date =       jsonObject.optString("date");
    }
}
