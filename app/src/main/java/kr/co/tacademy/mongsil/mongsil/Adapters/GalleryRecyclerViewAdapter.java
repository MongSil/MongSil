package kr.co.tacademy.mongsil.mongsil.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kr.co.tacademy.mongsil.mongsil.R;

/**
 * Created by Han on 2016-07-30.
 */
@Deprecated
public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.GalleryImageViewHolder> {

    GalleryRecyclerViewAdapter() { }

    public static final class GalleryImageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;

        public GalleryImageViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    @Override
    public GalleryImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.layout_gallery_item, viewGroup, false);

        return new GalleryImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryImageViewHolder viewHolder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
