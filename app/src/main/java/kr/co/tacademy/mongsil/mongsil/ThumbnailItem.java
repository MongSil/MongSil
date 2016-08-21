package kr.co.tacademy.mongsil.mongsil;

import android.graphics.Bitmap;

import com.zomato.photofilters.imageprocessors.Filter;

/**
 * Created by Han on 2016-08-21.
 */
public class ThumbnailItem {
    public Bitmap image;
    public Filter filter;

    public ThumbnailItem() { }
    public ThumbnailItem(Bitmap image) {
        this.image = image;
        filter = new Filter();
    }
}