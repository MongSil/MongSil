package kr.co.tacademy.mongsil.mongsil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
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

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        postRecyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_post, container, false);

        postAdapter = new PostRecyclerViewAdapter(MongSilApplication.getMongSilContext());
        postRecyclerView.setAdapter(postAdapter);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setOnScrollListener(
                new EndlessRecyclerOnScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int current_page) {
                        if (maxLoadSize != loadOnResult) {
                            new AsyncPostJSONList().execute(area1, String.valueOf(loadOnResult));
                        } else {
                            this.setLoadingState(false);
                        }
                    }
                });
        return postRecyclerView;
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
                        post.postId = jsonObject.getInt("postId");
                        post.userId = jsonObject.getInt("userId");
                        post.username = jsonObject.getString("username");
                        post.profileImg = jsonObject.getString("profileImg");
                        post.content = jsonObject.getString("content");
                        post.bgImg = jsonObject.getString("bgImg");
                        post.weatherCode = jsonObject.getInt("weatherCode");
                        post.area1 = jsonObject.getString("area1");
                        post.area2 = jsonObject.getString("area2");
                        post.date = jsonObject.getString("date");
                        post.replyCount = jsonObject.getInt("replyCount");
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                    if(area1.equals(post.area1)) {
                        postAdapter.addPost(post);
                        postAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    @Override
    public void onLocationChange(String location) {
        area1 = location;
        new AsyncPostJSONList().execute(area1, "");
        postAdapter.items.clear();
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null) {
            area1 = getArguments().getString(AREA1);
        }
        new AsyncPostJSONList().execute(area1, "");
        postAdapter.items.clear();
        postAdapter.notifyDataSetChanged();
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
            if (result != null && result.post.size() > 0) {
                int maxResultSize = result.post.size();
                loadOnResult += maxResultSize;
                maxLoadSize = result.totalCount;

                // Result의 Date와 오늘 날짜를 비교하는 로직
                String compare = result.post.get(maxResultSize - 1).date.split(" ")[0];
                for (int i = maxResultSize - 1; i >= 0; i--) {
                    String toCompare = result.post.get(i).date.split(" ")[0];
                    if (TimeData.compareDate(compare, toCompare)) {
                        result.post.add(i + 1, new Post(0, result.post.get(i + 1).date));
                        compare = toCompare;
                    }
                    if (i == 0) {
                        result.post.add(0, new Post(0, result.post.get(0).date));
                    }
                }

                postAdapter.add(result.post);
            }
        }
    }
}
