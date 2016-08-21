package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubfilter;

import java.util.ArrayList;
import java.util.List;

public class ImgFilterActivity extends BaseActivity {
    private static final int FILTER_PHOTO = 2;
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }
    Bitmap imgFiltering;
    Handler handler = new Handler();

    // 툴바
    Toolbar toolbar;
    TextView tbDone;

    // 이미지
    ImageView imgImport;

    // 리사이클러뷰
    RecyclerView filterRecycler;
    FilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_filter);

        imgFiltering = getIntent().getParcelableExtra("data");

        // 툴바
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        tbDone = (TextView) toolbar.findViewById(R.id.text_done);

        // 받은 이미지
        imgImport = (ImageView) findViewById(R.id.img_import);

        // 필터 리스트
        filterRecycler = (RecyclerView) findViewById(R.id.filter_recycler);
/*

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(30));
        myFilter.addSubFilter(new ContrastSubFilter(1.1f));
        Bitmap outputImage = myFilter.process(inputImage);
*/

        init();

    }

    private void init() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        tbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("data", imgFiltering);
                setResult(FILTER_PHOTO);
                finish();
            }
        });

        Bitmap resizedImg = BitmapUtil.resize(
                        imgFiltering, 640, false);

        imgImport.setImageBitmap(resizedImg);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        filterRecycler.setLayoutManager(layoutManager);
        filterRecycler.setHasFixedSize(true);

        adapter = new FilterAdapter();
        filterRecycler.setAdapter(adapter);

        imgInit();

    }

    private void imgInit() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap filteredImg = BitmapUtil.resize(
                        imgFiltering, 640, false);
                ThumbnailItem imgOriginal = new ThumbnailItem(filteredImg);
                ThumbnailItem imgClear = new ThumbnailItem(filteredImg);
                ThumbnailItem imgWarm = new ThumbnailItem(filteredImg);
                ThumbnailItem imgCold = new ThumbnailItem(filteredImg);
                ThumbnailItem imgBlurry = new ThumbnailItem(filteredImg);
                ThumbnailItem imgBnW = new ThumbnailItem(filteredImg);

                // 원본
                ThumbnailsManager.clearThumbs();
                ThumbnailsManager.addThumb(imgOriginal);

                imgClear.filter = SampleFilters.getAweStruckVibeFilter();
                ThumbnailsManager.addThumb(imgClear);
                imgWarm.filter = SampleFilters.getLimeStutterFilter();
                ThumbnailsManager.addThumb(imgWarm);
                imgCold.filter = SampleFilters.getNightWhisperFilter();
                ThumbnailsManager.addThumb(imgCold);
                imgBlurry.filter = madeFilter.getBnWFilter();
                ThumbnailsManager.addThumb(imgBnW);

                List<ThumbnailItem> thumbnailItems =
                        ThumbnailsManager.processThumbs(getApplicationContext());
                String[] filterTitles = getResources().getStringArray(R.array.filter_title);
                for(int i = 0; i < thumbnailItems.size() ; i++) {
                    adapter.add(thumbnailItems.get(i), filterTitles[i]);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private static class madeFilter {
        madeFilter() { }

        private static Filter getBnWFilter() {
            Filter filter = new Filter();
            filter.addSubFilter(new ColorOverlaySubfilter(100, .0f, .0f, .0f));
            return filter;
        }
    }

    private class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

        List<ThumbnailItem> thumnailItems;
        ArrayList<String> filterTitleItem;

        FilterAdapter() {
            thumnailItems = new ArrayList<ThumbnailItem>();
            filterTitleItem = new ArrayList<String>();
        }

        private void add(ThumbnailItem thumbs, String title) {
            thumnailItems.add(thumbs);
            filterTitleItem.add(title);
        }

        public final class FilterViewHolder extends RecyclerView.ViewHolder {

            private final View view;
            private final ImageView imgFilterItem;
            private final TextView filterTitle;

            public FilterViewHolder(View view) {
                super(view);
                this.view = view;
                imgFilterItem = (ImageView) view.findViewById(R.id.img_filter_item);
                filterTitle = (TextView) view.findViewById(R.id.text_filter_title);
            }
        }

        @Override
        public FilterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = getLayoutInflater()
                    .inflate(R.layout.layout_filter_img, viewGroup, false);
            return new FilterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FilterViewHolder holder, final int position) {
            holder.imgFilterItem.setImageBitmap(thumnailItems.get(position).image);
            holder.imgFilterItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Filter imgFiltered = thumnailItems.get(position).filter;
                    imgFiltering = imgFiltered.processFilter(BitmapUtil.resize(
                            imgFiltering, 640, false));
                    imgImport.setImageBitmap(imgFiltering);
                }
            });
            holder.filterTitle.setText(filterTitleItem.get(position));
        }

        @Override
        public int getItemCount() {
            return filterTitleItem.size();
        }
    }




    // 툴바 메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
