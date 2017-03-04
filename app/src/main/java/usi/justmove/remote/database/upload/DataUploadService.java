package usi.justmove.remote.database.upload;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import usi.justmove.R;
import usi.justmove.gathering.base.StateMachine;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.remote.database.controllers.GoogleDriveController;
import usi.justmove.remote.database.controllers.SwitchDriveController;
import usi.justmove.remote.database.upload.strategies.WifiPowerMemoryInputProvider;
import usi.justmove.remote.database.upload.strategies.WifiPowerMemorySMState;
import usi.justmove.remote.database.upload.strategies.WifiPowerMemorySMSymbol;
import usi.justmove.remote.database.upload.strategies.WifiPowerMemoryStateMachineListener;
import android.provider.Settings.Secure;
/**
 * Created by usi on 16/01/17.
 */

public class DataUploadService extends Service {
    private Thread stateMachineThread;
    private StateMachine<WifiPowerMemorySMState, WifiPowerMemorySMSymbol> stateMachine;
    private long stateMachineFreq;
    private long maxDbSize;
    private long uploadTreshold;
    private Uploader uploader;
    private int minBatteryLevel;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UPLOAD SERVICE", "STARTED SERVICE");

        stateMachineFreq = Long.parseLong(getApplicationContext().getString(R.string.stateMachineFreq));
        maxDbSize = Long.parseLong(getApplicationContext().getString(R.string.uploaderMaxDbSize));
        uploadTreshold = Long.parseLong(getApplicationContext().getString(R.string.uploaderUploadThreshold));
//        uploadTreshold = 0;
        minBatteryLevel = Integer.parseInt(getApplicationContext().getString(R.string.uploaderMinBatteryLevel));
//        minBatteryLevel = 0;

        WifiPowerMemorySMState[][] transitions = new WifiPowerMemorySMState[3][3];
        transitions[WifiPowerMemorySMState.WAITING.ordinal()][WifiPowerMemorySMSymbol.WAIT.ordinal()] = WifiPowerMemorySMState.WAITING;
        transitions[WifiPowerMemorySMState.WAITING.ordinal()][WifiPowerMemorySMSymbol.UPLOAD.ordinal()] = WifiPowerMemorySMState.UPLOADING;
        transitions[WifiPowerMemorySMState.WAITING.ordinal()][WifiPowerMemorySMSymbol.FORCE_UPLOAD.ordinal()] = WifiPowerMemorySMState.FORCED_UPLOADING;
        transitions[WifiPowerMemorySMState.UPLOADING.ordinal()][WifiPowerMemorySMSymbol.WAIT.ordinal()] = WifiPowerMemorySMState.WAITING;
        transitions[WifiPowerMemorySMState.UPLOADING.ordinal()][WifiPowerMemorySMSymbol.UPLOAD.ordinal()] = WifiPowerMemorySMState.UPLOADING;
        transitions[WifiPowerMemorySMState.UPLOADING.ordinal()][WifiPowerMemorySMSymbol.FORCE_UPLOAD.ordinal()] = WifiPowerMemorySMState.FORCED_UPLOADING;
        transitions[WifiPowerMemorySMState.FORCED_UPLOADING.ordinal()][WifiPowerMemorySMSymbol.WAIT.ordinal()] = WifiPowerMemorySMState.WAITING;
        transitions[WifiPowerMemorySMState.FORCED_UPLOADING.ordinal()][WifiPowerMemorySMSymbol.FORCE_UPLOAD.ordinal()] = WifiPowerMemorySMState.FORCED_UPLOADING;
        transitions[WifiPowerMemorySMState.FORCED_UPLOADING.ordinal()][WifiPowerMemorySMSymbol.UPLOAD.ordinal()] = WifiPowerMemorySMState.UPLOADING;

        stateMachine = new StateMachine<>(
                new WifiPowerMemoryInputProvider(getApplicationContext(), maxDbSize, minBatteryLevel),
                transitions,
                WifiPowerMemorySMState.WAITING, stateMachineFreq);

        uploader = new Uploader(
                Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID),
                new SwitchDriveController(getApplicationContext().getString(R.string.server_address), getApplicationContext().getString(R.string.token), getApplicationContext().getString(R.string.password)),
                SQLiteController.getInstance(getApplicationContext()),
                uploadTreshold);

        //add the observer
        stateMachine.addObserver(new WifiPowerMemoryStateMachineListener() {
            @Override
            protected void processWaitingState() {
//                Log.d("DATA UPLOAD SERVICE", "STATE: WAITING");
                //do nothing
                return;
            }

            @Override
            protected void processUploadingState() {
                Log.d("DATA UPLOAD SERVICE", "STATE: UPLOADING");
                uploader.upload();
            }

            @Override
            protected void processForcedUploadingState() {
//                Log.d("DATA UPLOAD SERVICE", "STATE: FORCED UPLOADING");
                processUploadingState();
            }
        });

        stateMachineThread = new Thread(stateMachine);
        //start state machine
        stateMachineThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stateMachine.terminate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}