package kr.co.tacademy.mongsil.mongsil.Activities;

import kr.co.tacademy.mongsil.mongsil.Activities.BaseActivity;

@Deprecated
public class ImgFilterActivity extends BaseActivity {
    /*static
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

        imgFiltering = getIntent().getParcelableExtra("photo");

        // 툴바
        toolbar = (Toolbar)findViewById(R.id.toolbar);
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

    private void filterSave() {
        Intent intent = new Intent();
        FileOutputStream fos = null;
        Bitmap finalImg = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mongsil/"
                    + "mongsil_" + System.currentTimeMillis() + ".jpg";
            File file = new File(savePath);
            fos = new FileOutputStream(file);
            imgFiltering.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            finalImg = BitmapFactory.decodeFile(savePath, options);
            intent.putExtra("data", finalImg);
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } finally {
            try {
                if(fos != null) {
                    fos.close();
                }
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
        setResult(RESULT_OK);
        finish();
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
*/
}
