package kr.co.tacademy.mongsil.mongsil;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Han on 2016-08-03.
 */
public class TutorialPane extends Fragment{
    final static String LAYOUT_POSITION = "layoutposition";

    TextView tutorialTitle, tutorialExplain;

    public static TutorialPane newInstance(int layoutPosition) {
        TutorialPane tutorialPane = new TutorialPane();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_POSITION, layoutPosition);
        tutorialPane.setArguments(args);
        return tutorialPane;
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