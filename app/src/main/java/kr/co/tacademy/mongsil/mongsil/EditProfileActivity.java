package kr.co.tacademy.mongsil.mongsil;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ccei on 2016-08-04.
 */
public class EditProfileActivity extends BaseActivity
        implements BottomPicDialogFragment.OnBottomPicDialogListener,
                MiddleSelectDialogFragment.OnMiddleSelectDialogListener,
                SelectLocationDialogFragment.OnSelectLocationListener {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    LinearLayout editProfileContainer;

    // 툴바
    TextView tbCancel, tbDone;

    // 프로필 사진
    LinearLayout imgProfileContainer;
    CircleImageView imgProfile;

    // 기본정보
    LinearLayout editNameContainer, editLocationContainer;
    EditText editName;
    TextView editLocation;

    // 계정삭제
    RelativeLayout leaveProfileContainer;

    private UpLoadValueObject upLoadFile = null;

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
        setContentView(R.layout.activity_edit_profile);

        editProfileContainer = (LinearLayout) findViewById(R.id.edit_profile_container);

        // 툴바 추가
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        tbCancel = (TextView) toolbar.findViewById(R.id.toolbar_cancel);
        tbCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMainActivityFromthis();
            }
        });
        tbDone = (TextView) toolbar.findViewById(R.id.toolbar_done);
        tbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PropertyManager.getInstance().setNickname(editName.getText().toString());
                PropertyManager.getInstance().setLocation(editLocation.getText().toString());
                new ProfileUpdateAsyncTask(
                        PropertyManager.getInstance().getNickname(),
                        PropertyManager.getInstance().getLocation())
                        .execute(upLoadFile);
            }
        });

        // 프로필 사진 부분
        imgProfileContainer =
                (LinearLayout) findViewById(R.id.img_profile_container);
        imgProfile = (CircleImageView) findViewById(R.id.img_profile);
        if(!PropertyManager.getInstance().getUserProfileImg().isEmpty()) {
            Glide.with(MongSilApplication.getMongSilContext())
                    .load(PropertyManager.getInstance().getUserProfileImg())
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.none_my_profile);
        }
        imgProfileContainer.setOnClickListener(new View.OnClickListener() {
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

        // 이름 편집 부분
        editNameContainer =
                (LinearLayout) findViewById(R.id.edit_name_container);
        editName = (EditText) findViewById(R.id.edit_name);
        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    editName.setHint("");
                } else {
                    editName.setHint(getResources().getText(R.string.posting));
                }
            }
        });
        editName.setText(PropertyManager.getInstance().getNickname());
        editNameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editName, 0);
            }
        });

        // 지역 편집 부분
        editLocationContainer =
                (LinearLayout) findViewById(R.id.edit_location_container);
        editLocation = (TextView) findViewById(R.id.edit_location);
        editLocation.setText(PropertyManager.getInstance().getLocation());
        editLocationContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapUtil.viewToBitmap(editProfileContainer);
                getSupportFragmentManager().beginTransaction()
                        .add(SelectLocationDialogFragment.newInstance(BitmapUtil.resizeBitmap(bitmap, 600)),
                                "select_location").commit();
            }
        });

        // 계정 삭제 부분
        leaveProfileContainer =
                (RelativeLayout) findViewById(R.id.leave_profile_container);
        leaveProfileContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleSelectDialogFragment.newInstance(99),
                                "middle_leave").commit();
            }
        });
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
            case CROP_FROM_CAMERA: {
                // 크롭된 이미지를 세팅
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    imgProfile.setImageBitmap(photo);
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
                cropIntent(currentSelectedUri);
                break;

            }
            case PICK_FROM_CAMERA: {
                //카메라캡쳐를 이용해 가져온 이미지
                File cameraPicture = new File(imageDir, currentFileName);
                upLoadFile = new UpLoadValueObject(
                        resizingFile(cameraPicture, "camera"), true);
                cropIntent(currentSelectedUri);
                break;
            }
        }
    }

    private File resizingFile(final File file, String divider) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
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
                if(out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    /*public synchronized static int getExifOrientation(String filepath)
    {
        int degree = 0;
        ExifInterface exif = null;

        try
        {
            exif = new ExifInterface(filepath);
        }
        catch (IOException e)
        {
            Log.e(TAG, "cannot read exif");
            e.printStackTrace();
        }

        if (exif != null)
        {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1)
            {
                // We only recognize a subset of orientation tag values.
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }

        return degree;
    }
    public synchronized static Bitmap getRotatedBitmap(Bitmap bitmap, int degrees)
    {
        if ( degrees != 0 && bitmap != null )
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try
            {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2)
                {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError ex)
            {
                // We have no memory to rotate. Return the original bitmap.
            }
        }

        return bitmap;
    }

    public synchronized static Bitmap safeDecodeBitmapFile(File file)
    {
        try
        {
            if (file.exists() == false)
            {
                return null;
            }

            // Max image size
            final int IMAGE_MAX_SIZE 	= 1080;
            BitmapFactory.Options bfo 	= new BitmapFactory.Options();
            bfo.inJustDecodeBounds 		= true;

            BitmapFactory.decodeFile(file.getAbsolutePath(), bfo);

            if(bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE)
            {
                bfo.inSampleSize = (int)Math.pow(2, (int)Math.round(Math.log(IMAGE_MAX_SIZE
                        / (double) Math.max(bfo.outHeight, bfo.outWidth)) / Math.log(0.5)));
            }
            bfo.inJustDecodeBounds = false;
            final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bfo);

            int degree = getExifOrientation(file.getAbsolutePath());

            return getRotatedBitmap(bitmap, degree);
        }
        catch(OutOfMemoryError ex)
        {
            ex.printStackTrace();

            return null;
        }
    }*/

    private  void  cropIntent(Uri cropUri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(cropUri, "image");

        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_FROM_CAMERA);
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
                PropertyManager.getInstance().setUserProfileImg("");
                imgProfile.setImageResource(R.drawable.none_my_profile);
                break;
        }
    }

    // 지역 선택 다이어로그 셀렉터 인터페이스에서 받음
    @Override
    public void onSelectLocation(String selectLocation) {
        editLocation.setText(selectLocation);
    }

    // 계정 삭제 다이어로그 셀렉터 인터페이스에서 받음
    @Override
    public void onMiddleSelect(int select) {
        switch (select) {
            case 99 :
                new AsyncUserRemoveRequest().execute();
        }
    }

    // 프로필 업로드
    private class ProfileUpdateAsyncTask extends AsyncTask<UpLoadValueObject, Void, UserData> {
        private String username;
        private String area;
        private String uploadCode;
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");

        public ProfileUpdateAsyncTask(String username, String area) {
            this.username = username;
            this.area = area;
        }

        @Override
        protected UserData doInBackground(UpLoadValueObject... objects) {
            Response response = null;

            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                // 이미지 업로드 코드 설정
                if(!PropertyManager.getInstance().getUserProfileImg().isEmpty()) {
                    uploadCode = "2";
                    if(objects == null) {
                        uploadCode = "3";
                    }
                } else {
                    uploadCode = "1";
                    if(objects == null) {
                        uploadCode = "0";
                    }
                }

                Request request = null;
                Log.e("업로드코드", uploadCode);
                // 이미지가 있을 경우 MultipartBody
                if(uploadCode.equals("3") || uploadCode.equals("0")) {
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("uploadCode", uploadCode);
                    builder.addFormDataPart("username", username);
                    builder.addFormDataPart("area", area);
                    if (objects != null) {
                        File file = objects[0].file;
                        builder.addFormDataPart("profileImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                    }
                    RequestBody fileUploadBody = builder.build();

                    //요청 세팅
                    request = new Request.Builder()
                            .url(String.format(NetworkDefineConstant.PUT_SERVER_USER_EDIT,
                                    PropertyManager.getInstance().getUserId()))
                            .put(fileUploadBody)
                            .build();
                } else { // 이미지가 없을 경우 formBody
                    RequestBody formBody = new FormBody.Builder()
                            .add("uploadCode", uploadCode)
                            .add("username", username)
                            .add("area", area)
                            .build();
                    //요청 세팅
                    request = new Request.Builder()
                            .url(String.format(NetworkDefineConstant.PUT_SERVER_USER_EDIT,
                                    PropertyManager.getInstance().getUserId()))
                            .put(formBody)
                            .build();
                }

                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();

                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONUserInfo(
                            new StringBuilder(responseBody.string()));
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
            return null;
        }

        @Override
        protected void onPostExecute(UserData result) {
            super.onPostExecute(result);
            Log.e("Result value : ", "> " + result);
            if (result != null) {
                if(upLoadFile != null) {
                    UpLoadValueObject fileValue = upLoadFile;
                    if (fileValue.tempFiles) {
                        fileValue.file.deleteOnExit(); //임시파일을 삭제한다
                    }
                    PropertyManager.getInstance().setUserProfileImg(result.profileImg);
                }
                toMainActivityFromthis();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleAloneDialogFragment.newInstance(3),
                                "middle_edit_profile_fail").commit();
            }
            tbDone.setEnabled(true);
        }
    }

    // 계정 삭제
    public class AsyncUserRemoveRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.DELETE_SERVER_USER_REMOVE,
                                PropertyManager.getInstance().getUserId()))
                        .delete()
                        .build();

                response = client.newCall(request).execute();
                boolean flag = response.isSuccessful();
                //응답 코드 200등등
                int responseCode = response.code();
                if (flag) {
                    Log.e("response결과", responseCode + "---" + response.message()); //읃답에 대한 메세지(OK)
                    Log.e("response응답바디", response.body().string()); //json으로 변신
                    return "success";
                }
            } catch (UnknownHostException une) {
                Log.e("aa", une.toString());
            } catch (UnsupportedEncodingException uee) {
                Log.e("bb", uee.toString());
            } catch (Exception e) {
                Log.e("cc", e.toString());
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
            if (result.equals("success")) {
                Intent intent = new Intent(EditProfileActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            } else if(result.equals("fail")) {
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleAloneDialogFragment.newInstance(2), "middle_dialog").commit();
            }
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
        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainActivityFromthis();
    }
}
