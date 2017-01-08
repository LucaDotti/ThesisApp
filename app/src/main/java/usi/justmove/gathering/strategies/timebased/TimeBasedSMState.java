package usi.justmove.gathering.strategies.timebased;

/**
 * Created by usi on 28/12/16.
 */
public enum TimeBasedSMState {
    //currently is day
    DAY,
    //currently is night
    NIGHT,
    //start state: don't know if it is day or night, need to wait for an input
    START;

    TimeBasedSMState getStartState() {
        return START;
    }
}
