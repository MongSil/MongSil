package kr.co.tacademy.mongsil.mongsil;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.MediaStoreSignature;

import java.util.List;

/**
 * Created by Han on 2016-07-30.
 */
public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.GalleryImageViewHolder>
            implements ListPreloader.PreloadSizeProvider<MediaStoreData> {

        private final List<MediaStoreData> data;
        private final int screenWidth;

        private int[] actualDimensions;

        GalleryRecyclerAdapter(Context context,
                               List<MediaStoreData> data) {
            this.data = data;

            setHasStableIds(true);

            screenWidth = getScreenWidth(context);
        }

        @Override
        public GalleryImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            final View view = inflater.inflate(R.layout.layout_gallery_item, viewGroup, false);
            view.getLayoutParams().width = screenWidth;

            if (actualDimensions == null) {
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (actualDimensions == null) {
                            actualDimensions = new int[] { view.getWidth(), view.getHeight() };
                        }
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }

            return new GalleryImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GalleryImageViewHolder viewHolder, int position) {
            MediaStoreData current = data.get(position);

            Key signature =
                    new MediaStoreSignature(current.mimeType, current.dateModified, current.orientation);

        }

        @Override
        public long getItemId(int position) {
            return data.get(position).rowId;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int[] getPreloadSize(MediaStoreData item, int adapterPosition, int perItemPosition) {
            return actualDimensions;
        }

        // Display#getSize(Point)
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        @SuppressWarnings("deprecation")
        private static int getScreenWidth(Context context) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            final int result;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point size = new Point();
                display.getSize(size);
                result = size.x;
            } else {
                result = display.getWidth();
            }
            return result;
        }

        public static final class GalleryImageViewHolder extends RecyclerView.ViewHolder {

            private final ImageView image;

            public GalleryImageViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
            }
        }
}
