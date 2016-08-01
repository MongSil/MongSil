package kr.co.tacademy.mongsil.mongsil;

import org.json.JSONObject;

/**
 * Created by ccei on 2016-08-01.
 */
public class ReplyData implements JSONParseHandler{
    int    replyId;
    int    postId;
    int    userId;
    String content;
    String date;

    @Override
    public void setData(JSONObject jsonObject) {
        this.replyId = jsonObject.optInt("replyId");
        this.postId = jsonObject.optInt("postId");
        this.userId = jsonObject.optInt("userId");
        this.content = jsonObject.optString("content");
        this.date = jsonObject.optString("date");
    }
}
