package kr.co.tacademy.mongsil.mongsil;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-07-27.
 */
public class PostData {
    int totalCount;
    ArrayList<Post> post;

    PostData (ArrayList<Post> post) {
        this.post = post;
    }
}
