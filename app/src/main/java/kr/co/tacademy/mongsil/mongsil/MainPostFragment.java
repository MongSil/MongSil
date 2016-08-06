package kr.co.tacademy.mongsil.mongsil;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-07-26.
 */
public class MainPostFragment extends Fragment {
    // 임의의 지역
    String location = "대전";

    RecyclerView postRecyclerView;

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
        postRecyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_post, container, false);
        postRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        MongSilApplication.getMongSilContext()));

        //if(!TextUtils.isEmpty(location)) {
            new AsyncPostJSONList().execute("jsonlist");
        //}
        return postRecyclerView;
    }

    // 글목록 가져오기
    public class AsyncPostJSONList extends AsyncTask<String, Integer,
            ArrayList<Post>> {

        ProgressDialog dialog;
        @Override
        protected ArrayList<Post> doInBackground(
                String... params) {
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.SERVER_POST)
                        .build();
                //동기 방식
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONPostRequestAllList(
                            new StringBuilder(responseBody.string()));
                }
            }catch (UnknownHostException une) {
                e("fileUpLoad", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("fileUpLoad2", uee.toString());
            } catch (Exception e) {
                e("fileUpLoad3", e.toString());
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getContext(),
                    "","잠시만 기다려 주세요 ...", true);
        }

        @Override
        protected void onPostExecute(ArrayList<Post>
                                             result) {
            dialog.dismiss();

            if(result != null && result.size() > 0){
                PostListRecyclerViewAdapter postAdapter =
                        new PostListRecyclerViewAdapter(result);
                postAdapter.notifyDataSetChanged();
                postRecyclerView.setAdapter(postAdapter);
            }
        }
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
