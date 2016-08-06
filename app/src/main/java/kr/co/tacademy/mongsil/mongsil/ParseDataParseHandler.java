package kr.co.tacademy.mongsil.mongsil;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseDataParseHandler {

    public static ArrayList<Post> getJSONPostRequestAllList(
            StringBuilder buf) {

        ArrayList<Post> jsonPostList = null;

        // 전체 - msg 생략
        JSONObject jsonObject = null;

        // page
        JSONObject jsonPage = null;

        // post
        JSONArray jsonArray = null;
        try {
            jsonObject = new JSONObject(buf.toString());

            jsonPage = jsonObject.getJSONObject("page");
            Page page = new Page();
            page.totalCount = jsonPage.getInt("totalCount");
            page.count = jsonPage.getInt("count");

            jsonArray = jsonObject.getJSONArray("post");
            jsonPostList = new ArrayList<Post>();
            int jsonArrSize = jsonArray.length();
            for (int i = 0; i < jsonArrSize; i++) {
                Post post = new Post();
                JSONObject jData = jsonArray.getJSONObject(i);

                post.content = jData.getString("content");
                post.username = jData.getString("username");
                post.date = jData.getString("date");
                post.area1 = jData.getString("area1");
                post.area2 = jData.getString("area2");
                post.postId = jData.getInt("postId");

                jsonPostList.add(post);
            }
        } catch (JSONException je) {
            Log.e("RequestAllList", "JSON파싱 중 에러발생", je);
        }
        return jsonPostList;
    }
}
