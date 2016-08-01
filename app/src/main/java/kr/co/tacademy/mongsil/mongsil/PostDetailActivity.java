package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;

public class PostDetailActivity extends BaseActivity {

    BottomSheetLayout bottomSheetLayout;
    ImageView imgThreeDot, imgWeatherIcon;
    TextView postContent, postLocation, postTime, postName, postCommentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 인텐트를 받아옴
        Intent intent = getIntent();
        final PostData postData = intent.getParcelableExtra("post_data");

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
        LinearLayout postContainer = (LinearLayout) findViewById(R.id.post_container);
        postContainer.setBackgroundResource(R.drawable.test_splash);

        imgWeatherIcon = (ImageView) findViewById(R.id.img_weather_icon);
        postContent = (TextView) findViewById(R.id.text_post_content);
        postContent.setText(postData.content);
        postLocation = (TextView) findViewById(R.id.text_post_location);
        postLocation.setText(postData.location);
        postTime = (TextView) findViewById(R.id.text_post_time);
        postTime.setText(postData.time);
        postName = (TextView) findViewById(R.id.text_post_name);
        postName.setText(postData.name);
        postCommentCount = (TextView) findViewById(R.id.text_post_comment_count);
        // postCommentCount.setText(postData.commentCount);

        // 코멘트 바텀 시트
        final int commentCount = 0;
        /*if(postData.commentCount != 0) {
            commentCount = postData.commentCount;
        }*/
        bottomSheetLayout =
                (BottomSheetLayout) findViewById(R.id.bottom_sheet_container);
        postCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentSheet(commentCount);
            }
        });
    }

    // TODO : 댓글창 만들기
    private void showCommentSheet(int postCommentCount) {
        View rootView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.layout_post_comment,
                        bottomSheetLayout, false);
        bottomSheetLayout.showWithSheetView(rootView);
        RelativeLayout commentLayout =
                (RelativeLayout) rootView.findViewById(R.id.comment_list_layout);
        commentLayout.setVisibility(View.VISIBLE);
        TextView share = (TextView) rootView.findViewById(R.id.text_share);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
