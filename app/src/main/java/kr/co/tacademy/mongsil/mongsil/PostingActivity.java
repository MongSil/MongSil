package kr.co.tacademy.mongsil.mongsil;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostingActivity extends BaseActivity
        implements SearchPOIDialogFragment.OnPOISearchListener,
        BottomPicDialogFragment.OnBottomPicDialogListener,
        SelectWeatherFragment.OnSelectWeatherListener {
    private static final int PAGER_MAGIC_COUNT = 131072;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int FILTER_FROM_CAMERA = 2;

    // 툴바 필드
    TextView tbLocation, tbSave;

    // 날씨
    ImageView imgPreview, leftWeather, rightWeather;
    ViewPager selectWeatherPager;
    int pagerPos = 0;

    // 배경
    ScrollView scrollPosting;
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
            this.file = file;
            this.tempFiles = tempFiles;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

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
                        .add(new SearchPOIDialogFragment(), "search_posting").commit();
            }
        });
        tbSave = (TextView) findViewById(R.id.text_save);
        tbSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postContent = editPosting.getText().toString();
                if (postContent.isEmpty()) {
                    getSupportFragmentManager().beginTransaction()
                            .add(MiddleAloneDialogFragment.newInstance(12),
                                    "middle_none_post").commit();
                    editPosting.requestFocus();
                } else {
                    tbSave.setEnabled(false);
                    // 아이콘 코드 임시 "0"
                    // 백그라운드 이미지 임시 ""
                    if (!intent.hasExtra("postdata")) {
                        new AsyncPostingRequest(upLoadFile).execute(
                                area1,  // 지역1
                                area2,  // 지역2
                                PropertyManager.getInstance().getUserId(), // 아이디
                                String.valueOf(pagerPos), // 날씨 테마 코드
                                "0",    // 아이콘 코드
                                postContent    // 글 내용
                        );
                    } else {
                        tbLocation.setEnabled(false);
                        Post post = intent.getParcelableExtra("postdata");
                        if (!post.bgImg.isEmpty()) {
                            Glide.with(getApplicationContext())
                                    .load(post.bgImg)
                                    .into(imgPostingBackground);
                        }
                        new AsyncModifyPostingRequest(intent.getStringExtra("bgImg"), upLoadFile).execute(
                                PropertyManager.getInstance().getUserId(), // 아이디
                                String.valueOf(pagerPos), // 날씨 테마 코드
                                "0",    // 아이콘 코드
                                postContent     // 글 내용
                        );
                    }
                }
            }
        });

        // 미리보기
        if (intent.hasExtra("area1")) {
            area1 = intent.getStringExtra("area1");
        } else {
            area1 = PropertyManager.getInstance().getLocation();
        }
        imgPreview = (ImageView) findViewById(R.id.img_preview);
        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upLoadFile == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(PostPreviewDialogFragment.newInstance(
                                    area1,
                                    editPosting.getText().toString(),
                                    pagerPos,
                                    0), "preview").commit();
                } else {

                }
            }
        });

        // 백그라운드 이미지
        scrollPosting = (ScrollView) findViewById(R.id.scroll_posting);
        scrollPosting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        imgPostingBackground = (ImageView) findViewById(R.id.img_posting_background);
        // TODO : 누나한테 글 쓰기 기본 배경 달라고해야함

        // 날씨 선택
        selectWeatherPager =
                (ViewPager) findViewById(R.id.viewpager_posting_select_weather);
        selectWeatherPager.setAdapter(
                new WeatherPagerAdapter(getSupportFragmentManager()));
        selectWeatherPager.setCurrentItem(((PAGER_MAGIC_COUNT / 2) - 4), false);
        leftWeather = (ImageView) findViewById(R.id.img_left_weather);
        leftWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPos = selectWeatherPager.getCurrentItem();
                selectWeatherPager.setCurrentItem(currentPos - 1);
            }
        });
        rightWeather = (ImageView) findViewById(R.id.img_right_weather);
        rightWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPos = selectWeatherPager.getCurrentItem();
                selectWeatherPager.setCurrentItem(currentPos + 1);
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
        tbLocation.setEnabled(false);
        if (!postData.area2.isEmpty()) {
            tbLocation.setText(String.valueOf(postData.area1 + ", " + postData.area2));
        } else {
            tbLocation.setText(String.valueOf(postData.area1));
        }

        if (!postData.bgImg.isEmpty()) {
            Glide.with(this).load(postData.bgImg).into(imgPostingBackground);
        }

        selectWeatherPager.setCurrentItem(postData.weatherCode);
        editPosting.setText(postData.content);
    }

    // 날씨 뷰페이저 어뎁터
    private class WeatherPagerAdapter extends FragmentStatePagerAdapter {

        WeatherPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return PAGER_MAGIC_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return SelectWeatherFragment.newInstance(position);
        }
    }

    @Override
    public void onSelectWeather(int position) {
        pagerPos = position;
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
                        File galleryPicture = new File(currentFileName);
                        upLoadFile = new UpLoadValueObject(
                                resizingFile(galleryPicture, "gallery"), true);
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
                filterIntent(currentSelectedUri);
                break;

            }
            case PICK_FROM_CAMERA: {
                //카메라캡쳐를 이용해 가져온 이미지
                File cameraPicture = new File(imageDir, currentFileName);
                upLoadFile = new UpLoadValueObject(
                        resizingFile(cameraPicture, "camera"), true);
                filterIntent(currentSelectedUri);
                break;
            }
        }
    }

    private File resizingFile(final File file, String divider) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Matrix matrix = new Matrix();
        if (divider.equals("camera")) {
            matrix.postRotate(90);
        }
        Bitmap orgImage = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        Bitmap resize = Bitmap.createBitmap(
                orgImage, 0, 0, orgImage.getWidth(), orgImage.getHeight(), matrix, true);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            resize.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return file;
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /*public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, currentFileName, null);
        return Uri.parse(path);
    }*/

    private void filterIntent(Uri filterUri) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(filterUri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, null));
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
            case 0:
                doTakePhotoAction();
                break;
            case 1:
                doTakeAlbumAction();
                break;
            case 2:
                imgPostingBackground.setImageResource(0);
                break;
        }
    }

    // TODO : ext 파일로 변환해서 형님께 드려야함
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

                Request request = null;
                Log.e("업로드코드", uploadCode);
                // 이미지가 있을 경우 MultipartBody
                if (uploadCode.equals("1")) {
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
                    if (object != null) {
                        File file = object.file;
                        builder.addFormDataPart("bgImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                    }

                    RequestBody requestBody = builder.build();

                    //요청 세팅
                    request = new Request.Builder()
                            .url(NetworkDefineConstant.POST_SERVER_POST)
                            .post(requestBody)
                            .build();
                } else { // 이미지가 없을 경우 formBody
                    RequestBody formBody = new FormBody.Builder()
                            .add("uploadCode", uploadCode)
                            .add("area1", args[0])
                            .add("area2", args[1])
                            .add("userId", args[2])
                            .add("weatherCode", args[3])
                            .add("iconCode", args[4])
                            .add("content", args[5])
                            .add("date", TimeData.getNow())
                            .build();
                    //요청 세팅
                    request = new Request.Builder()
                            .url(NetworkDefineConstant.POST_SERVER_POST)
                            .post(formBody)
                            .build();
                }

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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

        AsyncModifyPostingRequest() {
        }

        AsyncModifyPostingRequest(String beforeBgImg, UpLoadValueObject object) {
            if (beforeBgImg != null) {
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

                if (!beforeBgImg.isEmpty()) {
                    uploadCode = "2";
                    if (object == null) {
                        uploadCode = "3";
                    }
                } else {
                    uploadCode = "1";
                    if (object == null) {
                        uploadCode = "0";
                    }
                }

                Request request = null;
                Log.e("업로드코드", uploadCode);
                // 이미지가 있을 경우 MultipartBody
                if (uploadCode.equals("3") || uploadCode.equals("0")) {
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("uploadCode", uploadCode);
                    builder.addFormDataPart("userId", args[0]);
                    builder.addFormDataPart("weatherCode", args[1]);
                    builder.addFormDataPart("iconCode", args[2]);
                    builder.addFormDataPart("content", args[3]);
                    builder.addFormDataPart("date", TimeData.getNow());
                    if (object != null) {
                        File file = object.file;
                        builder.addFormDataPart("bgImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                    }

                    RequestBody requestBody = builder.build();

                    //요청 세팅
                    request = new Request.Builder()
                            .url(String.format(NetworkDefineConstant.PUT_SERVER_POST,
                                    modifyPostId))
                            .put(requestBody)
                            .build();
                } else { // 이미지가 없을 경우 formBody
                    RequestBody formBody = new FormBody.Builder()
                            .add("uploadCode", uploadCode)
                            .add("userId", args[0])
                            .add("weatherCode", args[1])
                            .add("iconCode", args[2])
                            .add("content", args[3])
                            .add("date", TimeData.getNow())
                            .build();
                    //요청 세팅
                    request = new Request.Builder()
                            .url(NetworkDefineConstant.POST_SERVER_POST)
                            .post(formBody)
                            .build();
                }

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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
