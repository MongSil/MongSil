package kr.co.tacademy.mongsil.mongsil;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
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

    RecyclerView postRecyclerView;
    ProgressBar footerProgress;
    PostRecyclerViewAdapter postAdapter;

    Handler handler;

    private int loadOnResult = 0;
    private int maxLoadSize = 1;

    public MainPostFragment() {
    }
    public static MainPostFragment newInstance(String location) {
        MainPostFragment fragment = new MainPostFragment();
        Bundle b = new Bundle();
        b.putString(AREA1, location);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        final Bundle initBundle = getArguments();
        postRecyclerView =
                (RecyclerView) view.findViewById(R.id.post_recycler);
        footerProgress = (ProgressBar) view.findViewById(R.id.footer_progress);
        postAdapter = new PostRecyclerViewAdapter();
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setOnScrollListener(
                new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(maxLoadSize != loadOnResult) {
                    LoadMore(initBundle.getString(AREA1));
                } else {
                    this.setLoadingState(false);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncPostJSONList().execute("", "");
    }

    private void LoadMore(final String location) {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new AsyncPostJSONList().execute(location, String.valueOf(loadOnResult));
                // 지역 바뀔때마다 바뀌어야함
            }
        }, 2000);
    }


    // 글목록 가져오기
    public class AsyncPostJSONList extends AsyncTask<String, Integer, PostData> {

        ProgressDialog dialog;

        @Override
        protected PostData doInBackground(String... args) {
            try{
                //OKHttp3사용
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                // TODO : 밑으로 내려올 때마다(로딩) skip을 통해 받아와야함(한페이지 10개)

                // TODO : 다른사람 프로필 사진을 클릭하면 프로필이 뜨게 만들어야함
                // TODO : 다른사람 프로필에서 리사이클러뷰는 그 사람 정보의 리스트

                // TODO : 지역마다 바뀌는걸로 바꿔야함
                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.SERVER_POST,
                                args[0], args[1]))
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
                e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("connectionFail", uee.toString());
            } catch (Exception e) {
                e("connectionFail", e.toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // TODO : NullPointer가 떴었음.
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
                maxLoadSize = result.totalCount;

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
                postAdapter.add(result.post);
                postRecyclerView.setAdapter(postAdapter);
            }
        }
    }
}
