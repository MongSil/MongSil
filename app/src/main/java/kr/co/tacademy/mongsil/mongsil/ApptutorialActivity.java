package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AppTutorialActivity extends AppCompatActivity {
    private static final int MAX_PAGES = 3;
    private int num_pages = 3;
    ImageView imgTutorialSkip;

    // TODO : 앱 설명 뷰페이저 인디케이터 만들어야함, 배경 하나씩 더 넣어야함
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_tutorial);
        final ViewPagerParallax pager = (ViewPagerParallax) findViewById(R.id.viewpager_tutorial);
        pager.set_max_pages(MAX_PAGES);
        pager.setBackgroundAsset(R.raw.ex_cloud_background01);
        pager.setAdapter(new my_adapter());

        imgTutorialSkip = (ImageView) findViewById(R.id.img_tutorial_skip);
        imgTutorialSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppTutorialActivity.this, MainActivity.class);
                startActivity(intent);
                AppTutorialActivity.this.finish();
            }
        });

        if (savedInstanceState!=null) {
            num_pages = savedInstanceState.getInt("num_pages");
            pager.setCurrentItem(savedInstanceState.getInt("current_page"), false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("num_pages", num_pages);
        final ViewPagerParallax pager = (ViewPagerParallax) findViewById(R.id.viewpager_tutorial);
        outState.putInt("current_page", pager.getCurrentItem());
    }

    private class my_adapter extends PagerAdapter {
        TextView tutorialTitle, tutorialExplain;
        @Override
        public int getCount() {
            return num_pages;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.layout_tutorial_content, null);
            tutorialTitle = (TextView) view.findViewById(R.id.text_tutorial_title);
            tutorialExplain = (TextView) view.findViewById(R.id.text_tutorial_explain);
            tutorialTitle.setText(Integer.toString(position));

            container.addView(view);

            return view;
        }

    }
}
