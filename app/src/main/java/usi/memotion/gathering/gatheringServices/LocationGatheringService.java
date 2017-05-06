package usi.memotion.gathering.gatheringServices;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import usi.memotion.MyApplication;
import usi.memotion.R;
import usi.memotion.stateMachines.strategies.timeBased.TBStateMachineListener;
import usi.memotion.local.database.controllers.LocalStorageController;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.local.database.tables.LocationTable;
import usi.memotion.stateMachines.strategies.timeBased.TBSMState;
import usi.memotion.stateMachines.strategies.timeBased.TBSMSymbol;
import usi.memotion.stateMachines.strategies.timeBased.TBStateMachine;
import usi.memotion.utils.FrequencyHelper;

/**
 * Created by Luca Dotti on 03/01/17.
 */

public class LocationGatheringService extends Service {
    private TBStateMachine stateMachine;
    private ScheduledExecutorService scheduler;
    private LocationTBStateMachineListener listener;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Locationservice", "INITIALIZED");
        long stateMachineFreq = Long.parseLong(getApplicationContext().getString(R.string.stateMachineFreq));
        long dayFreq = Long.parseLong(getApplicationContext().getString(R.string.locationDayFreq));
        long nightFreq = Long.parseLong(getApplicationContext().getString(R.string.locationNightFreq));
        long minDistance = Long.parseLong(getApplicationContext().getString(R.string.locationMinDistance));
        String dayStart = getApplicationContext().getString(R.string.dayStart);
        String dayEnd = getApplicationContext().getString(R.string.dayEnd);

        //transitions of the state machine
        TBSMState[][] transitions = new TBSMState[4][4];
        transitions[TBSMState.START.ordinal()][TBSMSymbol.IS_DAY.ordinal()] = TBSMState.DAY;
        transitions[TBSMState.START.ordinal()][TBSMSymbol.IS_NIGHT.ordinal()] = TBSMState.NIGHT;
        transitions[TBSMState.DAY.ordinal()][TBSMSymbol.IS_NIGHT.ordinal()] = TBSMState.NIGHT;
        transitions[TBSMState.DAY.ordinal()][TBSMSymbol.IS_DAY.ordinal()] = TBSMState.DAY;
        transitions[TBSMState.NIGHT.ordinal()][TBSMSymbol.IS_DAY.ordinal()] = TBSMState.DAY;
        transitions[TBSMState.NIGHT.ordinal()][TBSMSymbol.IS_NIGHT.ordinal()] = TBSMState.NIGHT;

        stateMachine = new TBStateMachine(transitions, TBSMState.START, dayStart, dayEnd);

        listener = new LocationTBStateMachineListener(dayFreq, nightFreq);
        //add the observer
        stateMachine.addObserver(listener);

        //start the state machine
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(stateMachine, 0, stateMachineFreq, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroy() {
        scheduler.shutdown();
        listener.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}

class LocationTimeTask extends TimerTask {
    private LocationManager mgr;
    private LocalStorageController localStorageController;
    private Context context;

    public LocationTimeTask() {
        context = MyApplication.getContext();
        localStorageController = SQLiteController.getInstance(context);
        mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void run() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("Locationservice", "" + location);
            if(location == null) {
                location = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d("Locationservice", "" + location);
                saveLocation(location);
            } else {
                saveLocation(location);
            }
        }

    }

    private void saveLocation(Location location) {
        ContentValues record = new ContentValues();

        record.put(LocationTable.KEY_LOCATION_TIMESTAMP, Long.toString(System.currentTimeMillis()));

        if(location == null) {
            record.put(LocationTable.KEY_LOCATION_LATITUDE, 0);
            record.put(LocationTable.KEY_LOCATION_LONGITUDE, 0);
            record.put(LocationTable.KEY_LOCATION_PROVIDER, "unknown");
        } else {
            record.put(LocationTable.KEY_LOCATION_LATITUDE, Double.toString(location.getLatitude()));
            record.put(LocationTable.KEY_LOCATION_LONGITUDE, Double.toString(location.getLongitude()));
            record.put(LocationTable.KEY_LOCATION_PROVIDER, location.getProvider());
        }


        localStorageController.insertRecord(LocationTable.TABLE_LOCATION, record);
        Log.d("Locationservice", "ADDED RECORD: ts:" + record.get(LocationTable.KEY_LOCATION_TIMESTAMP) + ", lat: " + record.get(LocationTable.KEY_LOCATION_LATITUDE) + ", long: " + record.get(LocationTable.KEY_LOCATION_LONGITUDE));
    }
}


class LocationTBStateMachineListener extends TBStateMachineListener {
    private ScheduledExecutorService scheduler;
    private TimerTask task;
    private boolean isDay;
    private boolean init;

    public LocationTBStateMachineListener(long dayFreq, long nightFreq) {
        super(dayFreq, nightFreq);
        task = new LocationTimeTask();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        init = true;
    }

    @Override
    protected void processDayState() {
        Log.d("Locationservice", "Day");

        if(init) {
            scheduler.scheduleAtFixedRate(task, 0, dayFreq, TimeUnit.MILLISECONDS);
            init = false;
        } else {
            if(!isDay) {
                scheduler.shutdown();
                scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(task, 0, dayFreq, TimeUnit.MILLISECONDS);
            }
        }


        isDay = true;
    }

    @Override
    protected void processNightState() {
        Log.d("Locationservice", "Night");

        if(init) {
            scheduler.scheduleAtFixedRate(task, 0, nightFreq, TimeUnit.MILLISECONDS);
            init = false;
        } else {
            if(isDay) {
                scheduler.shutdown();
                scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(task, 0, nightFreq, TimeUnit.MILLISECONDS);
            }
        }


        isDay = false;
    }

    public void stop() {
        scheduler.shutdown();
    }
}