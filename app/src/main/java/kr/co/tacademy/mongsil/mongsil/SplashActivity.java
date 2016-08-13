package kr.co.tacademy.mongsil.mongsil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class SplashActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_PHONE_STATE = 100;

    LinearLayout splashContainer;
    ImageView imgSplashHere, imgSplashTitle;
    ImageView imgSplashMongsil, imgSplashShadow;
    Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashContainer = (LinearLayout) findViewById(R.id.splash_container);
        // 타이틀
        imgSplashHere = (ImageView) findViewById(R.id.img_splash_title_text);
        imgSplashTitle = (ImageView) findViewById(R.id.img_splash_title);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgSplashHere.setVisibility(View.VISIBLE);
                Animation titleAnimation =
                        AnimationUtils.loadAnimation(
                                getApplicationContext(), R.anim.anim_alpha);
                imgSplashHere.startAnimation(titleAnimation);
            }
        }, 3000);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgSplashTitle.setVisibility(View.VISIBLE);
                Animation titleAnimation2 =
                        AnimationUtils.loadAnimation(
                                getApplicationContext(), R.anim.anim_alpha);
                imgSplashTitle.startAnimation(titleAnimation2);
            }
        }, 3500);

        // 몽실 이미지
        imgSplashMongsil = (ImageView) findViewById(R.id.img_splash_mongsil);
        ((AnimationDrawable) imgSplashMongsil.getDrawable()).start();
        Animation mongsilAnimation =
                AnimationUtils.loadAnimation(
                        this, R.anim.splash_interpolator);
        imgSplashMongsil.startAnimation(mongsilAnimation);

        // 몽실 그림자
        imgSplashShadow = (ImageView) findViewById(R.id.img_splash_mongsil_shadow);
        Animation shadowAnimation =
                AnimationUtils.loadAnimation(
                        this, R.anim.anim_alpha_shadow);
        imgSplashShadow.startAnimation(shadowAnimation);
    }

    // 로그인 요청
    public class AsyncLoginRequest extends AsyncTask<String, String, String> {

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
                        .add("userId", PropertyManager.getInstance().getUserId())
                        .build();
                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.POST_SERVER_USER_LOGIN)
                        .post(formBody)
                        .build();

                response = client.newCall(request).execute();
                ResponseBody responseBody = response.body();
                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.postJSONUserLogin(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                Log.e("AsyncLogin:UHE", une.toString());
            } catch (UnsupportedEncodingException uee) {
                Log.e("AsyncLogin:UEE", uee.toString());
            } catch (Exception e) {
                Log.e("AsyncLogin:E", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return "connection_fail";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("응답바디 msg 값 : ", result);
            if (result.equalsIgnoreCase("success")) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 4500);
            }else if (result.equalsIgnoreCase("fail")) {
                getSupportFragmentManager().beginTransaction().
                        add(MiddleAloneDialogFragment.newInstance(91),
                                "middle_login_fail").commit();
                splashContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SplashActivity.this, AppTutorialActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();
                    }
                });

            } else if (result.equals("일치하는 회원정보가 없습니다.")) {
                splashContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SplashActivity.this, AppTutorialActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();
                    }
                });

            } else if (result.equals("connection_fail")) {
                getSupportFragmentManager().beginTransaction().
                        add(MiddleAloneDialogFragment.newInstance(92),
                                "middle_connection_fail").commit();
            }
        }
    }

    public void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                            PERMISSION_REQUEST_PHONE_STATE);
            } else {
                //사용자가 언제나 허락
                if (PropertyManager.getInstance().getDeviceId().isEmpty()) {
                    PropertyManager.getInstance().setDeviceId(getDevicesUUID());
                }
                Log.e("생성된 UUID", PropertyManager.getInstance().getDeviceId());
                new AsyncLoginRequest().execute();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE_STATE:
                //사용자가 퍼미션을 OK했을 경우
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PropertyManager.getInstance().getDeviceId().isEmpty()) {
                        PropertyManager.getInstance().setDeviceId(getDevicesUUID());
                    }
                    Log.e("생성된 UUID", PropertyManager.getInstance().getDeviceId());
                    new AsyncLoginRequest().execute();
                } else {
                    //사용자가 퍼미션을 거절했을 경우
                    finish();
                }
                break;
        }
    }

    private String getDevicesUUID() {
        final TelephonyManager tm = (TelephonyManager)
                getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        Log.e("생성된 UUID : ", deviceId);
        return deviceId;
    }
}
