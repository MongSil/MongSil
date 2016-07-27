package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
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
 * Created by ccei on 2016-07-26.
 */
public class MainPostFragment extends Fragment {

    public MainPostFragment() { }
    public static MainPostFragment newInstance() {
        MainPostFragment f = new MainPostFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RecyclerView postRecyclerView =
                (RecyclerView) inflater.inflate(R.layout.fragment_main_post, container, false);
        postRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        MongSilApplication.getMongSilContext()));
        postRecyclerView.setAdapter(new PostRecyclerViewAdapter());

        return postRecyclerView;
    }

    // 포스트 리스트 어답터
    public static class PostRecyclerViewAdapter
            extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {
        private static final int DATA_COUNT = 3;

        PostRecyclerViewAdapter() { }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final TextView postdate;
            final RecyclerView postRecyclerView;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                postdate =
                        (TextView) view.findViewById(R.id.text_post_date);
                postRecyclerView =
                        (RecyclerView) view.findViewById(R.id.post_item_recycler);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.layout_post_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        }

        @Override
        public int getItemViewType(int position) {
            // TODO: PostData에서 Type을 얻어와서 Type변환을 통해 날짜구분을 해야한다
        }

        @Override
        public int getItemCount() {
            return DATA_COUNT;
        }
    }
}
