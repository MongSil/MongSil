package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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

import com.mukesh.image_processing.ImageProcessor;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.ColorOverlaySubfilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoFilterActivity extends BaseActivity {
    Bitmap imgFiltering;

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

        imgFiltering = getIntent().getParcelableExtra("photo");

        // 툴바
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tbDone = (TextView) toolbar.findViewById(R.id.text_done);

        // 받은 이미지
        imgImport = (ImageView) findViewById(R.id.img_import);

        // 필터 리스트
        filterRecycler = (RecyclerView) findViewById(R.id.filter_recycler);

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
                filterSave();
            }
        });

        Bitmap resizedImg = BitmapUtil.resize(
                imgFiltering, 320, false);

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

    private void filterSave() {
        Intent intent = new Intent();
        intent.putExtra("data", imgFiltering);
        setResult(RESULT_OK);
        finish();
    }

    private void imgInit() {
        ImageProcessor imageProcessor = new ImageProcessor();
        List<Bitmap> imgFilterList = new ArrayList<Bitmap>();

        Bitmap filteredImg = BitmapUtil.resize(
                imgFiltering, 320, false);
        Bitmap imgClear = imageProcessor.sharpen(filteredImg, 10);
        Bitmap imgWarm = imageProcessor.doColorFilter(filteredImg, 224, 205, 77);
        Bitmap imgCold = imageProcessor.doColorFilter(filteredImg, 224, 217, 237);
        Bitmap imgBlurry = imageProcessor.applyGaussianBlur(filteredImg);
        Bitmap imgBnW = imageProcessor.applyBlackFilter(filteredImg);

        imgFilterList.add(filteredImg);
        imgFilterList.add(imgClear);
        imgFilterList.add(imgWarm);
        imgFilterList.add(imgCold);
        imgFilterList.add(imgBlurry);
        imgFilterList.add(imgBnW);

        String[] filterTitles = getResources().getStringArray(R.array.filter_title);
        for (int i = 0; i < imgFilterList.size(); i++) {
            adapter.add(imgFilterList.get(i), filterTitles[i]);
        }
        adapter.notifyDataSetChanged();
    }

    private class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

        List<Bitmap> thumnailItems;
        ArrayList<String> filterTitleItem;

        FilterAdapter() {
            thumnailItems = new ArrayList<Bitmap>();
            filterTitleItem = new ArrayList<String>();
        }

        private void add(Bitmap thumbs, String title) {
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
            holder.imgFilterItem.setImageBitmap(thumnailItems.get(position));
            holder.imgFilterItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imgImport.setImageBitmap(imgFiltering);
                    imgFiltering = imgImport.getDrawingCache();
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
            case android.R.id.home:
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
