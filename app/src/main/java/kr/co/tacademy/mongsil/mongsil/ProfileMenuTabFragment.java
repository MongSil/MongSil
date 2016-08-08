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
import android.widget.TextView;

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

    ProgressBar footerProgress;
    RecyclerView userPostRecycler;
    PostRecyclerViewAdapter postAdapter;
    Handler handler;

    int lastVisibleItem;
    private int loadOnResult = 0;
    private int maxLoadSize = 1;

    public ProfileMenuTabFragment() { }

    public static ProfileMenuTabFragment newInstance(int tabInfo, int userId) {
        ProfileMenuTabFragment fragment = new ProfileMenuTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TABINFO, tabInfo);
        bundle.putInt(USERID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        final Bundle initBundle = getArguments();
        final String userId = String.valueOf(initBundle.getInt(USERID));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(
                MongSilApplication.getMongSilContext());

        userPostRecycler = (RecyclerView) view.findViewById(R.id.post_recycler);
        footerProgress = (ProgressBar) view.findViewById(R.id.footer_progress);
        userPostRecycler.setLayoutManager(layoutManager);
        postAdapter = new PostRecyclerViewAdapter(getActivity().getSupportFragmentManager());

        if(initBundle.getInt(TABINFO) == 0) {
            // 나의 이야기 탭
            // TODO : 만들어야함
            userPostRecycler.setPadding(16, 0, 16, 0);
            userPostRecycler.setOnScrollListener(
                    new EndlessRecyclerOnScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int current_page) {
                            if(maxLoadSize != loadOnResult) {
                                LoadMore(userId);
                            } else {
                                this.setLoadingState(false);
                            }
                        }
                    });
            new AsyncUserPostJSONList().execute(userId, "");
        } else {
            // 내가 쓴 댓글 탭
            userPostRecycler.setAdapter(new MyCommentRecyclerViewAdapter());
        }

        return view;
    }
    private void LoadMore(final String userId) {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new AsyncUserPostJSONList().execute(
                        String.valueOf(userId),
                        String.valueOf(loadOnResult));
            }
        }, 2000);
    }
    // 내가 쓴 댓글 어답터
    public static class MyCommentRecyclerViewAdapter
            extends RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder> {
        private static final int TEMP_COUNT = 5;

        MyCommentRecyclerViewAdapter() { }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final TextView textPostContent, textMyCommentContent, textMyCommentTime;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                textPostContent =
                        (TextView) view.findViewById(R.id.text_post_content);
                textMyCommentContent =
                        (TextView) view.findViewById(R.id.text_my_comment_content);
                textMyCommentTime =
                        (TextView) view.findViewById(R.id.text_my_comment_time);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.layout_my_comment_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textPostContent.setText(String.valueOf("댓글 내용이다" + position));
            int date = 20+position;
            holder.textMyCommentTime.setText(String.valueOf(String.valueOf("7월 " + date + "일")));
        }

        @Override
        public int getItemCount() {
            return TEMP_COUNT;
        }
    }
    // 글목록 가져오기
    public class AsyncUserPostJSONList extends AsyncTask<String, Integer, PostData> {

        ProgressDialog dialog;

        @Override
        protected PostData doInBackground(String... args) {
            try{
                //OKHttp3사용
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
}
