package kr.co.tacademy.mongsil.mongsil;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.lang.reflect.Field;

/**
 * Created by ccei on 2016-07-27.
 */
public class MongSilApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NotoSansKR-Regular.otf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NotoSansKR-Bold.otf"))
                .addCustom1(Typekit.createFromAsset(this, "fonts/NotoSansKR-Medium.otf"));
        context = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(TypekitContextWrapper.wrap(base));
    }

    public static Context getMongSilContext() {
        return context;
    }
}
