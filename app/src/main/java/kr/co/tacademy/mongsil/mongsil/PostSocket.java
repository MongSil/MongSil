package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by ccei on 2016-08-20.
 */
public class PostSocket {
    //일반채팅메세지
    public static final int TYPE_MESSAGE = 0;

    private int mType;//위의 셋중 하나
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

    private PostSocket() {}

    public int getmType() {
        return mType;
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

    public int getIconCode() {
        return iconCode;
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

    public static class Builder {
        private final int mType;
        int    mPostId;
        int    mUserId;
        String mUsername;
        String mProfileImg;
        String mContent;
        String mBgImg;
        int    mWeatherCode;
        String mArea1;
        String mArea2;
        String mDate;
        int    mReplyCount;

        public Builder(int type) {
            mType = type;
        }
        public Builder postId(int postId) {
            mPostId = postId;
            return this;
        }
        public Builder userId(int userId) {
            mUserId = userId;
            return this;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }
        public Builder profileImg(String profileImg) {
            mProfileImg = profileImg;
            return this;
        }
        public Builder content(String content) {
            mContent = content;
            return this;
        }
        public Builder bgImg(String bgImg) {
            mBgImg = bgImg;
            return this;
        }
        public Builder weatherCode(int weatherCode) {
            mWeatherCode = weatherCode;
            return this;
        }
        public Builder area1(String area1) {
            mArea1 = area1;
            return this;
        }
        public Builder area2(String area2) {
            mArea2 = area2;
            return this;
        }
        public Builder date(String date) {
            mDate = date;
            return this;
        }
        public Builder replyCount(int replyCount) {
            mReplyCount = replyCount;
            return this;
        }

        public PostSocket build() {
            PostSocket postSocket = new PostSocket();
            postSocket.postId =      mPostId;
            postSocket.userId =      mUserId;
            postSocket.username =    mUsername;
            postSocket.profileImg =  mProfileImg;
            postSocket.content =     mContent;
            postSocket.bgImg =       mBgImg;
            postSocket.weatherCode = mWeatherCode;
            postSocket.area1 =       mArea1;
            postSocket.area2 =       mArea2;
            postSocket.date =        mDate;
            postSocket.replyCount =  mReplyCount;
            return postSocket;
        }
    }
}
