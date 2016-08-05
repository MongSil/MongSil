package kr.co.tacademy.mongsil.mongsil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ccei on 2016-07-26.
 */
public class MainPostFragment extends Fragment {
    // 임의의 지역
    String location = "대전";
    ArrayList<Post> posts;

    public MainPostFragment() { }
    public static MainPostFragment newInstance() {
        MainPostFragment f = new MainPostFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // TODO : 글목록 스와이프리프레쉬 추가
        RecyclerView postRecyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_post, container, false);
        postRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        MongSilApplication.getMongSilContext()));

        //if(!TextUtils.isEmpty(location)) {
            try {
                // 요청에 대한 포장 클래스
                // 하나의 요청에 대한 클래스를 보통 정의할 수 있음
                PostListRequest request = new PostListRequest(location);
                request.setTag(this);
                NetworkManager.getInstance().getNetworkData(request,
                        new NetworkManager.OnResultListener<PostData>() {
                            @Override
                            public void onSuccess(NetworkRequest<PostData> request, PostData result) {
                                if(result.post != null && result.post.size() >0 ) {
                                    posts.addAll(result.post);
                                } else {
                                    Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(NetworkRequest<PostData> request,
                                                  int errorCode, int responseCode,
                                                  String message, Throwable exception) {
                                // TODO : 정보 받는데 실패했다(연결이 안됐다) 알려줌
                            }
                        });
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        //}

        PostListRecyclerViewAdapter adapter =
                new PostListRecyclerViewAdapter(posts);
        postRecyclerView.setAdapter(adapter);

        return postRecyclerView;
    }
/*
    private class PostListTask extends AsyncTask<String, Integer, Post> {
        @Override
        protected Post doInBackground(String... strings) {
            String urlText = NetworkDefineConstant.SERVER_POST;
            try {
                URL url = new URL(urlText);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept", "application/json");
                int code = connection.getResponseCode();
                if(code >= HttpURLConnection.HTTP_OK &&
                        code < HttpURLConnection.HTTP_MULT_CHOICE) {
                    Gson gson = new Gson();
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    PostData data = gson.fromJson(isr, PostData.class);
                    return data.post;
                }
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            return null;
        }
    }*/
}
