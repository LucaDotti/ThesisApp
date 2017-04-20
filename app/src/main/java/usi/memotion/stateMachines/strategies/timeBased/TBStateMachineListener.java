package usi.memotion.stateMachines.strategies.timeBased;

import java.util.Observable;
import java.util.Observer;

import usi.memotion.stateMachines.base.StateMachineListener;

/**
 * Created by usi on 07/01/17.
 */

public abstract class TBStateMachineListener implements Observer, StateMachineListener {
    protected TBSMState currentState;
    protected long dayFreq;
    protected long nightFreq;

    public TBStateMachineListener(long dayFreq, long nightFreq) {
        this.dayFreq = dayFreq;
        this.nightFreq = nightFreq;
    }

    @Override
    public void update(Observable o, Object arg) {
        TBSMState newState = (TBSMState) arg;

        if(currentState != newState) {
            currentState = newState;
            processStateChanged();
        }
    }

    @Override
    public void processStateChanged() {
        switch(currentState) {
            case DAY:
                processDayState();
                break;
            case NIGHT:
                processNightState();
                break;
        }
    }

    protected abstract void processDayState();
    protected abstract void processNightState();
}
