package kr.co.tacademy.mongsil.mongsil;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostingActivity extends BaseActivity
        implements SearchPoiDialogFragment.OnPOISearchListener,
                BottomPicDialogFragment.OnBottomPicDialogListener {
    private static final int WEATHER_COUNT = 13;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int FILTER_FROM_CAMERA = 2;

    // 툴바 필드
    TextView tbLocation, tbSave;

    // 날씨
    ImageView imgPreview, leftWeather, rightWeather;
    ViewPager selectWeatherPager;
    int pagerPos;

    // 배경
    ImageView imgPostingBackground;

    // 포스팅
    EditText editPosting;

    // 카메라
    ImageView imgCamera;

    private String area1 = "";
    private String area2 = "";

    private UpLoadValueObject upLoadFile;

    class UpLoadValueObject {
        File file; //업로드할 파일
        boolean tempFiles; //임시파일 유무

        public UpLoadValueObject(File file, boolean tempFiles) {
            this.file = resizingFile(file);
            this.tempFiles = tempFiles;
        }

        private File resizingFile(final File file) {
            new AsyncTask<Void, Void, Void>() {
                Bitmap resizedBitmap = null;

                @Override
                protected Void doInBackground(Void... voids) {
                    Looper.prepare();
                    try {
                        resizedBitmap = Glide.with(getApplicationContext()).
                                load(file).asBitmap().into(100, 100).get();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    } catch (ExecutionException ee) {
                        ee.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (resizedBitmap != null) {
                        Log.d("resizingFile", "Image resizing Done!");
                        tempSavedBitmapFile(resizedBitmap);
                    }
                    Log.d("resizingFile", "Image resizing fail...");
                }
            };
            return file;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        final Intent intent = getIntent();

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_camera_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        tbLocation = (TextView) findViewById(R.id.text_posting_location);
        tbLocation.setText(PropertyManager.getInstance().getLocation());
        tbLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(new SearchPoiDialogFragment(), "search_posting").commit();
            }
        });
        tbSave = (TextView) findViewById(R.id.text_save);
        tbSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tbSave.setEnabled(false);
                // 아이콘 코드 임시 "0"
                // 백그라운드 이미지 임시 ""
                if (!intent.hasExtra("postdata")) {
                    new AsyncPostingRequest(upLoadFile).execute(
                            area1,  // 지역1
                            area2,  // 지역2
                            PropertyManager.getInstance().getUserId(), // 아이디
                            String.valueOf(selectWeatherPager.getCurrentItem()), // 날씨 테마 코드
                            "0",    // 아이콘 코드
                            editPosting.getText().toString()    // 글 내용
                    );
                } else {
                    tbLocation.setEnabled(false);
                    Post post = intent.getParcelableExtra("postdata");
                    if(!post.bgImg.isEmpty()) {
                        Glide.with(getApplicationContext())
                                .load(post.bgImg)
                                .into(imgPostingBackground);
                    }
                    new AsyncModifyPostingRequest(intent.getStringExtra("bgImg"), upLoadFile).execute(
                            PropertyManager.getInstance().getUserId(), // 아이디
                            String.valueOf(selectWeatherPager.getCurrentItem()), // 날씨 테마 코드
                            "0",    // 아이콘 코드
                            editPosting.getText().toString()    // 글 내용
                    );
                }
            }
        });

        // 미리보기
        area1 = PropertyManager.getInstance().getLocation();
        imgPreview = (ImageView) findViewById(R.id.img_preview);
        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(PostPreviewDialogFragment.newInstance(
                                area1,
                                editPosting.getText().toString(),
                                0), "preview").commit();
            }
        });


        // 날씨 이미지 백그라운드
        imgPostingBackground = (ImageView)findViewById(R.id.img_posting_background);

        /*imgPostingBackground.setImageResource(
                    WeatherData.imgFromWeatherCode(String.valueOf(position), 1));*/

        // 날씨 선택
        selectWeatherPager =
                (ViewPager) findViewById(R.id.viewpager_posting_select_weather);
        selectWeatherPager.setAdapter(new WeatherPagerAdapter());
        leftWeather = (ImageView) findViewById(R.id.img_left_weather);
        leftWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pagerPos = selectWeatherPager.getCurrentItem();
                if (pagerPos < WEATHER_COUNT && pagerPos > 0) {
                    selectWeatherPager.setCurrentItem(pagerPos - 1);
                } else if (pagerPos == 0) {
                    selectWeatherPager.setCurrentItem(WEATHER_COUNT - 1);
                }
            }
        });
        rightWeather = (ImageView) findViewById(R.id.img_right_weather);
        rightWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pagerPos = selectWeatherPager.getCurrentItem();
                if (pagerPos < WEATHER_COUNT && pagerPos > 0) {
                    selectWeatherPager.setCurrentItem(pagerPos + 1);
                } else if (pagerPos == WEATHER_COUNT - 1) {
                    selectWeatherPager.setCurrentItem(0);
                }
            }
        });

        // 포스팅
        editPosting = (EditText) findViewById(R.id.edit_posting);
        editPosting.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    editPosting.setHint("");
                } else {
                    editPosting.setHint(getResources().getText(R.string.posting));
                }
            }
        });
        // 카메라
        imgCamera = (ImageView) findViewById(R.id.img_posting_camera);
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSDCardAvailable()) {
                    Toast.makeText(getApplicationContext(), "SD 카드가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String currentAppPackage = getPackageName();
                imageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), currentAppPackage);

                getSupportFragmentManager().beginTransaction()
                        .add(BottomPicDialogFragment.newInstance(), "bottom_pic").commit();
            }
        });

        if (intent.hasExtra("postdata")) {
            Post postData = intent.getParcelableExtra("postdata");
            modifyPosting(postData);
        }

    }

    private String modifyPostId = null;

    private void modifyPosting(Post postData) {
        modifyPostId = String.valueOf(postData.postId);
        if (!postData.area2.isEmpty()) {
            tbLocation.setText(String.valueOf(postData.area1 + ", " + postData.area2));
        } else {
            tbLocation.setText(String.valueOf(postData.area1));
        }

        selectWeatherPager.setCurrentItem(postData.weatherCode);
        editPosting.setText(postData.content);
    }

    // 날씨 뷰페이저 어뎁터
    private class WeatherPagerAdapter extends PagerAdapter {
        ImageView imgWeatherIcon;

        WeatherPagerAdapter() {
        }

        @Override
        public int getCount() {
            return WEATHER_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater()
                    .inflate(R.layout.layout_posting_select_weather, container, false);
            imgWeatherIcon = (ImageView) view.findViewById(R.id.img_weather_icon);
            imgWeatherIcon.setImageResource(
                    WeatherData.imgFromWeatherCode(String.valueOf(position + 1), 0));
            if (imgWeatherIcon.isShown()) {
                ((AnimationDrawable) imgWeatherIcon.getDrawable()).start();
            }
            container.addView(view);
            return view;
        }
    }

    /**
     * 카메라에서 이미지 가져오기
     */
    Uri currentSelectedUri; //업로드할 현재 이미지에 대한 Uri
    File imageDir; //카메라로 찍은 사진을 저장할 디렉토리
    String currentFileName;  //파일이름

    private void doTakePhotoAction() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //업로드할 파일의 이름
        currentFileName = "upload_" + String.valueOf(System.currentTimeMillis() / 1000) + ".jpg";
        currentSelectedUri = Uri.fromFile(new File(imageDir, currentFileName));
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, currentSelectedUri);
        startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case FILTER_FROM_CAMERA: {
                // 크롭된 이미지를 세팅
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    imgPostingBackground.setImageBitmap(photo);
                }
                break;
            }
            case PICK_FROM_ALBUM: {
                currentSelectedUri = data.getData();
                if (currentSelectedUri != null) {
                    //실제 Image의 full path name을 얻어온다.
                    if (findImageFileNameFromUri(currentSelectedUri)) {
                        upLoadFile = new UpLoadValueObject(new File(currentFileName), true);
                    }
                } else {
                    Bundle extras = data.getExtras();
                    Bitmap returedBitmap = (Bitmap) extras.get("data");
                    if (tempSavedBitmapFile(returedBitmap)) {
                        Log.e("임시이미지파일저장", "저장됨");
                    } else {
                        Log.e("임시이미지파일저장", "실패");
                    }
                }
                FilterIntent(currentSelectedUri);
                break;

            }
            case PICK_FROM_CAMERA: {
                //카메라캡쳐를 이용해 가져온 이미지

                upLoadFile = new UpLoadValueObject(new File(imageDir, currentFileName), true);
                FilterIntent(currentSelectedUri);
                break;
            }
        }
    }

    /*public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, currentFileName, null);
        return Uri.parse(path);
    }*/

    // TODO : 필터로 변신
    private void FilterIntent(Uri cropUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(cropUri, "image/*");

       /* intent.putExtra("outputX", 200); // crop한 이미지의 x축 크기
        intent.putExtra("outputY", 400); // crop한 이미지의 y축 크기
        intent.putExtra("aspectX", 1); // crop 박스의 x축 비율
        intent.putExtra("aspectY", 2); // crop 박스의 y축 비율*/
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, FILTER_FROM_CAMERA);
    }

    private boolean tempSavedBitmapFile(Bitmap tempBitmap) {
        boolean flag = false;
        try {
            currentFileName = "upload_" + (System.currentTimeMillis() / 1000);
            String fileSuffix = ".jpg";
            //임시파일을 실행한다.
            File tempFile = File.createTempFile(
                    currentFileName,            // prefix
                    fileSuffix,                   // suffix
                    imageDir                   // directory
            );
            final FileOutputStream bitmapStream = new FileOutputStream(tempFile);
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bitmapStream);
            upLoadFile = new UpLoadValueObject(tempFile, true);
            if (bitmapStream != null) {
                bitmapStream.close();
            }
            currentSelectedUri = Uri.fromFile(tempFile);
            flag = true;
        } catch (IOException i) {
            Log.e("저장중 문제발생", i.toString(), i);
        }
        return flag;
    }

    private boolean findImageFileNameFromUri(Uri tempUri) {
        boolean flag = false;

        //실제 Image Uri의 절대이름
        String[] IMAGE_DB_COLUMN = {MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                //Primary Key값을 추출
                String imagePK = String.valueOf(ContentUris.parseId(tempUri));
                //Image DB에 쿼리를 날린다.

                cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_DB_COLUMN,
                        MediaStore.Images.Media._ID + "=?",
                        new String[]{imagePK}, null, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    currentFileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                    Log.e("fileName", String.valueOf(currentFileName));
                    flag = true;
                }
            } catch (SQLiteException sqle) {
                Log.e("findImage....", sqle.toString(), sqle);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return flag;
    }

    // 이미지 편집 다이어로그 셀렉터 인터페이스에서 받음
    @Override
    public void onSelectBottomPic(int select) {
        switch (select) {
            case 0 :
                doTakePhotoAction();
                break;
            case 1 :
                doTakeAlbumAction();
                break;
            case 2 :
                imgPostingBackground.setImageResource(0);
                break;
        }
    }


    // 포스팅 요청
    public class AsyncPostingRequest extends AsyncTask<String, String, String> {

        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

        private UpLoadValueObject object;

        AsyncPostingRequest() {
        }

        AsyncPostingRequest(UpLoadValueObject object) {
            this.object = object;
        }

        @Override
        protected String doInBackground(String... args) {

            // TODO : 다 null 붙여주고 doinBackground 안에 넣어주기
            Response response = null;

            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                // 이미지 업로드 코드 설정
                String uploadCode = "1";
                if (object == null) {
                    uploadCode = "0";
                }
                Log.e("업로드코드", uploadCode);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("uploadCode", uploadCode);
                builder.addFormDataPart("area1", args[0]);
                builder.addFormDataPart("area2", args[1]);
                builder.addFormDataPart("userId", args[2]);
                builder.addFormDataPart("weatherCode", args[3]);
                builder.addFormDataPart("iconCode", args[4]);
                builder.addFormDataPart("content", args[5]);
                builder.addFormDataPart("date", TimeData.getNow());
                if(object != null) {
                    File file = object.file;
                    builder.addFormDataPart("bgImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                }

                RequestBody requestBody = builder.build();

                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.POST_SERVER_POST)
                        .post(requestBody) //반드시 post로
                        .build();

                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    Log.e("response결과", responseCode + "---" + response.message()); //읃답에 대한 메세지(OK)
                    Log.e("response응답바디", response.body().string()); //json으로 변신
                    return "success";
                }
            } catch (UnknownHostException une) {
                Log.e("une", une.toString());
            } catch (UnsupportedEncodingException uee) {
                Log.e("uee", uee.toString());
            } catch (Exception e) {
                Log.e("e", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return "fail";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equalsIgnoreCase("success")) {
                Intent intent = new Intent(PostingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("area1", area1);
                startActivity(intent);
                finish();
            } else if (result.equalsIgnoreCase("fail")) {
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleAloneDialogFragment.newInstance(2), "middle_post_fail").commit();
            }
            tbSave.setEnabled(true);
        }
    }

    // 포스팅 수정 요청
    public class AsyncModifyPostingRequest extends AsyncTask<String, String, String> {
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

        String beforeBgImg = null;
        UpLoadValueObject object;

        AsyncModifyPostingRequest() { }

        AsyncModifyPostingRequest(String beforeBgImg, UpLoadValueObject object) {
            if(beforeBgImg != null) {
                this.beforeBgImg = beforeBgImg;
            }
            this.object = object;
        }

        @Override
        protected String doInBackground(String... args) {

            Response response = null;

            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                String uploadCode;

                if(!beforeBgImg.isEmpty()) {
                    uploadCode = "2";
                    if(object == null) {
                        uploadCode = "3";
                    }
                } else {
                    uploadCode = "1";
                    if(object == null) {
                        uploadCode = "0";
                    }
                }
                Log.e("업로드코드", uploadCode);
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("uploadCode", uploadCode);
                    builder.addFormDataPart("userId", args[0]);
                    builder.addFormDataPart("weatherCode", args[1]);
                    builder.addFormDataPart("iconCode", args[2]);
                    builder.addFormDataPart("content", args[3]);
                    builder.addFormDataPart("date", TimeData.getNow());
                if(object != null) {
                    File file = object.file;
                    builder.addFormDataPart("bgImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                }

                RequestBody requestBody = builder.build();

                //요청 세팅
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.PUT_SERVER_POST,
                                modifyPostId))
                        .put(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    Log.e("response결과", responseCode + "---" + response.message()); //읃답에 대한 메세지(OK)
                    Log.e("response응답바디", response.body().string()); //json으로 변신
                    return "success";
                }
            } catch (UnknownHostException une) {
                Log.e("une", une.toString());
            } catch (UnsupportedEncodingException uee) {
                Log.e("uee", uee.toString());
            } catch (Exception e) {
                Log.e("e", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return "fail";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equalsIgnoreCase("success")) {
                Intent intent = new Intent(PostingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("area1", area1);
                startActivity(intent);
                finish();
            } else if (result.equalsIgnoreCase("fail")) {
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleAloneDialogFragment.newInstance(2), "middle_post_fail").commit();
            }
            tbSave.setEnabled(true);
            tbLocation.setEnabled(true);
        }
    }

    // POI 검색
    @Override
    public void onPOISearch(POIData POIData) {
        area1 = POIData.upperAddrName;
        area2 = POIData.middleAddrName;
        if (!area2.isEmpty()) {
            tbLocation.setText(String.valueOf(area1 + ", " + area2));
        } else {
            tbLocation.setText(area1);
        }
    }

    public boolean isSDCardAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void toMainActivityFromthis() {
        Intent intent = new Intent(PostingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toMainActivityFromthis();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        toMainActivityFromthis();
        super.onBackPressed();
    }
}
