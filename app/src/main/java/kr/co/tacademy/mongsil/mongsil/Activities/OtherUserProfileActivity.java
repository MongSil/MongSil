package kr.co.tacademy.mongsil.mongsil.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.tacademy.mongsil.mongsil.Enums.DataEnum;
import kr.co.tacademy.mongsil.mongsil.Fragments.MiddleAloneDialogFragment;
import kr.co.tacademy.mongsil.mongsil.Fragments.ProfileMenuTabFragment;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.AsyncTaskJSONParser;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.UserData;
import kr.co.tacademy.mongsil.mongsil.MongSilApplication;
import kr.co.tacademy.mongsil.mongsil.R;
import kr.co.tacademy.mongsil.mongsil.Utils.BlurBuilder;

public class OtherUserProfileActivity extends BaseActivity {
    private static final String USERID = "userid";

    private String userId;

    public OtherUserProfileActivity() {
    }

    ImageView imgProfileBackground;
    CircleImageView imgProfile;
    TextView textMyName, textMyLocation;
    ImageView imgSetting, imgAlarm, imgClose;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_menu);
        Intent intent = getIntent();
        if (intent.hasExtra(USERID)) {
            userId = intent.getStringExtra(USERID);
        }

        imgProfileBackground = (ImageView) findViewById(R.id.img_profile_background);

        imgProfile = (CircleImageView) findViewById(R.id.img_profile);

        textMyName = (TextView) findViewById(R.id.text_my_name);
        textMyLocation = (TextView) findViewById(R.id.text_my_location);

        imgSetting = (ImageView) findViewById(R.id.img_setting);
        //imgAlarm = (ImageView) findViewById(R.id.img_alarm);
        imgSetting.setVisibility(View.INVISIBLE);
        //imgAlarm.setVisibility(View.INVISIBLE);

        imgClose = (ImageView) findViewById(R.id.img_close);

        viewPager = (ViewPager) findViewById(R.id.viewpager_menu);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        asyncUserData.execute(userId);

    }

    private void init(final UserData data) {
        if (!data.getProfileImg().isEmpty()) {
            Glide.with(MongSilApplication.getMongSilContext())
                    .load(data.getProfileImg())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource,
                                                    GlideAnimation<? super Bitmap> glideAnimation) {
                            imgProfile.setImageBitmap(resource);
                            imgProfileBackground.setImageBitmap(
                                    BlurBuilder.blur(resource, 5));
                        }
                    });
            Log.e("프로필이미지 value : ", " " + data.getProfileImg());
        } else {
            imgProfile.setImageResource(R.drawable.none_my_profile);
        }
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OtherUserProfileActivity.this, PhotoViewActivity.class);
                intent.putExtra("profileImg", data.getProfileImg());
                startActivity(intent);
            }
        });

        textMyName.setText(data.getUsername());
        textMyLocation.setText(data.getArea());

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        if (viewPager != null) {
            MenuViewPagerAdapter adapter =
                    new MenuViewPagerAdapter(getSupportFragmentManager());
            String[] tabTitle = MongSilApplication.getMongSilContext()
                    .getResources().getStringArray(R.array.menu_tab_title);
            adapter.appendFragment(
                    ProfileMenuTabFragment
                            .newInstance(0, String.valueOf(data.getUserId())), tabTitle[0]);
            adapter.appendFragment(
                    ProfileMenuTabFragment
                            .newInstance(1, String.valueOf(data.getUserId())), tabTitle[1]);
            viewPager.setAdapter(adapter);
        }

        final Typeface normalFont = Typeface.createFromAsset(getAssets(), "fonts/NotoSansKR-Regular.otf");
        final Typeface boldFont = Typeface.createFromAsset(getAssets(), "fonts/NotoSansKR-Bold.otf");
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(OtherUserProfileActivity.this);
                tab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.setText(tab.getText());

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTypeface(boldFont);
                }
            }
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                TextView text = (TextView) tab.getCustomView();
                if (text != null) {
                    text.setTypeface(boldFont);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                if (text != null) {
                    text.setTypeface(normalFont);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    // 메뉴 뷰페이저 어답터
    private static class MenuViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<ProfileMenuTabFragment> fragments
                = new ArrayList<ProfileMenuTabFragment>();
        private final ArrayList<String> tabTitle = new ArrayList<String>();

        public MenuViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void appendFragment(ProfileMenuTabFragment fragment, String title) {
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

    // 유저정보 얻어옴
    AsyncTaskJSONParser<UserData> asyncUserData = new AsyncTaskJSONParser<UserData>
            (DataEnum.USER_DATA, new AsyncTaskJSONParser.ProcessResponse<UserData>() {
                @Override
                public void process(UserData result) {
                    if (result != null) {
                        init(result);
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .add(MiddleAloneDialogFragment.newInstance(13),
                                        "middle_user_info_fail").commit();
                    }
                }
            });
}
