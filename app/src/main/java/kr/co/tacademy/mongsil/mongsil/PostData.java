package kr.co.tacademy.mongsil.mongsil;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by ccei on 2016-07-27.
 */
public class PostData implements JSONParseHandler, Parcelable {
    Post post;

    @Override
    public void setData(JSONObject jsonObject) {
        JSONObject object = jsonObject.optJSONObject("post");
        this.post = new Post();
        post.setData(object);
    }

    public static final int TYPE_LAYOUT_DATE = 0;
    public static final int TYPE_LAYOUT_POST = 1;
    public static final int TYPE_LAYOUT_MY_POST = 2;

    int type = 1;
    String time;
    int imgProfile;
    String name;
    String content;
    String location = "대전";
    int imgWeather;
    int imgBackGround;
    int commentCount = 0;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeString(time);
        parcel.writeInt(imgProfile);
        parcel.writeString(name);
        parcel.writeString(content);
        parcel.writeString(location);
        parcel.writeInt(imgWeather);
        parcel.writeInt(imgBackGround);
        parcel.writeInt(commentCount);
    }

    PostData(){ }

    private PostData(Parcel in) {
        type = in.readInt();
        time = in.readString();
        imgProfile = in.readInt();
        name = in.readString();
        content = in.readString();
        location = in.readString();
        imgWeather = in.readInt();
        imgBackGround = in.readInt();
        commentCount = in.readInt();
    }

    public static final Parcelable.Creator<PostData> CREATOR
            = new Parcelable.Creator<PostData>() {
        @Override
        public PostData createFromParcel(Parcel parcel) {
            return new PostData(parcel);
        }

        @Override
        public PostData[] newArray(int i) {
            return new PostData[i];
        }
    };

    public void setTimeData(int type, String time) {
        this.type = type;
        this.time = time;
    }

    public void setData(int type, String time, int imgProfile,
                        String name, String content, int imgWeather,
                        int imgBackGround, int commentCount) {
        this.type = type;
        this.time = time;
        this.imgProfile = imgProfile;
        this.name = name;
        this.content = content;
        this.imgWeather = imgWeather;
        this.imgBackGround = imgBackGround;
        this.commentCount = commentCount;
    }
}
