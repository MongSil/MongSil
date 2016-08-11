package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUpActivity extends BaseActivity
        implements SignupSelectLocationDialogFragment.OnSelectLocationListener{

    // 이름, 지역 부분
    EditText editName;
    TextView location;

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
        location = (TextView) findViewById(R.id.text_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location.requestFocus();
                if(editName.getText().toString().isEmpty()) {
                    editName.setHint(getResources().getText(R.string.name));
                }
                new SignupSelectLocationDialogFragment().show(getSupportFragmentManager(), "select");
            }
        });

        imgDone = (ImageView) findViewById(R.id.img_signup_done);
        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editName.getText().toString().trim().isEmpty()) {
                    // 이름을 입력하지 않았을 때
                    // TODO : 텍스트 아래에 나올건지 다이어로그로 나올건지 논의
                }
                if(location.getText().toString().trim().isEmpty() ||
                        location.getText().toString().equals("지역")) {
                    // 지역을 입력하지 않았을 때
                }

                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                // TODO : 서버에 회원가입 전송 후 PropertyManager.getInstance().setUserId( ~~ );
                startActivity(intent);
                SignUpActivity.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSelect(String selectLocation) {
        location.setText(selectLocation);
    }
}
