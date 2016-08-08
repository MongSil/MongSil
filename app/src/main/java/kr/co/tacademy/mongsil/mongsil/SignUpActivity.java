package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUpActivity extends BaseActivity {

    // 이름, 지역 부분
    FrameLayout nameLayout, locationLayout;
    EditText editName;
    TextView location;
    View underlineName, underlineLocation;

    // 완료
    ImageView imgDone;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameLayout = (FrameLayout) findViewById(R.id.signup_name_container);
        editName = (EditText) findViewById(R.id.edit_name);
        underlineName = findViewById(R.id.underline_name);
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                underlineName.setBackgroundColor(getResources().getColor(R.color.gray));
                editName.requestFocus();
            }
        });

        locationLayout = (FrameLayout) findViewById(R.id.signup_location_container);
        location = (TextView) findViewById(R.id.text_location);
        underlineLocation = findViewById(R.id.underline_location);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : 지역선택 다이어로그 뜨게 해야함
                underlineLocation.setBackgroundColor(getResources().getColor(R.color.gray));
                editName.requestFocus();
            }
        });

        imgDone = (ImageView) findViewById(R.id.img_signup_done);
        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                SignUpActivity.this.finish();
            }
        });
    }
}
