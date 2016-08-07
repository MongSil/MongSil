package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends BaseActivity {

    // 프로필 사진 첨부 부분(삭제하기로 함)
    //CircleImageView imgSignUpProfile;

    // 이름, 지역 부분
    LinearLayout nameLayout, locationLayout;
    EditText editName, editLocation;
    View underlineName, underlineLocation;
    Animation anim;
    boolean isScaleAnim;

    // 완료
    ImageView imgDone;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        /*imgSignUpProfile = (CircleImageView)findViewById(R.id.img_signup_profile);
        imgSignUpProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                            .add(BottomDialogFragment.newInstance(0), "bottom")
                            .addToBackStack("bottom").commit();
            }
        });*/

        nameLayout = (LinearLayout) findViewById(R.id.name_layout);
        editName = (EditText) findViewById(R.id.edit_name);
        underlineName = findViewById(R.id.underline_name);
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scaleToBig(nameLayout);
                underlineName.setBackgroundColor(getResources().getColor(R.color.gray));
                editName.requestFocus();
            }
        });
        /*editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    scaleToSmall(nameLayout);
                }
            }
        });*/

        locationLayout = (LinearLayout) findViewById(R.id.location_layout);
        editLocation = (EditText) findViewById(R.id.edit_location);
        underlineLocation = findViewById(R.id.underline_location);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scaleToBig(locationLayout);
                underlineLocation.setBackgroundColor(getResources().getColor(R.color.gray));
                editName.requestFocus();
            }
        });
        /*editLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b){
                    scaleToSmall(locationLayout);
                }
            }
        });*/

        imgDone = (ImageView) findViewById(R.id.img_signup_done);
        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                SignUpActivity.this.finish();
            }
        });
    }

    public void scaleToBig(View v) {
        this.isScaleAnim = true;
        this.anim = new ScaleAnimation(
                1f, 0.2f, // Start and end values for the X axis scaling
                1f, 0.5f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(anim);
    }

    public void scaleToSmall(View v) {
        this.isScaleAnim = false;
        anim.setFillBefore(true);
        v.startAnimation(anim);
    }
}
