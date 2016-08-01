package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PostingActivity extends AppCompatActivity {

    // 툴바 필드
    TextView location, save;

    // 날씨
    ImageView imgPreview, leftWeather, rightWeather;

    // 포스팅
    EditText editPosting;

    // 카메라
    ImageView imgCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        location = (TextView) findViewById(R.id.text_posting_location);
        save = (TextView) findViewById(R.id.text_save);

        // 날씨 선택
        imgPreview = (ImageView) findViewById(R.id.img_preview);
        ViewPager selectWeather =
                (ViewPager) findViewById(R.id.viewpager_posting_select_weather);
        //selectWeather.setAdapter();
        leftWeather = (ImageView) findViewById(R.id.img_left_weather);
        rightWeather = (ImageView) findViewById(R.id.img_right_weather);

        // 포스팅
        editPosting = (EditText) findViewById(R.id.edit_posting);

        // 카메라
        imgCamera = (ImageView) findViewById(R.id.img_posting_camera);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                startActivity(new Intent(getApplication(), MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
        super.onBackPressed();
    }
}
