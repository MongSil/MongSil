package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AppTutorialActivity extends AppCompatActivity {
    private static final int MAX_PAGES = 3;

    ViewPagerParallax pager;
    ImageView imgTutorialSkip;
    LinearLayout indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_tutorial);

        pager = (ViewPagerParallax) findViewById(R.id.viewpager_tutorial);
        pager.set_max_pages(MAX_PAGES);
        pager.setBackgroundAsset(R.drawable.tutorial_background);
        pager.setAdapter(new tutorialPagerAdapter(getSupportFragmentManager()));
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Unused
            }
        });

        buildIndicator();

        imgTutorialSkip = (ImageView) findViewById(R.id.img_tutorial_skip);
        imgTutorialSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppTutorialActivity.this, SignUpActivity.class);
                startActivity(intent);
                AppTutorialActivity.this.finish();
            }
        });
    }

    private void buildIndicator(){
        indicator = (LinearLayout) findViewById(R.id.viewPager_count_dots);

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for(int i = 0 ; i < MAX_PAGES ; i++){
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.indicator_none_dot);
            dot.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dot.setAdjustViewBounds(true);
            dot.setPadding(padding, 0, padding, 0);
            indicator.addView(dot);
        }
        setIndicator(0);
    }

    private void setIndicator(int index){
        if(index < MAX_PAGES){
            for(int i = 0 ; i < MAX_PAGES ; i++){
                ImageView dot = (ImageView) indicator.getChildAt(i);
                if(i == index){
                    dot.setImageResource(R.drawable.indicator_dot);
                }else {
                    dot.setImageResource(R.drawable.indicator_none_dot);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    private class tutorialPagerAdapter extends FragmentStatePagerAdapter {

        public tutorialPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TutorialPane.newInstance(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        @Override
        public int getCount() {
            return MAX_PAGES;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();


            View title = page.findViewById(R.id.text_tutorial_title);
            View explain = page.findViewById(R.id.text_tutorial_explain);

            // 첫번째

            // 두번째
            View city = page.findViewById(R.id.img_tutorial_city);

            // 세번째
            View write = page.findViewById(R.id.img_tutorial_write);

            /*if (position <= 1) {
                page.setTranslationX(pageWidth * -position);
            }*/

            if(position <= -1.0f || position >= 1.0f) {
            } else if( position == 0.0f ) {
            } else {
                if (title != null) {
                    title.setTranslationX(pageWidth * position);
                    title.setAlpha(1.0f - Math.abs(position));
                }
                if (explain != null) {
                    explain.setTranslationX(pageWidth * position);
                    explain.setAlpha(1.0f - Math.abs(position));
                }

                if (city != null) {
                    city.setTranslationX((float)(pageWidth/1.2 * position));
                }

                if (write != null) {
                    write.setTranslationX((float)(pageWidth/1.2 * position));
                }
            }
        }
    }
}
