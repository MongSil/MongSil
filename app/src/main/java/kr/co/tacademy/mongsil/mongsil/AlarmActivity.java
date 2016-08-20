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

/**
 * Created by Han on 2016-08-05.
 */
public class AlarmActivity extends BaseActivity {

    RefreshRecyclerView alarmRecycler;
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            alarmRecycler.notifyDataSetChanged();
        }
    };

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

        alarmRecycler = (RefreshRecyclerView) findViewById(R.id.alarm_recycler);
        alarmRecycler.setMode(Mode.REFRESH);
        alarmRecycler.setRereshListener(new RefreshListener() {
            @Override
            public void pullToReresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        handler.sendEmptyMessage(1);
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                // none
            }
        });
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        alarmRecycler.setLayoutManager(layoutManager);
        alarmRecycler.setAdapter(new AlarmRecyclerViewAdapter());
        // TODO : 서버에서 댓글 목록을 받아온다 - GCM 사용
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
