package usi.justmove.gathering.gatheringServices;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import usi.justmove.database.base.DbController;
import usi.justmove.database.controllers.LocalDbController;
import usi.justmove.database.tables.PhoneLockTable;

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
}

class ScreenEventsReceiver extends BroadcastReceiver {
    private DbController dbController;
    private boolean isLock;

    public ScreenEventsReceiver(Context context) {
        dbController = new LocalDbController(context, "JustMove");
        isLock = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record = new HashMap<>();

        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON) && isLock) {
            return;
        }

        if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            isLock = false;
        } else {
            isLock = true;
        }

        record.put(PhoneLockTable.KEY_PHONELOCK_ID, null);
        record.put(PhoneLockTable.KEY_PHONELOCK_TIMESTAMP, Long.toString(System.currentTimeMillis()));
        record.put(PhoneLockTable.KEY_PHONELOCK_STATUS, isLock ? "lock" : "unlock");
        records.add(record);
        dbController.insertRecords(PhoneLockTable.TABLE_PHONELOCK, records);

        Log.d("LOCK SERVICE", "Added record:  status: " + (isLock ? "lock" : "unlock"));
    }
}