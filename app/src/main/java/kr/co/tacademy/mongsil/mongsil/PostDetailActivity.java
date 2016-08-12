package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
                MiddleSelectDialogFragment.OnMiddleSelectDialogListener{
    private static final String POST_ID = "postid";

    String postId;
    // 툴바 필드
    Toolbar toolbar;
    TextView tbTitle;
    ImageView tbThreeDot;

    // 글 필드
    ImageView imgBackground, imgWeatherIcon;
    TextView postContent, postLocation, postTime, postName, postReplyCount;
    Post post;

    RelativeLayout replyEditContainer;
    boolean isReplyContainer;

    // 댓글 필드
    LinearLayout replyContainer;
    RecyclerView replyRecycler;
    ReplyRecyclerViewAdapter replyAdapter;
    ImageView imgNoneReply;
    TextView share, noneReply, replySend;
    EditText editReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        post = new Post();
        Intent intent = getIntent();
        postId = intent.getStringExtra(POST_ID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setContentInsetsRelative(0, 16);
        tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tbThreeDot = (ImageView) toolbar.findViewById(R.id.img_threeDot);

        imgBackground = (ImageView) findViewById(R.id.img_post_detail_background);
        imgWeatherIcon = (ImageView) findViewById(R.id.img_preview_weather_icon);

        postContent = (TextView) findViewById(R.id.text_preview_post_content);
        postLocation = (TextView) findViewById(R.id.text_preview_post_location);
        postTime = (TextView) findViewById(R.id.text_preview_post_time);
        postName = (TextView) findViewById(R.id.text_preview_post_name);
        postReplyCount = (TextView) findViewById(R.id.text_preview_post_comment_count);
        editReply = (EditText) findViewById(R.id.edit_reply);

        replyEditContainer = (RelativeLayout) findViewById(R.id.reply_edit_container);
        replyEditContainer.setVisibility(View.GONE);
        replyContainer =
                (LinearLayout) findViewById(R.id.comment_bottom_sheet);
        replyContainer.setVisibility(View.GONE);

        replyAdapter = new ReplyRecyclerViewAdapter();
        replyRecycler = (RecyclerView) findViewById(R.id.reply_recycler);
        imgNoneReply = (ImageView) findViewById(R.id.img_none_reply_icon);
        noneReply = (TextView) findViewById(R.id.text_none_reply);
        editReply = (EditText) findViewById(R.id.edit_reply);
        replySend = (TextView) findViewById(R.id.text_reply_send);

        new AsyncPostDetailReplyJSONList().execute(postId);
        new AsyncPostDetailJSONList().execute(postId);
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
        tbThreeDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().add(
                        BottomEditDialogFragment.newInstance(), "bottom_post_detail").commit();
            }
        });

        // background 이미지
        if (!post.bgImg.isEmpty()) {
            Glide.with(this).load(post.bgImg).into(imgBackground);
        } else {

            // TODO : weatherCode에 맞는 테마 배경을 넣어야함
            // WeatherData.imgFromWeatherCode(String.valueOf(post.weatherCode), 1));
            imgBackground.setImageResource(R.drawable.splash_background);
        }
        // 날씨 아이콘
        imgWeatherIcon.setImageResource(
                WeatherData.imgFromWeatherCode(
                        String.valueOf(post.weatherCode), 0));

        // TODO : 아이콘 코드 추가해야함

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

        // 댓글
        replyRecycler.setLayoutManager(
                new LinearLayoutManager(MongSilApplication.getMongSilContext()));
        postReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isReplyContainer) {
                    Animation upAnim = AnimationUtils.loadAnimation(
                            getApplicationContext(), R.anim.anim_slide_in_bottom);
                    upAnim.setFillAfter(true);
                    replyEditContainer.startAnimation(upAnim);
                    replyEditContainer.setVisibility(View.VISIBLE);
                    replyContainer.setVisibility(View.VISIBLE);
                    isReplyContainer = true;
                } else {
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
                    new AsyncReplingResponse().execute(
                            PropertyManager.getInstance().getUserId(),
                            editReply.getText().toString()
                    );
                    editReply.setText("");
                }
            }
        });


        /*postReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                // TODO : 댓글 수에 따라 창의 최대 위치가 변경됨
                if(post.replyCount > 3) {
                    replyContainer.setWeightSum(1);
                } else if(post.replyCount > 0) {
                }
                commentBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(View bottomSheet, int newState) {

                    }
                    @Override
                    public void onSlide(View bottomSheet, float slideOffset) {

                    }
                });
                showCommentSheet();
            }
        });*/
    }

    // 글 상세내용 가져오기
    public class AsyncPostDetailJSONList extends AsyncTask<String, Integer, Post> {

        Response response;

        @Override
        protected Post doInBackground(String... args) {
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
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();

                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONPostDetailRequestList(
                            new StringBuilder(responseBody.string()));
                }
                responseBody.close();
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

        Response response;

        @Override
        protected ArrayList<ReplyData> doInBackground(String... args) {
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(
                                NetworkDefineConstant.GET_SERVER_POST_DETAIL_REPLY,
                                args[0]))
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
                responseBody.close();
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
                replyRecycler.setVisibility(View.VISIBLE);
                imgNoneReply.setVisibility(View.GONE);
                noneReply.setVisibility(View.GONE);

                replyAdapter.add(result);
                replyRecycler.setAdapter(replyAdapter);
            }
        }
    }

    // 댓글달기
    public class AsyncReplingResponse extends AsyncTask<String, String, String> {

        Response response;

        @Override
        protected String doInBackground(String... args) {

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
                        .url(String.format(NetworkDefineConstant.POST_SERVER_POST_REPLY,
                                postId))
                        .post(formBody) //반드시 post로
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("success")) {
                new AsyncPostDetailReplyJSONList().execute(
                        postId);
            } else if (s.equals("fail")) {
                // 실패
            }
        }
    }

    // 글 삭제
    public class AsyncPostRemoveResponse extends AsyncTask<String, String, String> {

        Response response;

        @Override
        protected String doInBackground(String... args) {
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("success")) {
                Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("post_remove", true);
                startActivity(intent);
                finish();
            } else if(s.equals("fail")) {
                getSupportFragmentManager().beginTransaction().
                        add(MiddleAloneDialogFragment.newInstance(1), "middle_fail").commit();
            }
        }
    }

    // 글 수정과 글 삭제 하단 다이어로그
    @Override
    public void onSelectBottomEdit(int select) {
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
        }
    }

    // 글 삭제 다이어로그
    @Override
    public void onMiddleSelect(int select) {
        switch (select) {
            case 0 :
                new AsyncPostRemoveResponse().execute(postId);
        }
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
        } else {
            super.onBackPressed();
            toMainActivityFromthis();
        }
    }

    private void toMainActivityFromthis() {
        Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
