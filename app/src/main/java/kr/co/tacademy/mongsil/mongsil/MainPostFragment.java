package kr.co.tacademy.mongsil.mongsil;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
    XRecyclerView postRecyclerView;
    Handler handler;

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
        postRecyclerView =
                (XRecyclerView) inflater.inflate(R.layout.fragment_post, container, false);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                // TODO : 기능 사용할까 물어봐야함
            }

            @Override
            public void onLoadMore() {
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO : 내릴때마다 새로 갱신되게 하지 않게해야함
                        Bundle b = new Bundle();
                        b.putString("location", "대전"); // 지역 바뀔때마다 바뀌어져야함
                        b.putInt("skip", layoutManager.findLastCompletelyVisibleItemPosition()+1);
                        new AsyncPostJSONList().execute(b);
                        postRecyclerView.refreshComplete();
                    }
                }, 2000);
            }
        });
        //if(!TextUtils.isEmpty(location)) {
            Bundle b = new Bundle();
            b.putString("location", "대전");
            b.putInt("skip", 0);
            new AsyncPostJSONList().execute(b);
        //}
        return postRecyclerView;
    }

    // 글목록 가져오기
    public class AsyncPostJSONList extends AsyncTask<Bundle, Integer,
            ArrayList<Post>> {

        ProgressDialog dialog;

        @Override
        protected ArrayList<Post> doInBackground(Bundle... bundles) {
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                // TODO : 밑으로 내려올 때마다(로딩) skip을 통해 받아와야함(한페이지 10개)
                // TODO : 시간체크를 해서 전 시간이 다음 시간과 맞지 않으면 코드를 보냄
                // TODO : 보내진 코드로 리사이클뷰 date로 재생성
                // TODO : 다른사람 프로필 사진을 클릭하면 프로필이 뜨게 만들어야함
                // TODO : 다른사람 프로필에서 리사이클러뷰는 그 사람 정보의 리스트

                // TODO : 지역마다 바뀌는걸로 바꿔야함
                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.SERVER_POST,
                                bundles[0].getString("location"),
                                bundles[0].getInt("skip")))
                        .build();
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.getJSONPostRequestAllList(
                            new StringBuilder(responseBody.string()));
                }
                responseBody.close();
            }catch (UnknownHostException une) {
                e("fileUpLoad", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("fileUpLoad", uee.toString());
            } catch (Exception e) {


                e("fileUpLoad", e.toString());
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
                PostRecyclerViewAdapter postAdapter =
                        new PostRecyclerViewAdapter(result);
                postAdapter.notifyDataSetChanged();
                postRecyclerView.setAdapter(postAdapter);
            }
        }
    }
}
