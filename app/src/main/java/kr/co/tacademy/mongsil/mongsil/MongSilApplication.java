package kr.co.tacademy.mongsil.mongsil;

import android.app.Application;
import android.content.Context;
import com.tsengvn.typekit.Typekit;

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
                .addItalic(Typekit.createFromAsset(this, "fonts/NotoSansKR-Medium.otf"));
        context = this;
    }

    public static Context getMongSilContext() {
        return context;
    }
}
