package kr.co.tacademy.mongsil.mongsil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.util.ValueIterator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Han on 2016-07-29.
 */
// 포스트 리스트 어답터
public class PostRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int LAYOUT_DATE = 1000;
    private static final int LAYOUT_POST = 2000;
    private static final int LAYOUT_MY_DATE = 3000;
    private static final int LAYOUT_MY_POST = 4000;

    List<Post> items;
    Context context;

    Post postData;

    PostRecyclerViewAdapter(Context context) {
        this.context = context;
        items = new ArrayList<Post>();
    }
    PostRecyclerViewAdapter(Activity activity, List<Post> items) {
        this.context = activity;
        this.items = items;
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

    int lastPosition = -1;
    // 글목록을 표시하는 뷰홀더
    public class PostViewHolder extends RecyclerView.ViewHolder {
        final View view;
        CardView cardviewPost;
        CircleImageView imgPostProfile;
        ImageView imgPostProfileIcon;
        final TextView postName, postContent, postTime;
        final RelativeLayout postContainer;
        final Button btnNext;

        public PostViewHolder(View view) {
            super(view);
            this.view = view;
            cardviewPost = (CardView) view.findViewById(R.id.cardview_post_item);
            imgPostProfile = (CircleImageView) view.findViewById(R.id.img_post_profile);
            imgPostProfileIcon = (ImageView) view.findViewById(R.id.img_post_profile_icon);
            postName = (TextView) view.findViewById(R.id.text_post_name);
            postContainer = (RelativeLayout) view.findViewById(R.id.post_content_container);
            postContent = (TextView) view.findViewById(R.id.text_post_content);
            postTime = (TextView) view.findViewById(R.id.text_post_time);
            btnNext = (Button) view.findViewById(R.id.btn_next);
        }

        public void setData(final Post post, int position) {
            if(!post.profileImg.equals("null")) {
                Glide.with(MongSilApplication.getMongSilContext())
                        .load(post.profileImg).into(imgPostProfile);
                imgPostProfileIcon.setVisibility(View.GONE);
                imgPostProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, OtherUserProfileActivity.class);
                        intent.putExtra("userid", String.valueOf(post.userId));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
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
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("post", post);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

/*

        private void startTransition(View view, ValueIterator.Element element) {
            Intent i = new Intent(getActivity(), PostDetailActivity.class);
            i.putExtra("ITEM_ID", element.getId());

            Pair<View, String>[] transitionPairs = new Pair[4];
            transitionPairs[0] = Pair.create(findViewById(R.id.toolbar), "toolbar"); // Transition the Toolbar
            transitionPairs[1] = Pair.create(view, "content_area"); // Transition the content_area (This will be the content area on the detail screen)

            // We also want to transition the status and navigation bar barckground. Otherwise they will flicker
            transitionPairs[2] = Pair.create(findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
            transitionPairs[3] = Pair.create(findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
            Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, transitionPairs).toBundle();

            ActivityCompat.startActivity(MainActivity.this, i, b);
        }
        // if we transition the status and navigation bar we have to wait till everything is available
        TransitionHelper.fixSharedElementTransitionForStatusAndNavigationBar(getActivity());
        // set a custom shared element enter transition
        TransitionHelper.setSharedElementEnterTransition(getActivity(), R.transition.detail_activity_shared_element_enter_transition);
        */
    }



    // 나의 이야기 날짜를 표시하는 뷰홀더
    public class MyDateViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final TextView postDate;

        public MyDateViewHolder(View view) {
            super(view);
            this.view = view;
            postDate = (TextView) view.findViewById(R.id.text_my_post_date);
        }

        public void setData(Post post) {
            postDate.setText(TimeData.dateCalculate(post.date));
            if(post.typeCode == 2) {
                postDate.setTextColor(0xEBEBEB);
            }
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
            myPostCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("post", post);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
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
            case LAYOUT_MY_DATE :
                view = inflater.inflate(R.layout.layout_my_post_date, parent, false);
                return new MyDateViewHolder(view);
            case LAYOUT_MY_POST :
                view = inflater.inflate(R.layout.layout_my_post_item, parent, false);
                return new MyPostViewHolder(view);
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
                ((PostViewHolder) holder).setData(items.get(position), position);
                break;
            case LAYOUT_MY_DATE:
                ((MyDateViewHolder) holder).setData(items.get(position));
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
            case Post.TYPE_LAYOUT_MY_DATE :
                return LAYOUT_MY_DATE;
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