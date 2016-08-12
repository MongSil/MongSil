package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements BottomEditDialogFragment.OnBottomEditDialogListener,
                MiddleSelectDialogFragment.OnMiddleSelectDialogListener {
    private static final int LAYOUT_DATE = 1000;
    private static final int LAYOUT_POST = 2000;
    private static final int LAYOUT_MY_POST = 3000;
    private static final int LAYOUT_MORE = 9999;

    List<Post> items;
    Context context;
    FragmentManager fm;

    Post postData;

    PostRecyclerViewAdapter(Context context) {
        this.context = context;
        items = new ArrayList<Post>();
    }
    PostRecyclerViewAdapter(Context context, FragmentManager fm) {
        this(context);
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
            postName = (TextView) view.findViewById(R.id.text_preview_post_name);
            postContainer = (RelativeLayout) view.findViewById(R.id.post_content_container);
            postContent = (TextView) view.findViewById(R.id.text_preview_post_content);
            postTime = (TextView) view.findViewById(R.id.text_preview_post_time);
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
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postid", String.valueOf(post.postId));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
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
                    postData = post;
                    if(fm != null) {
                        fm.beginTransaction()
                                .add(BottomEditDialogFragment.newInstance(),
                                        "bottom_user_post").commit();
                    }
                }
            });
            myPostCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postid", String.valueOf(post.postId));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
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

    // 글 삭제
    public class AsyncPostRemoveResponse extends AsyncTask<String, String, String> {

        Response response;

        @Override
        protected String doInBackground(String... args) {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.DELETE_SERVER_POST_REMOVE,
                                args[0]))
                        .delete()
                        .build();

                response = client.newCall(request).execute();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    Log.e("response결과", responseCode + "---" + response.message()); //읃답에 대한 메세지(OK)
                    Log.e("response응답바디", response.body().string()); //json으로 변신
                    return "success";
                }
            } catch (UnknownHostException une) {
                Log.e("aa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                Log.e("bb", uee.toString());
            } catch (Exception e) {
                Log.e("cc", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return "fail";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("success")) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("post_remove", true);
                context.startActivity(intent);
            } else if(s.equals("fail")) {
                if (fm != null ) {
                    fm.beginTransaction().
                            add(MiddleAloneDialogFragment.newInstance(1), "middle_fail").commit();
                }
            }
        }
    }

    // 글 수정과 글 삭제 하단 다이어로그
    @Override
    public void onSelectBottomEdit(int select) {
        switch (select) {
            case 0:
                Intent intent = new Intent(context, PostingActivity.class);
                intent.putExtra("postdata", postData);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case 1:
                if(fm != null) {
                    fm.beginTransaction()
                            .add(MiddleSelectDialogFragment.newInstance(0),
                                    "middle_post_remove").commit();
                }
                break;
        }
    }

    // 글 삭제 다이어로그
    @Override
    public void onMiddleSelect(int select) {
        switch (select) {
            case 0 :
                new AsyncPostRemoveResponse().execute(String.valueOf(postData.postId));
        }
    }
}