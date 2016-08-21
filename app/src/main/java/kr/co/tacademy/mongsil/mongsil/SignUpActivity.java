package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class SignUpActivity extends BaseActivity
        implements SelectLocationDialogFragment.OnSelectLocationListener,
                GPSManager.LocationCallback{

    GPSManager gpsManager;
    RelativeLayout signUpContainer;

    // 이름, 지역 부분
    EditText editName;
    TextView location, nameFail, locationFail;

    // 완료
    ImageView imgDone;

    @Override
    protected void onResume() {
        super.onResume();
        gpsManager = new GPSManager(this, this);
        gpsManager.googleApiStart();
    }

    @Override
    public void handleNewLocation(Location location) {
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        Log.e("handleNewLocation 실행 : ", lat +" "+ lng);

    }

    // TODO : GPS가 켜져 있으면 지역이 자동으로 바뀌게 해야함
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpContainer = (RelativeLayout) findViewById(R.id.signup_container);

        editName = (EditText) findViewById(R.id.edit_name);
        /*editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName.setHint("");
            }
        });*/
        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    editName.setHint("");
                    editName.setCursorVisible(true);
                } else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
                    editName.setHint(getResources().getText(R.string.name));
                    editName.setCursorVisible(false);
                }
            }
        });
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()
                        && !location.getText().toString().equals("지역")) {
                    imgDone.setVisibility(View.VISIBLE);
                } else {
                    imgDone.setVisibility(View.GONE);
                }
            }
        });
        nameFail = (TextView) findViewById(R.id.text_name_fail);

        location = (TextView) findViewById(R.id.text_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location.requestFocus();
                if(editName.getText().toString().isEmpty()) {
                    editName.setHint(getResources().getText(R.string.name));
                }
                /*if (gpsManager != null) {
                    gpsManager.connect();
                }*/
                signUpContainer.buildDrawingCache();
                Bitmap b = signUpContainer.getDrawingCache();
                getSupportFragmentManager().beginTransaction()
                        .add(SelectLocationDialogFragment.newInstance(BitmapUtil.resizeBitmap(b, 600)),
                                "select_location").commit();
            }
        });
        locationFail = (TextView) findViewById(R.id.text_location_fail);

        imgDone = (ImageView) findViewById(R.id.img_signup_done);
        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isDone = false;
                nameFail.setVisibility(View.INVISIBLE);
                locationFail.setVisibility(View.INVISIBLE);
                if(editName.getText().toString().trim().isEmpty()) {
                    // 이름을 입력하지 않았을 때
                    nameFail.setVisibility(View.VISIBLE);
                    editName.requestFocus();
                    isDone = false;
                } else {
                    isDone = true;
                }
                if(location.getText().toString().trim().isEmpty() ||
                        location.getText().toString().trim().equals("지역")) {
                    // 지역을 선택하지 않았을 때
                    locationFail.setVisibility(View.VISIBLE);
                    isDone = false;
                }
                if(isDone) {
                    imgDone.setEnabled(false);
                    new AsyncSignUpRequest().execute(
                            editName.getText().toString().trim(),
                            location.getText().toString().trim());
                }
            }
        });
    }

    // 역지오코딩 AsyncTask
    public class AsyncReGeoJSONList extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .addHeader("Accept", "application/json")
                        .addHeader("appKey", NetworkDefineConstant.SK_APP_KEY)
                        .url(String.format(
                                NetworkDefineConstant.SK_REVERSE_GEOCOING,
                                args[0], args[1]))
                        .build();
                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();

                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                Log.e("응답 코드 : ", responseCode + "");
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONResGeo(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("connectionFail", uee.toString());
            } catch (Exception e) {
                e("connectionFail", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("역지오코딩 결과 :", result+"");
            if (result != null) {
                String GPSlocation = LocationData.ChangeToShortName(result);
                Log.e("GPSlocation 결과 :", GPSlocation);
                location.setText(GPSlocation);
            }
        }
    }

    // 가입 요청
    public class AsyncSignUpRequest extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                //요청 Body 세팅==> 그전 Query Parameter세팅과 같은 개념
                RequestBody formBody = new FormBody.Builder()
                        .add("deviceId", PropertyManager.getInstance().getDeviceId())
                        .add("username", args[0])
                        .add("area", args[1])
                        .add("date", TimeData.getNow())
                        .build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.POST_SERVER_USER_SIGN_UP)
                        .post(formBody) //반드시 post로
                        .build();

                response = client.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                if (flag) {
                    return ParseDataParseHandler.postJSONUserSignUp(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                Log.e("AsyncSignUp:UHE", une.toString());
            } catch (UnsupportedEncodingException uee) {
                Log.e("AsyncSignUp:UEE", uee.toString());
            } catch (Exception e) {
                Log.e("AsyncSignUp:E", e.toString());
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
            Log.e("회원가입 결과", result+"");
            if (!result.isEmpty() | !result.equals("null")) {
                PropertyManager.getInstance().setNickname(editName.getText().toString());
                PropertyManager.getInstance().setLocation(location.getText().toString());
                PropertyManager.getInstance().setUserId(result);
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (result.equals("fail")) {
                // 실패
                getSupportFragmentManager().beginTransaction()
                        .add(MiddleAloneDialogFragment.newInstance(90),
                                "middle_signup_fail").commit();
            }
            imgDone.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSelectLocation(String selectLocation) {
        location.setText(selectLocation);
        if(!editName.getText().toString().isEmpty()) {
            imgDone.setVisibility(View.VISIBLE);
        }
    }
}
