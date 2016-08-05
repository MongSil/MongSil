package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.jcodecraeer.xrecyclerview.ArrowRefreshHeader;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

/**
 * Created by Han on 2016-08-05.
 */
public class AlarmActivity extends AppCompatActivity {

    XRecyclerView alarmRecycler;
    Handler handler;

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

        alarmRecycler = (XRecyclerView) findViewById(R.id.alarm_recycler);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        alarmRecycler.setLayoutManager(layoutManager);
        alarmRecycler.setAdapter(new AlarmRecyclerViewAdapter());
        handler = new Handler();
        alarmRecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO : 서버에서 댓글 목록을 받아온다
                        alarmRecycler.refreshComplete();
                    }
                }, 2000);

            }

            @Override
            public void onLoadMore() {

            }
        });
    }
}
