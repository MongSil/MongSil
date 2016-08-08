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

        // post
        JSONArray jsonArray = null;
        PostData jsonPostData;
        ArrayList<Post> jsonPostList = null;

        try {
            jsonObject = new JSONObject(buf.toString());

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

            jsonPostData = new PostData(jsonPostList);
            jsonPostData.totalCount = jsonObject.getInt("totalCount");

            return jsonPostData;
        } catch (JSONException je) {
            Log.e("GET:PostRequestAllList", "JSON파싱 중 에러발생", je);
        }
        return null;
    }
    public static SearchPoiInfo getJSONPoiList(StringBuilder buf) {

        // 전체
        JSONObject jsonObject = null;

        // searchPoiInfo
        SearchPoiInfo searchPoiInfo;
        JSONObject jsonPois = null;

        // POIData Array
        JSONArray jsonArray = null;
        ArrayList<POIData> jsonPOIDataList = null;

        try {
            jsonObject = new JSONObject(buf.toString())
                    .getJSONObject("searchPoiInfo");

            jsonPois = jsonObject.getJSONObject("pois");
            jsonArray = jsonPois.getJSONArray("poi");

            jsonPOIDataList = new ArrayList<POIData>();
            int jsonArrSize = jsonArray.length();
            for (int i = 0; i < jsonArrSize; i++) {
                POIData POIData = new POIData();
                JSONObject jData = jsonArray.getJSONObject(i);

                POIData.name = jData.getString("name");
                POIData.noorLat = jData.getString("noorLat");
                POIData.noorLon = jData.getString("noorLon");
                POIData.upperAddrName = jData.getString("upperAddrName");
                POIData.middleAddrName = jData.getString("middleAddrName");
                POIData.lowerAddrName = jData.getString("lowerAddrName");

                jsonPOIDataList.add(POIData);
            }

            searchPoiInfo = new SearchPoiInfo(jsonPOIDataList);
            searchPoiInfo.totalCount = jsonObject.getInt("totalCount");
            searchPoiInfo.page = jsonObject.getInt("page");

            return searchPoiInfo;
        } catch (JSONException je) {
            Log.e("GET:PoiAllList", "JSON파싱 중 에러발생", je);
        }
        return null;
    }
    public static WeatherData getJSONWeatherList(
            StringBuilder buf) {

        // 전체
        JSONObject jsonObject = null;

        // weather - minutely
        JSONArray jsonMinutely = null;
        JSONObject jsonrain = null;

        // weather
        WeatherData data;

        try {
            jsonObject = new JSONObject(buf.toString())
                    .getJSONObject("weather").getJSONArray("minutely")
                    .getJSONObject(0).getJSONObject("sky");
            data = new WeatherData();

            data.name = jsonObject.getString("name");
            data.code = jsonObject.getString("code");

            return data;
        } catch (JSONException je) {
            Log.e("GET:WeatherList", "JSON파싱 중 에러발생", je);
        }
        return null;
    }
}
