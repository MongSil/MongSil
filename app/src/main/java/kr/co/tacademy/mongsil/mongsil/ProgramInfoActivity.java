package kr.co.tacademy.mongsil.mongsil;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class ProgramInfoActivity extends AppCompatActivity {

    RelativeLayout apacheLicenseContainer, MITLicenseContainer;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_info);

        // 툴바
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        webView = (WebView) findViewById(R.id.webview_license);

        apacheLicenseContainer = (RelativeLayout) findViewById(R.id.apache_license_container);
        apacheLicenseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(webView.isShown()) {
                    webView.setVisibility(View.GONE);
                }
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl("http://www.apache.org/licenses/LICENSE-2.0.txt");
            }
        });
        MITLicenseContainer = (RelativeLayout) findViewById(R.id.mit_license_container);
        MITLicenseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(webView.isShown()) {
                    webView.setVisibility(View.GONE);
                }
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl("https://opensource.org/licenses/MIT");
            }
        });
    }

    // 툴바 메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(webView.isShown()) {
            webView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
