package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    // 툴바 필드
    TextView tbTitle;
    ImageView tbSearch;

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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        tbTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tbSearch = (ImageView) toolbar.findViewById(R.id.toolbar_search);

        // 슬라이딩 메뉴(프로필 메뉴 추가)
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        slidingMenu.setMenu(
                loadSlidingMenu());

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

    // 슬라이딩메뉴 뷰
    public View loadSlidingMenu() {
        View menu = getLayoutInflater().inflate(R.layout.layout_sliding_menu, null);

        CircleImageView imgProfile =
                (CircleImageView) menu.findViewById(R.id.img_profile);

        TextView textMyName, textMyLocation;
        textMyName = (TextView) menu.findViewById(R.id.text_my_name);
        textMyLocation = (TextView) menu.findViewById(R.id.text_my_location);
        textMyName.setText("몽실이");
        textMyLocation.setText("대전");

        ImageView imgSetting, imgAlarm, imgClose;
        imgSetting = (ImageView) menu.findViewById(R.id.img_setting);
        imgClose = (ImageView) menu.findViewById(R.id.img_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingMenu.toggle();
            }
        });

        ViewPager viewPager = (ViewPager) menu.findViewById(R.id.viewpager_menu);
        if(viewPager != null) {
            MenuViewPagerAdapter adapter =
                    new MenuViewPagerAdapter(getSupportFragmentManager());
            String[] tabTitle = MongSilApplication.getMongSilContext()
                    .getResources().getStringArray(R.array.menu_tab_title);
            adapter.appendFragment(SlidingMenuTabFragment.newInstance(0), tabTitle[0]);
            adapter.appendFragment(SlidingMenuTabFragment.newInstance(1), tabTitle[1]);
            viewPager.setAdapter(adapter);
        }

        TabLayout tabLayout = (TabLayout) menu.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return menu;
    }

    // 메뉴 뷰페이저 어답터
    private static class MenuViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<SlidingMenuTabFragment> fragments
                = new ArrayList<SlidingMenuTabFragment>();
        private final ArrayList<String> tabTitle = new ArrayList<String>();

        public MenuViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void appendFragment(SlidingMenuTabFragment fragment, String title) {
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

    // 일주일 날씨 어답터
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                slidingMenu.showMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
