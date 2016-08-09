package kr.co.tacademy.mongsil.mongsil;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ccei on 2016-08-09.
 */
// 내가 쓴 댓글 어답터
public class ReplyRecyclerViewAdapter
        extends RecyclerView.Adapter<ReplyRecyclerViewAdapter.ViewHolder> {

    List<ReplyData> items;
    ReplyRecyclerViewAdapter() { items = new ArrayList<ReplyData>(); }

    public void add(ArrayList<ReplyData> replyItems) {
        for(int i = 0 ; i < replyItems.size() ; i++) {
            items.add(replyItems.get(i));
        }
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final TextView textPostContent, textMyCommentContent, textMyCommentTime;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textPostContent =
                    (TextView) view.findViewById(R.id.text_post_content);
            textMyCommentContent =
                    (TextView) view.findViewById(R.id.text_my_comment_content);
            textMyCommentTime =
                    (TextView) view.findViewById(R.id.text_my_comment_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_my_comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textPostContent.setText(
                items.get(position).postContent);
        holder.textMyCommentContent.setText(
                items.get(position).content);
        holder.textMyCommentTime.setText(
                TimeData.replyDateCalculate(items.get(position).date));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

