package kr.co.tacademy.mongsil.mongsil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewActivity extends AppCompatActivity {

    ImageView imgClose, importImg;
    PhotoViewAttacher photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        String importImgURL = getIntent().getStringExtra("profileImg");

        imgClose = (ImageView) findViewById(R.id.img_close_x);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        importImg = (ImageView) findViewById(R.id.img_import);
        Glide.with(this).load(importImgURL).asBitmap().into(importImg);
        photoView = new PhotoViewAttacher(importImg);
    }
}
