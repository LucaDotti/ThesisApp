package usi.memotion.stateMachines.strategies.wifiPowerDbSize;

import android.util.Log;

import java.util.Observable;
import java.util.Observer;

import usi.memotion.remote.database.upload.Uploader;
import usi.memotion.stateMachines.base.StateMachineListener;
import usi.memotion.stateMachines.strategies.timeBased.TBSMState;

import static usi.memotion.stateMachines.strategies.timeBased.TBSMState.NIGHT;

/**
 * Created by usi on 20/04/17.
 */

public class WPMStateMachineListener implements Observer, StateMachineListener {
    private WPMSMState currentState;
    private Uploader uploader;

    public WPMStateMachineListener(Uploader uploader) {
        this.uploader = uploader;
    }

    @Override
    public void processStateChanged() {
        switch(currentState) {
            case FORCED_UPLOADING:
            case UPLOADING:
                processUploadingState();
                break;
            case WAITING:
                processWaitingState();
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        WPMSMState newState = (WPMSMState) arg;

        if(currentState != newState) {
            currentState = newState;
            processStateChanged();
        }
        currentState = WPMSMState.WAITING;
    }

    public void processUploadingState() {
        Log.d("STATE MACHINE", "STATE UPLOADING");
        uploader.upload();
    }

    public void processWaitingState() {
        Log.d("STATE MACHINE", "STATE WAITING");
    }
}
