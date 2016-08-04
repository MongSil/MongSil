package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SplashActivity extends BaseActivity {

    ImageView imgSplash, imgtextSplash;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgSplash = (ImageView) findViewById(R.id.img_splash);
        // Glide.with(this).load().
        handler.postDelayed(new ConnectThread(), 100); //2000
    }
    private class ConnectThread extends Thread {
        //private boolean threadFlag;

        public void run() {
            // Socket socket = new Socket("localhost", 80);
            // TODO: 서버에서 5초안에 응답을 하지 않으면 다이어로그를 띄움(네트워크 연결이 노원할)
            // TODO: if - UUID가 서버에 존재하는지 여부 검사
            // TODO: UUID가 서버에 존재하면 바로 메인으로
            // TODO: UUID가 서버에 없다면 텍스트를 띄움(테스트는 텍스트를 띄움)
                imgtextSplash = (ImageView) findViewById(R.id.img_text_splash);
                imgtextSplash.setVisibility(View.VISIBLE);
                imgtextSplash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SplashActivity.this, AppTutorialActivity.class);
                        startActivity(intent);
                        interrupt();
                    }
                });
            /*catch (IOException e) {
                Log.e("Server Connection Error", "서버에 접속할 수 없습니다.");
            }*/
        }

    }
}
