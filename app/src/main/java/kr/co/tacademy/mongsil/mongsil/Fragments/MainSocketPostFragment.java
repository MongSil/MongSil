package kr.co.tacademy.mongsil.mongsil.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kr.co.tacademy.mongsil.mongsil.Activities.MainActivity;
import kr.co.tacademy.mongsil.mongsil.Libraries.EndlessRecyclerOnScrollListener;
import kr.co.tacademy.mongsil.mongsil.MongSilApplication;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.NetworkDefineConstant;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.Post;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.PostData;
import kr.co.tacademy.mongsil.mongsil.Adapters.PostRecyclerViewAdapter;
import kr.co.tacademy.mongsil.mongsil.Managers.PropertyManager;
import kr.co.tacademy.mongsil.mongsil.R;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.TimeData;

/**
 * Created by ccei on 2016-08-19.
 */
@Deprecated
public class MainSocketPostFragment extends Fragment
        implements MainActivity.OnLocationChangeListener {
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

    Handler handler = new Handler();

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
    public void onAttach(Context context) {
        super.onAttach(context);
        postAdapter = new PostRecyclerViewAdapter((MainActivity)context, Posts);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            area1 = getArguments().getString(AREA1);
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
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
    TextView nonePost;
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        nonePost = (TextView) view.findViewById(R.id.text_none_post);
        postRecyclerView =
                (RecyclerView) view.findViewById(R.id.post_recycler);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setOnScrollListener(
                new EndlessRecyclerOnScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int current_page) {
                        if (maxLoadSize != loadOnResult && maxLoadSize > loadOnResult) {
                            LoadMore();
                        } else {
                            this.setLoadingState(false);
                        }
                    }
                });
        return view;
    }

    private void LoadMore() {
        showPostList(loadOnResult);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        socket.disconnect();

        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("err", onErr);
        socket.off("login", onLogin);
        socket.off("sendPostList", onPostList);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ProgressDialog dialog = new ProgressDialog(getContext());
                    dialog.setMessage("서버와의 연결에 실패했습니다.\n잠시만 기다려주세요...");
                }
            }, 2000);
        }
    };

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

                            post.setPostId(jData.getInt("postId"));
                            post.setUserId(jData.getInt("userId"));
                            post.setUsername(jData.getString("username"));
                            post.setProfileImg(jData.getString("profileImg"));
                            post.setContent(jData.getString("content"));
                            post.setBgImg(jData.getString("bgImg"));
                            post.setWeatherCode(jData.getInt("weatherCode"));
                            post.setArea1(jData.getString("area1"));
                            post.setArea2(jData.getString("area2"));
                            post.setDate(jData.getString("date"));
                            post.setReplyCount(jData.getInt("replyCount"));

                            jsonPostList.add(post);
                        }

                        jsonPostData = new PostData(jsonPostList);
                        jsonPostData.setTotalCount(jsonObject.getInt("totalCount"));
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                    if (jsonPostData != null && jsonPostData.getPost().size() > 0) {
                        int maxResultSize = jsonPostData.getPost().size();
                        loadOnResult += maxResultSize;
                        maxLoadSize = jsonPostData.getTotalCount();
                        // Result의 Date와 오늘 날짜를 비교하는 로직
                        String compare = jsonPostData.getPost().get(maxResultSize - 1).getDate().split(" ")[0];
                        for (int i = maxResultSize - 1; i >= 0; i--) {
                            String toCompare = jsonPostData.getPost().get(i).getDate().split(" ")[0];
                            if (TimeData.compareDate(compare, toCompare)) {
                                jsonPostData.getPost().add(i + 1, new Post(0, jsonPostData.getPost().get(i + 1).getDate()));
                                compare = toCompare;
                            }
                            if (i == 0) {
                                jsonPostData.getPost().add(0, new Post(0, jsonPostData.getPost().get(0).getDate()));
                            }
                        }
                        postAdapter.add(jsonPostList);
                    }
                }
            });
        }
    };

    @Override
    public void onLocationChange(String location) {
        area1 = location;
        Posts.clear();
        showPostList(0);
        postAdapter.notifyDataSetChanged();
    }

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
}
