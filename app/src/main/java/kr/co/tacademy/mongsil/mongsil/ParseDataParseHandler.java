package kr.co.tacademy.mongsil.mongsil;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseDataParseHandler {

    public static PostData getJSONPostRequestAllList(
            StringBuilder buf) {

        // 전체 - msg 생략
        JSONObject jsonObject = null;

        // page
        JSONObject jsonPage = null;
        Page pageData = null;

        // post
        JSONArray jsonArray = null;
        PostData jsonPostData;
        ArrayList<Post> jsonPostList = null;

        try {
            jsonObject = new JSONObject(buf.toString());

            jsonPage = jsonObject.getJSONObject("page");
            pageData = new Page();
            pageData.totalCount = jsonPage.getInt("totalCount");
            pageData.count = jsonPage.getInt("count");

            jsonArray = jsonObject.getJSONArray("post");
            jsonPostList = new ArrayList<Post>();
            int jsonArrSize = jsonArray.length();
            for (int i = 0; i < jsonArrSize; i++) {
                Post post = new Post();
                JSONObject jData = jsonArray.getJSONObject(i);

                post.postId = jData.getInt("postId");
                post.content = jData.getString("content");
                post.userId = jData.getInt("userId");
                post.username = jData.getString("username");
                post.profileImg = jData.getString("profileImg");
                post.date = jData.getString("date");
                post.area1 = jData.getString("area1");
                post.area2 = jData.getString("area2");

                jsonPostList.add(post);
            }
            return new PostData(pageData, jsonPostList);
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return null;
    }
}
