package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SangWoo on 2016-07-26.
 */
public class SlidingMenuFragment extends Fragment {

    public static SlidingMenuFragment newInstance() {
        SlidingMenuFragment f = new SlidingMenuFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
    }

    ImageView imgProfile;
    TextView textName, textLocation;
    TextView textMyPost, textSignedMongsil, textSignedMongsilNum;
    TextView textMakeMongsil, textInviteFriend;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_sliding_menu, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewpager_menu);
        if(viewPager != null) {
            setupWithViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager();
        imgProfile = (ImageView) v.findViewById(R.id.img_profile);
        textName = (TextView) v.findViewById(R.id.text_name);
        textLocation = (TextView) v.findViewById(R.id.text_location);

        textMyPost = (TextView) v.findViewById(R.id.text_my_post);
        textSignedMongsil = (TextView) v.findViewById(R.id.text_signed_mongsil);
        textSignedMongsilNum = (TextView) v.findViewById(R.id.text_signed_mongsil_num);
        textMakeMongsil = (TextView) v.findViewById(R.id.text_make_mongsil);
        textInviteFriend = (TextView) v.findViewById(R.id.text_invite_friend);

        return v;
    }

    private static class MenuViewPagerAdapter extends FragmentPagerAdapter {
        private static final int VIEW_COUNT = 2;
        private final ArrayList<SlidingMenuTabFragment> fragments
                = new ArrayList<SlidingMenuTabFragment>;
        private final ArrayList<String> tabTitle = new ArrayList<String>;

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

    private void setupWithViewPager(ViewPager viewPager) {
        MenuViewPagerAdapter adapter =
                new MenuViewPagerAdapter(getFragmentManager());
        String[] tabTitle = getResources().getStringArray(R.array.menu_tab_title);
        adapter.appendFragment(SlidingMenuTabFragment.newInstance(0), tabTitle[0]);
        adapter.appendFragment(SlidingMenuTabFragment.newInstance(1), tabTitle[2]);
        viewPager.setAdapter(adapter);
    }
}
