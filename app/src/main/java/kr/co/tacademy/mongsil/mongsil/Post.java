package kr.co.tacademy.mongsil.mongsil;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ccei on 2016-08-02.
 */
public class Post implements Parcelable{
    public static final int TYPE_LAYOUT_DATE = 0;
    public static final int TYPE_LAYOUT_POST = 1;
    public static final int TYPE_LAYOUT_MY_POST = 2;
    int typeCode = 1;

    int    postId;
    int    userId;
    String username;
    String profileImg;
    String content;
    String bgImg;
    int    weatherCode;
    int    iconCode;
    String area1;
    String area2;
    String date;
    int    replyCount;

    Post() { }
    Post(int typeCode, String date) {
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
        iconCode = in.readInt();
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
        parcel.writeInt(iconCode);
        parcel.writeString(area1);
        parcel.writeString(area2);
        parcel.writeString(date);
        parcel.writeInt(replyCount);
    }
}
