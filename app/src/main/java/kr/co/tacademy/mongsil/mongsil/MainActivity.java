package kr.co.tacademy.mongsil.mongsil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tbTitle;
    ImageView tbMenu, tbAlarm, tbSetting;


    SlidingMenu slidingMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        slidingMenu.setMenu(R.layout.menu_sliding);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.toolbar_alarm) {
                    return true;
                }
                if(id == R.id.toolbar_setting) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {


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
