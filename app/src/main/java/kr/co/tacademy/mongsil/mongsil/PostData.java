package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by ccei on 2016-07-27.
 */
public class PostData {
    public static final int TYPE_LAYOUT_DATE = 0;
    public static final int TYPE_LAYOUT_POST = 1;
    public static final int TYPE_LAYOUT_MY_POST = 2;
    int type = 1;
    String time;
    int imgProfile;
    String name;
    String content;
    int imgWeather;
    int imgBackGround;
    int commentCount = 0;

    PostData(){ }

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
