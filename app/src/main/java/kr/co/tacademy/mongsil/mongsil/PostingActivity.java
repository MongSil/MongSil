package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostingActivity extends BaseActivity implements SearchPoiDialogFragment.OnSelectListener {
    private static final int WEATHER_COUNT = 13;

    // 툴바 필드
    TextView location, save;

    // 날씨
    ImageView imgPreview, leftWeather, rightWeather;
    ViewPager selectWeatherPager;
    int pagerPos;

    // 포스팅
    EditText editPosting;

    // 카메라
    ImageView imgCamera;

    private String uploadCode = "0";
    private String area1 = "";
    private String area2 = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        Intent intent = getIntent();


        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_camera_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        location = (TextView) findViewById(R.id.text_posting_location);
        location.setText(PropertyManager.getInstance().getLocation());
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(new SearchPoiDialogFragment(), "search_posting").commit();
            }
        });
        save = (TextView) findViewById(R.id.text_save);
        area1 = PropertyManager.getInstance().getLocation();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아이콘 코드 임시 "0"
                // 백그라운드 이미지 임시 ""

                new AsyncPostingResponse().execute(
                        uploadCode,    // 이미지 업로드 코드
                        area1,  // 지역1
                        area2,  // 지역2
                        PropertyManager.getInstance().getUserId(), // 아이디
                        String.valueOf(selectWeatherPager.getCurrentItem()), // 날씨 테마 코드
                        "0",    // 아이콘 코드
                        "",     // 백그라운드 이미지
                        editPosting.getText().toString()    // 글 내용
                );
            }
        });

        // 미리보기
        imgPreview = (ImageView) findViewById(R.id.img_preview);
        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(PostPreviewDialogFragment.newInstance(
                                area1,
                                editPosting.getText().toString(),
                                0), "preview").commit();
            }
        });

        // 날씨 선택
        selectWeatherPager =
                (ViewPager) findViewById(R.id.viewpager_posting_select_weather);
        selectWeatherPager.setAdapter(new WeatherPagerAdapter());
        leftWeather = (ImageView) findViewById(R.id.img_left_weather);
        leftWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pagerPos = selectWeatherPager.getCurrentItem();
                if (pagerPos < WEATHER_COUNT && pagerPos >= 0) {
                    selectWeatherPager.setCurrentItem(pagerPos++);
                } else if (pagerPos == 0) {
                    pagerPos = WEATHER_COUNT - 1;
                    selectWeatherPager.setCurrentItem(pagerPos);
                }
            }
        });
        rightWeather = (ImageView) findViewById(R.id.img_right_weather);
        rightWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pagerPos = selectWeatherPager.getCurrentItem();
                if (pagerPos < WEATHER_COUNT && pagerPos >= 0) {
                    selectWeatherPager.setCurrentItem(pagerPos++);
                } else if (pagerPos == WEATHER_COUNT - 1) {
                    pagerPos = 0;
                    selectWeatherPager.setCurrentItem(pagerPos);
                }
            }
        });

        // 포스팅
        editPosting = (EditText) findViewById(R.id.edit_posting);
        editPosting.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    editPosting.setHint("");
                } else {
                    editPosting.setHint(getResources().getText(R.string.posting));
                }
            }
        });
        // 카메라
        imgCamera = (ImageView) findViewById(R.id.img_posting_camera);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : 사진을 선택한 후 이미지 코드를 설정해야함(1)
                // uploadCode = "1";
            }
        });
    }

    private class WeatherPagerAdapter extends PagerAdapter {
        ImageView imgWeatherBackground, imgWeatherIcon;

        WeatherPagerAdapter() {
        }

        @Override
        public int getCount() {
            return WEATHER_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView((View) object);

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater()
                    .inflate(R.layout.layout_posting_select_weather, container, false);
            imgWeatherIcon = (ImageView) view.findViewById(R.id.img_preview_weather_icon);
            imgWeatherIcon.setImageResource(
                    WeatherData.imgFromWeatherCode(String.valueOf(position + 1), 0));
            if (imgWeatherIcon.isShown()) {
                ((AnimationDrawable) imgWeatherIcon.getDrawable()).start();
            }
            imgWeatherBackground = (ImageView) view.findViewById(R.id.img_weather_background);
            /*imgWeatherBackground.setImageResource(
                    WeatherData.imgFromWeatherCode(String.valueOf(position), 1));*/
            container.addView(view);
            return view;
        }
    }

    public class AsyncPostingResponse extends AsyncTask<String, String, String> {

        Response response;

        @Override
        protected String doInBackground(String... args) {
            //업로드는 타임 및 리드타임을 넉넉히 준다.
            try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
            RequestBody formBody = new FormBody.Builder()
                    .add("uploadCode", args[0])
                    .add("area1", args[1])
                    .add("area2", args[2])
                    .add("userId", args[3])
                    .add("weatherCode", args[4])
                    .add("iconCode", args[5])
                    .add("bgImg", args[6])
                    .add("content", args[7])
                    .add("date", TimeData.getNow())
                    .build();
            //요청 세팅
            Request request = new Request.Builder()
                    .url(NetworkDefineConstant.POST_SERVER_POST)
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
        } catch(UnknownHostException une) {
            Log.e("aa", une.toString());
        } catch(UnsupportedEncodingException uee) {
            Log.e("bb", uee.toString());
        } catch(Exception e) {
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
            Intent intent = new Intent(PostingActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("area1", area1);
            startActivity(intent);
            finish();
        } else if(s.equals("fail")) {
            // 실패
        }
    }

}

    @Override
    public void onSelect(POIData POIData) {
        area1 = POIData.upperAddrName;
        area2 = POIData.middleAddrName;
        if (!area2.isEmpty()) {
            location.setText(String.valueOf(area1 + ", " + area2));
        } else {
            location.setText(area1);
        }
    }

    private void toMainActivityFromthis() {
        Intent intent = new Intent(PostingActivity.this, MainActivity.class);
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

    @Override
    public void onBackPressed() {
        toMainActivityFromthis();
        super.onBackPressed();
    }
}
