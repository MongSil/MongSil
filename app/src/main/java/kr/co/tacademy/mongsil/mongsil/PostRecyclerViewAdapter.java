package kr.co.tacademy.mongsil.mongsil;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Han on 2016-07-29.
 */

// 포스트 리스트 어답터
public class PostRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<PostData> items = new ArrayList<PostData>();
    private int SELECTOR = 0;

    private static final int LAYOUT_DATE = 1000;
    private static final int LAYOUT_POST = 2000;

    public void add(PostData data) {
        items.add(data);
        notifyDataSetChanged();
    }

    PostRecyclerViewAdapter() { }
    PostRecyclerViewAdapter(int selector) {
        SELECTOR = selector;
    }

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
    // TODO : 나의 이야기(슬라이딩메뉴) 안나옴. 고쳐야함
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

    public class MyPostViewHolder extends RecyclerView.ViewHolder {
        final View view;

        ImageView imgMyPostBackGround, imgThreeDot;
        TextView textMyPostContent, textMyPostInfo;

        public MyPostViewHolder(View view) {
            super(view);
            this.view = view;
            imgMyPostBackGround = (ImageView) view.findViewById(R.id.img_my_post_background);
            imgThreeDot = (ImageView) view.findViewById(R.id.img_threeDot);
            imgThreeDot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : 프레그먼트매니저를 구해서 바텀다이어로그 설정
                    /*FragmentManager fm = FragmentManager();
                    fm.beginTransaction()
                            .add(new BottomDialogFragment(), "bottom")
                            .addToBackStack("bottom").commit();*/
                }
            });
            textMyPostContent = (TextView) view.findViewById(R.id.text_my_post_content);
            textMyPostInfo = (TextView) view.findViewById(R.id.text_my_post_info);
        }

        public void setMyData(PostData data) {
            // TODO : 서버에서 내 작성글 목록 삽입
            imgMyPostBackGround.setImageResource(data.imgBackGround);
            textMyPostContent.setText(data.content);
            textMyPostInfo.setText(String.valueOf(data.time + " - 댓글 " + data.commentCount));
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
                if(SELECTOR != 0) {
                    view = inflater.inflate(R.layout.layout_my_post_item, parent, false);
                } else {
                    view = inflater.inflate(R.layout.layout_post_item, parent, false);
                }
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
                if(SELECTOR != 0) {
                    ((MyPostViewHolder)holder).setMyData(items.get(position));
                } else {
                    ((PostViewHolder)holder).setMyData(items.get(position));
                }
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