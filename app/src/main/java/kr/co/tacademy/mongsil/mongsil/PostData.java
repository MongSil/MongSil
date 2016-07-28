package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by ccei on 2016-07-27.
 */
public class PostData {
    public static final int TYPE_LAYOUT_DATE = 0;
    public static final int TYPE_LAYOUT_POST = 1;
    int type;
    String date;
    int imgProfile;
    String name;
    String postContent;

    PostData(){ }

    public void setTypeData(int type) {
        this.type = type;
    }

    public void setData(int type, String date, int imgProfile, String name, String postContent) {
        this.type = type;
        this.date = date;
        this.imgProfile = imgProfile;
        this.name = name;
        this.postContent = postContent;
    }
}
