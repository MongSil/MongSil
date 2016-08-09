package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUpActivity extends BaseActivity
        implements SignupSelectLocationDialogFragment.OnSelectLocationListener{

    // 이름, 지역 부분
    EditText editName;
    TextView location;
    View underlineName, underlineLocation;

    // 완료
    ImageView imgDone;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editName = (EditText) findViewById(R.id.edit_name);
        underlineName = findViewById(R.id.underline_name);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                underlineName.setBackgroundColor(getResources().getColor(R.color.gray));
                if(!editName.isFocused()) {
                    underlineName.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        location = (TextView) findViewById(R.id.text_location);
        underlineLocation = findViewById(R.id.underline_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                underlineLocation.setBackgroundColor(getResources().getColor(R.color.gray));
                getSupportFragmentManager().beginTransaction()
                        .add(new SignupSelectLocationDialogFragment(), "selectlocation")
                        .addToBackStack("selectlocation").commit();
                if(!editName.isFocused()) {
                    underlineName.setBackgroundColor(getResources().getColor(R.color.light_gray));
                }
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

    @Override
    public void onSelect(String selectLocation) {
        location.setText(selectLocation);
    }
}
