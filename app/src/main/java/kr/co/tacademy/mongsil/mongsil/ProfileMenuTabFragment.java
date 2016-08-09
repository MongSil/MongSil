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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-07-29.
 */
public class ProfileMenuTabFragment extends Fragment {
    public static final String TABINFO = "tabinfo";
    public static final String USERID = "userid";

    RecyclerView userPostRecycler;
    PostRecyclerViewAdapter postAdapter;
    ReplyRecyclerViewAdapter replyAdapter;

    private int loadOnResult = 0;
    private int maxLoadSize = 1;

    public ProfileMenuTabFragment() { }

    public static ProfileMenuTabFragment newInstance(int tabInfo, String userId) {
        ProfileMenuTabFragment fragment = new ProfileMenuTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TABINFO, tabInfo);
        bundle.putString(USERID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        final Bundle initBundle = getArguments();
        final String userId = initBundle.getString(USERID);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(
                MongSilApplication.getMongSilContext());

        userPostRecycler = (RecyclerView) view.findViewById(R.id.post_recycler);
        userPostRecycler.setLayoutManager(layoutManager);
        userPostRecycler.setPadding(16, 0, 16, 0);
        postAdapter = new PostRecyclerViewAdapter(getActivity().getSupportFragmentManager());

        if(initBundle.getInt(TABINFO) == 0) {
            // 나의 이야기 탭
            userPostRecycler.setOnScrollListener(
                    new EndlessRecyclerOnScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int current_page) {
                            if(maxLoadSize != loadOnResult) {
                                PostLoadMore(userId);
                            } else {
                                this.setLoadingState(false);
                            }
                        }
                    });
            init(userId);
        } else {
            // 내가 쓴 댓글 탭

            userPostRecycler.setOnScrollListener(
                    new EndlessRecyclerOnScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int current_page) {
                            if(maxLoadSize != loadOnResult) {
                                ReplyLoadMore(userId);
                            } else {
                                this.setLoadingState(false);
                            }
                        }
                    });
            init(userId);
        }

        return view;
    }
    private void PostLoadMore(String userId) {
        new AsyncUserPostJSONList().execute(userId, String.valueOf(loadOnResult));
    }

    private void ReplyLoadMore(String userId) {
        new AsyncUserReplyJSONList().execute(userId, String.valueOf(loadOnResult));
    }

    private void init(String userId) {
        new AsyncUserPostJSONList().execute(userId, "");
        new AsyncUserReplyJSONList().execute(userId, "");
    }

    // 나의 이야기
    public class AsyncUserPostJSONList extends AsyncTask<String, Integer, PostData> {

        @Override
        protected PostData doInBackground(String... args) {
            try{
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                // MainPostFragment 참조
                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.SERVER_USER_POST,
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
                e("fileUpLoad", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("fileUpLoad", uee.toString());
            } catch (Exception e) {
                e("fileUpLoad", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(PostData result) {
            if(result != null && result.post.size() > 0){
                int maxResultSize = result.post.size();
                loadOnResult += maxResultSize;
                maxLoadSize = result.totalCount;

                String compare = result.post.get(maxResultSize - 1).date.split(" ")[0];
                result.post.get(maxResultSize - 1).typeCode = 2;
                for (int i = maxResultSize - 1; i >= 0 ; i--) {
                    result.post.get(i).typeCode = 2;

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
                userPostRecycler.setAdapter(postAdapter);
            }
        }
    }

    // 내가 쓴 댓글
    public class AsyncUserReplyJSONList
            extends AsyncTask<String, Integer, ArrayList<ReplyData>> {

        @Override
        protected ArrayList<ReplyData> doInBackground(String... args) {
            try{
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.SERVER_USER_REPLY,
                                args[0], args[1]))
                        .build();
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONUsersReplyRequestList(
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
        protected void onPostExecute(ArrayList<ReplyData> result) {
            if(result != null && result.size() > 0){
                int maxResultSize = result.size();
                loadOnResult += maxResultSize;
                maxLoadSize = result.get(0).totalCount;

                replyAdapter.add(result);
                userPostRecycler.setAdapter(replyAdapter);
            }
        }
    }
}
