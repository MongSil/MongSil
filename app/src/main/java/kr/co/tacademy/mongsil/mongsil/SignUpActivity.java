package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SignUpActivity extends BaseActivity
        implements SelectLocationDialogFragment.OnSelectLocationListener{

    // 이름, 지역 부분
    EditText editName;
    TextView location, nameFail, locationFail;

    // 완료
    ImageView imgDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editName = (EditText) findViewById(R.id.edit_name);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName.setHint("");
            }
        });
        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    editName.setHint("");
                } else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
                    editName.setHint(getResources().getText(R.string.name));
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
                if (editable != null && location != null) {
                    imgDone.setVisibility(View.VISIBLE);
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
                getSupportFragmentManager().beginTransaction()
                        .add(SelectLocationDialogFragment.newInstance(),
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

    // 가입 요청
    public class AsyncSignUpRequest extends AsyncTask<String, String, String> {

        Response response;

        @Override
        protected String doInBackground(String... args) {

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
            if (result.isEmpty() | result.equals("null")) {
                PropertyManager.getInstance().setNickname(editName.getText().toString());
                PropertyManager.getInstance().setLocation(location.getText().toString());
                PropertyManager.getInstance().setUserId(result);
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
    public void onSelect(String selectLocation) {
        location.setText(selectLocation);
    }
}
