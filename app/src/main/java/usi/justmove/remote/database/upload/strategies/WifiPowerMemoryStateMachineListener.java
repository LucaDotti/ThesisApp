package usi.justmove.remote.database.upload.strategies;

import android.util.Log;

import java.util.Observable;
import java.util.Observer;

import usi.justmove.gathering.base.StateMachineListener;

/**
 * Created by usi on 17/01/17.
 */

public abstract class WifiPowerMemoryStateMachineListener implements Observer, StateMachineListener {
    protected WifiPowerMemorySMState currentState;

    @Override
    public void update(Observable o, Object arg) {
        currentState = (WifiPowerMemorySMState) arg;
        processStateChanged();
    }

    @Override
    public void processStateChanged() {
        switch(currentState) {
            case UPLOADING:
                processUploadingState();
                break;
            case FORCED_UPLOADING:
                processForcedUploadingState();
                break;
            case WAITING:
                processWaitingState();
                break;
        }
    }

    protected abstract void processWaitingState();
    protected abstract void processUploadingState();
    protected abstract void processForcedUploadingState();
}
