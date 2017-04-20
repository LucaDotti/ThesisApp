package usi.memotion.gathering.gatheringServices;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import usi.memotion.MyApplication;
import usi.memotion.R;
import usi.memotion.local.database.controllers.LocalStorageController;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.local.database.tables.UsedAppTable;

import android.util.Log;

/**
 * Created by usi on 03/01/17.
 */

public class UsedAppGatheringService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("USED APP SERVICE", "START");
        long freq = Long.parseLong(getApplicationContext().getString(R.string.usedAppsFreq));
        Timer timer = new Timer();
        timer.schedule(new UsedAppTask(getApplicationContext()), 0, freq);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

class UsedAppTask extends TimerTask {
    private ActivityManager mgr;
    private LocalStorageController localStorageController;


    public UsedAppTask(Context context) {
        mgr = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        localStorageController = SQLiteController.getInstance(context);
    }

    @Override
    public void run() {
        List<ActivityManager.RunningServiceInfo> processes = mgr.getRunningServices(50);

        getRunningApp();
        for (ActivityManager.RunningServiceInfo process : processes) {

            ContentValues record = new ContentValues();

            record.put(UsedAppTable.KEY_USED_APP_TIMESTAMP, Long.toString(System.currentTimeMillis()));
            record.put(UsedAppTable.KEY_USED_APP_NAME, process.service.getPackageName());
            localStorageController.insertRecord(UsedAppTable.TABLE_USED_APP, record);
            Log.d("USED APPS SERVICE", "Added record: ts: " + record.get(UsedAppTable.KEY_USED_APP_TIMESTAMP) + ", name: " + record.get(UsedAppTable.KEY_USED_APP_NAME) + ", type: " + record.get(UsedAppTable.KEY_USED_APP_TYPE));
        }
    }

        private void getRunningApp() {
            Hashtable<String, List<ActivityManager.RunningServiceInfo>> hashtable = new Hashtable<String, List<ActivityManager.RunningServiceInfo>>();
            ActivityManager am = (ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo rsi : am.getRunningServices(Integer.MAX_VALUE)) {
                String pkgName = rsi.service.getPackageName();
                if (hashtable.get(pkgName) == null) {
                    List<ActivityManager.RunningServiceInfo> list = new ArrayList<ActivityManager.RunningServiceInfo>();
                    list.add(rsi);
                    hashtable.put(pkgName, list);
                } else {
                    hashtable.get(pkgName).add(rsi);
                }
            }


            hashtable.clear();
            hashtable = null;
        }
}
