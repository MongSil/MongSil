package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AppTutorialActivity extends BaseActivity {
    private static final int MAX_PAGES = 3;

    ViewPagerParallax pager;
    ImageView imgTutorialSkip;
    LinearLayout indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_tutorial);

        if(savedInstanceState != null) {
            pager = (ViewPagerParallax) findViewById(R.id.viewpager_tutorial);
            pager.set_max_pages(MAX_PAGES);
            pager.setBackgroundAsset(R.drawable.tutorial_background);
            pager.setAdapter(new tutorialPagerAdapter(getSupportFragmentManager()));
            pager.setPageTransformer(true, new CrossFadePageTransformer());
        }

        pager = (ViewPagerParallax) findViewById(R.id.viewpager_tutorial);
        pager.set_max_pages(MAX_PAGES);
        pager.setBackgroundAsset(R.drawable.tutorial_background);
        pager.setAdapter(new tutorialPagerAdapter(getSupportFragmentManager()));
        pager.setPageTransformer(true, new CrossFadePageTransformer());
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
                endTutorial();
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

    private void endTutorial() {
        Intent intent = new Intent(AppTutorialActivity.this, SignUpActivity.class);
        startActivity(intent);
        AppTutorialActivity.this.finish();
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

    public class CrossFadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();


            View title = page.findViewById(R.id.text_tutorial_title);
            View explain = page.findViewById(R.id.text_tutorial_explain);

            // 첫번째
            View cloudCutTwo = page.findViewById(R.id.img_one_cloud02_cut);
            View cloudCutThree = page.findViewById(R.id.img_one_cloud03_cut);

            // 두번째
            View navi = page.findViewById(R.id.img_tutorial_navi);
            View city = page.findViewById(R.id.img_tutorial_city);
            View cloud2CutTwo = page.findViewById(R.id.img_two_cloud02_cut);
            View cloud2CutThree = page.findViewById(R.id.img_two_cloud03_cut);

            // 세번째

            View write = page.findViewById(R.id.img_tutorial_write);
            View cloud3CutTwo = page.findViewById(R.id.img_three_cloud02_cut);
            View cloud3CutThree = page.findViewById(R.id.img_three_cloud03_cut);

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

                // 첫번째
                if (cloudCutTwo != null) {
                    cloudCutTwo.setTranslationX(-(float)(pageWidth/1.5 * position));
                }
                if (cloudCutThree != null) {
                    cloudCutThree.setTranslationX(-(float)(pageWidth/0.8 * position));
                }

                // 두번째
                if (city != null) {
                    city.setTranslationX((float)(pageWidth/1.2 * position));
                }
                if (navi != null) {
                    navi.setTranslationX((float)(pageWidth/1.2 * position));
                }
                if (cloud2CutTwo != null) {
                    cloud2CutTwo.setTranslationX((float)(pageWidth/0.7 * position));

                }
                if (cloud2CutThree != null) {
                    cloud2CutThree.setTranslationX(-(float)(pageWidth/1.1 * position));
                }

                // 세번째
                if (write != null) {
                    write.setTranslationX((float)(pageWidth/1.2 * position));
                }
                if (cloud3CutTwo != null) {
                    cloud3CutTwo.setTranslationX((float)(pageWidth/0.7 * position));
                }
                if (cloud3CutThree != null) {
                    cloud3CutThree.setTranslationX((float)(pageWidth/1.6 * position));
                }

            }
        }
    }
}
