package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PostDetailActivity extends BaseActivity {
    int commentCount = 0;

    CoordinatorLayout postContainer;
    ImageView imgThreeDot, imgWeatherIcon;
    TextView postContent, postLocation, postTime, postName, postCommentCount;

    // 댓글
    RelativeLayout commentBottomSheet;
    BottomSheetBehavior commentBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 인텐트를 받아옴
        Intent intent = getIntent();
        final Post post = intent.getParcelableExtra("post_data");

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        imgThreeDot = (ImageView) findViewById(R.id.img_threeDot);

        // background 이미지
        postContainer = (CoordinatorLayout) findViewById(R.id.post_container);
        postContainer.setBackgroundResource(R.drawable.test_splash);

        imgWeatherIcon = (ImageView) findViewById(R.id.img_weather_icon);
        postContent = (TextView) findViewById(R.id.text_post_content);
        postContent.setText(post.content);
        postLocation = (TextView) findViewById(R.id.text_post_location);
        //postLocation.setText(post.location);
        postTime = (TextView) findViewById(R.id.text_post_time);
        String[] date = post.date.split(" ");
        postTime.setText(TimeData.PostTime(date[1]));
        postName = (TextView) findViewById(R.id.text_post_name);
        postName.setText(post.username);
        postCommentCount = (TextView) findViewById(R.id.text_post_comment_count);
        // postCommentCount.setText(post.commentCount);

        // 코멘트 바텀 시트
        commentBottomSheet =
                (RelativeLayout) findViewById(R.id.comment_bottom_sheet);
        commentBehavior = BottomSheetBehavior.from(commentBottomSheet);
        commentBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        postCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                // TODO : 댓글 수에 따라 창의 최대 위치가 변경됨
                if(commentCount > 3) {

                } else if(commentCount > 0) {

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
        });
    }

    // TODO : 댓글창 만들기
    private void showCommentSheet() {
        TextView share = (TextView) findViewById(R.id.text_share);
        // 공유하기를 눌렀을 때
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 댓글 리사이클러뷰
        /*if(postCommentCount > 0) {
            RecyclerView commentRecycler =
                    (RecyclerView) rootView.findViewById(R.id.comment_recycler);
            commentRecycler.setVisibility(View.VISIBLE);
            commentRecycler.setLayoutManager(
                    new LinearLayoutManager(getApplicationContext()));
            //commentRecycler.setAdapter( -- );
        }*/
    }

    private void closePostDetail() {
        commentBehavior.setPeekHeight(0);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closePostDetail();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closePostDetail();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
