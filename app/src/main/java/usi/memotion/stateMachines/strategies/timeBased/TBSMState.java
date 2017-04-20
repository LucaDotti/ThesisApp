package usi.memotion.stateMachines.strategies.timeBased;

/**
 * Created by usi on 28/12/16.
 */
public enum TBSMState {
    //currently is day
    DAY,
    //currently is night
    NIGHT,
    //start state: don't know if it is day or night, need to wait for an input
    START;

    TBSMState getStartState() {
        return START;
    }
}
