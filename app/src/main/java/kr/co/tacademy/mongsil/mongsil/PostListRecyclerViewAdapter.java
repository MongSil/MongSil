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
 * Created by ccei on 2016-08-05.
 */
public class PostListRecyclerViewAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Post> postItems = new ArrayList<Post>();

    private static final int LAYOUT_POST = 2000;

    PostListRecyclerViewAdapter(ArrayList<Post> postItems) {
        this.postItems = postItems;
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
            postTime.setText(post.date); //.trim().split(" ")[1]
            postContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                    //intent.putExtra("post_data", data);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        view = inflater.inflate(R.layout.layout_post_item, parent, false);return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((PostViewHolder)holder).setMyData(postItems.get(position));
    }

    @Override
    public int getItemCount() {
        return postItems.size();
    }
}