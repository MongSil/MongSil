package kr.co.tacademy.mongsil.mongsil.JSONParsers.Models;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-07-27.
 */
public class PostData {
    int totalCount = 0;
    ArrayList<Post> post;

    public PostData (ArrayList<Post> post) {
        this.post = post;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public ArrayList<Post> getPost() {
        return post;
    }

    public void setPost(ArrayList<Post> post) {
        this.post = post;
    }
}
