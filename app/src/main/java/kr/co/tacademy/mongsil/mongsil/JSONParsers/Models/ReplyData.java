package kr.co.tacademy.mongsil.mongsil.JSONParsers.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ccei on 2016-08-01.
 */
public class ReplyData implements Parcelable {
    public static final int TYPE_POST_REPLY = 0;
    public static final int TYPE_USERS_REPLY = 1;

    int typeCode = 0;

    int    totalCount = 0;
    int    replyId;
    int    userId;
    String username;
    String profileImg;
    String content;
    String date;
    int    postId;
    String postContent;

    public ReplyData() { }

    private ReplyData(Parcel in) {
        totalCount = in.readInt();
        replyId = in.readInt();
        userId = in.readInt();
        username = in.readString();
        profileImg = in.readString();
        content = in.readString();
        date = in.readString();
        postId = in.readInt();
        postContent = in.readString();
    }

    public int getTypeCode() {
        return typeCode;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getReplyId() {
        return replyId;
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

    public String getDate() {
        return date;
    }

    public int getPostId() {
        return postId;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
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

    public void setDate(String date) {
        this.date = date;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ReplyData createFromParcel(Parcel src) {
            return new ReplyData(src);
        }

        public ReplyData[] newArray(int size) {
            return new ReplyData[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(totalCount);
        parcel.writeInt(replyId);
        parcel.writeInt(userId);
        parcel.writeString(username);
        parcel.writeString(profileImg);
        parcel.writeString(content);
        parcel.writeString(date);
        parcel.writeInt(postId);
        parcel.writeString(postContent);
    }
}
