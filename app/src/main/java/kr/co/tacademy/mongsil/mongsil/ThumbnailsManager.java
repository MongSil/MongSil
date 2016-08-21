package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Varun on 30/06/15.
 *         <p/>
 *         Singleton Class Used to Manage filters and process them all at once
 */
public final class ThumbnailsManager {
    private static List<ThumbnailItem> filterThumbs = new ArrayList<ThumbnailItem>(10);
    private static List<ThumbnailItem> processedThumbs = new ArrayList<ThumbnailItem>(10);

    private ThumbnailsManager() {
    }

    public static void addThumb(ThumbnailItem thumbnailItem) {
        filterThumbs.add(thumbnailItem);
    }

    public static List<ThumbnailItem> processThumbs(Context context) {
        for (ThumbnailItem thumb : filterThumbs) {
            // scaling down the image
            float size = context.getResources().getDimension(R.dimen.thumbnail_size);
            thumb.image = BitmapUtil.resizeSquare(thumb.image, (int)size);
            thumb.image = thumb.filter.processFilter(thumb.image);
            processedThumbs.add(thumb);
        }
        return processedThumbs;
    }

    public static void clearThumbs() {
        filterThumbs = new ArrayList<>();
        processedThumbs = new ArrayList<>();
    }
}