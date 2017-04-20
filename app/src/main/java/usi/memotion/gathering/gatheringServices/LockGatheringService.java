package usi.memotion.gathering.gatheringServices;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import usi.memotion.local.database.controllers.LocalStorageController;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.local.database.tables.PhoneLockTable;

/**
 * Created by Luca Dotti on 03/01/17.
 */
public class LockGatheringService extends Service {
    private BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new ScreenEventsReceiver(getApplicationContext());

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        getApplicationContext().registerReceiver(receiver, filter);
        Log.d("LOCK SERVICE", "Receiver registered");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}

class ScreenEventsReceiver extends BroadcastReceiver {
    private LocalStorageController localStorageController;
    private boolean isLock;

    public ScreenEventsReceiver(Context context) {
        localStorageController = SQLiteController.getInstance(context);
        isLock = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ContentValues record = new ContentValues();

        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON) && isLock) {
            return;
        }

        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            isLock = false;
        } else {
            isLock = true;
        }

        record.put(PhoneLockTable.KEY_PHONELOCK_TIMESTAMP, Long.toString(System.currentTimeMillis()));
        record.put(PhoneLockTable.KEY_PHONELOCK_STATUS, isLock ? "lock" : "unlock");
        localStorageController.insertRecord(PhoneLockTable.TABLE_PHONELOCK, record);

        Log.d("LOCK SERVICE", "Added record:  status: " + (isLock ? "lock" : "unlock"));
    }
}