package kr.co.tacademy.mongsil.mongsil.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.ToggleButton;

import kr.co.tacademy.mongsil.mongsil.Managers.PropertyManager;
import kr.co.tacademy.mongsil.mongsil.R;

public class SettingActivity extends BaseActivity {

    Switch commentAlarm, saveGallery;
    RelativeLayout programInfoContainer;
    ToggleButton toggleGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 툴바
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        commentAlarm = (Switch) findViewById(R.id.switch_comment);
        commentAlarm.setChecked(PropertyManager.getInstance().getAlarm());
        commentAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PropertyManager.getInstance().setAlarm(b);
            }
        });
        /*saveGallery = (Switch) findViewById(R.id.save_gallery_switch);
        saveGallery.setChecked(PropertyManager.getInstance().getSaveGallery());
        saveGallery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PropertyManager.getInstance().setSaveGallery(b);
            }
        });*/
        programInfoContainer = (RelativeLayout) findViewById(R.id.program_info_container);
        programInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, ProgramInfoActivity.class));
            }
        });
        toggleGPS = (ToggleButton) findViewById(R.id.toggle_gps);
        toggleGPS.setChecked(PropertyManager.getInstance().getUseGPS());
        toggleGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PropertyManager.getInstance().setUseGps(b);
            }
        });
    }

    // 툴바 메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                toMainActivityFromthis();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toMainActivityFromthis() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainActivityFromthis();
    }
}
