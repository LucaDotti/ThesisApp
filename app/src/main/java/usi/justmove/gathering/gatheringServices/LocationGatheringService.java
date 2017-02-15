package usi.justmove.gathering.gatheringServices;

import android.Manifest;
import android.app.Service;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usi.justmove.R;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocationTable;
import usi.justmove.gathering.base.StateMachine;
import usi.justmove.gathering.strategies.timebased.TimeBasedInputProvider;
import usi.justmove.gathering.strategies.timebased.TimeBasedSMState;
import usi.justmove.gathering.strategies.timebased.TimeBasedSMSymbol;
import usi.justmove.gathering.strategies.timebased.TimeBasedStateMachineListener;
import usi.justmove.utils.FrequencyHelper;

/**
 * Created by Luca Dotti on 03/01/17.
 */

public class LocationGatheringService extends Service {
    private Thread stateMachineThread;
    private StateMachine<TimeBasedSMState, TimeBasedSMSymbol> stateMachine;

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
        TimeBasedSMState[][] transitions = new TimeBasedSMState[4][4];
        transitions[TimeBasedSMState.START.ordinal()][TimeBasedSMSymbol.IS_DAY.ordinal()] = TimeBasedSMState.DAY;
        transitions[TimeBasedSMState.START.ordinal()][TimeBasedSMSymbol.IS_NIGHT.ordinal()] = TimeBasedSMState.NIGHT;
        transitions[TimeBasedSMState.DAY.ordinal()][TimeBasedSMSymbol.IS_NIGHT.ordinal()] = TimeBasedSMState.NIGHT;
        transitions[TimeBasedSMState.DAY.ordinal()][TimeBasedSMSymbol.IS_DAY.ordinal()] = TimeBasedSMState.DAY;
        transitions[TimeBasedSMState.NIGHT.ordinal()][TimeBasedSMSymbol.IS_DAY.ordinal()] = TimeBasedSMState.DAY;
        transitions[TimeBasedSMState.NIGHT.ordinal()][TimeBasedSMSymbol.IS_NIGHT.ordinal()] = TimeBasedSMState.NIGHT;

        stateMachine = new StateMachine<>(new TimeBasedInputProvider(dayStart, dayEnd), transitions, TimeBasedSMState.START, stateMachineFreq);

        //add the observer
        stateMachine.addObserver(new LocationTimeBasedStateMachineListener(getApplicationContext(), dayFreq, nightFreq, minDistance));
        stateMachineThread = new Thread(stateMachine);
        //start state machine
        stateMachineThread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

class LocationTimeBasedStateMachineListener extends TimeBasedStateMachineListener {
    private Context context;
    private LocationManager mgr;
    private LocationListener listener;
    private float minDistance;
    private HandlerThread locationHandlerThread;

    public LocationTimeBasedStateMachineListener(Context context, long dayFreq, long nightFreq, float minDistance) {
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

            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, FrequencyHelper.getElapseTimeMillis(dayFreq), minDistance, listener, locationHandlerThread.getLooper());
        }

    }

    @Override
    protected void processNightState() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Log.d("LOCATION SERVICE", "NIGHT");
        mgr.removeUpdates(listener);
        Looper.prepare();
        mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FrequencyHelper.getElapseTimeMillis(nightFreq), minDistance, listener);
    }
}

class LocationEventListener implements LocationListener {
    private Context context;
    private LocalStorageController localStorageController;

    public LocationEventListener(Context context) {
        this.context = context;
        localStorageController = new SQLiteController(context);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("LOCATION SERVICE", "LOCATION CHANGED");
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record = new HashMap<>();

        record.put(LocationTable.KEY_LOCATION_ID, null);
        record.put(LocationTable.KEY_LOCATION_TIMESTAMP, Long.toString(System.currentTimeMillis()));
        record.put(LocationTable.KEY_LOCATION_LATITUDE, Double.toString(location.getLatitude()));
        record.put(LocationTable.KEY_LOCATION_LONGITUDE, Double.toString(location.getLongitude()));
        record.put(LocationTable.KEY_LOCATION_PROVIDER, "GPS");
        records.add(record);
        localStorageController.insertRecords(LocationTable.TABLE_LOCATION, records);
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
