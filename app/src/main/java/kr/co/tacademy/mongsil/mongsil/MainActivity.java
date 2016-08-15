package kr.co.tacademy.mongsil.mongsil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class MainActivity extends BaseActivity
        implements SearchPoiDialogFragment.OnPOISearchListener,
                MiddleSelectDialogFragment.OnMiddleSelectDialogListener {
    // 툴바 필드
    TextView tbTitle;
    ImageView tbSearch;

    // 날씨 필드
    FrameLayout weatherContainer;
    ImageView animBackgroundWeather, imgWeatherIcon;
    TextView day, week, month;

    // 글목록 프레그먼트
    MainPostFragment mainPostFragment;

    // 슬라이딩메뉴
    SlidingMenu slidingMenu;

    // 글쓰기 버튼
    FloatingActionButton btnCapturePost;

    // GPSInfo
    GPSInfo gpsInfo;
    Handler handler = new Handler();

    @Override
    protected void onStart() {
        super.onStart();
        requestGPSPermission();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        // 글 삭제를 하고난 뒤의 다이어로그 창
        if (intent.getBooleanExtra("post_remove", false)) {
            getSupportFragmentManager().beginTransaction().
                    add(MiddleAloneDialogFragment.newInstance(0), "middle_done").commit();
        }

        // 글 작성 프레그먼트와 슬라이딩메뉴 프레그먼트를 선언
        if (savedInstanceState == null) {
            mainPostFragment = MainPostFragment.newInstance(
                    PropertyManager.getInstance().getLocation());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_post_fragment_container, mainPostFragment)
                    .commit();
        }
        // 툴바 추가
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tbTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(new SearchPoiDialogFragment(), "search_main").commit();
            }
        });
        tbSearch = (ImageView) toolbar.findViewById(R.id.toolbar_search);
        tbSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        // 슬라이딩 메뉴(프로필메뉴)
        slidingMenu = new SlidingMenu(getApplicationContext());
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        slidingMenu.setMenu(loadSlidingMenu());

        // 날씨
        weatherContainer = (FrameLayout) findViewById(R.id.main_weather_container);
        animBackgroundWeather = (ImageView) findViewById(R.id.anim_background_weather);
        imgWeatherIcon = (ImageView) findViewById(R.id.img_weather_icon);
        imgWeatherIcon.setAnimation(AnimationApplyInterpolater(
                R.anim.bounce_interpolator, new LinearInterpolator()));

        day = (TextView) findViewById(R.id.text_day);
        day.setText(TimeData.dayFormat);
        week = (TextView) findViewById(R.id.text_week);
        week.setText(TimeData.weakFormat);
        month = (TextView) findViewById(R.id.text_month);
        month.setText(TimeData.monthFormat);

        // 글쓰기 버튼
        btnCapturePost = (FloatingActionButton) findViewById(R.id.btn_capture_post);
        btnCapturePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PostingActivity.class));
            }
        });
        tbTitle.setText(PropertyManager.getInstance().getLocation());

        if (!getIntent().hasExtra("area1")) {
            new AsyncLatLonWeatherJSONList().execute(
                    PropertyManager.getInstance().getLatLocation(),
                    PropertyManager.getInstance().getLonLocation());
        } else {
            // 글을 작성하고 난 후의 지역 설정
            String[] latLon =
                    LocationData.ChangeToLatLon(getIntent().getStringExtra("area1"));
            new AsyncLatLonWeatherJSONList().execute(latLon[0], latLon[1]);
        }

    }

    // 애니메이션 인터폴레이터 적용
    private Animation AnimationApplyInterpolater(
            int resourceId, final Interpolator interpolator) {
        Animation animation = AnimationUtils.loadAnimation(this, resourceId);
        animation.setInterpolator(interpolator);
        return animation;
    }

    // 날씨를 검색해서 지역 정보를 받아옴
    @Override
    public void onPOISearch(POIData POIData) {
        if (POIData != null) {
            String location = POIData.upperAddrName;
            tbTitle.setText(location);
            new AsyncLatLonWeatherJSONList().execute(POIData.noorLat, POIData.noorLon);
            mainPostFragment = MainPostFragment.newInstance(location);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_post_fragment_container, mainPostFragment)
                    .commit();
        }
    }

    ImageView imgProfileBackground;
    CircleImageView imgProfile;
    TextView textMyName, textMyLocation;
    ImageView imgSetting, imgAlarm, imgClose;
    ViewPager viewPager;
    TabLayout tabLayout;

    // 슬라이딩메뉴 뷰
    public View loadSlidingMenu() {
        View menu = getLayoutInflater().inflate(R.layout.layout_profile_menu, null);

        imgProfileBackground =
                (ImageView) menu.findViewById(R.id.img_profile_background);

        imgProfile =
                (CircleImageView) menu.findViewById(R.id.img_profile);
        Log.e("프로필이미지 value : ", " " + PropertyManager.getInstance().getUserProfileImg());
        if (!PropertyManager.getInstance().getUserProfileImg().isEmpty()) {
            Glide.with(MongSilApplication.getMongSilContext())
                    .load(PropertyManager.getInstance().getUserProfileImg())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource,
                                                    GlideAnimation<? super Bitmap> glideAnimation) {
                            imgProfile.setImageBitmap(resource);
                            imgProfileBackground.setImageBitmap(
                                    BlurBuilder.blur(resource, 5));
                        }
                    });
        } else {
            imgProfile.setImageResource(R.drawable.none_my_profile);
        }
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("UserProfile : ", PropertyManager.getInstance().getUserProfileImg());
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            }
        });
        textMyName = (TextView) menu.findViewById(R.id.text_my_name);
        textMyLocation = (TextView) menu.findViewById(R.id.text_my_location);
        textMyName.setText(PropertyManager.getInstance().getNickname());
        textMyLocation.setText(PropertyManager.getInstance().getLocation());

        imgSetting = (ImageView) menu.findViewById(R.id.img_setting);
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });
        imgAlarm = (ImageView) menu.findViewById(R.id.img_alarm);
        imgAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AlarmActivity.class));
            }
        });
        imgClose = (ImageView) menu.findViewById(R.id.img_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.toggle();
            }
        });

        viewPager = (ViewPager) menu.findViewById(R.id.viewpager_menu);
        if (viewPager != null) {
            MenuViewPagerAdapter adapter =
                    new MenuViewPagerAdapter(getSupportFragmentManager());
            String[] tabTitle = MongSilApplication.getMongSilContext()
                    .getResources().getStringArray(R.array.menu_tab_title);
            adapter.appendFragment(
                    ProfileMenuTabFragment
                            .newInstance(0, PropertyManager.getInstance().getUserId()), tabTitle[0]);
            adapter.appendFragment(
                    ProfileMenuTabFragment
                            .newInstance(1, PropertyManager.getInstance().getUserId()), tabTitle[1]);
            viewPager.setAdapter(adapter);
        }

        // 탭 레이아웃 설정
        tabLayout = (TabLayout) menu.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        final Typeface normalFont = Typeface.createFromAsset(getAssets(), "fonts/NotoSansKR-Regular.otf");
        final Typeface boldFont = Typeface.createFromAsset(getAssets(), "fonts/NotoSansKR-Bold.otf");
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(MainActivity.this);
                tab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.setText(tab.getText());

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTypeface(boldFont);
                }
            }
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                TextView text = (TextView) tab.getCustomView();
                if (text != null) {
                    text.setTypeface(boldFont);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                if (text != null) {
                    text.setTypeface(normalFont);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return menu;
    }

    // 메뉴 뷰페이저 어답터
    private static class MenuViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<ProfileMenuTabFragment> fragments
                = new ArrayList<ProfileMenuTabFragment>();
        private final ArrayList<String> tabTitle = new ArrayList<String>();

        public MenuViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void appendFragment(ProfileMenuTabFragment fragment, String title) {
            fragments.add(fragment);
            tabTitle.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle.get(position);
        }
    }

    // 툴바 메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                slidingMenu.showMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 백 버튼 눌렀을 때
    @Override
    public void onBackPressed() {
        if (slidingMenu.isMenuShowing()) {
            slidingMenu.toggle();
            return;
        }
        super.onBackPressed();
    }

    // 위도, 경도 날씨 AsyncTask
    public class AsyncLatLonWeatherJSONList extends AsyncTask<String, Integer, WeatherData> {

        @Override
        protected WeatherData doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .addHeader("Accept", "application/json")
                        .addHeader("appKey", NetworkDefineConstant.SK_APP_KEY)
                        .url(String.format(
                                NetworkDefineConstant.SK_WEATHER_LAT_LON,
                                args[0], args[1]))
                        .build();

                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();

                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONWeatherList(
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
        protected void onPostExecute(WeatherData result) {
            if (result != null) {
                imgWeatherIcon.setImageResource(WeatherData.imgFromWeatherCode(result.code, 0));
                weatherContainer.setBackgroundResource(WeatherData.imgFromWeatherCode(result.code, 1));
                animBackgroundWeather.setImageResource(WeatherData.imgFromWeatherCode(result.code, 2));
                if (animBackgroundWeather.isShown()) {
                    ((AnimationDrawable) animBackgroundWeather.getDrawable()).start();
                }
                if (imgWeatherIcon.isShown()) {
                    ((AnimationDrawable) imgWeatherIcon.getDrawable()).start();
                }
            }
        }
    }

    // 역지오코딩 AsyncTask
    public class AsyncReGeoJSONList extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .addHeader("Accept", "application/json")
                        .addHeader("appKey", NetworkDefineConstant.SK_APP_KEY)
                        .url(String.format(
                                NetworkDefineConstant.SK_REVERSE_GEOCOING,
                                args[0], args[1]))
                        .build();
                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();

                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONResGeo(
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
        protected void onPostExecute(String result) {
            if (result != null) {
                String GPSlocation = LocationData.ChangeToShortName(result);
                tbTitle.setText(GPSlocation);
                mainPostFragment = MainPostFragment.newInstance(GPSlocation);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_post_fragment_container, mainPostFragment)
                        .commit();
            }
        }
    }

    // 아래 전부 GPS 기능 관련
    @Override
    protected void onStop() {
        super.onStop();
        if (gpsInfo != null) {
            gpsInfo.stopGPS();
        }
    }

    private void getLocation() {
        if (PropertyManager.getInstance().getUseGPS()) {
            if (gpsInfo == null) {
                gpsInfo = new GPSInfo(getApplicationContext());
            }
            if (gpsInfo.isGetLocation()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String lat = String.valueOf(gpsInfo.getLatitude());
                        String lon = String.valueOf(gpsInfo.getLongitude());
                        Log.e("locaiton info:", lat + ", " + lon);
                        if(!(lat.equals("0.0") || lon.equals("0.0"))) {
                            new AsyncLatLonWeatherJSONList().execute(lat, lon);
                            new AsyncReGeoJSONList().execute(lat, lon);
                        }
                    }
                }, 2000);
            } else {
                if(!(gpsInfo.isGPSEnabled || gpsInfo.isNetworkEnabled)) {
                    getSupportFragmentManager().beginTransaction()
                            .add(MiddleSelectDialogFragment.newInstance(10),
                                    "middle_gps_setting_request").commitAllowingStateLoss();
                }
            }
        }
    }

    @Override
    public void onMiddleSelect(int select) {
        switch (select) {
            case 10 :
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                break;
        }
    }

    public void requestGPSPermission() {
        if (!PropertyManager.getInstance().getUseGPS()) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermission();
                return;
            }
            requestPermission();
        } else {
            getLocation();
        }
    }

    private static final int RC_FINE_LOCATION = 100;

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                RC_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_FINE_LOCATION) {
            if (permissions != null && permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
            }
        }
    }
}
