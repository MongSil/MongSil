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
    public static final String AREA1 = "area1";
    public static final String SKIP = "skip";

    XRecyclerView postRecyclerView;
    PostRecyclerViewAdapter postAdapter;
    Handler handler;

    private int loadOnResult = 0;
    private int maxLoadSize = 1;

    public MainPostFragment() { }
    public static MainPostFragment newInstance() {
        MainPostFragment f = new MainPostFragment();
        return f;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        postRecyclerView =
                (XRecyclerView) inflater.inflate(R.layout.fragment_post, container, false);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setPullRefreshEnabled(false);
        postRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                // TODO : 기능 사용할까 물어봐야함
            }

            @Override
            public void onLoadMore() {
                if (loadOnResult != maxLoadSize) {
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // TODO : 내릴때마다 화면이 새로 갱신되게 하지 않게해야함
                            Bundle b = new Bundle();
                            b.putString(AREA1, "대전"); // 지역 바뀔때마다 바뀌어져야함
                            b.putInt(SKIP, loadOnResult);
                            new AsyncPostJSONList().execute(b);
                            postRecyclerView.loadMoreComplete();
                        }
                    }, 1500);
                } else {
                    postRecyclerView.noMoreLoading();
                }
            }
        });
        //if(!TextUtils.isEmpty(location)) {
        Bundle b = new Bundle();
        b.putString(AREA1, "대전");
        b.putInt(SKIP, 0);
        new AsyncPostJSONList().execute(b);
        //}
        return postRecyclerView;
    }

    // 글목록 가져오기
    public class AsyncPostJSONList extends AsyncTask<Bundle, Integer, PostData> {

        ProgressDialog dialog;

        @Override
        protected PostData doInBackground(Bundle... bundles) {
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
                                bundles[0].getString(AREA1),
                                bundles[0].getInt(SKIP)))
                        .build();
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (responseCode >= 400) return null;
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
        protected void onPostExecute(PostData result) {
            dialog.dismiss();

            // TODO : 리사이클러뷰에 새로 추가를 하는 로직이 필요함
            if(result != null && result.post.size() > 0){
                int maxResultSize = result.post.size();
                loadOnResult += maxResultSize;
                maxLoadSize = result.page.totalCount;

                String compare = result.post.get(maxResultSize - 1).date.split(" ")[0];
                for (int i = maxResultSize - 1 ; i >= 0 ; i--) {
                    String toCompare = result.post.get(i).date.split(" ")[0];
                    if(TimeData.compareDate(compare, toCompare)) {
                        result.post.add(i+1, new Post(0, result.post.get(i+1).date));
                        compare = toCompare;
                    }
                    if(i == 0) {
                        result.post.add(0, new Post(0, result.post.get(0).date));
                    }
                }

                postAdapter = new PostRecyclerViewAdapter();
                postRecyclerView.setAdapter(postAdapter);
                postAdapter.add(result.post);
            }
        }
    }
}
