package kr.co.tacademy.mongsil.mongsil.JSONParsers.Models;

/**
 * Created by ccei on 2016-08-01.
 */
public class UserData {
    int     userId;
    String  username;
    String  profileImg;
    String  area;
    String  date;

    public int getUserId() {
        return userId;
    }

    public String getProfileImg() {
        return this.profileImg;
    }

    public String getUsername() {
        return username;
    }

    public String getArea() {
        return area;
    }

    public String getDate() {
        return date;
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

    public void setArea(String area) {
        this.area = area;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
