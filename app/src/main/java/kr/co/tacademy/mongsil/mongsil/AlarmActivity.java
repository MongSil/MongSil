package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dinuscxj.refresh.RecyclerRefreshLayout;

/**
 * Created by Han on 2016-08-05.
 */
@Deprecated
public class AlarmActivity extends BaseActivity {

    RecyclerRefreshLayout recyclerRefreshLayout;
    RecyclerView alarmRecycler;
    AlarmRecyclerViewAdapter adapter;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        adapter = new AlarmRecyclerViewAdapter();
        recyclerRefreshLayout = (RecyclerRefreshLayout) findViewById(R.id.alarm_refresh_layout);
        recyclerRefreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 위로 올릴 때
                        recyclerRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        alarmRecycler = (RecyclerView) findViewById(R.id.alarm_recycler);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        alarmRecycler.setLayoutManager(layoutManager);
        alarmRecycler.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // 밑으로 내릴 때
            }
        });
        adapter = new AlarmRecyclerViewAdapter();
        alarmRecycler.setAdapter(adapter);
        // 서버에서 댓글 목록을 받아온다 - GCM 사용
    }

    // 툴바 메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toMainActivityFromthis();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toMainActivityFromthis() {
        Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainActivityFromthis();
    }
}