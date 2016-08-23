package kr.co.tacademy.mongsil.mongsil;

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

    ReplyData() { }

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
