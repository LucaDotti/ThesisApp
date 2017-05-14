package usi.memotion.gathering.gatheringServices;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.Calendar;
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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static usi.memotion.R.string.dayEnd;
import static usi.memotion.R.string.stateMachineFreq;

/**
 * Created by Luca Dotti on 03/01/17.
 */

public class LocationGatheringService extends Service {
    public static String LOCATION_ALARM_INTENT = "location_alarm_intent";
    private TBStateMachine stateMachine;
    private ScheduledExecutorService scheduler;
    private LocationTBStateMachineListener listener;
    private BroadcastReceiver broadcastReceiver;

    private LocalStorageController localStorageController;
    private LocationManager mgr;
    private AlarmManager alarmManager;
    private Context context;
    private long dayFreq;
    private long nightFreq;
    private int dayId;
    private int nightId;
    private String nightTimeS;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Locationservice", "INITIALIZED");
        context = MyApplication.getContext();
        localStorageController = SQLiteController.getInstance(MyApplication.getContext());
        mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        broadcastReceiver = new LocationBroadcastReceiver();
        IntentFilter filter = new IntentFilter(LOCATION_ALARM_INTENT);
        context.registerReceiver(broadcastReceiver, filter);
        long stateMachineFreq = Long.parseLong(getApplicationContext().getString(R.string.stateMachineFreq));
        dayFreq = Long.parseLong(getApplicationContext().getString(R.string.locationDayFreq));
        nightFreq = Long.parseLong(getApplicationContext().getString(R.string.locationNightFreq));
        dayId = 0;
        nightId = 1;
        long minDistance = Long.parseLong(getApplicationContext().getString(R.string.locationMinDistance));
        String dayStart = getApplicationContext().getString(R.string.dayStart);
        nightTimeS = getApplicationContext().getString(dayEnd);

        //transitions of the state machine
        TBSMState[][] transitions = new TBSMState[4][4];
        transitions[TBSMState.START.ordinal()][TBSMSymbol.IS_DAY.ordinal()] = TBSMState.DAY;
        transitions[TBSMState.START.ordinal()][TBSMSymbol.IS_NIGHT.ordinal()] = TBSMState.NIGHT;
        transitions[TBSMState.DAY.ordinal()][TBSMSymbol.IS_NIGHT.ordinal()] = TBSMState.NIGHT;
        transitions[TBSMState.DAY.ordinal()][TBSMSymbol.IS_DAY.ordinal()] = TBSMState.DAY;
        transitions[TBSMState.NIGHT.ordinal()][TBSMSymbol.IS_DAY.ordinal()] = TBSMState.DAY;
        transitions[TBSMState.NIGHT.ordinal()][TBSMSymbol.IS_NIGHT.ordinal()] = TBSMState.NIGHT;

        stateMachine = new TBStateMachine(transitions, TBSMState.START, dayStart, nightTimeS);

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private Calendar getNightStartDateTime() {
        String[] splitted = nightTimeS.split(":");
        Calendar sNight = Calendar.getInstance();
        sNight.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitted[0]));
        sNight.set(Calendar.MINUTE, Integer.parseInt(splitted[1]));
        sNight.set(Calendar.SECOND, Integer.parseInt(splitted[2]));
        return sNight;
    }

    private PendingIntent getIntent(boolean day) {
        Intent intent = new Intent(LocationGatheringService.LOCATION_ALARM_INTENT);
        if(day) {
            intent.putExtra("freq", dayFreq);
            intent.putExtra("day", true);
            return PendingIntent.getBroadcast(context,
                    dayId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            intent.putExtra("freq", nightFreq);
            intent.putExtra("day", false);
            return PendingIntent.getBroadcast(context,
                    nightId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
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

                boolean day = intent.getBooleanExtra("day", true);
                long freq = intent.getLongExtra("freq", 0);
                PendingIntent pIntent = getIntent(day);
                Calendar now = Calendar.getInstance();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, now.getTimeInMillis() + freq, pIntent);
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

        private boolean isDay;
        private boolean init;

        public LocationTBStateMachineListener(long dayFreq, long nightFreq) {
            super(dayFreq, nightFreq);
            Calendar now = Calendar.getInstance();
            if(now.getTimeInMillis() < getNightStartDateTime().getTimeInMillis()) {
                isDay = true;
            } else {
                isDay = false;
            }
            init = true;
        }

        @Override
        protected void processDayState() {
            Log.d("Locationservice", "Day");

            if(init || !isDay) {
                Log.d("Locationservice", "really day");
                PendingIntent nightIntent = getIntent(false);
                alarmManager.cancel(nightIntent);
                Calendar now = Calendar.getInstance();

                PendingIntent dayIntent = getIntent(true);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), dayIntent);
            }

            isDay = true;
            init = false;
        }

        @Override
        protected void processNightState() {
            Log.d("Locationservice", "Night");

            if(init || isDay) {
                Log.d("Locationservice", "really night");
                PendingIntent dayIntent = getIntent(true);
                alarmManager.cancel(dayIntent);
                Calendar now = Calendar.getInstance();
                PendingIntent nightIntent = getIntent(false);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), nightIntent);
            }

            isDay = false;
            init = false;
        }

    }

}













//class LocationTimeTask extends TimerTask {
//    private LocationManager mgr;
//    private LocalStorageController localStorageController;
//    private Context context;
//
//    public LocationTimeTask() {
//        context = MyApplication.getContext();
//        localStorageController = SQLiteController.getInstance(context);
//        mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//    }
//
//    @Override
//    public void run() {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Location location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            Log.d("Locationservice", "" + location);
//            if(location == null) {
//                location = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                Log.d("Locationservice", "" + location);
//                saveLocation(location);
//            } else {
//                saveLocation(location);
//            }
//        }
//
//    }
//
//    private void saveLocation(Location location) {
//        ContentValues record = new ContentValues();
//
//        record.put(LocationTable.KEY_LOCATION_TIMESTAMP, Long.toString(System.currentTimeMillis()));
//
//        if(location == null) {
//            record.put(LocationTable.KEY_LOCATION_LATITUDE, 0);
//            record.put(LocationTable.KEY_LOCATION_LONGITUDE, 0);
//            record.put(LocationTable.KEY_LOCATION_PROVIDER, "unknown");
//        } else {
//            record.put(LocationTable.KEY_LOCATION_LATITUDE, Double.toString(location.getLatitude()));
//            record.put(LocationTable.KEY_LOCATION_LONGITUDE, Double.toString(location.getLongitude()));
//            record.put(LocationTable.KEY_LOCATION_PROVIDER, location.getProvider());
//        }
//
//
//        localStorageController.insertRecord(LocationTable.TABLE_LOCATION, record);
//        Log.d("Locationservice", "ADDED RECORD: ts:" + record.get(LocationTable.KEY_LOCATION_TIMESTAMP) + ", lat: " + record.get(LocationTable.KEY_LOCATION_LATITUDE) + ", long: " + record.get(LocationTable.KEY_LOCATION_LONGITUDE));
//    }
//}