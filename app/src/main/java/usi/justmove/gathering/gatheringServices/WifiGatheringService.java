package usi.justmove.gathering.gatheringServices;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.app.Service;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import usi.justmove.database.base.DbController;
import usi.justmove.database.controllers.LocalDbController;
import usi.justmove.database.tables.PhoneLockTable;
import usi.justmove.database.tables.WiFiTable;
import usi.justmove.gathering.base.StateMachine;
import usi.justmove.gathering.base.StateMachineListener;
import usi.justmove.gathering.strategies.timebased.TimeBasedInputProvider;
import usi.justmove.gathering.strategies.timebased.TimeBasedSMState;
import usi.justmove.gathering.strategies.timebased.TimeBasedSMSymbol;
import usi.justmove.gathering.strategies.timebased.TimeBasedStateMachineListener;

/**
 * Created by Luca Dotti on 29/12/16.
 */
public class WifiGatheringService extends Service {
    private BroadcastReceiver receiver;
    private Thread stateMachineThread;
    private StateMachine<TimeBasedSMState, TimeBasedSMSymbol> stateMachine;

    @Override
    public void onCreate() {
        super.onCreate();

        //broadcast receiver for the scan results
        receiver = new WifiEventsReceiver(getApplicationContext());
        //register receiver
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(receiver, filter);

        //transitions of the state machine
        TimeBasedSMState[][] transitions = new TimeBasedSMState[4][4];
        transitions[TimeBasedSMState.START.ordinal()][TimeBasedSMSymbol.IS_DAY.ordinal()] =  TimeBasedSMState.DAY;
        transitions[TimeBasedSMState.START.ordinal()][TimeBasedSMSymbol.IS_NIGHT.ordinal()] =  TimeBasedSMState.NIGHT;
        transitions[TimeBasedSMState.DAY.ordinal()][TimeBasedSMSymbol.IS_NIGHT.ordinal()] =  TimeBasedSMState.NIGHT;
        transitions[TimeBasedSMState.DAY.ordinal()][TimeBasedSMSymbol.IS_DAY.ordinal()] =  TimeBasedSMState.DAY;
        transitions[TimeBasedSMState.NIGHT.ordinal()][TimeBasedSMSymbol.IS_DAY.ordinal()] =  TimeBasedSMState.DAY;
        transitions[TimeBasedSMState.NIGHT.ordinal()][TimeBasedSMSymbol.IS_NIGHT.ordinal()] =  TimeBasedSMState.NIGHT;

        //the state machine
        stateMachine = new StateMachine<>(new TimeBasedInputProvider("07:00:00", "23:00:00"), transitions, TimeBasedSMState.START, 1000);
        //add the observer
        stateMachine.addObserver(new WifiTimeBasedStateMachineListener(getApplicationContext(), 1000, 10*1000));
        stateMachineThread = new Thread(stateMachine);
        //start state machine
        stateMachineThread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

class WifiTimeBasedStateMachineListener extends TimeBasedStateMachineListener {
    private Timer timer;
    private TimerTask task;
    private Context context;

    public WifiTimeBasedStateMachineListener(Context context, long dayFreq, long nightFreq) {
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
    private DbController dbController;

    public WifiEventsReceiver(Context context) {
        dbController = new LocalDbController(context, "JustMove");
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

            List<Map<String, String>> records = new ArrayList<>();
            WifiManager mgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                List<ScanResult> scans = mgr.getScanResults();
                Map<String, String> record = new HashMap<>();

                for(ScanResult scan: scans) {
                    record.put(WiFiTable.KEY_WIFI_ID, null);
                    record.put(WiFiTable.KEY_WIFI_TIMESTAMP, Long.toString(System.currentTimeMillis() - SystemClock.elapsedRealtime() + (scan.timestamp / 1000)));
                    record.put(WiFiTable.KEY_WIFI_SSID, scan.BSSID);
                    record.put(WiFiTable.KEY_WIFI_FREQ, Integer.toString(scan.frequency));
                    record.put(WiFiTable.KEY_WIFI_LEVEL, Integer.toString(scan.level));
                    records.add(record);
                    Log.d("WIFI SERVICE", "Added record: ts: " + record.get(WiFiTable.KEY_WIFI_TIMESTAMP) + ", BSSID: " + record.get(WiFiTable.KEY_WIFI_SSID) + ", FREQ: " + record.get(WiFiTable.KEY_WIFI_FREQ) + ", LEVEL: " + record.get(WiFiTable.KEY_WIFI_LEVEL));
                    record = new HashMap<>();
                }

                dbController.insertRecords(WiFiTable.TABLE_WIFI, records);
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
    }
}