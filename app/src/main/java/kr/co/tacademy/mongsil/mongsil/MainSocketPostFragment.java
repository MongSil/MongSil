package kr.co.tacademy.mongsil.mongsil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by ccei on 2016-08-19.
 */
public class MainSocketPostFragment extends Fragment {
    public static final String AREA1 = "area1";

    private String area1 = null;

    private Socket socket;
    {
        try {
            socket = IO.socket(NetworkDefineConstant.SOCKET_SERVER_POST);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public MainSocketPostFragment() {
        super();
    }

    //실제 데이터가 모여져 있는 곳
    private List<Post> Posts = new ArrayList<Post>();

    RecyclerView postRecyclerView;
    PostRecyclerViewAdapter postAdapter;

    private int loadOnResult = 0;
    private int maxLoadSize = 1;

    public static MainSocketPostFragment newInstance(String location) {
        MainSocketPostFragment fragment = new MainSocketPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AREA1, location);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //더미로 어댑터를 채운다.
        postAdapter = new PostRecyclerViewAdapter(activity, Posts);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            area1 = getArguments().getString("area1");
        }
        socket.on("err", onErr);
        socket.on("loginCheck", onLogin);
        socket.on("sendPostList", onPostList);
        socket.connect();
        loginConfirm();
        showPostList(0);
        //postAdapter.items.clear();
        postAdapter.notifyDataSetChanged();
    }

    private void loginConfirm() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", PropertyManager.getInstance().getUserId());
            jsonObject.put("deviceId", PropertyManager.getInstance().getDeviceId());
            socket.emit("login", jsonObject);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }
    private void showPostList(int skip) {
        final int COUNT = 10;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put("area1", area1)
                    .put("skip", skip)
                    .put("count", COUNT);
            socket.emit("showPostList", jsonObject);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        postRecyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_post, container, false);

        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setOnScrollListener(
                new EndlessRecyclerOnScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int current_page) {
                        if (maxLoadSize != loadOnResult) {
                            LoadMore();
                        } else {
                            this.setLoadingState(false);
                        }
                    }
                });
        return postRecyclerView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();

        socket.off("err", onErr);
        socket.off("login", onLogin);
        socket.off("sendPostList", onPostList);
    }

    private Emitter.Listener onErr = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String err;
                    try {
                        err = data.getString("err");
                        Log.e("err", " " + err);
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String msg;
                    try {
                        msg = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    if(msg == "success") {
                        return;
                    } else {
                    }
                }
            });
        }
    };

    private Emitter.Listener onPostList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 전체 - msg 생략
                    JSONObject jsonObject = (JSONObject) args[0];

                    // post
                    JSONArray jsonArray = null;
                    PostData jsonPostData = null;
                    ArrayList<Post> jsonPostList = null;
                    try {
                        jsonArray = jsonObject.getJSONArray("post");
                        jsonPostList = new ArrayList<Post>();
                        int jsonArrSize = jsonArray.length();
                        for (int i = 0; i < jsonArrSize; i++) {
                            JSONObject jData = jsonArray.getJSONObject(i);
                            Post post = new Post();
                            post.postId = jData.getInt("postId");
                            post.userId = jData.getInt("userId");
                            post.username = jData.getString("username");
                            post.profileImg = jData.getString("profileImg");
                            post.content = jData.getString("content");
                            post.bgImg = jData.getString("bgImg");
                            post.weatherCode = jData.getInt("weatherCode");
                            post.area1 = jData.getString("area1");
                            post.area2 = jData.getString("area2");
                            post.date = jData.getString("date");
                            post.replyCount = jData.getInt("replyCount");

                            jsonPostList.add(post);
                        }

                        jsonPostData = new PostData(jsonPostList);
                        jsonPostData.totalCount = jsonObject.getInt("totalCount");
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                    if (jsonPostData != null && jsonPostData.post.size() > 0) {
                        int maxResultSize = jsonPostData.post.size();
                        loadOnResult += maxResultSize;
                        maxLoadSize = jsonPostData.totalCount;
                        // Result의 Date와 오늘 날짜를 비교하는 로직
                        String compare = jsonPostData.post.get(maxResultSize - 1).date.split(" ")[0];
                        for (int i = maxResultSize - 1; i >= 0; i--) {
                            String toCompare = jsonPostData.post.get(i).date.split(" ")[0];
                            if (TimeData.compareDate(compare, toCompare)) {
                                jsonPostData.post.add(i + 1, new Post(0, jsonPostData.post.get(i + 1).date));
                                compare = toCompare;
                            }
                            if (i == 0) {
                                jsonPostData.post.add(0, new Post(0, jsonPostData.post.get(0).date));
                            }
                        }
                        postAdapter.add(jsonPostList);
                    }
                }
            });
        }
    };

    private void addPostList(Post Post) {
        /*Posts.add(new Post.Builder()
                .postId(Post.postId)
                .userId(Post.userId)
                .username(Post.username)
                .profileImg(Post.profileImg)
                .content(Post.content)
                .bgImg(Post.bgImg)
                .weatherCode(Post.weatherCode)
                .area1(Post.area1)
                .area2(Post.area2)
                .date(Post.date)
                .replyCount(Post.replyCount)
                .build());*/
        postAdapter.notifyItemInserted(1);
    }

    private void LoadMore() {
        showPostList(loadOnResult);
    }
}
