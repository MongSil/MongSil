package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by Han on 2016-08-05.
 */

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.AlarmViewHolder> {
    public static final int MAX_COUNT = 5;

    AlarmRecyclerViewAdapter() { }

    public static final class AlarmViewHolder extends RecyclerView.ViewHolder {

        final ImageView imgProfile;
        final TextView alarmContent, alarmTime;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            imgProfile = (ImageView) itemView.findViewById(R.id.img_profile);
            alarmContent = (TextView) itemView.findViewById(R.id.text_alarm_content);
            alarmTime = (TextView) itemView.findViewById(R.id.text_alarm_time);
        }
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.layout_alarm_item, viewGroup, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder viewHolder, int position) {
        //viewHolder.alarmContent.setText("<b>우왕</b>: Text"));
        viewHolder.alarmTime.setText(String.valueOf("8월" + position+1 + "일"));
    }

    @Override
    public int getItemCount() {
        return MAX_COUNT;
    }
}