package kr.co.tacademy.mongsil.mongsil;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ccei on 2016-08-02.
 */
public class Post {
    public static final int TYPE_LAYOUT_DATE = 0;
    public static final int TYPE_LAYOUT_POST = 1;
    public static final int TYPE_LAYOUT_MY_POST = 2;
    int typeCode = 1;

    //String bgImg;

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
}
