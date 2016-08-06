package kr.co.tacademy.mongsil.mongsil;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ccei on 2016-08-02.
 */
public class Post implements Parcelable {
    public static final int TYPE_LAYOUT_DATE = 0;
    public static final int TYPE_LAYOUT_POST = 1;
    public static final int TYPE_LAYOUT_MY_POST = 2;
    int typeCode = 1;

    //String bgImg;

    int    postId;
    String content;
    int    userId;
    String username;
    String profileImg;
    String date;
    String area1;
    String area2;

    Post() { }
    Post(int typeCode, String date) {
        this.typeCode = typeCode;
        this.date = date;
    }
    private Post(Parcel in) {
        postId = in.readInt();
        content = in.readString();
        userId = in.readInt();
        username = in.readString();
        profileImg = in.readString();
        date = in.readString();
        area1 = in.readString();
        area2 = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(postId);
        parcel.writeString(content);
        parcel.writeInt(userId);
        parcel.writeString(username);
        parcel.writeString(profileImg);
        parcel.writeString(date);
        parcel.writeString(area1);
        parcel.writeString(area2);
    }

    public static final Parcelable.Creator<Post> CREATOR
            = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel parcel) {
            return new Post(parcel);
        }

        @Override
        public Post[] newArray(int i) {
            return new Post[i];
        }
    };
}
