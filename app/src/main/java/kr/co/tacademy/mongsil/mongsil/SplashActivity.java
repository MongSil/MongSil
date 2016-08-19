package kr.co.tacademy.mongsil.mongsil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

public class SplashActivity extends BaseActivity {
    LinearLayout splashContainer;
    ImageView imgSplashHere, imgSplashTitle;
    ImageView imgSplashMongsil, imgSplashShadow;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new AsyncLoginRequest().execute();
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
        if(imgSplashMongsil.isShown()) {
            ((AnimationDrawable) imgSplashMongsil.getDrawable()).start();
        }
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
        @Override
        protected String doInBackground(String... args) {
            Response response = null;
            try {
                //업로드는 타임 및 리드타임을 넉넉히 준다.
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                if(PropertyManager.getInstance().getDeviceId().isEmpty()) {
                    PropertyManager.getInstance().setDeviceId(getDevicesUUID());
                }

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
                    Log.e("UserId : ", " -- " + PropertyManager.getInstance().getUserId());
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
                splashContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toMainActivityFromthis();
                    }
                });
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

    private String getDevicesUUID() {
        UUID deviceUUID = null;
        try {
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            deviceUUID = UUID.nameUUIDFromBytes(deviceId.getBytes("utf8"));
            Log.e("생성된 UUID : ", deviceId);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return deviceUUID.toString();
    }

    private void toMainActivityFromthis() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
