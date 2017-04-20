package usi.memotion.remote.database.upload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import usi.memotion.R;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.remote.database.controllers.SwitchDriveController;
import usi.memotion.stateMachines.strategies.wifiPowerDbSize.WPMSMState;
import usi.memotion.stateMachines.strategies.wifiPowerDbSize.WPMSMSymbol;
import usi.memotion.stateMachines.strategies.wifiPowerDbSize.WPMStateMachine;
import usi.memotion.stateMachines.strategies.wifiPowerDbSize.WPMStateMachineListener;

import android.provider.Settings.Secure;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by usi on 16/01/17.
 */

public class DataUploadService extends Service {
    private WPMStateMachine stateMachine;
    private ScheduledExecutorService scheduler;
    private long stateMachineFreq;
    private long maxDbSize;
    private long uploadTreshold;
    private Uploader uploader;
    private int minBatteryLevel;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UPLOAD SERVICE", "STARTED SERVICE");

        stateMachineFreq = Long.parseLong(getApplicationContext().getString(R.string.uploaderStateMachineFreq));
        maxDbSize = Long.parseLong(getApplicationContext().getString(R.string.uploaderMaxDbSize));
        uploadTreshold = Long.parseLong(getApplicationContext().getString(R.string.uploaderUploadThreshold));
//        uploadTreshold = 0;
        minBatteryLevel = Integer.parseInt(getApplicationContext().getString(R.string.uploaderMinBatteryLevel));
//        minBatteryLevel = 0;

        WPMSMState[][] transitions = new WPMSMState[3][3];
        transitions[WPMSMState.WAITING.ordinal()][WPMSMSymbol.WAIT.ordinal()] = WPMSMState.WAITING;
        transitions[WPMSMState.WAITING.ordinal()][WPMSMSymbol.UPLOAD.ordinal()] = WPMSMState.UPLOADING;
        transitions[WPMSMState.WAITING.ordinal()][WPMSMSymbol.FORCE_UPLOAD.ordinal()] = WPMSMState.FORCED_UPLOADING;
        transitions[WPMSMState.UPLOADING.ordinal()][WPMSMSymbol.WAIT.ordinal()] = WPMSMState.WAITING;
        transitions[WPMSMState.UPLOADING.ordinal()][WPMSMSymbol.UPLOAD.ordinal()] = WPMSMState.UPLOADING;
        transitions[WPMSMState.UPLOADING.ordinal()][WPMSMSymbol.FORCE_UPLOAD.ordinal()] = WPMSMState.FORCED_UPLOADING;
        transitions[WPMSMState.FORCED_UPLOADING.ordinal()][WPMSMSymbol.WAIT.ordinal()] = WPMSMState.WAITING;
        transitions[WPMSMState.FORCED_UPLOADING.ordinal()][WPMSMSymbol.FORCE_UPLOAD.ordinal()] = WPMSMState.FORCED_UPLOADING;
        transitions[WPMSMState.FORCED_UPLOADING.ordinal()][WPMSMSymbol.UPLOAD.ordinal()] = WPMSMState.UPLOADING;

        stateMachine = new WPMStateMachine(transitions, WPMSMState.WAITING, maxDbSize, minBatteryLevel);

        uploader = new Uploader(
                Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID),
                new SwitchDriveController(getApplicationContext().getString(R.string.server_address), getApplicationContext().getString(R.string.token), getApplicationContext().getString(R.string.password)),
                SQLiteController.getInstance(getApplicationContext()),
                uploadTreshold);

        //add the observer
        stateMachine.addObserver(new WPMStateMachineListener(uploader));

        //start the state machine
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(stateMachine, 0, stateMachineFreq, TimeUnit.MILLISECONDS);


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
//        stateMachine.terminate();
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