package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

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
        PostRecyclerViewAdapter adapter = new PostRecyclerViewAdapter();
        postRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        MongSilApplication.getMongSilContext()));
        postRecyclerView.setAdapter(adapter);

        ////// sample code
        PostData data = new PostData();
        PostData data1 = new PostData();
        PostData data2 = new PostData();
        PostData data3 = new PostData();
        PostData data4 = new PostData();
        data.setTimeData(0, "Today");
        adapter.add(data);
        data1.setData(1, "10:25 AM", R.mipmap.ic_launcher, "스님", "날이 밝구나");
        adapter.add(data1);
        data2.setData(1, "02:25 PM", R.mipmap.ic_launcher, "주지스님", "날씨가 덥구나");
        adapter.add(data2);
        data3.setTimeData(0, "2016.07.29");
        adapter.add(data3);
        data4.setData(1, "05:20 PM", R.mipmap.ic_launcher, "동자스님", "밖에 나가고 싶어요 빼앢");
        adapter.add(data4);
        //////

        return postRecyclerView;
    }

    // 포스트 리스트 어답터
    public static class PostRecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<PostData> items = new ArrayList<PostData>();

        private static final int LAYOUT_DATE = 1000;
        private static final int LAYOUT_POST = 2000;

        public void add(PostData data) {
            items.add(data);
            notifyDataSetChanged();
        }

        PostRecyclerViewAdapter() { }

        // 날짜를 표시하는 뷰홀더
        public class DateViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final TextView postDate;

            public DateViewHolder(View view) {
                super(view);
                this.view = view;
                 postDate = (TextView) view.findViewById(R.id.text_post_date);
            }

            public void setMyData(PostData data) {
                // TODO: 서버에서 전송한 '날짜'데이터 삽입, 오늘이면 Today, 어제면 어제..
                 postDate.setText(data.time);
            }
        }

        // 글목록을 표시하는 뷰홀더
        public class PostViewHolder extends RecyclerView.ViewHolder {
            final View view;
            ImageView imgPostProfile;
            final TextView postName, postContent, postTime;
            final Button btnNext;

            public PostViewHolder(View view) {
                super(view);
                this.view = view;
                imgPostProfile = (ImageView) view.findViewById(R.id.img_post_profile);
                postName = (TextView) view.findViewById(R.id.text_post_name);
                postContent = (TextView) view.findViewById(R.id.text_post_content);
                postTime = (TextView) view.findViewById(R.id.text_post_time);
                btnNext = (Button) view.findViewById(R.id.btn_next);
            }

            public void setMyData(PostData data) {
                // TODO: 서버에서 전송한 게시글 목록 삽입
                imgPostProfile.setImageResource(data.imgProfile);
                postName.setText(data.name);
                postContent.setText(data.content);
                postTime.setText(data.time);
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
            return items.size();
        }
    }
}
