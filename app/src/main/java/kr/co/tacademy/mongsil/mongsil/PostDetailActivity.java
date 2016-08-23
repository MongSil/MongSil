package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class PostDetailActivity extends BaseActivity
        implements BottomEditDialogFragment.OnBottomEditDialogListener,
        MiddleSelectDialogFragment.OnMiddleSelectDialogListener,
        ReplyAdapterCallback {
    private static final String POST = "post";
    private static final String REPLY = "reply";

    private String postId;
    private String userId;

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
    ReplyData reply;

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

    private int loadOnResult = 0;
    private int maxLoadSize = 1;

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
            userId = String.valueOf(post.userId);
            init();
        } else if (intent.hasExtra(REPLY)) {
            // 내가 쓴 댓글 목록
            reply = intent.getParcelableExtra(REPLY);
            new AsyncPostDetailJSONList().execute(String.valueOf(reply.postId));
        }
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
        Log.e("asdf", post.weatherCode+"");
        if (post.bgImg.isEmpty() || post.bgImg.equals("null")) {
            imgBackground.setImageResource(
                    WeatherData.imgFromWeatherCode(String.valueOf(post.weatherCode), 4));
        } else {
            Glide.with(this).load(post.bgImg).into(imgBackground);
        }

        if (PropertyManager.getInstance().getUserId().equals(String.valueOf(userId))) {
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

        new AsyncPostDetailReplyJSONList().execute(String.valueOf(post.postId), "0");
        // 댓글
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        replyRecycler.setLayoutManager(layoutManager);
        replyRecycler.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (maxLoadSize != loadOnResult) {
                    new AsyncPostDetailReplyJSONList().execute(postId, String.valueOf(loadOnResult));
                } else {
                    this.setLoadingState(false);
                }
            }
        });
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
                saveShareImg();
            }
        });

        // 보내기
        replySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editReply.getText().toString().isEmpty()) {
                    if (data == null) {
                        new AsyncReplingRequest().execute(
                                PropertyManager.getInstance().getUserId(),
                                editReply.getText().toString());
                    } else {
                        new AsyncModifyReplyRequest().execute(
                                PropertyManager.getInstance().getUserId(),
                                editReply.getText().toString(),
                                String.valueOf(data.replyId));
                    }
                    editReply.setText("");
                }
            }
        });
    }

    // TODO : 공유하기 추가해야함
    private void saveShareImg() {
        checkPermission();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, post.content);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && post.bgImg != null) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                FileOutputStream fos;
                String address = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/Android/data/mongsil/" + System.currentTimeMillis() + ".jpg";
                try {
                    fos = new FileOutputStream(address);
                    Bitmap captureView = BitmapUtil.viewToBitmap(imgBackground);
                    captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                File file = new File(address);
                Uri uri = Uri.fromFile(file);
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                if(!PropertyManager.getInstance().getSaveGallery()) {
                    file.deleteOnExit();
                }
            }
        }
        startActivity(Intent.createChooser(intent, "공유하기"));
    }

    private final int PERMISSION_REQUEST_STORAGE = 101;

    private void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_STORAGE);
            } else {
                //사용자가 언제나 허락
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    saveShareImg();
                    //사용자가 퍼미션을 OK했을 경우
                } else {
                    //사용자가 퍼미션을 거절했을 경우
                }
                break;
        }
    }

    // 애니메이션 인터폴레이터 적용
    private Animation AnimationApplyInterpolater(
            int resourceId, final Interpolator interpolator) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), resourceId);
        animation.setInterpolator(interpolator);
        return animation;
    }

    // 글 상세내용 가져오기
    public class AsyncPostDetailJSONList extends AsyncTask<String, Integer, Post> {
        @Override
        protected Post doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.GET_SERVER_POST_DETAIL,
                                args[0]))
                        .build();
                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();

                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONPostDetailRequestList(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("connectionFail", uee.toString());
            } catch (Exception e) {
                e("connectionFail", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Post result) {
            post = result;
            init();
        }
    }

    // 댓글목록 가져오기
    public class AsyncPostDetailReplyJSONList extends AsyncTask<String, Integer, ArrayList<ReplyData>> {
        @Override
        protected ArrayList<ReplyData> doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.GET_SERVER_POST_DETAIL_REPLY,
                                args[0], args[1]))
                        .build();
                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();

                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONReplyRequestList(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("connectionFail", uee.toString());
            } catch (Exception e) {
                e("connectionFail", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ReplyData> result) {
            if (result != null && result.size() > 0) {
                int maxResultSize = result.size();
                loadOnResult += maxResultSize;
                maxLoadSize = result.get(0).totalCount;
                replyRecycler.setVisibility(View.VISIBLE);
                imgNoneReply.setVisibility(View.GONE);
                noneReply.setVisibility(View.GONE);
                replyAdapter.add(result);
                replyRecycler.smoothScrollToPosition(result.size() - 1);
            } else {
                loadOnResult = 0;
                data = null;
                replyRecycler.setVisibility(View.GONE);
                imgNoneReply.setVisibility(View.VISIBLE);
                noneReply.setVisibility(View.VISIBLE);
                replyAdapter.notifyDataSetChanged();
            }
        }
    }

    // 댓글달기
    public class AsyncReplingRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                RequestBody formBody = new FormBody.Builder()
                        .add("userId", args[0])
                        .add("content", args[1])
                        .add("date", TimeData.getNow())
                        .add("writerId", userId)
                        .build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.POST_SERVER_POST_REPLY,
                                postId))
                        .post(formBody) //반드시 post로
                        .build();
                response = client.newCall(request).execute();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                Log.e("응답코드 - ", responseCode+"");
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
                new AsyncPostDetailReplyJSONList().execute(
                        postId, "0");
                Integer replyCountUp = Integer.valueOf(postReplyCount.getText().toString());
                postReplyCount.setText(String.valueOf(replyCountUp + 1));
            } else if (result.equals("fail")) {
                // 실패
            }
        }
    }

    // 댓글수정
    public class AsyncModifyReplyRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                RequestBody formBody = new FormBody.Builder()
                        .add("userId", args[0])
                        .add("content", args[1])
                        .add("date", TimeData.getNow())
                        .build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.PUT_SERVER_REPLY,
                                postId, args[2]))
                        .put(formBody)
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
                new AsyncPostDetailReplyJSONList().execute(
                        postId, "0");
            } else if (result.equals("fail")) {
                // 실패
            }
        }
    }

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
                Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                setResult(RESULT_OK, intent);
                finish();
            } else if (result.equals("fail")) {
                getSupportFragmentManager().beginTransaction().
                        add(MiddleAloneDialogFragment.newInstance(1), "middle_fail").commit();
            }
        }
    }

    // 댓글 삭제
    public class AsyncReplyRemoveRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.DELETE_SERVER_REPLY_REMOVE,
                                args[0], args[1]))
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
                data = null;
                new AsyncPostDetailReplyJSONList().execute(postId, "0");
                Integer replyCount = Integer.valueOf(postReplyCount.getText().toString());
                postReplyCount.setText(String.valueOf(replyCount - 1));
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
                editReply.setText(data.content);
                editReply.requestFocus();
                break;
            case 3:
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
                new AsyncPostRemoveRequest().execute(postId);
                break;
            case 1:
                new AsyncReplyRemoveRequest().execute(postId, String.valueOf(data.replyId));
                break;
        }
    }

    @Override
    public void onLongSelectCallback(ReplyData data) {
        this.data = data;
        getSupportFragmentManager().beginTransaction()
                .add(BottomEditDialogFragment.newInstance(data),
                        "bottom_reply_edit").commit();
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
        Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
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