package kr.co.tacademy.mongsil.mongsil.Adapters;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.tacademy.mongsil.mongsil.Activities.OtherUserProfileActivity;
import kr.co.tacademy.mongsil.mongsil.Activities.PostDetailActivity;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.ReplyData;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.TimeData;
import kr.co.tacademy.mongsil.mongsil.Managers.PropertyManager;
import kr.co.tacademy.mongsil.mongsil.MongSilApplication;
import kr.co.tacademy.mongsil.mongsil.R;

/**
 * Created by ccei on 2016-08-09.
 */
// 내가 쓴 댓글 어답터
public class ReplyRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int POST_REPLY = 4000;
    public static final int USERS_REPLY = 5000;

    ReplyAdapterCallback callback;
    FragmentManager fragmentManager;
    List<ReplyData> items;

    public ReplyRecyclerViewAdapter(ReplyAdapterCallback callback) {
        this.callback = callback;
        items = new ArrayList<ReplyData>();
    }

    public ReplyRecyclerViewAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        items = new ArrayList<ReplyData>();
    }

    public void add(ArrayList<ReplyData> replyItems) {
        items.clear();
        for(int i = 0 ; i < replyItems.size() ; i++) {
            items.add(replyItems.get(i));
        }
        this.notifyDataSetChanged();
    }

    // 글 상세보기 댓글
    public class PostReplyViewHolder extends RecyclerView.ViewHolder {
        final View view;
        CircleImageView imgProfile;
        ImageView imgProfileIcon;
        RelativeLayout contentContainer;
        final TextView textName, textCommentContent;

        public PostReplyViewHolder(View view) {
            super(view);
            this.view = view;
            imgProfile = (CircleImageView) view.findViewById(R.id.img_profile);
            imgProfileIcon = (ImageView) view.findViewById(R.id.img_none_profile_icon);
            contentContainer = (RelativeLayout) view.findViewById(R.id.reply_content_container);
            textName = (TextView) view.findViewById(R.id.text_name);
            textCommentContent =
                    (TextView) view.findViewById(R.id.text_comment_content);
        }

        private void setData(final ReplyData data) {
            if(!data.getProfileImg().equals("null")) {
                Glide.with(MongSilApplication.getMongSilContext())
                        .load(data.getProfileImg()).into(imgProfile);
                imgProfileIcon.setVisibility(View.GONE);
            } else {
                imgProfile.setImageResource(R.color.little_dark_gray);
                imgProfileIcon.setVisibility(View.VISIBLE);
            }

            imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            MongSilApplication.getMongSilContext(), OtherUserProfileActivity.class);
                    intent.putExtra("userid", String.valueOf(data.getUserId()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MongSilApplication.getMongSilContext().startActivity(intent);
                }
            });

            if(PropertyManager.getInstance().getUserId().equals(String.valueOf(data.getUserId()))) {
                contentContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.onReplySelectCallback(data);
                    }
                });
            }
            textName.setText(data.getUsername());
            textCommentContent.setText(data.getContent());
        }
    }
    // 내가 쓴 댓글
    public class UsersReplyViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final CardView userReplyCard;
        final TextView textPostContent, textMyCommentContent, textMyCommentTime;

        public UsersReplyViewHolder(View view) {
            super(view);
            this.view = view;
            userReplyCard =
                    (CardView) view.findViewById(R.id.cardview_reply_item);
            textPostContent =
                    (TextView) view.findViewById(R.id.text_post_content);
            textMyCommentContent =
                    (TextView) view.findViewById(R.id.text_my_comment_content);
            textMyCommentTime =
                    (TextView) view.findViewById(R.id.text_my_comment_time);
        }

        private void setData(final ReplyData data) {
            userReplyCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                    intent.putExtra("reply", data);
                    view.getContext().startActivity(intent);
                }
            });
            textPostContent.setText(data.getPostContent());
            textMyCommentContent.setText(data.getContent());
            textMyCommentTime.setText(TimeData.replyDateCalculate(data.getDate()));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case POST_REPLY :
                view = inflater.inflate(R.layout.layout_comment_item, parent, false);
                return new PostReplyViewHolder(view);
            case USERS_REPLY :
                view = inflater.inflate(R.layout.layout_my_comment_item, parent, false);
                return new UsersReplyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case POST_REPLY :
                ((PostReplyViewHolder)holder).setData(items.get(position));
                break;
            case USERS_REPLY :
                ((UsersReplyViewHolder)holder).setData(items.get(position));
                break;
        }
    }
    @Override
    public int getItemViewType(int position) {
        ReplyData data = items.get(position);
        switch (data.getTypeCode()) {
            case ReplyData.TYPE_POST_REPLY :
                return POST_REPLY;
            case ReplyData.TYPE_USERS_REPLY :
                return USERS_REPLY;
        }
        return super.getItemViewType(position);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}

