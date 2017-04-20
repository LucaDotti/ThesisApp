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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LOCATION SERVICE", "INITIALIZED");
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

        //add the observer
        stateMachine.addObserver(new LocationTBStateMachineListener(getApplicationContext(), dayFreq, nightFreq, minDistance));

        //start the state machine
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(stateMachine, 0, stateMachineFreq, TimeUnit.MILLISECONDS);
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

class LocationTBStateMachineListener extends TBStateMachineListener {
    private Context context;
    private LocationManager mgr;
    private LocationListener listener;
    private float minDistance;
    private HandlerThread locationHandlerThread;

    public LocationTBStateMachineListener(Context context, long dayFreq, long nightFreq, float minDistance) {
        super(dayFreq, nightFreq);
        this.context = context;
        this.minDistance = minDistance;

        mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void processDayState() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION SERVICE", "DAY");
            if(listener != null) {
                mgr.removeUpdates(listener);
            }

            listener = new LocationEventListener(context);

            locationHandlerThread = new HandlerThread("LocationHandlerThread");
            locationHandlerThread.start();

            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, dayFreq, minDistance, listener, locationHandlerThread.getLooper());
        }

    }

    @Override
    protected void processNightState() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Log.d("LOCATION SERVICE", "NIGHT");
        if(listener != null) {
            mgr.removeUpdates(listener);
        } else {
            listener = new LocationEventListener(context);
        }

        Looper.prepare();
        mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FrequencyHelper.getElapseTimeMillis(nightFreq), minDistance, listener);
        mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
}

class LocationEventListener implements LocationListener {
    private Context context;
    private LocalStorageController localStorageController;

    public LocationEventListener(Context context) {
        this.context = context;
        localStorageController = SQLiteController.getInstance(context);
    }

    @Override
    public void onLocationChanged(Location location) {
        ContentValues record = new ContentValues();

        record.put(LocationTable.KEY_LOCATION_TIMESTAMP, Long.toString(System.currentTimeMillis()));
        record.put(LocationTable.KEY_LOCATION_LATITUDE, Double.toString(location.getLatitude()));
        record.put(LocationTable.KEY_LOCATION_LONGITUDE, Double.toString(location.getLongitude()));
        record.put(LocationTable.KEY_LOCATION_PROVIDER, "GPS");
        localStorageController.insertRecord(LocationTable.TABLE_LOCATION, record);
        Log.d("LOCATION SERVICE", "ADDED RECORD: ts:" + record.get(LocationTable.KEY_LOCATION_TIMESTAMP) + ", lat: " + record.get(LocationTable.KEY_LOCATION_LATITUDE) + ", long: " + record.get(LocationTable.KEY_LOCATION_LONGITUDE));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
