package kr.co.tacademy.mongsil.mongsil;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Han on 2016-08-03.
 */
public class TutorialPane extends Fragment{
    final static String LAYOUT_NAME = "layoutname";

    public static TutorialPane newInstance(int layoutName) {
        TutorialPane tutorialPane = new TutorialPane();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_NAME, layoutName);
        tutorialPane.setArguments(args);
        return tutorialPane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(getArguments().getInt(LAYOUT_NAME, -1), container, false);
        return viewGroup;
    }
}