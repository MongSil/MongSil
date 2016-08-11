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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Han on 2016-07-29.
 */
// 포스트 리스트 어답터
public class PostRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int LAYOUT_DATE = 1000;
    private static final int LAYOUT_POST = 2000;
    private static final int LAYOUT_MY_POST = 3000;
    private static final int LAYOUT_MORE = 9999;

    List<Post> items;
    FragmentManager fm;

    PostRecyclerViewAdapter() {
        items = new ArrayList<Post>();
    }
    PostRecyclerViewAdapter(FragmentManager fm) {
        this();
        this.fm = fm;
    }

    public void add(ArrayList<Post> postItems) {
        if(postItems.get(0).typeCode == 0 &&
                items.size() != 0) {
            postItems.remove(0);
        }
        for(int i = 0 ; i < postItems.size() ; i++) {
            items.add(postItems.get(i));
        }
        this.notifyDataSetChanged();
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

        public void setData(Post post) {
            postDate.setText(TimeData.dateCalculate(post.date));
            if(post.typeCode == 2) {
                postDate.setTextColor(0xEBEBEB);
            }
        }
    }

    // 글목록을 표시하는 뷰홀더
    public class PostViewHolder extends RecyclerView.ViewHolder {
        final View view;
        CircleImageView imgPostProfile;
        ImageView imgPostProfileIcon;
        final TextView postName, postContent, postTime;
        final RelativeLayout postContainer;
        final Button btnNext;

        public PostViewHolder(View view) {
            super(view);
            this.view = view;
            imgPostProfile = (CircleImageView) view.findViewById(R.id.img_post_profile);
            imgPostProfileIcon = (ImageView) view.findViewById(R.id.img_post_profile_icon);
            postName = (TextView) view.findViewById(R.id.text_post_name);
            postContainer = (RelativeLayout) view.findViewById(R.id.post_content_container);
            postContent = (TextView) view.findViewById(R.id.text_post_content);
            postTime = (TextView) view.findViewById(R.id.text_post_time);
            btnNext = (Button) view.findViewById(R.id.btn_next);
        }

        public void setData(final Post post) {
            if(!post.profileImg.equals("null")) {
                Glide.with(MongSilApplication.getMongSilContext())
                        .load(post.profileImg).into(imgPostProfile);
                imgPostProfileIcon.setVisibility(View.GONE);
            } else {
                imgPostProfile.setImageResource(R.color.little_dark_gray);
                imgPostProfileIcon.setVisibility(View.VISIBLE);
            }
            postName.setText(post.username);
            postContent.setText(post.content);

            postTime.setText(TimeData.timeCalculate(post.date));
            postContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                    intent.putExtra("postid", String.valueOf(post.postId));
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

        public void setData(final Post post) {
            if(!post.bgImg.equals("null")) {
                Glide.with(MongSilApplication.getMongSilContext()
                                ).load(post.bgImg).into(imgMyPostBackGround);
                myPostContent.setTextColor(
                        MongSilApplication.getMongSilContext().
                                getResources().getColor(android.R.color.white));
                myPostInfo.setTextColor(
                        MongSilApplication.getMongSilContext().
                                getResources().getColor(android.R.color.white));
            }
            myPostContent.setText(post.content);
            String[] date = post.date.split(" ");
            myPostInfo.setText(
                    String.valueOf(TimeData.PostTime(date[1])
                            + " - 댓글 " + post.replyCount));
            imgThreeDot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fm.beginTransaction()
                            .add(BottomDialogFragment.newInstance(
                                    1, String.valueOf(post.postId)), "bottom_user_post").commit();
                }
            });
            myPostCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                    intent.putExtra("postid", String.valueOf(post.postId));
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    // 하단 로딩 뷰홀더
    public class FootViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final ProgressBar loadProgress;

        public FootViewHolder(View view) {
            super(view);
            this.view = view;
            loadProgress = (ProgressBar) view.findViewById(R.id.load_progress);
        }

        public void setData() {
            loadProgress.setIndeterminate(true);
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
            case LAYOUT_MORE :
                view = inflater.inflate(R.layout.layout_foot_more, parent, false);
                return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case LAYOUT_DATE:
                ((DateViewHolder) holder).setData(items.get(position));
                break;
            case LAYOUT_POST:
                ((PostViewHolder) holder).setData(items.get(position));
                break;
            case LAYOUT_MY_POST:
                ((MyPostViewHolder) holder).setData(items.get(position));
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