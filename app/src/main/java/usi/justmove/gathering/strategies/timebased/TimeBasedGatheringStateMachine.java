package usi.justmove.gathering.strategies.timebased;

import usi.justmove.gathering.base.StateMachine;

/**
 *
 *  +---+     +---+
 *  | S | --> | D |
 *  +---+     +---+
 *    |       ^   |
 *    v       |   |
 *  +---+ ----+   |
 *  | N |         |
 *  +---+ <-------+
 *
 *
 * Created by Luca Dotti on 28/12/16.
 */

public class TimeBasedGatheringStateMachine implements StateMachine<TimeBasedSMState, TimeBasedSMSymbol> {
    //current state
    private TimeBasedSMState currentState;
    //transition function
    private TimeBasedSMState[][] transitionFunction;

    public TimeBasedGatheringStateMachine() {
        currentState = TimeBasedSMState.START;
        transitionFunction = new TimeBasedSMState[4][4];
        transitionFunction[TimeBasedSMState.START.ordinal()][TimeBasedSMSymbol.IS_DAY.ordinal()] =  TimeBasedSMState.DAY;
        transitionFunction[TimeBasedSMState.START.ordinal()][TimeBasedSMSymbol.IS_NIGHT.ordinal()] =  TimeBasedSMState.NIGHT;
        transitionFunction[TimeBasedSMState.DAY.ordinal()][TimeBasedSMSymbol.IS_NIGHT.ordinal()] =  TimeBasedSMState.NIGHT;
        transitionFunction[TimeBasedSMState.DAY.ordinal()][TimeBasedSMSymbol.IS_DAY.ordinal()] =  TimeBasedSMState.DAY;
        transitionFunction[TimeBasedSMState.NIGHT.ordinal()][TimeBasedSMSymbol.IS_DAY.ordinal()] =  TimeBasedSMState.DAY;
        transitionFunction[TimeBasedSMState.NIGHT.ordinal()][TimeBasedSMSymbol.IS_NIGHT.ordinal()] =  TimeBasedSMState.NIGHT;
    }

    @Override
    public void transition(TimeBasedSMSymbol symbol) {
        currentState = transitionFunction[currentState.ordinal()][symbol.ordinal()];
    }

    @Override
    public TimeBasedSMState currentState() {
        return currentState;
    }
}