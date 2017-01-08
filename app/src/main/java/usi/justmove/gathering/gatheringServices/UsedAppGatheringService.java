package usi.justmove.gathering.gatheringServices;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import usi.justmove.database.base.DbController;
import usi.justmove.database.controllers.LocalDbController;
import usi.justmove.database.tables.LocationTable;
import usi.justmove.database.tables.SMSTable;
import usi.justmove.database.tables.UsedAppTable;
import usi.justmove.database.tables.UsedAppTypeTable;
import usi.justmove.gathering.base.StateMachine;
import usi.justmove.gathering.strategies.timebased.TimeBasedSMState;
import usi.justmove.gathering.strategies.timebased.TimeBasedSMSymbol;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.util.Log;

/**
 * Created by usi on 03/01/17.
 */

public class UsedAppGatheringService extends Service {
    private BroadcastReceiver receiver;
    private Thread stateMachineThread;
    private StateMachine<TimeBasedSMState, TimeBasedSMSymbol> stateMachine;

    @Override
    public void onCreate() {
        super.onCreate();

        Timer timer = new Timer();
        timer.schedule(new UsedAppTask(getApplicationContext()), 0, 60*1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

class UsedAppTask extends TimerTask {
    private ActivityManager mgr;
    private DbController dbController;


    public UsedAppTask(Context context) {
        mgr = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        dbController = new LocalDbController(context, "JustMove");
    }

    @Override
    public void run() {
        List<RunningAppProcessInfo> processes = mgr.getRunningAppProcesses();

        for(RunningAppProcessInfo process: processes) {

            List<Map<String, String>> records = new ArrayList<>();
            Map<String, String> record = new HashMap<>();

            record.put(UsedAppTable.KEY_USED_APP_ID, null);
            record.put(UsedAppTable.KEY_USED_APP_TIMESTAMP, Long.toString(System.currentTimeMillis()));
            record.put(UsedAppTable.KEY_USED_APP_NAME, process.processName);
            record.put(UsedAppTable.KEY_USED_APP_TYPE, "0");
            records.add(record);
            dbController.insertRecords(UsedAppTable.TABLE_USED_APP, records);
            Log.d("USED APPS SERVICE", "Added record: ts: " + record.get(UsedAppTable.KEY_USED_APP_TIMESTAMP) + ", name: " + record.get(UsedAppTable.KEY_USED_APP_NAME) + ", type: " + record.get(UsedAppTable.KEY_USED_APP_TYPE));
        }
    }
}
