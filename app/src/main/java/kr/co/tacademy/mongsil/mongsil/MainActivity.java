package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends AppCompatActivity {

    // 툴바 필드
    TextView tbTitle;
    ImageView tbMenu, tbAlarm, tbSetting;

    // 날씨 필드
    ImageView imgweathericon;
    TextView day, week, month;

    // 글작성 프레그먼트
    MainPostFragment mainPostFragment;

    // 슬라이딩메뉴
    SlidingMenu slidingMenu;

    // 사진찍어 글쓰기 버튼
    FloatingActionButton btnCapturePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 글 작성 프레그먼트와 슬라이딩메뉴 프레그먼트를 선언
        // TODO: 추후 프레그먼트의 newInstance를 수정
        if ( savedInstanceState == null ) {
            mainPostFragment = MainPostFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_post_fragment_container, mainPostFragment);
            ft.commit();
        }

        // 툴바 추가
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tbMenu = (ImageView) toolbar.findViewById(R.id.toolbar_menu);
        tbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.showMenu();
            }
        });
        tbAlarm = (ImageView) toolbar.findViewById(R.id.toolbar_alarm);
        tbSetting = (ImageView) toolbar.findViewById(R.id.toolbar_setting);

        // 슬라이딩 메뉴(프로필 메뉴 추가)
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        slidingMenu.setMenu(R.layout.fragment_sliding_menu);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.sliding_menu_container,
                        SlidingMenuFragment.newInstance()).commit();

        // 날씨
        View wv = findViewById(R.id.weather_info);
        imgweathericon = (ImageView) wv.findViewById(R.id.img_weather_icon);
        day = (TextView) wv.findViewById(R.id.text_day);
        week = (TextView) wv.findViewById(R.id.text_week);
        month = (TextView) wv.findViewById(R.id.text_month);

        RecyclerView weatherRecyclerView =
                (RecyclerView)findViewById(R.id.weather_recycler);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(
                this.getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        weatherRecyclerView.setAdapter(new WeatherRecyclerViewAdapter());
        // 사진찍어 글쓰기 버튼
        btnCapturePost = (FloatingActionButton) findViewById(R.id.btn_capture_post);

    }

    public static class WeatherRecyclerViewAdapter
            extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.ViewHolder> {
        private static final int WHEATHER_COUNT = 6;

        WeatherRecyclerViewAdapter() { }
        public class ViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final TextView textSmallDay;
            final ImageView imgSmallWeather;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                textSmallDay =
                        (TextView) view.findViewById(R.id.text_small_day);
                imgSmallWeather =
                        (ImageView) view.findViewById(R.id.img_small_whether_icon);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.layout_small_weather_info, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textSmallDay.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return WHEATHER_COUNT;
        }
    }

    @Override
    public void onBackPressed() {
        if(slidingMenu.isMenuShowing()) {
            slidingMenu.toggle();
            return;
        }
        super.onBackPressed();
    }
}
