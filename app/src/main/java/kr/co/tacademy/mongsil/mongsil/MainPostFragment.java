package kr.co.tacademy.mongsil.mongsil;

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

    RecyclerView postRecyclerView;
    PostRecyclerViewAdapter postAdapter;

    private int loadOnResult = 0;
    private int maxLoadSize = 1;

    public MainPostFragment() {
    }
    public static MainPostFragment newInstance(String location) {
        MainPostFragment fragment = new MainPostFragment();
        Bundle bundle;
        bundle = new Bundle();
        bundle.putString(AREA1, location);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        postRecyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_post, container, false);

        postAdapter = new PostRecyclerViewAdapter(getContext());
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        postRecyclerView.setLayoutManager(layoutManager);
        postRecyclerView.setOnScrollListener(
                new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(maxLoadSize != loadOnResult) {
                    LoadMore();
                } else {
                    this.setLoadingState(false);
                }
            }
        });

        return postRecyclerView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        new AsyncPostJSONList().execute(bundle.getString(AREA1), "");
        postAdapter.items.clear();
        postAdapter.notifyDataSetChanged();
    }

    private void LoadMore() {
        Bundle bundle = getArguments();
        new AsyncPostJSONList().execute(bundle.getString(AREA1), String.valueOf(loadOnResult));
    }


    // 글목록 가져오기
    public class AsyncPostJSONList extends AsyncTask<String, Integer, PostData> {
        @Override
        protected PostData doInBackground(String... args) {
            try{
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                // TODO : 다른사람 프로필 사진을 클릭하면 프로필이 뜨게 만들어야함
                // TODO : 다른사람 프로필에서 리사이클러뷰는 그 사람 정보의 리스트

                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.GET_SERVER_POST,
                                args[0], args[1]))
                        .build();
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();

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
        protected void onPostExecute(PostData result) {
            if(result != null && result.post.size() > 0){
                int maxResultSize = result.post.size();
                loadOnResult += maxResultSize;
                maxLoadSize = result.totalCount;

                // Result의 Date와 오늘 날짜를 비교하는 로직
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
