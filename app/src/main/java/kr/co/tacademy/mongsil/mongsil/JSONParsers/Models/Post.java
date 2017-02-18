package kr.co.tacademy.mongsil.mongsil.JSONParsers.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ccei on 2016-08-02.
 */
public class Post implements Parcelable{
    public static final int TYPE_LAYOUT_DATE = 0;
    public static final int TYPE_LAYOUT_POST = 1;
    public static final int TYPE_LAYOUT_MY_POST = 2;
    public static final int TYPE_LAYOUT_MY_DATE = 3;
    int typeCode = 1;

    int    postId;
    int    userId;
    String username;
    String profileImg;
    String content;
    String bgImg;
    int    weatherCode;
    String area1;
    String area2;
    String date;
    int    replyCount;

    public int getTypeCode() {
        return typeCode;
    }

    public int getPostId() {
        return postId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public String getContent() {
        return content;
    }

    public String getBgImg() {
        return bgImg;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public String getArea1() {
        return area1;
    }

    public String getArea2() {
        return area2;
    }

    public String getDate() {
        return date;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    public void setArea1(String area1) {
        this.area1 = area1;
    }

    public void setArea2(String area2) {
        this.area2 = area2;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public Post() { }
    public Post(int typeCode, String date) {
        this.typeCode = typeCode;
        this.date = date;
    }

    private Post(Parcel in) {
        postId = in.readInt();
        userId = in.readInt();
        username = in.readString();
        profileImg = in.readString();
        content = in.readString();
        bgImg = in.readString();
        weatherCode = in.readInt();
        area1 = in.readString();
        area2 = in.readString();
        date = in.readString();
        replyCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Post createFromParcel(Parcel src) {
            return new Post(src);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(postId);
        parcel.writeInt(userId);
        parcel.writeString(username);
        parcel.writeString(profileImg);
        parcel.writeString(content);
        parcel.writeString(bgImg);
        parcel.writeInt(weatherCode);
        parcel.writeString(area1);
        parcel.writeString(area2);
        parcel.writeString(date);
        parcel.writeInt(replyCount);
    }
}
