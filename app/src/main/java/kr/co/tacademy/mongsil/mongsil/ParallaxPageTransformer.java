package kr.co.tacademy.mongsil.mongsil;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

// TODO : 이 트랜스포머를 보면서 배워야함.. 필요한게 다들어가있음
// http://fiskurgit.github.io/parallax_and_alpha_tricks_on_android_viewpager
public class ParallaxPageTransformer extends ActionBarActivity {

    static final int NUM_PAGES = 5;

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    Button skip;
    Button done;
    ImageButton next;

    /*
        This is nasty but as the transparency of the fragments increases when swiping the underlying
        Activity becomes visible, so we change the pager opacity on the last slide in
        setOnPageChangeListener below
     */
    boolean isOpaque = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            Setting this makes sure we draw fullscreen, without this the transparent Activity shows
            the bright orange notification header from the main Activity below
        */
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_tutorial);

        getSupportActionBar().hide();

        skip = Button.class.cast(findViewById(R.id.skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });

        next = ImageButton.class.cast(findViewById(R.id.next));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        });

        done = Button.class.cast(findViewById(R.id.done));
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
            }
        });

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //See note above for why this is needed
                if(position == NUM_PAGES - 2 && positionOffset > 0){
                    if(isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                }else{
                    if(!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.tutorial_background_opaque));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if(position == NUM_PAGES - 2){
                    skip.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                }else if(position < NUM_PAGES - 2){
                    skip.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    done.setVisibility(View.GONE);
                }else if(position == NUM_PAGES - 1){
                    endTutorial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Unused
            }
        });

        buildCircles();
    }

    /*
        The last fragment is transparent to enable the swipe-to-finish behaviour seen on Google's apps
        So our viewpager circle indicator needs to show NUM_PAGES - 1
     */
    private void buildCircles(){
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for(int i = 0 ; i < NUM_PAGES - 1 ; i++){
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.circle);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index){
        if(index < NUM_PAGES){
            for(int i = 0 ; i < NUM_PAGES - 1 ; i++){
                ImageView circle = (ImageView) circles.getChildAt(i);
                if(i == index){
                    circle.setImageResource(R.drawable.circle_selected);
                }else {
                    circle.setImageResource(R.drawable.circle);
                }
            }
        }
    }

    private void endTutorial(){
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TutorialPane tp = null;
            switch(position){
                case 0:
                    tp = TutorialPane.newInstance(R.layout.fragment_tutorial_one);
                    break;
                case 1:
                    tp = TutorialPane.newInstance(R.layout.fragment_tutorial_two);
                    break;
                case 2:
                    tp = TutorialPane.newInstance(R.layout.fragment_tutorial_three);
                    break;
                case 3:
                    tp = TutorialPane.newInstance(R.layout.fragment_tutorial_four);
                    break;
                case 4:
                    tp = TutorialPane.newInstance(R.layout.fragment_tutorial_transparent);
                    break;
            }

            return tp;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.background);
            View text = page.findViewById(R.id.content);

            View phone = page.findViewById(R.id.phone);
            View map = page.findViewById(R.id.map);
            View mountain = page.findViewById(R.id.mountain);
            View mountainNight = page.findViewById(R.id.mountain_night);
            View rain = page.findViewById(R.id.rain);
            View hands = page.findViewById(R.id.screenshot);

            if (position <= 1) {
                page.setTranslationX(pageWidth * -position);
            }

            if(position <= -1.0f || position >= 1.0f) {
            } else if( position == 0.0f ) {
            } else {
                if(backgroundView != null) {
                    backgroundView.setAlpha(1.0f - Math.abs(position));
                }

                //Text both translates in/out and fades in/out
                if (text != null) {
                    text.setTranslationX(pageWidth * position);
                    text.setAlpha(1.0f - Math.abs(position));
                }

                //Map + phone - map simple translate, phone parallax effect
                if(map != null){
                    map.setTranslationX(pageWidth * position);
                }

                if(phone != null){
                    phone.setTranslationX((float)(pageWidth/1.2 * position));
                }

                //Mountain day - fade in/out
                if(mountain != null){
                    mountain.setAlpha(1.0f - Math.abs(position));
                }

                //Mountain night - fade in, but translate out, rain fades in but parallax translate out
                if(mountainNight != null){
                    if(position < 0){
                        mountainNight.setTranslationX(pageWidth * position);
                    }else{
                        mountainNight.setAlpha(1.0f - Math.abs(position));
                    }
                }

                if(rain != null){
                    if(position < 0){
                        rain.setTranslationX((float)(pageWidth/1.2 * position));
                    }else{
                        rain.setAlpha(1.0f - Math.abs(position));
                    }
                }

                //Long click device + hands - translate both way but only fade out
                if(hands != null){
                    hands.setTranslationX(pageWidth * position);
                    if(position < 0) {
                        hands.setAlpha(1.0f - Math.abs(position));
                    }
                }
            }
        }
    }
}