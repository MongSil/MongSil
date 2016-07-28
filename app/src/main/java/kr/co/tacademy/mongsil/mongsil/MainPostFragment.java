package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

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
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 데이터 카운트 - PostData형 아이템이 추가되면 삭제 예정
        private static final int DATA_COUNT = 3;

        List<PostData> items = new ArrayList<PostData>();

        private static final int LAYOUT_DATE = 1000;
        private static final int LAYOUT_POST = 2000;

        PostRecyclerViewAdapter() { }

        // 날짜를 표시하는 뷰홀더
        public class DateViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final TextView postdate;

            public DateViewHolder(View view) {
                super(view);
                this.view = view;
                postdate =
                        (TextView) view.findViewById(R.id.text_post_date);
            }

            public void setMyData(PostData data) {
                // TODO: 서버에서 전송한 '날짜'데이터 삽입, 오늘이면 Today, 어제면 어제..
            }
        }

        // 글목록을 표시하는 뷰홀더
        public class PostViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final TextView postdate;

            public PostViewHolder(View view) {
                super(view);
                this.view = view;
                postdate =
                        (TextView) view.findViewById(R.id.text_post_date);
            }

            public void setMyData(PostData data) {
                // TODO: 서버에서 전송한 게시글 목록 삽입
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = null;
            switch (viewType) {
                case LAYOUT_DATE :
                    view = inflater.inflate(R.layout.layout_post_date, parent, false);
                    return new DateViewHolder(view);
                case LAYOUT_POST :
                    view = inflater.inflate(R.layout.layout_post_list, parent, false);
                    return new PostViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case LAYOUT_DATE :
                    ((DateViewHolder)holder).setMyData(items.get(position));
                    break;
                case  LAYOUT_POST :
                    ((PostViewHolder)holder).setMyData(items.get(position));
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            PostData data = items.get(position);
            switch (data.type) {
                case PostData.TYPE_LAYOUT_DATE :
                    return LAYOUT_DATE;
                case PostData.TYPE_LAYOUT_POST :
                    return LAYOUT_POST;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return DATA_COUNT;
        }
    }
}
