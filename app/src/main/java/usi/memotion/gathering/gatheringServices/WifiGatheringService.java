package usi.memotion.gathering.gatheringServices;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.app.Service;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import usi.memotion.R;
import usi.memotion.local.database.controllers.LocalStorageController;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.local.database.tables.WiFiTable;
import usi.memotion.stateMachines.strategies.timeBased.TBSMState;
import usi.memotion.stateMachines.strategies.timeBased.TBSMSymbol;
import usi.memotion.stateMachines.strategies.timeBased.TBStateMachine;
import usi.memotion.stateMachines.strategies.timeBased.TBStateMachineListener;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class WifiGatheringService extends Service {
    private TBStateMachine stateMachine;
    private ScheduledExecutorService scheduler;
    private BroadcastReceiver receiver;


    @Override
    public void onCreate() {
        super.onCreate();

        long stateMachineFreq = Long.parseLong(getApplicationContext().getString(R.string.stateMachineFreq));
        long dayFreq = Long.parseLong(getApplicationContext().getString(R.string.wifiDayFreq));
        long nightFreq = Long.parseLong(getApplicationContext().getString(R.string.wifiNightFreq));
        String dayStart = getApplicationContext().getString(R.string.dayStart);
        String dayEnd = getApplicationContext().getString(R.string.dayEnd);

        //broadcast receiver for the scan results
        receiver = new WifiEventsReceiver(getApplicationContext());
        //register receiver
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(receiver, filter);

        //transitions of the state machine
        TBSMState[][] transitions = new TBSMState[4][4];
        transitions[TBSMState.START.ordinal()][TBSMSymbol.IS_DAY.ordinal()] =  TBSMState.DAY;
        transitions[TBSMState.START.ordinal()][TBSMSymbol.IS_NIGHT.ordinal()] =  TBSMState.NIGHT;
        transitions[TBSMState.DAY.ordinal()][TBSMSymbol.IS_NIGHT.ordinal()] =  TBSMState.NIGHT;
        transitions[TBSMState.DAY.ordinal()][TBSMSymbol.IS_DAY.ordinal()] =  TBSMState.DAY;
        transitions[TBSMState.NIGHT.ordinal()][TBSMSymbol.IS_DAY.ordinal()] =  TBSMState.DAY;
        transitions[TBSMState.NIGHT.ordinal()][TBSMSymbol.IS_NIGHT.ordinal()] =  TBSMState.NIGHT;

        stateMachine = new TBStateMachine(transitions, TBSMState.START, dayStart, dayEnd);

        //add the observer
        stateMachine.addObserver(new WifiTBStateMachineListener(getApplicationContext(), dayFreq, nightFreq));

        //start the state machine
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(stateMachine, 0, stateMachineFreq, TimeUnit.MILLISECONDS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

class WifiTBStateMachineListener extends TBStateMachineListener {
    private Timer timer;
    private TimerTask task;
    private Context context;

    public WifiTBStateMachineListener(Context context, long dayFreq, long nightFreq) {
        super(dayFreq, nightFreq);
        this.context = context;
    }

    @Override
    protected void processDayState() {
        processState(dayFreq);
    }

    @Override
    protected void processNightState() {
        processState(nightFreq);
    }

    private void processState(long freq) {
        Log.d("WIFI", "SCHEDULED");
        if(timer != null && task != null) {
            timer.cancel();
            task.cancel();
        }

        timer = new Timer();
        task = new WifiScanTask(context);

        timer.schedule(task, 0, freq);
    }
}

class WifiEventsReceiver extends BroadcastReceiver {
    private LocalStorageController localStorageController;

    public WifiEventsReceiver(Context context) {
        localStorageController = SQLiteController.getInstance(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

            List<Map<String, String>> records = new ArrayList<>();
            WifiManager mgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                List<ScanResult> scans = mgr.getScanResults();
                ContentValues record = new ContentValues();
                Log.d("WIFI", "Scan nb " + scans.size());
                for(ScanResult scan: scans) {
                    record.put(WiFiTable.KEY_WIFI_TIMESTAMP, Long.toString(System.currentTimeMillis() - SystemClock.elapsedRealtime() + (scan.timestamp / 1000)));
                    record.put(WiFiTable.KEY_WIFI_SSID, scan.BSSID);
                    record.put(WiFiTable.KEY_WIFI_FREQ, Integer.toString(scan.frequency));
                    record.put(WiFiTable.KEY_WIFI_LEVEL, Integer.toString(scan.level));
                    Log.d("WIFI SERVICE", "Added record: ts: " + record.get(WiFiTable.KEY_WIFI_TIMESTAMP) + ", BSSID: " + record.get(WiFiTable.KEY_WIFI_SSID) + ", FREQ: " + record.get(WiFiTable.KEY_WIFI_FREQ) + ", LEVEL: " + record.get(WiFiTable.KEY_WIFI_LEVEL));
                    localStorageController.insertRecord(WiFiTable.TABLE_WIFI, record);
                    record = new ContentValues();
                }
            }
        }
    }
}

class WifiScanTask extends TimerTask {
    private WifiManager wifiMgr;

    public WifiScanTask(Context context) {
        wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void run() {
        wifiMgr.startScan();
        Log.d("WIFI", "SCANN");
    }
}