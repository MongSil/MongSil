package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
}
