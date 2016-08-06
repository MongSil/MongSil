package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Han on 2016-07-29.
 */
// 포스트 리스트 어답터
public class PostRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // TODO : PostDetailActivity를 열 때 인텐트에 PostId정보를 서버로 보내서 받아와야함
    // TODO : 시간과 글 내용을 나눌 방법을 찾아야함
    List<Post> items = new ArrayList<Post>();
    FragmentManager fm;

    private static final int LAYOUT_DATE = 1000;
    private static final int LAYOUT_POST = 2000;
    private static final int LAYOUT_MY_POST = 3000;

    PostRecyclerViewAdapter() { }
    PostRecyclerViewAdapter(ArrayList<Post> items) {
        this.items = items;
    }
    PostRecyclerViewAdapter(FragmentManager fm, ArrayList<Post> items) {
        this(items);
        this.fm = fm;
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

        public void setMyData(Post post) {
            // TODO: 서버에서 전송한 '날짜'데이터 삽입, 오늘이면 Today, 어제면 어제..
            String[] date = post.date.split(" ");
            postDate.setText(TimeData.dateCalculate(date[0]));
            // TODO: 프로필이랑 글목록 크기 다르게하기( 프로필은 위에 14dp)
        }
    }

    // 글목록을 표시하는 뷰홀더
    public class PostViewHolder extends RecyclerView.ViewHolder {
        final View view;
        CircleImageView imgPostProfile;
        final TextView postName, postContent, postTime;
        final RelativeLayout postContainer;
        final Button btnNext;

        public PostViewHolder(View view) {
            super(view);
            this.view = view;
            imgPostProfile = (CircleImageView) view.findViewById(R.id.img_post_profile);
            postName = (TextView) view.findViewById(R.id.text_post_name);
            postContainer = (RelativeLayout) view.findViewById(R.id.post_content_container);
            postContent = (TextView) view.findViewById(R.id.text_post_content);
            postTime = (TextView) view.findViewById(R.id.text_post_time);
            btnNext = (Button) view.findViewById(R.id.btn_next);
        }

        public void setMyData(final Post post) {
            // TODO: 서버에서 전송한 게시글 목록 삽입
            //imgPostProfile.setImageResource(post.);
            postName.setText(post.username);
            postContent.setText(post.content);

            String[] date = post.date.split(" ");
            postTime.setText(TimeData.dateCalculate(date[1]));
            postContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                    intent.putExtra("post_data", post);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    // 나의 이야기 탭 부분 뷰홀더
    public class MyPostViewHolder extends RecyclerView.ViewHolder {
        final View view;

        CardView myPostCard;
        ImageView imgMyPostBackGround, imgThreeDot;
        View littleBlackBackGround;
        TextView myPostContent, myPostInfo;

        public MyPostViewHolder(View view) {
            super(view);
            this.view = view;
            myPostCard = (CardView) view.findViewById(R.id.cardview_my_post_item);
            imgMyPostBackGround = (ImageView) view.findViewById(R.id.img_my_post_background);
            littleBlackBackGround = view.findViewById(R.id.little_black_background);
            imgThreeDot = (ImageView) view.findViewById(R.id.img_threeDot);
            myPostContent = (TextView) view.findViewById(R.id.text_my_post_content);
            myPostInfo = (TextView) view.findViewById(R.id.text_my_post_info);
        }

        public void setMyData(final Post post) {
            // TODO : 서버에서 내 작성글 목록 삽입
            /*
            imgMyPostBackGround.setImageResource( -- 백그라운드 이미지 post.back ~ -- );
            if(data.imgBackGround != null) {

                littleBlackBackGround.setVisibility(View.VISIBLE);
                myPostContent.setTextColor(
                        MongSilApplication.getMongSilContext().
                                getResources().getColor(android.R.color.white));
                myPostInfo.setTextColor(
                        MongSilApplication.getMongSilContext().
                                getResources().getColor(android.R.color.white));
            }*/
            myPostContent.setText(post.content);
            String[] date = post.date.split(" ");
            myPostInfo.setText(
                    // 0에 댓글 카운트가 들어감
                    String.valueOf(TimeData.PostTime(date[1])
                            + " - 댓글 " + 0));
            imgThreeDot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fm.beginTransaction()
                            .add(BottomDialogFragment.newInstance(1), "bottom")
                            .addToBackStack("bottom").commit();
                }
            });
            myPostCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                    intent.putExtra("post_data", post);
                    view.getContext().startActivity(intent);
                }
            });
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
                view = inflater.inflate(R.layout.layout_post_item, parent, false);
                return new PostViewHolder(view);
            case LAYOUT_MY_POST :
                view = inflater.inflate(R.layout.layout_my_post_item, parent, false);
                return new MyPostViewHolder(view);
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
            case LAYOUT_MY_POST :
                ((MyPostViewHolder)holder).setMyData(items.get(position));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Post data = items.get(position);
        switch (data.typeCode) {
            case Post.TYPE_LAYOUT_DATE :
                return LAYOUT_DATE;
            case Post.TYPE_LAYOUT_POST :
                return LAYOUT_POST;
            case Post.TYPE_LAYOUT_MY_POST :
                return LAYOUT_MY_POST;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}