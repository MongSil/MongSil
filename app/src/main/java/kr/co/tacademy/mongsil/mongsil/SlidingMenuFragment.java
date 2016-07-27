package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        View v = inflater.inflate(R.layout.fragment_sliding_menu, container, false);
        imgProfile = (ImageView) v.findViewById(R.id.img_profile);
        textName = (TextView) v.findViewById(R.id.text_name);
        textLocation = (TextView) v.findViewById(R.id.text_location);

        textMyPost = (TextView) v.findViewById(R.id.text_my_post);
        textSignedMongsil = (TextView) v.findViewById(R.id.text_signed_mongsil);
        textSignedMongsilNum = (TextView) v. findViewById(R.id.text_signed_mongsil_num);
        RecyclerView myMongsilRecyclerView =
                (RecyclerView) v.findViewById(R.id.my_mongsil_recycler);
        myMongsilRecyclerView.setLayoutManager(new LinearLayoutManager(
                MongSilApplication.getMongSilContext()));
        myMongsilRecyclerView.setAdapter(new MyMongsilRecyclerViewAdapter());

        textMakeMongsil = (TextView) v.findViewById(R.id.text_make_mongsil);
        textInviteFriend = (TextView) v.findViewById(R.id.text_invite_friend);

        return v;
    }

    public static class MyMongsilRecyclerViewAdapter
                extends RecyclerView.Adapter<MyMongsilRecyclerViewAdapter.ViewHolder> {

        public MyMongsilRecyclerViewAdapter() { }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final ImageView imgSignedMongsil;
            final TextView textSignedMongsilName, textSignedMongsilNum;
            public ViewHolder(View view) {
                super(view);
                this.view = view;
                imgSignedMongsil = (ImageView) view.findViewById(R.id.img_signed_mongsil);
                textSignedMongsilName = (TextView) view.findViewById(R.id.text_signed_mongsil_name);
                textSignedMongsilNum = (TextView) view.findViewById(R.id.text_signed_mongsil_num);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.layout_mongsil_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textSignedMongsilName.setText(String.valueOf("test" + position));
            holder.textSignedMongsilNum.setText(String.valueOf(position+1));
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
