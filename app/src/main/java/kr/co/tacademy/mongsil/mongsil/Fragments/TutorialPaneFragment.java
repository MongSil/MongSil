package kr.co.tacademy.mongsil.mongsil.Fragments;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.tacademy.mongsil.mongsil.R;

/**
 * Created by Han on 2016-08-03.
 */
public class TutorialPaneFragment extends Fragment{
    final static String LAYOUT_POSITION = "layoutposition";

    public static TutorialPaneFragment newInstance(int layoutPosition) {
        TutorialPaneFragment tutorialPaneFragment = new TutorialPaneFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_POSITION, layoutPosition);
        tutorialPaneFragment.setArguments(args);
        return tutorialPaneFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        Resources res = getResources();
        switch(getArguments().getInt(LAYOUT_POSITION)){
            case 0:
                view = inflater.inflate(
                                res.getLayout(R.layout.fragment_tutorial_one),
                                container, false);
                break;
            case 1:
                view = inflater.inflate(
                                res.getLayout(R.layout.fragment_tutorial_two),
                                container, false);
                break;
            case 2:
                view = inflater.inflate(
                                res.getLayout(R.layout.fragment_tutorial_three),
                                container, false);
                break;
        }

        return view;
    }
}