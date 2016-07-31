package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgThreeDot, imgWeatherIcon;
    TextView postContent, postLocation, postTime, postName, postCommentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 인텐트를 받아옴
        Intent intent = getIntent();
        PostData postData = intent.getParcelableExtra("post_data");

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        imgThreeDot = (ImageView) findViewById(R.id.img_threeDot);

        // 바탕 이미지
        LinearLayout postContainer = (LinearLayout) findViewById(R.id.post_container);

        postContainer.setBackground(getResources().getDrawable(R.drawable.test_splash));

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
        postCommentCount.setText(postData.commentCount);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
