package kr.co.tacademy.mongsil.mongsil;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-07-27.
 */
public class PostData {
    Page page;
    ArrayList<Post> post;

    PostData (Page page, ArrayList<Post> post) {
        this.page = page;
        this.post = post;
    }
}
