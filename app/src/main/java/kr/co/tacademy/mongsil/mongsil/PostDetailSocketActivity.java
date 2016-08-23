package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Deprecated
public class PostDetailSocketActivity extends BaseActivity
        implements BottomEditDialogFragment.OnBottomEditDialogListener,
        MiddleSelectDialogFragment.OnMiddleSelectDialogListener,
        ReplyAdapterCallback {


    private static final String POST = "post";
    private static final String POSTID = "postid";

    String postId;
    String writerId;

    ScrollView scrollPostingBackground;

    // 툴바 필드
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView tbTitle;
    ImageView tbThreeDot;

    // 글 필드
    ImageView imgBackground, imgWeatherIcon;
    TextView postContent, postLocation, postTime, postName, postReplyCount;
    Post post;

    // 댓글 필드
    ReplyData data;
    LinearLayout replyContainer;
    RelativeLayout replyEditContainer;
    boolean isReplyContainer;
    RecyclerView replyRecycler;
    ReplyRecyclerViewAdapter replyAdapter;
    ImageView imgNoneReply;
    TextView share, noneReply, replySend;
    EditText editReply;

    private boolean isModify = false;
    private boolean isDelete = false;

    private Socket socket;
    {
        try {
            socket = IO.socket(NetworkDefineConstant.SOCKET_SERVER_POST);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        post = new Post();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        appBar = (AppBarLayout) findViewById(R.id.appbar_post_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tbThreeDot = (ImageView) toolbar.findViewById(R.id.img_threeDot);

        scrollPostingBackground = (ScrollView) findViewById(R.id.scroll_posting_background);
        imgBackground = (ImageView) findViewById(R.id.img_post_detail_background);
        imgWeatherIcon = (ImageView) findViewById(R.id.img_weather_icon);

        postContent = (TextView) findViewById(R.id.text_post_content);
        postLocation = (TextView) findViewById(R.id.text_post_location);
        postTime = (TextView) findViewById(R.id.text_post_time);
        postName = (TextView) findViewById(R.id.text_post_name);
        postReplyCount = (TextView) findViewById(R.id.text_post_comment_count);
        editReply = (EditText) findViewById(R.id.edit_reply);

        replyEditContainer = (RelativeLayout) findViewById(R.id.reply_edit_container);
        replyEditContainer.setVisibility(View.GONE);
        replyContainer =
                (LinearLayout) findViewById(R.id.comment_bottom_sheet);
        replyContainer.setVisibility(View.GONE);

        replyAdapter = new ReplyRecyclerViewAdapter(this);
        replyRecycler = (RecyclerView) findViewById(R.id.reply_recycler);
        imgNoneReply = (ImageView) findViewById(R.id.img_none_reply_icon);
        noneReply = (TextView) findViewById(R.id.text_none_reply);
        editReply = (EditText) findViewById(R.id.edit_reply);
        replySend = (TextView) findViewById(R.id.text_reply_send);

        Intent intent = getIntent();
        if (intent.hasExtra(POST)) {
            // 글 목록, 나의 이야기 목록
            post = intent.getParcelableExtra(POST);
            postId = String.valueOf(post.postId);
            writerId = String.valueOf(post.userId);
            init();
        } else if (intent.hasExtra(POSTID)) {
            // 내가 쓴 댓글 목록
            postId = intent.getStringExtra(POSTID);
            showPostDetail();
        }
        socketInit();
    }

    private void init() {
        // 툴바
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        if (!post.area2.isEmpty()) {
            tbTitle.setText(String.valueOf(post.area1 + ", " + post.area2));
        } else {
            tbTitle.setText(String.valueOf(post.area1));
        }

        // background 이미지
        scrollPostingBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        if (!post.bgImg.isEmpty()) {
            Glide.with(this).load(post.bgImg).into(imgBackground);
        } else {
            imgBackground.setImageResource(
                    WeatherData.imgFromWeatherCode(String.valueOf(post.weatherCode), 4));
        }

        if (PropertyManager.getInstance().getUserId().equals(String.valueOf(post.userId))) {
            tbThreeDot.setVisibility(View.VISIBLE);
            tbThreeDot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSupportFragmentManager().beginTransaction().add(
                            BottomEditDialogFragment.newInstance(), "bottom_post_detail").commit();
                }
            });
        } else {
            tbThreeDot.setVisibility(View.GONE);
        }
        // 날씨 아이콘
        imgWeatherIcon.setImageResource(
                WeatherData.imgFromWeatherCode(
                        String.valueOf(post.weatherCode), 0));
        imgWeatherIcon.setAnimation(AnimationApplyInterpolater(
                R.anim.bounce_interpolator, new LinearInterpolator()));
        AnimationDrawable animation =
                (AnimationDrawable) imgWeatherIcon.getDrawable();
        if(animation != null) {
            animation.start();
        }

        // 포스트 내용
        postContent.setText(post.content);

        // 포스트 지역
        postLocation.setText(post.area1);

        // 포스트 시간
        String[] date = post.date.split(" ");
        postTime.setText(TimeData.PostTime(date[1]));

        // 포스트 작성자명
        postName.setText(post.username);

        // 포스트 코멘트 수
        postReplyCount.setText(String.valueOf(post.replyCount));

        //new AsyncPostDetailReplyJSONList().execute(String.valueOf(post.postId));
        // 댓글
        replyRecycler.setLayoutManager(
                new LinearLayoutManager(MongSilApplication.getMongSilContext()));
        replyRecycler.setAdapter(replyAdapter);
        postReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isReplyContainer) {
                    appBar.setExpanded(false);
                    Animation upAnim = AnimationUtils.loadAnimation(
                            getApplicationContext(), R.anim.anim_slide_in_bottom);
                    upAnim.setFillAfter(true);
                    replyEditContainer.startAnimation(upAnim);
                    replyEditContainer.setVisibility(View.VISIBLE);
                    replyContainer.setVisibility(View.VISIBLE);
                    isReplyContainer = true;
                } else {
                    appBar.setExpanded(true);
                    Animation downAnim = AnimationUtils.loadAnimation(
                            getApplicationContext(), R.anim.anim_slide_out_bottom);
                    downAnim.setFillAfter(true);
                    downAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            replyEditContainer.setVisibility(View.GONE);
                            replyContainer.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    replyEditContainer.startAnimation(downAnim);
                    isReplyContainer = false;
                }
            }
        });

        // 공유하기
        share = (TextView) findViewById(R.id.text_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                //intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, post.content);
                //intent.putExtra(Intent.EXTRA_STREAM, post.bgImg);

                Intent chooser = Intent.createChooser(intent, "공유");
                startActivity(chooser);
            }
        });

        // 보내기
        replySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editReply.getText().toString().isEmpty()) {
                    if (data == null) {
                        writeReply(editReply.getText().toString());
                    } else {
                        modifyReply(data.replyId, editReply.getText().toString());
                    }
                    editReply.setText("");
                }
            }
        });
    }

    // 애니메이션 인터폴레이터 적용
    private Animation AnimationApplyInterpolater(
            int resourceId, final Interpolator interpolator) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), resourceId);
        animation.setInterpolator(interpolator);
        return animation;
    }

    private void socketInit() {
        socket.on("err", onErr);
        socket.on("sendPostDetail", onPostDetail);
        socket.on("sendReplyList", onReplyList);
        socket.on("writeReplyOk", onWriteReplyOk);
        socket.on("deleteReplyOk", onDeleteReplyOk);
        socket.on("deletePostOk", onDeletePostOk);
        socket.connect();
        showReplyList(0);
        replyAdapter.notifyDataSetChanged();
    }

    private void showPostDetail() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put("postId", postId);
            socket.emit("showPostDetail", jsonObject);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private void showReplyList(int skip) {
        final int COUNT = 20;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put("postId", postId)
                    .put("userId", PropertyManager.getInstance().getUserId())
                    .put("skip", skip)
                    .put("count", COUNT);
            socket.emit("showReplyList", jsonObject);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private void writeReply(String content) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put("postId", postId)
                    .put("userId", PropertyManager.getInstance().getUserId())
                    .put("content", content)
                    .put("date", TimeData.getNow())
                    .put("writerId", writerId);
            socket.emit("writeReply", jsonObject);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private void modifyReply(int replyId, String content) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put("replyId", replyId)
                    .put("content", content);
            socket.emit("modifyReply", jsonObject);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private void deleteReply(int replyId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put("replyId", replyId);
            socket.emit("deleteReply", jsonObject);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        isDelete = false;
    }

    private void deletePost() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject
                    .put("postId", postId);
            socket.emit("deletePost", jsonObject);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        isDelete = false;
    }

    private Emitter.Listener onErr = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String err;
                    try {
                        err = data.getString("err");
                        Log.e("err", " " + err);
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onPostDetail = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    Post postData = null;
                    try {
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                        postData = new Post();

                        postData.postId = jsonData.getInt("postId");
                        postData.userId = jsonData.getInt("userId");
                        postData.username = jsonData.getString("username");
                        postData.content = jsonData.getString("content");
                        postData.bgImg = jsonData.getString("bgImg");
                        postData.weatherCode = jsonData.getInt("weatherCode");
                        postData.area1 = jsonData.getString("area1");
                        postData.area2 = jsonData.getString("area2");
                        postData.date = jsonData.getString("date");
                        postData.replyCount = jsonData.getInt("replyCount");

                    } catch (JSONException je) {
                        Log.e("Socket:PostDetail", "JSON파싱 중 에러발생", je);
                    }

                    if (postData != null) {
                        post = postData;
                        init();
                    }
                }
            });
        }
    };

    private Emitter.Listener onReplyList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    JSONArray jsonUsersReply = null;
                    ArrayList<ReplyData> jsonReplyList = null;

                    try {
                        jsonUsersReply = jsonObject.getJSONArray("reply");
                        jsonReplyList = new ArrayList<ReplyData>();
                        int jsonArrSize = jsonUsersReply.length();
                        for (int i = 0; i < jsonArrSize; i++) {
                            JSONObject jData = jsonUsersReply.getJSONObject(i);
                            ReplyData replyData = new ReplyData();
                            if (i == 0) {
                                replyData.totalCount = jsonObject.getInt("totalCount");
                            }

                            replyData.replyId = jData.getInt("replyId");
                            replyData.userId = jData.getInt("userId");
                            replyData.username = jData.getString("username");
                            replyData.profileImg = jData.getString("profileImg");
                            replyData.content = jData.getString("content");
                            replyData.date = jData.getString("date");
                            replyData.postId = jData.getInt("postId");

                            jsonReplyList.add(replyData);
                        }
                    } catch (JSONException je) {
                        Log.e("Socket:ReplyRequest", "JSON파싱 중 에러발생", je);
                    }

                    if (jsonReplyList != null && jsonReplyList.size() > 0) {
                        replyRecycler.setVisibility(View.VISIBLE);
                        imgNoneReply.setVisibility(View.GONE);
                        noneReply.setVisibility(View.GONE);
                        replyAdapter.add(jsonReplyList);
                        replyRecycler.smoothScrollToPosition(jsonReplyList.size() - 1);
                    } else {
                        data = null;
                        replyRecycler.setVisibility(View.GONE);
                        imgNoneReply.setVisibility(View.VISIBLE);
                        noneReply.setVisibility(View.VISIBLE);
                        replyAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private Emitter.Listener onWriteReplyOk = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showReplyList(0);
                    replyAdapter.notifyDataSetChanged();
                    if (!isModify) {
                        Integer replyCountUp = Integer.valueOf(postReplyCount.getText().toString());
                        postReplyCount.setText(String.valueOf(replyCountUp + 1));
                    } else {
                        isModify = false;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDeleteReplyOk = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = (JSONObject)args[0];
                        String msg = jsonObject.getString("msg");
                        data = null;
                        showReplyList(0);
                        replyAdapter.notifyDataSetChanged();
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                    Integer replyCountUp = Integer.valueOf(postReplyCount.getText().toString());
                    postReplyCount.setText(String.valueOf(replyCountUp - 1));
                }
            });
        }
    };

    private Emitter.Listener onDeletePostOk = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String msg = null;
                    try {
                        JSONObject jsonObject = (JSONObject)args[0];
                        msg = jsonObject.getString("msg");
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                    if (msg != null) {
                        if (msg.equals("success")) {
                            Intent intent = new Intent(PostDetailSocketActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("post_remove", true);
                            startActivity(intent);
                            finish();
                        } else if (msg.equals("fail")) {
                            getSupportFragmentManager().beginTransaction().
                                    add(MiddleAloneDialogFragment.newInstance(1), "middle_fail").commit();
                        }
                    }
                }
            });
        }
    };

    // 글 삭제
    public class AsyncPostRemoveRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.DELETE_SERVER_POST_REMOVE,
                                args[0]))
                        .delete()
                        .build();

                response = client.newCall(request).execute();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    Log.e("response결과", responseCode + "---" + response.message()); //읃답에 대한 메세지(OK)
                    Log.e("response응답바디", response.body().string()); //json으로 변신
                    return "success";
                }
            } catch (UnknownHostException une) {
                Log.e("aa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                Log.e("bb", uee.toString());
            } catch (Exception e) {
                Log.e("cc", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return "fail";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("success")) {
                Intent intent = new Intent(PostDetailSocketActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("post_remove", true);
                startActivity(intent);
                finish();
            } else if (result.equals("fail")) {
                getSupportFragmentManager().beginTransaction().
                        add(MiddleAloneDialogFragment.newInstance(1), "middle_fail").commit();
            }
        }
    }

    // 글 수정과 글 삭제, 댓글 수정과 댓글 삭제 하단 다이어로그
    @Override
    public void onSelectBottomEdit(int select, ReplyData data) {
        switch (select) {
            case 0:
                Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
                intent.putExtra("postdata", post);
                startActivity(intent);
                break;
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleSelectDialogFragment.newInstance(0),
                                "middle_post_remove").commit();
                break;
            case 2:
                this.data = data;
                isModify = true;
                editReply.setText(data.content);
                editReply.requestFocus();
                break;
            case 3:
                this.data = data;
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleSelectDialogFragment.newInstance(1),
                                "middle_reply_remove").commit();
                break;
        }
    }

    // 글 삭제, 댓글 삭제 다이어로그
    @Override
    public void onMiddleSelect(int select) {
        switch (select) {
            case 0:
                isDelete = true;
                deletePost();
                //new AsyncPostRemoveRequest().execute(postId);
                break;
            case 1:
                isDelete = true;
                deleteReply(data.replyId);
                //new AsyncReplyRemoveRequest().execute(postId, String.valueOf(data.replyId));
                break;
        }
    }

    @Override
    public void onReplySelectCallback(ReplyData data) {
        this.data = data;
        getSupportFragmentManager().beginTransaction()
                .add(BottomEditDialogFragment.newInstance(data),
                        "bottom_reply_edit").commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        socket.off("err", onErr);
        socket.off("sendPostDetail", onPostDetail);
        socket.off("sendReplyList", onReplyList);
        socket.off("writeReplyOk", onWriteReplyOk);
        socket.off("deleteReplyOk", onDeleteReplyOk);
        socket.off("deletePostOk", onDeletePostOk);
    }

    @Override
    public void onBackPressed() {
        if (isReplyContainer) {
            Animation downAnim = AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.anim_slide_out_bottom);
            downAnim.setFillAfter(true);
            downAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    replyEditContainer.setVisibility(View.GONE);
                    replyContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            replyEditContainer.startAnimation(downAnim);
            isReplyContainer = false;
            appBar.setExpanded(true);
        } else {
            super.onBackPressed();
            toMainActivityFromthis();
        }
    }

    private void toMainActivityFromthis() {
        Intent intent = new Intent(PostDetailSocketActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toMainActivityFromthis();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
