package kr.co.tacademy.mongsil.mongsil;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        setContentView(R.layout.activity_edit_profile);

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
                if(upLoadFile != null) {
                    new FileUpLoadAsyncTask(PropertyManager.getInstance().getNickname(),
                            PropertyManager.getInstance().getLocation()).execute(upLoadFile);
                }
            }
        });

        // 프로필 사진 부분
        imgProfileContainer =
                (LinearLayout) findViewById(R.id.img_profile_container);
        imgProfile = (CircleImageView) findViewById(R.id.img_profile);
        //TODO : 로그가 왜 안뜨지
        Log.e("UserProfile : ", PropertyManager.getInstance().getUserProfileImg());
        if(!PropertyManager.getInstance().getUserProfileImg().equals("null")
                || !PropertyManager.getInstance().getUserProfileImg().equals("")) {
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
                myImageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), currentAppPackage);

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
                editName.requestFocus();
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
                getSupportFragmentManager().beginTransaction()
                        .add(SelectLocationDialogFragment.newInstance(),
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
    File myImageDir; //카메라로 찍은 사진을 저장할 디렉토리
    String currentFileName;  //파일이름

    private void doTakePhotoAction() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //업로드할 파일의 이름
        currentFileName = "upload_" + String.valueOf(System.currentTimeMillis() / 1000) + ".png";
        currentSelectedUri = Uri.fromFile(new File(myImageDir, currentFileName));
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
                        //ArrayList에 업로드할  객체를 추가한다.
                        upLoadFile = new UpLoadValueObject(new File(currentFileName), false);
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
                upLoadFile = new UpLoadValueObject(new File(myImageDir, currentFileName), false);
                cropIntent(currentSelectedUri);
                break;
            }
        }
    }
    private  void  cropIntent(Uri cropUri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(cropUri, "image/*");

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
                    myImageDir                   // directory
            );
            final FileOutputStream bitmapStream = new FileOutputStream(tempFile);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap resized = Bitmap.createScaledBitmap( tempBitmap, 300, 300, true );
            resized.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
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

    // 계정 삭제
    public class AsyncUserRemoveRequest extends AsyncTask<String, String, String> {

        Response response;

        @Override
        protected String doInBackground(String... args) {
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
            } else if(result.equals("fail")) {
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleAloneDialogFragment.newInstance(1), "middle_dialog").commit();
            }
        }
    }

    // 프로필 업로드
    private class FileUpLoadAsyncTask extends AsyncTask<UpLoadValueObject, Void, String> {
        private String username;
        private String area;
        private String uploadCode;
        //업로드할 Mime Type 설정
        private final MediaType IMAGE_MIME_TYPE = MediaType.parse("image/*");


        public FileUpLoadAsyncTask() {
        }

        public FileUpLoadAsyncTask(String username, String area) {
            this.username = username;
            this.area = area;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(UpLoadValueObject... objects) {
            Response response = null;

            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

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
                Log.e("업로드코드", uploadCode);
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("uploadCode", uploadCode);
                builder.addFormDataPart("username", username);
                builder.addFormDataPart("area", area);
                if(objects != null) {
                    File file = objects[0].file;
                    builder.addFormDataPart("profileImg", file.getName(), RequestBody.create(IMAGE_MIME_TYPE, file));
                }

                RequestBody fileUploadBody = builder.build();

                //요청 세팅
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.PUT_SERVER_USER_EDIT,
                                PropertyManager.getInstance().getUserId()))
                        .put(fileUploadBody)
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
            if (result.equalsIgnoreCase("success")) {
                UpLoadValueObject fileValue = upLoadFile;
                if (fileValue.tempFiles) {
                    fileValue.file.deleteOnExit(); //임시파일을 삭제한다
                }
                toMainActivityFromthis();
            } else if(result.equals("fail")){
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleAloneDialogFragment.newInstance(3),
                                "middle_edit_profile_fail").commit();
            }
            tbDone.setEnabled(true);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainActivityFromthis();
    }
}
