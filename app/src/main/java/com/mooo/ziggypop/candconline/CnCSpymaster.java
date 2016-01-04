package com.mooo.ziggypop.candconline;

import android.app.Application;
import android.content.Context;

/**
 * Created by ziggypop on 1/4/16.
 * This is a hack to be able to get the context statically.
 */
public class CnCSpymaster extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        CnCSpymaster.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return CnCSpymaster.context;
    }
}
