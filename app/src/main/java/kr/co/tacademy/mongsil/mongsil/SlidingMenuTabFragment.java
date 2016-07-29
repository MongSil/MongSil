package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ccei on 2016-07-29.
 */
public class SlidingMenuTabFragment extends Fragment {
    public static final String TABINFO = "tabinfo";

    public SlidingMenuTabFragment() { }

    public static SlidingMenuTabFragment newInstance(int tabInfo) {
        SlidingMenuTabFragment fragment = new SlidingMenuTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TABINFO, tabInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_main_post, container, false);
        Bundle initBundle = getArguments();
        //recyclerView.setLayoutManager();
        return recyclerView;
    }

    // TODO: 뷰페이저 프레그먼트 추가
}
