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
                post.userId = jData.getInt("userId");
                post.username = jData.getString("username");
                post.profileImg = jData.getString("profileImg");
                post.content = jData.getString("content");
                post.bgImg = jData.getString("bgImg");
                post.weatherCode = jData.getInt("weatherCode");
                post.area1 = jData.getString("area1");
                post.area2 = jData.getString("area2");
                post.date = jData.getString("date");
                post.replyCount = jData.getInt("replyCount");

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

    public static Post getJSONPostDetailRequestList(
            StringBuilder buf) {

        // data
        JSONObject jsonObject = null;
        JSONObject jsonData = null;

        try {
            jsonObject = new JSONObject(buf.toString());
            jsonData = jsonObject.getJSONObject("data");
                Post post = new Post();

                post.postId = jsonData.getInt("postId");
                post.userId = jsonData.getInt("userId");
                post.username = jsonData.getString("username");
                post.content = jsonData.getString("content");
                post.bgImg = jsonData.getString("bgImg");
                post.weatherCode = jsonData.getInt("weatherCode");
                post.area1 = jsonData.getString("area1");
                post.area2 = jsonData.getString("area2");
                post.date = jsonData.getString("date");
                post.replyCount = jsonData.getInt("replyCount");

            return post;
        } catch (JSONException je) {
            Log.e("GET:PostRequestAllList", "JSON파싱 중 에러발생", je);
        }
        return null;
    }

    public static ArrayList<ReplyData> getJSONUsersReplyRequestList(
            StringBuilder buf) {

        // data
        JSONObject jsonObject = null;
        JSONArray jsonUsersReply = null;
        ArrayList<ReplyData> jsonReplyList;

        try {
            jsonObject = new JSONObject(buf.toString());
            jsonUsersReply = jsonObject.getJSONArray("usersReply");

            jsonReplyList = new ArrayList<ReplyData>();
            int jsonArrSize = jsonUsersReply.length();
            for(int i = 0 ; i < jsonArrSize ; i++) {
                ReplyData replyData = new ReplyData();
                JSONObject jData = jsonUsersReply.getJSONObject(i);
                if(i == 0) {
                    replyData.totalCount = jsonObject.getInt("totalCount");
                }

                replyData.replyId = jData.getInt("replyId");
                replyData.userId = jData.getInt("userId");
                replyData.username = jData.getString("username");
                replyData.profileImg = jData.getString("profileImg");
                replyData.content = jData.getString("content");
                replyData.date = jData.getString("date");
                replyData.postId = jData.getInt("postId");
                replyData.postContent = jData.getString("postContent");

                jsonReplyList.add(replyData);
            }
            return jsonReplyList;
        } catch (JSONException je) {
            Log.e("GET:UsersReplyRequest", "JSON파싱 중 에러발생", je);
        }
        return null;
    }

    public static ArrayList<ReplyData> getJSONReplyRequestList(
            StringBuilder buf) {

        // data
        JSONObject jsonObject = null;
        JSONArray jsonUsersReply = null;
        ArrayList<ReplyData> jsonReplyList;

        try {
            jsonObject = new JSONObject(buf.toString());
            jsonUsersReply = jsonObject.getJSONArray("reply");

            jsonReplyList = new ArrayList<ReplyData>();
            int jsonArrSize = jsonUsersReply.length();
            for(int i = 0 ; i < jsonArrSize ; i++) {
                ReplyData replyData = new ReplyData();
                JSONObject jData = jsonUsersReply.getJSONObject(i);
                if(i == 0) {
                    replyData.totalCount = jsonObject.getInt("totalCount");
                }

                replyData.replyId = jData.getInt("replyId");
                replyData.userId = jData.getInt("userId");
                replyData.username = jData.getString("username");
                replyData.profileImg = jData.getString("profileImg");
                replyData.content = jData.getString("content");
                replyData.date = jData.getString("date");
                replyData.postId = jData.getInt("postId");

                jsonReplyList.add(replyData);
            }
            return jsonReplyList;
        } catch (JSONException je) {
            Log.e("GET:ReplyRequestList", "JSON파싱 중 에러발생", je);
        }
        return null;
    }

    public static SearchPOIInfo getJSONPoiList(StringBuilder buf) {

        // 전체
        JSONObject jsonObject = null;

        // searchPoiInfo
        SearchPOIInfo searchPoiInfo;
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

            searchPoiInfo = new SearchPOIInfo(jsonPOIDataList);
            searchPoiInfo.totalCount = jsonObject.getInt("totalCount");
            searchPoiInfo.page = jsonObject.getInt("page");

            return searchPoiInfo;
        } catch (JSONException je) {
            Log.e("GET:PoiAllList", "JSON파싱 중 에러발생", je);
        }
        return null;
    }

    public static String getJSONResGeo(StringBuilder buf) {

        // 전체
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(buf.toString())
                    .getJSONObject("addressInfo");

            String cityDo = jsonObject.getString("city_do");

            return cityDo;
        } catch (JSONException je) {
            Log.e("GET:PoiAllList", "JSON파싱 중 에러발생", je);
        }
        return null;
    }

    public static WeatherData getJSONWeatherList(
            StringBuilder buf) {

        // 전체 - weather - minutely(array) - (0)sky
        JSONObject jsonObject = null;

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

    public static UserData getJSONUserInfo(StringBuilder buf) {

        // 전체 - data
        JSONObject jsonObject = null;

        // UserData
        UserData data;

        try {
            jsonObject = new JSONObject(buf.toString())
                    .getJSONObject("data");
            data = new UserData();

            data.userId = jsonObject.getInt("userId");
            data.username = jsonObject.getString("username");
            data.profileImg = jsonObject.getString("profileImg");
            data.area = jsonObject.getString("area");
            data.date = jsonObject.getString("date");

            return data;
        } catch (JSONException je) {
            Log.e("GET:UserDataList", "JSON파싱 중 에러발생", je);
        }
        return null;
    }

    public static String postJSONUserLogin(StringBuilder buf) {
        JSONObject jsonObject;

        try {
            JSONObject object = new JSONObject(buf.toString());
            String result = object.getString("msg");
            return result;
        } catch (JSONException je) {
            Log.e("POST:UserLogin", "JSON파싱 중 에러발생", je);
        }
        return null;
    }
    public static String postJSONUserSignUp(StringBuilder buf) {
        JSONObject jsonObject;

        try {
            JSONObject object = new JSONObject(buf.toString());
            String result = object.getString("userId");
            return result;
        } catch (JSONException je) {
            Log.e("POST:UserLogin", "JSON파싱 중 에러발생", je);
        }
        return null;
    }
}
