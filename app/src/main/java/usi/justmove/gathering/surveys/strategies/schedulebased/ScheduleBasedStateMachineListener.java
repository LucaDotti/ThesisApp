package usi.justmove.gathering.surveys.strategies.schedulebased;

import java.util.Observable;
import java.util.Observer;

import usi.justmove.gathering.base.StateMachineListener;

/**
 * Created by usi on 06/02/17.
 */

public abstract class ScheduleBasedStateMachineListener implements Observer, StateMachineListener {
    protected ScheduleBasedState currentState;

    @Override
    public void update(Observable o, Object arg) {
        currentState = (ScheduleBasedState) arg;
        processStateChanged();
    }

    @Override
    public void processStateChanged() {
        switch(currentState) {
            case NOTIFY:
                processNotifyState();
                break;
            case WAIT:
                processWaitState();
                break;
        }
    }

    protected abstract void processWaitState();
    protected abstract void processNotifyState();
}
