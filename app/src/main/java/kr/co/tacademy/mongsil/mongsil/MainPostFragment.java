package kr.co.tacademy.mongsil.mongsil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    ArrayList<Post> posts;

    // 임의의 지역
    String location = "대전";

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
        RecyclerView postRecyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_post, container, false);
        PostRecyclerViewAdapter adapter = new PostRecyclerViewAdapter();
        postRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        MongSilApplication.getMongSilContext()));
        postRecyclerView.setAdapter(adapter);

        /*if(!TextUtils.isEmpty(location)) {
            try {
                // 요청에 대한 포장 클래스
                // 하나의 요청에 대한 클래스를 보통 정의할 수 있음
                PostListRequest request = new PostListRequest(location);
                request.setTag(this);
                NetworkManager.getInstance().getNetworkData(request,
                        new NetworkManager.OnResultListener<Post>() {
                            @Override
                            public void onSuccess(NetworkRequest<Post> request, Post result) {
                                posts.add(result);
                            }
                            @Override
                            public void onFailure(NetworkRequest<Post> request, int errorCode, int responseCode, String message, Throwable exception) {
                                //
                            }
                        });
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        }*/

        ////// test code
        PostData data = new PostData();
        PostData data1 = new PostData();
        PostData data2 = new PostData();
        PostData data3 = new PostData();
        PostData data4 = new PostData();
        data.setTimeData(0, "Today");
        adapter.add(data);
        data1.setData(1, "10:25 AM", R.mipmap.ic_launcher, "스님",
                "날이 밝구나", 0, 0, 10);
        adapter.add(data1);
        data2.setData(1, "02:25 PM", R.mipmap.ic_launcher, "주지스님",
                "날씨가 덥구나", 0, R.drawable.back_cloud, 3);
        adapter.add(data2);
        data3.setTimeData(0, "2016.07.29");
        adapter.add(data3);
        data4.setData(1, "05:20 PM", R.mipmap.ic_launcher, "동자스님",
                "밖에 나가고 싶어요 빼앢", 0, 0, 1);
        adapter.add(data4);
        //////

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
