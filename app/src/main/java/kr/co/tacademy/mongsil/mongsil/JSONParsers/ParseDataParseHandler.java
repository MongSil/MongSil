package kr.co.tacademy.mongsil.mongsil.JSONParsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.POIData;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.Post;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.PostData;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.ReplyData;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.UserData;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.WeatherData;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.SearchPOIInfo;

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

                post.setPostId(jData.getInt("postId"));
                post.setUserId(jData.getInt("userId"));
                post.setUsername(jData.getString("username"));
                post.setProfileImg(jData.getString("profileImg"));
                post.setContent(jData.getString("content"));
                post.setBgImg(jData.getString("bgImg"));
                post.setWeatherCode(jData.getInt("weatherCode"));
                post.setArea1(jData.getString("area1"));
                post.setArea2(jData.getString("area2"));
                post.setDate(jData.getString("date"));
                post.setReplyCount(jData.getInt("replyCount"));

                jsonPostList.add(post);
            }

            jsonPostData = new PostData(jsonPostList);
            jsonPostData.setTotalCount(jsonObject.getInt("totalCount"));

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

                post.setPostId(jsonData.getInt("postId"));
                post.setUserId(jsonData.getInt("userId"));
                post.setUsername(jsonData.getString("username"));
                post.setContent(jsonData.getString("content"));
                post.setBgImg(jsonData.getString("bgImg"));
                post.setWeatherCode(jsonData.getInt("weatherCode"));
                post.setArea1(jsonData.getString("area1"));
                post.setArea2(jsonData.getString("area2"));
                post.setDate(jsonData.getString("date"));
                post.setReplyCount(jsonData.getInt("replyCount"));

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
                    replyData.setTotalCount(jsonObject.getInt("totalCount"));
                }

                replyData.setReplyId(jData.getInt("replyId"));
                replyData.setUserId(jData.getInt("userId"));
                replyData.setUsername(jData.getString("username"));
                replyData.setProfileImg(jData.getString("profileImg"));
                replyData.setContent(jData.getString("content"));
                replyData.setDate(jData.getString("date"));
                replyData.setPostId(jData.getInt("postId"));
                replyData.setPostContent(jData.getString("postContent"));

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
                    replyData.setTotalCount(jsonObject.getInt("totalCount"));
                }

                replyData.setReplyId(jData.getInt("replyId"));
                replyData.setUserId(jData.getInt("userId"));
                replyData.setUsername(jData.getString("username"));
                replyData.setProfileImg(jData.getString("profileImg"));
                replyData.setContent(jData.getString("content"));
                replyData.setDate(jData.getString("date"));
                replyData.setPostId(jData.getInt("postId"));

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

                POIData.setName(jData.getString("name"));
                POIData.setNoorLat(jData.getString("noorLat"));
                POIData.setNoorLon(jData.getString("noorLon"));
                POIData.setUpperAddrName(jData.getString("upperAddrName"));
                POIData.setMiddleAddrName(jData.getString("middleAddrName"));
                POIData.setLowerAddrName(jData.getString("lowerAddrName"));

                jsonPOIDataList.add(POIData);
            }

            searchPoiInfo = new SearchPOIInfo(jsonPOIDataList);
            searchPoiInfo.setTotalCount(jsonObject.getInt("totalCount"));
            searchPoiInfo.setPage(jsonObject.getInt("page"));

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

            data.setName(jsonObject.getString("name"));
            data.setCode(jsonObject.getString("code"));

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

            data.setUserId(jsonObject.getInt("userId"));
            data.setUsername(jsonObject.getString("username"));
            data.setProfileImg(jsonObject.getString("profileImg"));
            data.setArea(jsonObject.getString("area"));
            data.setDate(jsonObject.getString("date"));

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
    public static String[] postJSONUserSignUp(StringBuilder buf) {
        JSONObject jsonObject = null;
        try {
            Log.e("asdf", buf.toString());
            jsonObject = new JSONObject(buf.toString());
            String[] result = new String[2];
            result[0] = jsonObject.getString("msg");
            result[1] = jsonObject.getString("userId");
            return result;
        } catch (JSONException je) {
            Log.e("POST:UserLogin", "JSON파싱 중 에러발생", je);
        }
        return null;
    }
}
