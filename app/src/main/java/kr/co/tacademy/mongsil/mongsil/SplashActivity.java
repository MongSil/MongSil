package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashActivity extends BaseActivity {

    LinearLayout splashContainer;
    ImageView imgSplashHere, imgSplashTitle;
    ImageView imgSplashMongsil, imgSplashShadow;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashContainer = (LinearLayout) findViewById(R.id.splash_container);

        // 타이틀
        imgSplashHere = (ImageView) findViewById(R.id.img_splash_title_text);
        imgSplashTitle = (ImageView) findViewById(R.id.img_splash_title);
        handler = new Handler();
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

        handler = new Handler();
        handler.postDelayed(new ConnectThread(), 4500);
    }
    private class ConnectThread extends Thread {
        //private boolean threadFlag;

        public void run() {
            // Socket socket = new Socket("localhost", 80);
            // TODO: 서버에서 5초안에 응답을 하지 않으면 다이어로그를 띄움(네트워크 연결이 노원할)
            // TODO: if - UUID가 서버에 존재하는지 여부 검사
            // TODO: UUID가 서버에 존재하면 바로 메인으로
            // TODO: UUID가 서버에 없다면 텍스트를 띄움(테스트는 텍스트를 띄움)
            splashContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SplashActivity.this, AppTutorialActivity.class);
                    startActivity(intent);
                    interrupt();
                    finish();
                }
            });
            /*catch (IOException e) {
                Log.e("Server Connection Error", "서버에 접속할 수 없습니다.");
            }*/
        }

    }
}
