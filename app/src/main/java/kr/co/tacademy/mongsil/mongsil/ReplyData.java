package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by ccei on 2016-08-01.
 */
public class ReplyData {
    public static final int TYPE_POST_REPLY = 0;
    public static final int TYPE_USERS_REPLY = 1;

    int typeCode = 0;

    int    totalCount;
    int    replyId;
    int    userId;
    String username;
    String profileImg;
    String content;
    String date;
    int    postId;
    String postContent;
}
