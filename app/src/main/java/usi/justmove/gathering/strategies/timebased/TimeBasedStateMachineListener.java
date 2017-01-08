package usi.justmove.gathering.strategies.timebased;

import java.util.Observable;
import java.util.Observer;

import usi.justmove.gathering.base.StateMachineListener;

import static usi.justmove.gathering.strategies.timebased.TimeBasedSMState.NIGHT;

/**
 * Created by usi on 07/01/17.
 */

public abstract class TimeBasedStateMachineListener implements Observer, StateMachineListener {
    protected TimeBasedSMState currentState;
    protected long dayFreq;
    protected long nightFreq;

    public TimeBasedStateMachineListener(long dayFreq, long nightFreq) {
        this.dayFreq = dayFreq;
        this.nightFreq = nightFreq;
    }

    @Override
    public void update(Observable o, Object arg) {
        currentState = (TimeBasedSMState) arg;
        processStateChanged();
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
