package kr.co.tacademy.mongsil.mongsil.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import kr.co.tacademy.mongsil.mongsil.Enums.DataEnum;
import kr.co.tacademy.mongsil.mongsil.Fragments.BottomEditDialogFragment;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.AsyncTaskJSONParser;
import kr.co.tacademy.mongsil.mongsil.Libraries.EndlessRecyclerOnScrollListener;
import kr.co.tacademy.mongsil.mongsil.Fragments.MiddleAloneDialogFragment;
import kr.co.tacademy.mongsil.mongsil.Fragments.MiddleSelectDialogFragment;
import kr.co.tacademy.mongsil.mongsil.MongSilApplication;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.NetworkDefineConstant;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.Post;
import kr.co.tacademy.mongsil.mongsil.Fragments.ProgressDialogFragment;
import kr.co.tacademy.mongsil.mongsil.Managers.PropertyManager;
import kr.co.tacademy.mongsil.mongsil.R;
import kr.co.tacademy.mongsil.mongsil.Adapters.ReplyAdapterCallback;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.ReplyData;
import kr.co.tacademy.mongsil.mongsil.Adapters.ReplyRecyclerViewAdapter;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.TimeData;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.WeatherData;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

public class PostDetailActivity extends BaseActivity
        implements BottomEditDialogFragment.OnBottomEditDialogListener,
        MiddleSelectDialogFragment.OnMiddleSelectDialogListener,
        MiddleAloneDialogFragment.OnMiddleAloneDialogListener,
        ReplyAdapterCallback {
    private static final String POST = "post";
    private static final String REPLY = "reply";

    private String postId;
    private String userId;

    CoordinatorLayout postDetailContainer;

    // 툴바 필드
    AppBarLayout appBar;
    Toolbar toolbar;
    TextView tbTitle;
    ImageView tbThreeDot;

    // 글 필드
    ScrollView scrollPostingBackground;
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
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postDetailContainer = (CoordinatorLayout) findViewById(R.id.post_detail_container);

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
            postId = String.valueOf(post.getPostId());
            userId = String.valueOf(post.getUserId());
            //init();
            asyncPostDetail.execute(postId);
        } else if (intent.hasExtra(REPLY)) {
            // 내가 쓴 댓글 목록
            reply = intent.getParcelableExtra(REPLY);
            asyncPostDetail.execute(String.valueOf(reply.getPostId()));
        } else if (intent.hasExtra("fcmMessage")) {
            Bundle b = getIntent().getBundleExtra("fcmMessage");
            postId = b.getString("postId");
            asyncPostDetail.execute(postId);
        }
    }

    ActionBar actionBar;

    private void init() {
        // 툴바
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        if (!post.getArea2().isEmpty() && !post.getArea2().equals("null")) {
            tbTitle.setText(String.valueOf(post.getArea1() + ", " + post.getArea2()));
        } else {
            tbTitle.setText(String.valueOf(post.getArea1()));
        }

        // background 이미지
        scrollPostingBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        if (post.getBgImg().isEmpty() || post.getBgImg().equals("null")) {
            imgBackground.setBackgroundResource(
                    WeatherData.imgFromWeatherCode(String.valueOf(post.getWeatherCode()), 4));
        } else {
            Glide.with(this)
                    .load(post.getBgImg())
                    .into(new ViewTarget<ImageView, GlideDrawable>(imgBackground) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                            // Set your resource on myView and/or start your animation here.
                            imgBackground.setBackgroundDrawable(resource);
                        }
                    });
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
                        String.valueOf(post.getWeatherCode()), 0));
        imgWeatherIcon.setAnimation(AnimationApplyInterpolater(
                R.anim.bounce_interpolator, new LinearInterpolator()));
        AnimationDrawable animation =
                (AnimationDrawable) imgWeatherIcon.getDrawable();
        if (animation != null) {
            animation.start();
        }

        // 포스트 내용
        postContent.setText(post.getContent());

        // 포스트 지역
        postLocation.setText(post.getArea1());

        // 포스트 시간
        String[] date = post.getDate().split(" ");
        postTime.setText(TimeData.PostTime(date[1]));

        // 포스트 작성자명
        postName.setText(post.getUsername());

        // 포스트 코멘트 수
        postReplyCount.setText(String.valueOf(post.getReplyCount()));

        asyncReplyList.execute(String.valueOf(post.getPostId()), "0");
        // 댓글
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(MongSilApplication.getMongSilContext());
        replyRecycler.setLayoutManager(layoutManager);
        replyRecycler.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (maxLoadSize != loadOnResult && maxLoadSize > loadOnResult) {
                    asyncReplyList.execute(postId, String.valueOf(loadOnResult));
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
                    if (post.getReplyCount() > 0) {
                        appBar.setExpanded(false);
                    }
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
                if(actionBar != null) {
                    actionBar.setHomeAsUpIndicator(0);
                }
                tbThreeDot.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveShareImg();
                    }
                }, 1000);
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
                                String.valueOf(data.getReplyId()));
                    }
                    editReply.setText("");
                }
            }
        });
        /*if(getIntent().hasExtra("fcmMessage")) {
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
        }*/
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void saveShareImg() {
        checkPermission();
        String fileName = "sns_upload_image_file.jpg";
        File snsShareDir = new File(Environment.getExternalStorageDirectory() +
                "/MongSil/");
        FileOutputStream fos;
        if (Build.VERSION.SDK_INT >= 23) {
            if (isStoragePermissionGranted()) {
                postDetailContainer.buildDrawingCache();
                Bitmap captureView = postDetailContainer.getDrawingCache();

                try {
                    if (!snsShareDir.exists()) {
                        if (!snsShareDir.mkdirs()) {
                        }
                    }
                    File file = new File(snsShareDir, fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(file);
                    captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_SUBJECT, post.getContent());
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                    Intent target = Intent.createChooser(intent, "공유하기");
                    startActivity(target);

                } catch (Exception e) {
                    Log.e("onTouch", e.toString(), e);
                }
            }
        } else {
            postDetailContainer.buildDrawingCache();
            Bitmap captureView = postDetailContainer.getDrawingCache();
            try {
                if (!snsShareDir.exists()) {
                    if (!snsShareDir.mkdirs()) {
                    }
                }
                File file = new File(snsShareDir, fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, post.getContent());
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                Intent target = Intent.createChooser(intent, "공유하기");
                startActivity(target);

            } catch (Exception e) {
                Log.e("onTouch", e.toString(), e);
            }
        }
        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        if(PropertyManager.getInstance().getUserId().equals(String.valueOf(post.getUserId()))) {
            tbThreeDot.setVisibility(View.VISIBLE);
        }
        if (snsShareDir != null && !PropertyManager.getInstance().getSaveGallery()) {
            snsShareDir.deleteOnExit();
        }
        /*Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, post.content);
        if(post.bgImg != null) {
            if (!post.bgImg.equals("null")) {
                intent.putExtra(Intent.EXTRA_TEXT, post.bgImg + "\n" + post.content);
            }
        }
        startActivity(Intent.createChooser(intent, "공유하기"));*/
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


    @Override
    public void onMiddleAlone(int select) {
        switch (select) {
            case 10:
                finish();
                break;
        }
    }

    // 글 상세내용 가져오기
    AsyncTaskJSONParser<Post> asyncPostDetail = new AsyncTaskJSONParser<Post>
            (DataEnum.POST_DATA, new AsyncTaskJSONParser.ProcessResponse<Post>() {
                @Override
                public void process(Post result) {
                    if (result != null) {
                        post = result;
                        init();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .add(MiddleAloneDialogFragment.newInstance(10),
                                        "middle_post_detail_load_fail").commit();
                    }
                }
            });

    // 댓글목록 가져오기
    AsyncTaskJSONParser<ArrayList<ReplyData>> asyncReplyList
            = new AsyncTaskJSONParser<ArrayList<ReplyData>>
            (DataEnum.REPLY_DATA, new AsyncTaskJSONParser.ProcessResponse<ArrayList<ReplyData>>() {
                @Override
                public void process(ArrayList<ReplyData> result) {
                    if (result != null && result.size() > 0) {
                        int maxResultSize = result.size();
                        maxLoadSize = result.get(0).getTotalCount();
                        if (maxResultSize < maxLoadSize) {
                            loadOnResult += maxResultSize;
                        }
                        Log.e(" 댓글 정보", maxResultSize + " " + loadOnResult);

                        replyRecycler.setVisibility(View.VISIBLE);
                        imgNoneReply.setVisibility(View.GONE);
                        noneReply.setVisibility(View.GONE);

                        replyAdapter.add(result);
                        if (result.size() <= maxLoadSize) {
                            replyRecycler.smoothScrollToPosition(result.size() - 1);
                        }
                    } else {
                        loadOnResult = 0;
                        data = null;
                        replyRecycler.setVisibility(View.GONE);
                        imgNoneReply.setVisibility(View.VISIBLE);
                        noneReply.setVisibility(View.VISIBLE);
                        replyAdapter.notifyDataSetChanged();
                    }
                }
            });

    // TODO Async보류
    // 댓글달기
    public class AsyncReplingRequest extends AsyncTask<String, String, String> {

        ProgressDialogFragment progressDialogFragment = ProgressDialogFragment.newInstance(0);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getSupportFragmentManager().beginTransaction()
                    .add(progressDialogFragment, "progress_dialog").commit();
        }

        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                RequestBody formBody = new FormBody.Builder()
                        .add("userId", args[0])
                        .add("content", args[1])
                        .add("date", TimeData.getNow())
                        .add("writerId", userId)
                        .build();
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.POST_SERVER_POST_REPLY,
                                postId))
                        .post(formBody) //반드시 post로
                        .build();
                response = client.newCall(request).execute();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                Log.e("응답코드 - ", responseCode + "");
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
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialogFragment.dismiss();
                }
            }, 1000);
            if (result.equals("success")) {
                asyncReplyList.execute(postId, "0");
                Integer replyCountUp = Integer.valueOf(postReplyCount.getText().toString());
                postReplyCount.setText(String.valueOf(replyCountUp + 1));
                if (Integer.valueOf(postReplyCount.getText().toString()) == 1) {
                    appBar.setExpanded(false);
                }
            } else if (result.equals("fail")) {
                // 실패
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleAloneDialogFragment.newInstance(3), "middle_reply_fail")
                        .commit();
            }
        }
    }

    // 댓글수정
    public class AsyncModifyReplyRequest extends AsyncTask<String, String, String> {

        ProgressDialogFragment progressDialogFragment = ProgressDialogFragment.newInstance(0);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getSupportFragmentManager().beginTransaction()
                    .add(progressDialogFragment, "progress_dialog").commit();
        }

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
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialogFragment.dismiss();
                }
            }, 1000);
            if (result.equals("success")) {
                asyncReplyList.execute(
                        postId, "0");
            } else if (result.equals("fail")) {
                // 실패
            }
        }
    }

    // 글 삭제
    AsyncTaskJSONParser<String> asyncPostRemove = new AsyncTaskJSONParser<String>
            (DataEnum.STRING_DATA.setTypeEnum(DataEnum.TypeEnum.REMOVE_POST),
                    new AsyncTaskJSONParser.ProcessResponse<String>() {
                        @Override
                        public void process(String result) {
                            if (result.equals("success")) {
                                Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else if (result.equals("fail")) {
                                getSupportFragmentManager().beginTransaction().
                                        add(MiddleAloneDialogFragment.newInstance(1), "middle_fail").commit();
                            }
                        }
                    });

    // 댓글 삭제
    AsyncTaskJSONParser<String> AsyncRemoveReply = new AsyncTaskJSONParser<String>
            (DataEnum.STRING_DATA.setTypeEnum(DataEnum.TypeEnum.REMOVE_REPLY),
                    new AsyncTaskJSONParser.ProcessResponse<String>() {
                        @Override
                        public void process(String result) {
                            if (result.equals("success")) {
                                data = null;
                                asyncReplyList.execute(postId, "");
                                Integer replyCount = Integer.valueOf(postReplyCount.getText().toString());
                                postReplyCount.setText(String.valueOf(replyCount - 1));
                            } else if (result.equals("fail")) {
                                getSupportFragmentManager().beginTransaction().
                                        add(MiddleAloneDialogFragment.newInstance(1), "middle_fail").commit();
                            }
                        }
                    });

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
                editReply.setText(data.getContent());
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
                asyncPostRemove.execute(postId);
                break;
            case 1:
                AsyncRemoveReply.execute(postId, String.valueOf(data.getReplyId()));
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