package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

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

public class MainActivity extends BaseActivity implements SearchPoiDialogFragment.OnSelectListener {
    // 툴바 필드
    TextView tbTitle;
    ImageView tbSearch;

    // 날씨 필드
    ImageView animBackgroundWeather, imgWeatherIcon;
    TextView day, week, month;

    // 글작성 프레그먼트
    MainPostFragment mainPostFragment;

    // 슬라이딩메뉴
    SlidingMenu slidingMenu;
    TabLayout tabLayout;

    // 사진찍어 글쓰기 버튼
    FloatingActionButton btnCapturePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 글 작성 프레그먼트와 슬라이딩메뉴 프레그먼트를 선언
        // TODO : GPS가 켜져 있을 경우 - GPS 지역
        // TODO : 안켜짐 - 가입시 선택한 지역 반환
        if ( savedInstanceState == null ) {
            mainPostFragment = MainPostFragment.newInstance(""); // 임시 - 전국 보여줌
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_post_fragment_container, mainPostFragment)
                    .commit();
        }
        // 툴바 추가
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
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
                        .add(new SearchPoiDialogFragment(), "search")
                        .addToBackStack("search").commit();
            }
        });
        tbSearch = (ImageView) toolbar.findViewById(R.id.toolbar_search);
        tbSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                if(Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT){
                    intent.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);
                }
                startActivity(intent);
            }
        });

        // 슬라이딩 메뉴(프로필 메뉴 추가)
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        slidingMenu.setMenu(loadSlidingMenu());

        // 날씨
        animBackgroundWeather = (ImageView) findViewById(R.id.anim_background_weather);
        ((AnimationDrawable) animBackgroundWeather.getDrawable()).start();
        imgWeatherIcon = (ImageView) findViewById(R.id.img_weather_icon);
        ((AnimationDrawable) imgWeatherIcon.getDrawable()).start();
        imgWeatherIcon.setAnimation(
                AnimationApplyInterpolater(
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
                finish();
            }
        });
    }

    // 애니메이션 인터폴레이터 적용
    private Animation AnimationApplyInterpolater(
            int resourceId, final Interpolator interpolator){
        Animation animation = AnimationUtils.loadAnimation(this, resourceId);
        animation.setInterpolator(interpolator);
        return animation;
    }

    // 슬라이딩메뉴 뷰
    public View loadSlidingMenu() {
        View menu = getLayoutInflater().inflate(R.layout.layout_profile_menu, null);

        TextView textMyName, textMyLocation;

        CircleImageView imgProfile =
                (CircleImageView) menu.findViewById(R.id.img_profile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            }
        });
        textMyName = (TextView) menu.findViewById(R.id.text_my_name);
        textMyLocation = (TextView) menu.findViewById(R.id.text_my_location);
        textMyName.setText("몽실이");
        textMyLocation.setText("대전");

        ImageView imgSetting, imgAlarm, imgClose;
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

        final ViewPager viewPager = (ViewPager) menu.findViewById(R.id.viewpager_menu);
        if(viewPager != null) {
            MenuViewPagerAdapter adapter =
                    new MenuViewPagerAdapter(getSupportFragmentManager());
            String[] tabTitle = MongSilApplication.getMongSilContext()
                    .getResources().getStringArray(R.array.menu_tab_title);
            // 자신의 USERID를 newInstance(0, "여기")에 넣는다 테스트로 8이라 가정
            adapter.appendFragment(ProfileMenuTabFragment.newInstance(0, "8"), tabTitle[0]);
            adapter.appendFragment(ProfileMenuTabFragment.newInstance(1, "8"), tabTitle[1]);
            viewPager.setAdapter(adapter);
        }

        tabLayout = (TabLayout) menu.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(this);
                tab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                tabTextView.setText(tab.getText());

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTypeface(null, Typeface.BOLD);
                }
            }
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                TextView text = (TextView) tab.getCustomView();
                text.setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTypeface(null, Typeface.NORMAL);
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

    // 날씨를 검색해서 지역 정보를 받아옴
    @Override
    public void onSelect(POIData POIData) {
        if(POIData != null) {
            String location = POIData.upperAddrName;
            tbTitle.setText(location);
            new AsyncWeatherJSONList().execute(POIData.noorLat, POIData.noorLon);
            mainPostFragment = MainPostFragment.newInstance(location);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_post_fragment_container, mainPostFragment)
                    .commit();
        }
    }

    // 툴바 메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                slidingMenu.showMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 백 버튼 눌렀을 때
    @Override
    public void onBackPressed() {
        if(slidingMenu.isMenuShowing()) {
            slidingMenu.toggle();
            return;
        }
        super.onBackPressed();
    }

    // 날씨 AsyncTask
    public class AsyncWeatherJSONList extends AsyncTask<String, Integer, WeatherData> {
        @Override
        protected WeatherData doInBackground(String... args) {
            try{
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
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();

                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONWeatherList(
                            new StringBuilder(responseBody.string()));
                }
                responseBody.close();
            }catch (UnknownHostException une) {
                e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("connectionFail", uee.toString());
            } catch (Exception e) {
                e("connectionFail", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(WeatherData result) {
            imgWeatherIcon.setImageResource(WeatherData.imgFromWeatherCode(result.code, 0));
            // TODO : animBackgroundWeather.setBackground(WeatherData.imgFromWeatherCode(result.code, 1));
        }
    }
}
