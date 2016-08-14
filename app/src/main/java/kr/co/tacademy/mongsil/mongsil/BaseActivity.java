package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by Han on 2016-08-01.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(TypekitContextWrapper.wrap(base));
    }
}