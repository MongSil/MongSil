package kr.co.tacademy.mongsil.mongsil;

import org.json.JSONObject;

/**
 * Created by ccei on 2016-08-02.
 */
public class Post implements JSONParseHandler {
    //String bgImg;
    String content;
    String username;
    String date;
    String area1;
    String area2;
    int    postId;

    @Override
    public void setData(JSONObject jsonObject) {
        //this.bgImg = jsonObject.optString("bgImg");
        this.content = jsonObject.optString("Content");
        this.date = jsonObject.optString("date");
        this.username = jsonObject.optString("username");
        this.area1 = jsonObject.optString("area1");
        this.area2 = jsonObject.optString("area2");
        this.postId = jsonObject.optInt("postId");
    }
}
