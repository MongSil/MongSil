package kr.co.tacademy.mongsil.mongsil;

import org.json.JSONObject;

/**
 * Created by ccei on 2016-08-02.
 */
public class Post implements JSONParseHandler {
    int    postId;
    int    userId;
    String area1;
    String area2;
    String weatherTheme;
    String themeIcon;
    String bgImg;
    String Content;
    String date;

    @Override
    public void setData(JSONObject jsonObject) {
        this.postId = jsonObject.optInt("postId");
        this.userId = jsonObject.optInt("userId");
        this.area1 = jsonObject.optString("area1");
        this.area2 = jsonObject.optString("area2");
        this.weatherTheme = jsonObject.optString("weatherTheme");
        this.themeIcon = jsonObject.optString("themeIcon");
        this.bgImg = jsonObject.optString("bgImg");
        this.Content = jsonObject.optString("Content");
        this.date = jsonObject.optString("date");
    }
}
