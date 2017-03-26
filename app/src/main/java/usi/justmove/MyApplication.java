package usi.justmove;

import android.app.Application;
import android.content.Context;

/**
 * Created by usi on 10/03/17.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}
