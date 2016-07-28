package kr.co.tacademy.mongsil.mongsil;

import android.app.Application;
import android.content.Context;

/**
 * Created by ccei on 2016-07-27.
 */
public class MongSilApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getMongSilContext() {
        return context;
    }
}
