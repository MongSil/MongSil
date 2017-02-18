package kr.co.tacademy.mongsil.mongsil.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kr.co.tacademy.mongsil.mongsil.Activities.MainActivity;
import kr.co.tacademy.mongsil.mongsil.Libraries.EndlessRecyclerOnScrollListener;
import kr.co.tacademy.mongsil.mongsil.MongSilApplication;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.NetworkDefineConstant;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.ParseDataParseHandler;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.Post;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.PostData;
import kr.co.tacademy.mongsil.mongsil.Adapters.PostRecyclerViewAdapter;
import kr.co.tacademy.mongsil.mongsil.R;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.TimeData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-07-26.
 */
public class MainPostFragment extends Fragment
        implements MainActivity.OnLocationChangeListener {
    public static final String AREA1 = "area1";

    private Socket socket;
    {
        try {
            socket = IO.socket(NetworkDefineConstant.SOCKET_SERVER_POST);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    Handler handler = new Handler();

    RecyclerView postRecyclerView;
    PostRecyclerViewAdapter postAdapter;

    private String area1;
    private int loadOnResult = 0;
    private int maxLoadSize = 1;

    public MainPostFragment() {
    }

    public static MainPostFragment newInstance(String location) {
        MainPostFragment fragment = new MainPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AREA1, location);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socket.connect();
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.on("newPostNotice", onNewPostNotice);
    }

    TextView nonePost;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        nonePost = (TextView) view.findViewById(R.id.text_none_post);
        nonePost.setText(getResources().getString(R.string.none_main_post));
        postRecyclerView =
                (RecyclerView) view.findViewById(R.id.post_recycler);

        postAdapter = new PostRecyclerViewAdapter(getActivity(), MongSilApplication.getMongSilContext());
        postRecyclerView.setAdapter(postAdapter);
        layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                this.setLoadingState(true);
                if (maxLoadSize != loadOnResult && maxLoadSize > loadOnResult) {
                    new AsyncPostJSONList().execute(area1, String.valueOf(loadOnResult));
                } else {
                    this.setLoadingState(false);
                }
            }
        });
        return view;
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(ProgressDialogFragment.newInstance(1),
                                    "fail_wait_progress").commit();
                }
            }, 2000);
        }
    };

    private Emitter.Listener onNewPostNotice = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 전체 - msg 생략
                    JSONObject jsonObject = (JSONObject) args[0];

                    // post
                    Post post = null;
                    try {
                        post = new Post();

                        post.setPostId(jsonObject.getInt("postId"));
                        post.setUserId(jsonObject.getInt("userId"));
                        post.setUsername(jsonObject.getString("username"));
                        post.setProfileImg(jsonObject.getString("profileImg"));
                        post.setContent(jsonObject.getString("content"));
                        post.setBgImg(jsonObject.getString("bgImg"));
                        post.setWeatherCode(jsonObject.getInt("weatherCode"));
                        post.setArea1(jsonObject.getString("area1"));
                        post.setArea2(jsonObject.getString("area2"));
                        post.setDate(jsonObject.getString("date"));
                        post.setReplyCount(jsonObject.getInt("replyCount"));

                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                    if(area1.equals(post.getArea1())) {
                        postAdapter.addPost(post);
                        postAdapter.notifyDataSetChanged();

                        if (maxLoadSize != loadOnResult && maxLoadSize > loadOnResult) {
                            loadOnResult = loadOnResult + 1;
                        }
                    }
                }
            });
        }
    };

    @Override
    public void onLocationChange(String location) {
        area1 = location;
        loadOnResult = 0;
        postAdapter.getItems().clear();
        postRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                this.setLoadingState(true);
                if (maxLoadSize != loadOnResult && maxLoadSize > loadOnResult) {
                    new AsyncPostJSONList().execute(area1, String.valueOf(loadOnResult));
                } else {
                    this.setLoadingState(false);
                }
            }
        });
        new AsyncPostJSONList().execute(area1, "");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null) {
            area1 = getArguments().getString(AREA1);
        }
        loadOnResult = 0;
        postAdapter.getItems().clear();
        new AsyncPostJSONList().execute(area1, "");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.off("newPostNotice", onNewPostNotice);
    }

    // 글목록 가져오기
    public class AsyncPostJSONList extends AsyncTask<String, Integer, PostData> {
        @Override
        protected PostData doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.GET_SERVER_POST,
                                args[0], args[1]))
                        .build();
                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();

                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONPostRequestAllList(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("connectionFail", uee.toString());
            } catch (Exception e) {
                e("connectionFail", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(PostData result) {
            if (result != null && result.getPost().size() > 0) {
                nonePost.setVisibility(View.GONE);
                int maxResultSize = result.getPost().size();
                loadOnResult += maxResultSize;
                maxLoadSize = result.getTotalCount();
                // Result의 Date와 오늘 날짜를 비교하는 로직
                String compare = result.getPost().get(maxResultSize - 1).getDate().split(" ")[0];
                for (int i = maxResultSize - 1; i >= 0; i--) {
                    String toCompare = result.getPost().get(i).getDate().split(" ")[0];
                    if (TimeData.compareDate(compare, toCompare)) {
                        result.getPost().add(i + 1, new Post(0, result.getPost().get(i + 1).getDate()));
                        compare = toCompare;
                    }
                    if (i == 0) {
                        result.getPost().add(0, new Post(0, result.getPost().get(0).getDate()));
                    }
                }

                postAdapter.add(result.getPost());
            } else {
                nonePost.setVisibility(View.GONE);
            }
        }
    }
}
